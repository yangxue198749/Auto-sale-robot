# VMS 迁移基线文档

**生成日期**: 2026-11-08  
**生成人**: Claude Code (Fable 5)  
**审核人**: 待 Codex 审核

---

## 1. 仓库结构

### 1.1 后端仓库 (vms/)

- **Git Root**: `D:/OPC/AI_Company/01_客户项目/Auto-sale-robot/vms`
- **最新 Commit**: `455688e9b313208de95889339d9f4bf047e6d00c`
- **Commit Message**: `feat: VMS 完整改造 - Oracle→MySQL、Redis 单点、本地存储、前后端分离`
- **当前 Dirty 文件数**: 2 (本地存储测试生成的临时文件)
- **GitHub**: https://github.com/yangxue198749/Auto-sale-robot (main 分支)

### 1.2 前端仓库 (vms-ui/)

- **Git Root**: `D:/OPC/AI_Company` (注意：vms-ui/ 不是独立仓库，在 AI_Company 大仓库内)
- **AI_Company 最新 Commit**: `36a838495fc096f980d425c59ba447f546461a73`
- **vms-ui/ Dirty 文件数**: 228 (新增的 Vue 工程未提交)
- **建议**: 将 vms-ui/ 初始化为独立 Git 仓库，便于版本追踪

---

## 2. 技术栈版本

### 2.1 后端

| 组件 | 版本 | 说明 |
|------|------|------|
| Java | 17.0.19 (OpenJDK) | LTS 版本 |
| Maven | 3.9.16 | 构建工具 |
| Spring Framework | 4.2.3.RELEASE | 老旧但稳定 |
| Apache Cayenne | 3.1.1 | ORM，已适配 MySQL |
| MySQL | 8.0.46 | 已从 Oracle 迁移 |
| Redis | 3.0.504 (Windows) | 单点模式，密码 redis123 |
| RabbitMQ | 3.12.13 | Erlang 14.2.5 |
| Apache Shiro | 1.2.2 | 认证授权 |
| Tomcat | 9.0.88 (embedded via cargo) | 端口 8080 |

### 2.2 前端

| 组件 | 版本 | 说明 |
|------|------|------|
| Node.js | 24.16.0 | 运行时 |
| npm | 11.13.0 | 包管理 |
| Vue | 3.5.45 | 前端框架 |
| Vue Router | 4.6.4 | 路由 |
| Pinia | 2.3.14 | 状态管理 |
| Vite | 6.4.1 | 构建工具，开发端口 5173 |
| TypeScript | 5.9.3 | 类型系统 |

---

## 3. 已完成改造

### 3.1 数据层迁移

- ✅ Oracle → MySQL 8.0
  - 85 张表通过 Cayenne DataMap 自动生成 DDL
  - 业务代码零改动（Cayenne 自动适配）
  - 主键表 AUTO_PK_SUPPORT 已创建

- ✅ Redis JedisCluster → JedisPool
  - MyJedisCluster / MyJedisUtils 改为单点连接池
  - 业务代码零改动

- ✅ 阿里云 OSS → 本地文件存储
  - LocalFileStorage 实现公共/私有 bucket
  - AliyunOssUtils 委托本地存储
  - 存储根目录: `D:/vms-data` (无中文路径)
  - Shiro 已放行 `/vmsimgs/**`

### 3.2 Web 容器

- ✅ Jetty 8 → Tomcat 9 (cargo 插件)
  - 支持 JDK 17
  - 端口 8080

### 3.3 前后端分离基础

- ✅ Vue 3 工程骨架 (vms-ui/)
  - 4 个页面: Login / Home / DeliveryTask / About
  - API 客户端: `/api` 代理到 8080，cookie 透传
  - E2E 测试: 12/12 通过

- ✅ REST 端点
  - `GET /vms/api/deliveryTask/listAll` (配送工单列表)
  - 显式鉴权: Shiro 放行 + controller `requireAuth()`
  - 401 返回 JSON，不破坏 REST 契约

### 3.4 业务闭环

- ✅ 配送工单服务端闭环
  - 创建/取消/状态流转/库存扣减/MCS 通知
  - 7 个集成测试全过

- ✅ MCS Mock
  - `tools/mcs_mock.py` (端口 8011)
  - 返回 `{"success": 0}`
  - 记录请求日志

### 3.5 迁移基线 (T-0.1 + T-0.2)

- ✅ 基线文档 `docs/migration/BASELINE.md`
  - 双仓库状态（vms/ + vms-ui/）
  - 技术栈版本清单
  - 已完成改造清单
  - 已知限制与待办事项

- ✅ 页面清单 `docs/migration/page-inventory.json`
  - 188 个菜单项（从 menu-vms.md 解析）
  - 351 个后端端点（从 63 个 Controller 扫描）
  - 165 个菜单项已映射到端点

- ✅ 权限矩阵 `docs/migration/permission-matrix.json`
  - 165 个权限点
  - 39 个高优先级（菜单页面）
  - 126 个中优先级（按钮操作）

- ✅ 基线检查脚本 `scripts/check-baseline.ps1`
  - 自动检查双仓库状态
  - 检查技术栈版本
  - 检查服务运行状态

### 3.5 迁移基线 (T-0.1 ~ T-1.3)

- ✅ T-0.1: 双仓库基线确认
  - 基线文档 `docs/migration/BASELINE.md`
  - 检查脚本 `scripts/check-baseline.ps1`

- ✅ T-0.2: 页面清单与权限矩阵
  - 188 个菜单项已解析 (menu-vms.md)
  - 351 个后端端点已扫描 (63 个 Controller)
  - 165 个权限点已映射
  - 输出: `docs/migration/page-inventory.json`, `permission-matrix.json`

- ✅ T-1.3: `/auth/me` 端点实现
  - 新增 `AuthApiController`
  - 返回用户信息: userId, userName, userAccount, company, roles, permissions
  - 未认证返回 401 JSON: `{"authenticated":false,"message":"User not authenticated"}`
  - 已认证返回 200 JSON: 完整用户信息
  - 测试通过: `tools/test-auth-api.ps1`

---

## 4. 已知限制

### 4.1 前端会话

- ❌ Vue 路由守卫用 Pinia 内存态，刷新页面丢失登录状态
  - Shiro session 仍有效，但前端不知道
  - **待解决**: 实现 `/vms/api/auth/me` 端点

### 4.2 登录安全

- ⚠️ 验证码日期绕过后门
  - `LoginController` 允许 `validCode = 当天日期` 绕过验证码
  - 生产环境需禁用

### 4.3 REST 鉴权

- ⚠️ 仅检查 `isAuthenticated()`，无细粒度权限
  - 所有登录用户可访问所有 REST 端点
  - **待解决**: 按角色/用户过滤数据

### 4.4 仓库结构

- ⚠️ vms-ui/ 不是独立 Git 仓库
  - 在 AI_Company 大仓库内，228 个未提交文件
  - **建议**: 初始化为独立仓库

---

## 5. 运行环境

### 5.1 本地开发

```
后端:
  Tomcat: http://127.0.0.1:8080
  MySQL: localhost:3306 (root / 123456, 数据库 vms)
  Redis: localhost:6379 (密码 redis123)
  RabbitMQ: localhost:5672 (vms_user / vms_dev_2026, vhost /)
  MCS Mock: http://127.0.0.1:8011

前端:
  Vite Dev: http://localhost:5173
  代理: /api → http://127.0.0.1:8080
```

### 5.2 启动命令

```bash
# 后端
cd vms
mvn -pl vms-web cargo:run

# 前端
cd vms-ui
npm run dev

# MCS Mock
cd vms/tools
python mcs_mock.py 8011
```

---

## 6. 测试覆盖

### 6.1 后端集成测试 (10/10 通过)

1. MysqlConnectionSmokeTest - Cayenne → MySQL 连接
2. SettingsSeedSmokeTest - 设置树加载
3. RedisConnectionSmokeTest - Redis 单点读写
4. RabbitMqSmokeTest - MQ 端到端收发
5. DeliveryTaskClosureTest - 工单状态机/幂等/重启恢复
6. StockDeductionClosureTest - 库存真实扣减
7. LocalFileStorageTest - 本地存储上传/读取/删除

### 6.2 前端 E2E 测试 (12/12 通过)

- Vue 首页 HTML
- 验证码接口
- 登录接口 (code=1)
- Shiro cookie 设置 (HttpOnly)
- 受保护端点访问 (200 with cookie)
- REST 鉴权 (401 without cookie)
- 无 cookie 拦截 (302)

---

## 7. 待迁移页面清单 (初步)

### 7.1 工单管理

- ✅ 配送工单 (已部分迁移，缺 CRUD)
- ⏳ 维护工单
- ⏳ 投放工单
- ⏳ 撤机工单
- ⏳ 工单查询

### 7.2 设备管理

- ⏳ 售货机管理
- ⏳ 货道设置
- ⏳ 闪购键设置
- ⏳ 走马灯设置
- ⏳ 节能设置
- ⏳ 价格设置
- ⏳ 皮肤设置

### 7.3 策略管理

- ⏳ 货道策略
- ⏳ 闪购键策略
- ⏳ 走马灯策略
- ⏳ 节能策略
- ⏳ 皮肤策略
- ⏳ 价格策略

### 7.4 基础数据

- ⏳ 区域管理
- ⏳ 合作伙伴
- ⏳ 客户管理
- ⏳ 商品类型
- ⏳ 商品管理

### 7.5 系统管理

- ⏳ 用户管理
- ⏳ 角色管理
- ⏳ 权限管理
- ⏳ 日志查询

**备注**: 详细页面清单和权限矩阵待 T-0.2 任务补充。

---

## 8. 下一步计划

按 Codex 建议，下一步执行:

1. **T-0.2**: 建立页面、动作与权限对照表
2. **T-1.3**: 实现 `/auth/me` 端点 (解决刷新丢状态)
3. **T-1.4**: Vue 会话初始化
4. **T-2.1~T-2.4**: 配送工单 REST CRUD (分页、权限范围)

---

## 9. 脚本

### 9.1 基线检查脚本

待创建: `scripts/check-baseline.ps1`

功能:
- 输出两个 Git 根目录
- 输出最新 commit SHA
- 输出 dirty 状态
- 输出 Java/Maven/Node/npm 版本
- 输出关键依赖版本
- 验证构建可复现

---

**文档版本**: v1.0  
**最后更新**: 2026-11-08
