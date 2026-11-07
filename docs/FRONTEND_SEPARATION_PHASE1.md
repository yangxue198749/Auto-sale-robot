# 前后端分离 Phase 1 报告

**日期**：2026-11-08  
**目标**：配送工单 JSP → Vue 3 + REST API 完整闭环（含登录鉴权）  
**结论**：✅ 打通，Vue 3 前端登录 → REST 鉴权 → 工单数据，12 项 E2E 测试 + 10 项后端测试全过

---

## 一、完成的工作

### 1.1 Vue 3 + Vite 工程骨架（vms-ui/）
- Vue 3.5 + TypeScript + Vite 8 + Vue Router 4 + Pinia
- 4 个页面：Login、Home、DeliveryTask、About
- 全局路由守卫（Pinia 内存态未登录跳登录页，带 redirect 回跳）
- API 客户端封装：
  - `/api` 前缀代理到后端
  - `credentials: 'include'` 带 Shiro cookie
  - 401 自动跳 `/login?redirect=...`
- E2E 测试：`tests/login.e2e.ps1`（PowerShell 模拟浏览器，12 项断言）

### 1.2 后端 REST 端点 + 鉴权
- `VmsApiController`：独立 controller，类级 `@RequestMapping("/vms/api")`
- `GET /vms/api/deliveryTask/listAll`：返回所有配送工单 JSON
- **显式鉴权**：Shiro 放行 `/vms/api/**` 让请求进 controller，controller 内部调 `requireAuth()` 校验 `SecurityUtils.getSubject().isAuthenticated()`，未登录抛 401
- 401 返回 `{"code":401,"message":"未登录"}`，前端捕获后跳登录页

### 1.3 跨域与 cookie 透传
- Vite 开发服务器 5173 端口
- `/api/**` 代理到 `http://127.0.0.1:8080`（去掉 `/api` 前缀）
- `cookieDomainRewrite: ''`：剥离后端 Set-Cookie 的 `Domain=127.0.0.1`，让 cookie 归属 `localhost`，浏览器可正常存储

---

## 二、过程中解决的问题

### 2.1 maven-compiler-plugin 2.3.2 与 Java 17 不兼容
- **现象**：`SortOrder.DESCENDING` 编译报"找不到符号"，但 javac 单独编译通过
- **根因**：旧版 compiler-plugin 解析 classpath 有问题
- **解决**：升级到 3.11.0

### 2.2 Cayenne 3.1.1 API 细节
- `SelectQuery` 继承 `QualifiedQuery`，只有 `setQualifier` 没有 `addQualifier`
- `SortOrder` 在 cayenne-server-3.1.1.jar，classpath 正常
- 改用 `setQualifier` + 字符串属性名避开旧 compiler 的常量解析问题

### 2.3 类级 @RequestMapping 拼接冲突
- 在 `DeliveryTaskController`（类级 `deliveryTask`）加方法级 `/api/...` 路径会拼接错
- **解决**：新建独立 `VmsApiController`，类级 `/vms/api`

### 2.4 Shiro cookie domain 与 Vite 代理冲突
- 后端 cookie 设置 `Domain=127.0.0.1`，前端 `localhost:5173` 跨域无法存储
- **解决**：Vite `cookieDomainRewrite: ''` 剥离 Domain，cookie 归属 `localhost`

### 2.5 REST 端点鉴权方式选择
- 方案 A：Shiro filterChain 配置 `/vms/api/** = authc`——会让所有 REST 请求被 Shiro 302 跳登录，破坏 REST 契约
- 方案 B（采用）：Shiro 放行 `/vms/api/**`，controller 内部 `SecurityUtils.getSubject().isAuthenticated()` 显式鉴权，未登录返回 401 JSON
- **优势**：REST 契约不被破坏，前端按 401 状态码处理，可扩展到细粒度权限

---

## 三、验证证据

### 3.1 E2E 测试（12/12 PASS）
```
[PASS] Vue homepage HTML
[PASS] captcha is jpeg
[PASS] login code=1
[PASS] login msg
[PASS] TOUCHARM_VMS cookie set
[PASS] cookie HttpOnly
[PASS] workbench 200 with cookie
[PASS] REST returns array
[PASS] task has id
[PASS] REST 200 with cookie
[PASS] REST 401 without cookie      ← 鉴权
[PASS] no-cookie blocked (throws on redirect)
```

运行：`cd vms-ui/tests && pwsh -File login.e2e.ps1`

### 3.2 后端回归（10/10 PASS）
MySQL/设置树/Redis/RabbitMQ/工单状态机/库存扣减/本地存储 7 个集成测试全部通过。

### 3.3 真实浏览器验证路径
- `http://localhost:5173/` → 工作台（需登录）
- `http://localhost:5173/login` → 登录页（账号 admin 密码 1，验证码当天日期 bypass）
- `http://localhost:5173/delivery-task` → 工单列表（登录后自动加载 `/vms/api/deliveryTask/listAll`）

---

## 四、已知限制

1. **Vue 路由守卫用 Pinia 内存态**：刷新页面会丢失登录状态，跳登录页。Shiro session 仍有效但前端不知道。
   - 改进方向：加 `/vms/api/auth/me` 轻量端点，main.ts 启动时调一次刷新 session 状态到 Pinia
2. **REST 鉴权仅检查已登录**：未做细粒度权限（如按角色/用户过滤工单）
3. **工单列表无分页**：当前返回全部配送工单，生产环境需改分页 + 过滤

---

## 五、待办

1. **工单 CRUD**：扩展 DeliveryTask.vue 支持创建、查看、取消
2. **其他页面迁移**：设备管理、策略管理、基础数据
3. **生产部署**：Vite build → dist/ → Tomcat 静态资源目录
4. **细粒度权限**：REST 端点按角色/用户过滤

---

## 六、文件清单

| 文件 | 说明 |
|------|------|
| `vms-ui/` | Vue 3 前端工程（全新） |
| `vms-ui/tests/login.e2e.ps1` | E2E 测试（12 项） |
| `vms-web/.../VmsApiController.java` | 新增 REST controller + 鉴权 |
| `vms-web/.../application-sso.xml` | Shiro 放行 `/vms/api/**` |
| `pom.xml` | maven-compiler-plugin 2.3.2 → 3.11.0 |
