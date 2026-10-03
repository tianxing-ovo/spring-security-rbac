# Spring Security RBAC

基于 **Spring Boot 3.5.16 + Spring Security 6.5.11 + JWT + Redis + MySQL** 构建的前后端分离无状态认证与细粒度 RBAC 权限管理脚手架

---

## 技术选型

* **核心框架**：Spring Boot `3.5.16`
* **安全框架**：Spring Security `6.5.11`
* **令牌协议**：JJWT `0.12.6`
* **缓存组件**：Spring Data Redis `3.5.13`
* **持久层**：MyBatis-Plus `3.5.7` + MySQL `8.x`
* **运行环境**：JDK `17`

---

## 初始账号与权限

|   用户名    | 默认密码 | 绑定角色 |              拥有权限              |      权限说明      |
|:-----------:|:--------:|:--------:|:----------------------------------:|:------------------:|
| **`admin`** | `123456` | `admin`  | `query`, `add`, `update`, `delete` |  拥有全部接口权限  |
| **`user`**  | `123456` |  `user`  |              `query`               | 仅拥有用户查询权限 |

---

## 双令牌机制

* **登录签发**：登录成功签发短效 `AccessToken`（30分钟）与长效 `RefreshToken`（7天）
* **日常鉴权**：客户端携带 `AccessToken` 请求接口，服务端本地纯无状态验签，零 Redis 查询损耗
* **无感续期**：`AccessToken` 过期时，客户端携带 `RefreshToken` 请求 `/auth/refresh` 静默换取新令牌
* **退出注销**：用户调用 `/logout` 退出登录，服务端从 Redis 中物理删除 `RefreshToken`，彻底切断续期链路

|      令牌类型      | 有效期 | 存储位置 |   核心职责   |   失效机制   |
|:------------------:|:------:|:--------:|:------------:|:------------:|
| **`AccessToken`**  | 30分钟 |  客户端  | 接口访问鉴权 | 到期自然失效 |
| **`RefreshToken`** |  7天   |  Redis   | 换取访问令牌 | 退出主动销毁 |

---

## 常见方案对比

|    解决方案    |          核心机制          |          核心优势          |           主要劣势           |    选型判定    |
|:--------------:|:--------------------------:|:--------------------------:|:----------------------------:|:--------------:|
| **双令牌机制** | AccessToken + RefreshToken |    零IO高并发且续期可控    |   AccessToken无法立即失效    | **本项目采用** |
| **白名单机制** | 登录时将有效Token写入缓存  |   绝对实时控制与精准踢人   |  每次请求必查缓存性能损耗大  |     未采用     |
| **黑名单机制** | 退出时将失效Token写入缓存  | 大幅节省缓存空间且即刻作废 | 每次请求仍需排查黑名单开销大 |     未采用     |

---

## 过滤器链与请求全生命周期

```text
 客户端
   │
   ▼
[SecurityFilterChain]
   │
   ├─► LogoutFilter
   │         └─► [POST /logout] ──► logoutSuccessHandler (退出成功)
   │
   ├─► JwtFilter
   │         ├─► [未携带令牌] (accessToken为空)
   │         ├─► [携带过期令牌] (accessToken过期)
   │         ├─► [携带无效令牌] (accessToken无效)
   │         └─► [白名单或携带有效令牌]
   │                    │
   │                    ▼
   ├─► UsernamePasswordAuthenticationFilter
   │         └─► [POST /login]
   │                   ├─► [校验通过] ──► successHandler (登录成功)
   │                   └─► [校验失败] ──► failureHandler (登录失败)
   │
   ├─► ExceptionTranslationFilter (统一捕获异常并转交accessDeniedHandler)
            │ (try: 放行请求)
            ▼
    AuthorizationFilter
            └─► [白名单或已认证]
                      │
                      ▼
             [DispatcherServlet]
                      │
                      ▼
               [@PreAuthorize]
                      ├─► [鉴权失败] ──► 抛出AccessDeniedException
                      └─► [鉴权成功]
                              │
                              ▼
                       [Controller] ──► 业务处理完成
```

---

## 接口清单

|   功能模块   | 接口名称 | 请求方法 |        接口路径        | 访问权限 |
|:------------:|:--------:|:--------:|:----------------------:|:--------:|
| **认证会话** | 用户登录 |  `POST`  |        `/login`        |   公开   |
| **认证会话** | 刷新令牌 |  `POST`  |    `/auth/refresh`     |   公开   |
| **认证会话** | 退出登录 |  `POST`  |       `/logout`        | 登录用户 |
| **用户管理** | 查询用户 |  `GET`   |        `/user`         | 登录用户 |
| **用户管理** | 添加用户 |  `POST`  |        `/user`         |  管理员  |
| **用户管理** | 修改用户 |  `PUT`   |      `/user/{id}`      |  管理员  |
| **用户管理** | 删除用户 | `DELETE` |      `/user/{id}`      |  管理员  |
| **安全凭证** | 获取认证 |  `GET`   |  `/me/authentication`  | 登录用户 |
| **安全凭证** | 获取主体 |  `GET`   |    `/me/principal`     | 登录用户 |
| **安全凭证** | 获取凭证 |  `GET`   | `/me/security-context` | 登录用户 |

---

## 响应状态码

|   状态码    |     提示信息     |       产生场景说明       |
|:-----------:|:----------------:|:------------------------:|
|  **`200`**  |       成功       |       接口调用成功       |
| **`40001`** |     登录失败     |     用户名或密码错误     |
| **`40101`** | accessToken为空  | 请求头缺少`accessToken`  |
| **`40102`** | accessToken过期  |      访问令牌已过期      |
| **`40103`** | accessToken无效  |  令牌签名无效或格式错误  |
| **`40104`** | refreshToken为空 | 请求头缺少`refreshToken` |
| **`40105`** | refreshToken过期 |   刷新令牌已过期或失效   |
| **`40301`** |     权限不足     |      访问未授权接口      |

---

## 开源协议

本项目基于 [MIT](LICENSE) 开源协议
