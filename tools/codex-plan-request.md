# 给 Codex 的计划请求

## 角色
你是 VMS 项目 CTO，请产出一份**可执行**的改造计划，覆盖从当前状态到"前后端分离 + 工单 CRUD + 多页面迁移 + 生产部署"完成。

## 当前状态（Claude Code 已完成）

### 后端（vms/）
- Java 8 + Spring 4.2 + Cayenne 3.1 + MySQL 8 + Redis 3.0（单点）+ RabbitMQ 3.12
- 本地 Tomcat 9（cargo 插件，8080 端口）
- 已迁移：Oracle→MySQL（Cayenne 自动适配，业务代码零改动）
- Redis JedisCluster→JedisPool（业务代码零改动）
- OSS→本地文件存储 `D:/vms-data`（Shiro 已放行 `/vmsimgs/**`，Spring 静态映射就绪）
- MCS mock：`tools/mcs_mock.py`（8011 端口，success=0）
- 配送工单服务端闭环 100%：创建/取消/状态流转/库存扣减/MCS 通知全验证
- REST 端点：`GET /vms/api/deliveryTask/listAll`（已加显式鉴权：Shiro 放行 + controller `requireAuth()` 校验 `isAuthenticated()`）
- Shiro 已放行 `/vms/api/**`（鉴权在 controller 内做）

### 前端（vms-ui/，独立仓库，不在 vms/ git 内）
- Vue 3.5 + TypeScript + Vite 8 + Vue Router 4 + Pinia
- 4 个页面：Login/Home/DeliveryTask/About
- API 客户端：`/api` 前缀代理到 8080，cookieDomainRewrite 已通，credentials:'include' 带 cookie，401 跳 /login?redirect=...
- 路由守卫：Pinia 内存态未登录跳登录页
- E2E 测试 12/12 通过（含登录/cookie/REST 401 鉴权）

### 测试
- 后端 7 个集成测试（MySQL/设置树/Redis/RabbitMQ/工单状态机/库存扣减/本地存储），全过
- 前端 E2E（PowerShell）12/12 通过
- 全部 commit 已推 GitHub：https://github.com/yangxue198749/Auto-sale-robot（main 分支）

### 已知限制
- Vue 路由守卫用 Pinia 内存态，刷新页面会丢失登录状态（Shiro session 仍有效但前端不知道）→ 需 `/vms/api/auth/me` 轻量端点

## 待办工作（请产出计划）

1. **工单 CRUD Vue 端**：DeliveryTask.vue 扩展到创建、查看详情、取消
2. **/vms/api/auth/me 端点**：解决刷新丢状态问题（返回当前用户 + 权限）
3. **其他三类工单闭环**：维护/投放/撤机，服务端逻辑已有，需 REST 端点 + Vue 页面
4. **其他页面迁移**：设备管理、策略管理、基础数据（按优先级排）
5. **生产部署**：Vite build → dist/ → Tomcat 静态资源；或 nginx 反代方案
6. **细粒度权限**：REST 端点按用户/角色过滤数据

## 计划格式要求

请按以下结构输出，便于 Claude Code 执行：

### 阶段划分
每个阶段独立可交付，阶段内按任务编号。每个任务：
- 任务 ID（如 T-1.1）
- 标题
- 文件范围（新增/修改哪些文件）
- 验收标准（测试/HTTP 断言）
- 估时（AI 协作下的粗略）
- 依赖（前置任务 ID）

### 任务编排建议
- 串行依赖清晰的，列出来
- 可并行的，标明
- Claude Code 执行 vs Codex 执行 vs 大聪明人工验证的分工

### 风险与决策点
- 列出需要大聪明拍板的关键决策（如：是否引入新的前端 UI 库、生产部署方案选型）
- 列出 Claude Code 执行时可能遇到的技术风险

## 约束
- 保持业务逻辑不变（不引入新功能，纯迁移+增强）
- 每个任务都应有可自动化验证的验收标准
- 计划粒度：一个任务 ≈ Claude Code 单次会话能完成（1-4 小时）
- 不要列"学习 XXX"这种任务，只列交付物明确的任务

请输出完整计划。
