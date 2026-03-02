# 在线景区门票订购与管理系统

本项目旨在为景区运营提供从用户购票到后台管理的全流程解决方案，采用前后端分离架构，包含后端接口、移动端客户端与管理端三大模块。系统设计遵循可扩展、可维护和高可用的原则。

## 模块概述

### 后端服务（`spring boot`）

- 构建于 Spring Boot 生态，采用 RESTful 风格的 API 设计。
- 负责核心业务逻辑、数据访问、事务管理及权限控制。
- 使用 Maven 作为依赖与构建工具；源码路径位于 `src/main/java/org/example/online_ticketing_system`。
- 提供票务类型、景区信息、订单处理、用户认证等接口。
- 数据库初始化与脚本文件存放在 `src/main/resources`（如 `ticket_types_table.sql`）。

### 移动端客户端（`uniapp`）

- 基于 uni-app 框架开发，支持多端（Android、iOS、小程序）一套代码运行。
- 页面组件在 `pages/` 目录，覆盖登录、注册、票种浏览、下单、订单查询等功能。
- 前端资源与脚本统一放置于 `static/`，包括请求封装、逻辑处理和样式表。
- 应用配置由 `manifest.json` 和 `pages.json` 管理，定义应用信息和路由。

### 管理端后台（`vue`）

- 使用 Vue 3 结合 Vite 进行构建，提供现代化单页管理界面。
- 支持景区信息维护、票种管理、订单审核、统计报表等管理功能。
- 组件源码位于 `src/components`，采用模块化设计以便扩展。
- 依赖治理由 `package.json` 管理，配合 `npm`/`yarn` 安装。
- 采用 Jest 进行前端组件的单元测试，相关测试文件在 `tests/`。

### 数据库

- `sql/` 目录包含完整的数据库建表语句及初始数据文件（`ticketing_db.sql`）。
- 后端项目同样包含用于本地环境初始化的 SQL 脚本。

## 目录结构概览

```
- spring boot/         # 后端 API 服务
- uniapp/              # 移动端客户端项目
- vue/                 # 后台管理端项目
- sql/                 # 数据库脚本
```

## 环境搭建与运行指南

1. **后端服务**
   - 安装[Java JDK 8或更高版本](https://www.oracle.com/java/technologies/javase-downloads.html)。
   - 进入 `spring boot` 目录，执行 `./mvnw spring-boot:run` 启动服务。
   - 修改 `src/main/resources/application.properties` 配置数据库连接信息。

2. **移动端**
   - 使用 HBuilderX 或 uni-app CLI 编译打包，根据目标平台生成 APK、IPA 或小程序包。

3. **管理端**
   - 切换至 `vue` 目录，运行 `npm install` 安装依赖。
   - 执行 `npm run dev` 启动本地开发服务器，访问默认地址查看页面。

## 开发与测试

- 后端单元测试位于 `spring boot/src/test/java`，建议使用 IDE 或 Maven 命令运行。
- 管理端使用 Jest 编写组件测试，透过 `npm run test` 执行。

## 贡献指南

若发现缺陷或有功能改进建议，可通过 GitHub 提交 Issue 或 Pull Request。欢迎社区参与，协作迭代完善项目。
