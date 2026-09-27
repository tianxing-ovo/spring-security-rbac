package com.ltx.controller;


import com.ltx.entity.Result;
import com.ltx.entity.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * PreAuthorize: 预授权 -> 在方法执行前进行权限校验 -> 决定是否允许方法的执行
 * PostAuthorize: 后授权 -> 在方法执行后对方法的结果进行权限校验 -> 决定是否允许返回结果或其中的某个属性访问
 *
 * @author tianxing
 */
@RestController
@RequestMapping("/user")
public class UserController {

    /**
     * 查询用户
     *
     * @return 通用响应对象
     */
    @GetMapping
    @PreAuthorize("hasAuthority('query') || hasAnyRole('user','admin')")
    public Result query() {
        return Result.success("查询成功");
    }

    /**
     * 添加用户
     *
     * @param user 用户
     * @return 通用响应对象
     */
    @PostMapping
    @PreAuthorize("hasAuthority('add') && hasRole('admin')")
    public Result add(@RequestBody User user) {
        return Result.success("添加成功").put("user", user);
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 通用响应对象
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('delete')")
    public Result delete(@PathVariable Long id) {
        return Result.success("删除成功").put("id", id);
    }

    /**
     * 更新用户
     *
     * @param id   用户id
     * @param user 用户
     * @return 通用响应对象
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update')")
    public Result update(@PathVariable Long id, @RequestBody User user) {
        return Result.success("修改成功").put("id", id).put("user", user);
    }
}
