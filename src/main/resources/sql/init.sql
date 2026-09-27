-- 创建数据库
create database if not exists spring_security;

-- 使用数据库
use spring_security;

-- 删除表
drop table if exists user;
drop table if exists role;
drop table if exists authority;
drop table if exists user_role;
drop table if exists role_authority;

-- 创建用户表
create table user
(
    id                      bigint       not null auto_increment primary key comment '主键ID',
    username                varchar(64)  not null comment '用户名',
    password                varchar(255) not null comment '密码',
    account_non_expired     tinyint(1)   not null default 1 comment '账户是否未过期',
    account_non_locked      tinyint(1)   not null default 1 comment '账户是否未锁定',
    credentials_non_expired tinyint(1)   not null default 1 comment '密码是否未过期',
    enabled                 tinyint(1)   not null default 1 comment '账户是否启用',
    unique key uk_username (username)
) comment = '用户表';

-- 创建角色表
create table role
(
    id               bigint      not null auto_increment primary key comment '主键ID',
    role_name        varchar(64) not null comment '角色名称',
    role_description varchar(255)         default null comment '角色描述',
    unique key uk_role_name (role_name)
) comment = '角色表';

-- 创建权限表
create table authority
(
    id                    bigint      not null auto_increment primary key comment '主键ID',
    authority_name        varchar(64) not null comment '权限标识',
    authority_description varchar(255)         default null comment '权限描述',
    unique key uk_authority_name (authority_name)
) comment = '权限表';

-- 创建用户-角色关联表
create table user_role
(
    id      bigint not null auto_increment primary key comment '主键ID',
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    unique key uk_user_role (user_id, role_id),
    key idx_role_id (role_id)
) comment = '用户-角色关联表';

-- 创建角色-权限关联表
create table role_authority
(
    id           bigint not null auto_increment primary key comment '主键ID',
    role_id      bigint not null comment '角色ID',
    authority_id bigint not null comment '权限ID',
    unique key uk_role_authority (role_id, authority_id),
    key idx_authority_id (authority_id)
) comment = '角色-权限关联表';

-- 插入用户表
-- password = 123456
insert into user (id, username, password)
values (1, 'admin', '$2a$10$xHA2XyxTFQNec209BgxMlONTIRZfKsTaSk6dZ8AdE//5XwIViUdjC'),
       (2, 'user', '$2a$10$UZLNUM8fzDJRGuDrB1C7RubvJERFeQ2Mr9lG808O9Y4MNhearJNbG');

-- 插入角色表
insert into role (id, role_name, role_description)
values (1, 'admin', '拥有系统所有权限'),
       (2, 'user', '仅拥有基础查询权限');

-- 插入权限表
insert into authority (id, authority_name, authority_description)
values (1, 'query', '查询用户'),
       (2, 'add', '添加用户'),
       (3, 'update', '修改用户'),
       (4, 'delete', '删除用户');

-- 插入用户-角色关联表
insert into user_role (user_id, role_id)
values (1, 1),
       (2, 2);

-- 插入角色-权限关联表
insert into role_authority (role_id, authority_id)
values (1, 1),
       (1, 2),
       (1, 3),
       (1, 4),
       (2,1);
