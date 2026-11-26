# REST 操作权限校验（第一批：权限 / 账户 / 系统配置 / 资金）

日期：2026-11-26 ｜ 任务卡：TASK-1795900000001 ｜ 状态：待 Claude Code CLI 终审 + 大聪明确认（权限域）

## 一、问题（已在基线 18ea521 复现）

全部 42 个 ApiController、244 处鉴权调用只做 `requireAuth()`（已登录）。`/vms/api/**` 在 Shiro 链为 anon，旧 `SSOAccessControlFilter` 按 URL 查权限码的机制不覆盖 REST。

实测（无任何角色的账号 `lowpriv`，`/auth/me` 返回 `permissions:[]`）：

| 调用 | 基线结果 |
| --- | --- |
| `POST /vms/api/sysRole/create` | 200，角色创建成功（落库） |
| `POST /vms/api/sysRole/bindElements` | 200，"保存成功"（实际写入 2 条权限绑定） |
| `GET /vms/api/sysUser/list`、`/vms/api/settings/types` | 200 |

即任何能登录的账号都能建角色、绑权限，再配合用户管理接口即可自我提权到全权；重置他人密码、改系统配置、改收款账户、发起退款同理。

主计划 T-1.2「操作权限映射 + 漏配默认拒绝」原本就要求做，实际提交从 T-1.3 开始，被跳过。

## 二、实现

### 组成
| 文件 | 作用 |
| --- | --- |
| `vms-web/.../web/security/RequirePermission.java` | 方法级注解，值为权限码（任一满足） |
| `vms-web/.../web/security/ApiPermissionInterceptor.java` | 统一校验：只管标注方法；未登录 401、无权限 403，JSON 格式与各 ApiController 一致 |
| `spring-mvc.xml` | `<mvc:interceptors>` 注册拦截器 |
| `database/vms_seed_sys_permission.sql` | 权限种子（见下） |
| `tools/gen_sys_permission_seed.py` | 由 `menu-vms.md` 生成种子 |

### 设计要点
- **权限口径沿用旧系统**：用户 → `T_SYS_USER_ROLE` → `T_SYS_ROLE_ELEMENT` → `T_SYS_ELEMENT.ELEMENT_CODE`；权限码全部取自 `menu-vms.md` 既有登记，不发明新模型。
- **每次请求实查数据库、不缓存**：权限撤销即时生效。不走 Shiro 的 `isPermitted`——REST 登录路径不经 `/toloadMenu`，`SSOToken.authInfo` 为空。
- **fail-closed**：权限查询抛异常、用户或上下文缺失，一律 403。
- **只校验标注方法**：未标注端点行为完全不变，回归面可控。
- **审计**：放行记 INFO（谁、哪个接口、凭哪个权限码），拒绝记 WARN（含所需权限码，仅服务端日志；响应体不泄露）。

### 标注的端点（20 个）
| 域 | 端点 | 权限码 |
| --- | --- | --- |
| 角色管理 | `/vms/api/sysRole` create / edit / bindElements / delete | `role-list` |
| 用户管理 | `/vms/api/sysUser` create / {id}/update / delete / {id}/resetPwd | `user-add` / `user-edit` / `user-delete` / `user-resePw` |
| 系统配置 | `GET /vms/api/settings/{pid}/settings`、save、delete | `sysSetting-setting-table` / `sysSetting-save` / `sysSetting-setting-delete` |
| 公司管理 | `/vms/api/company` create / update / addCapacity / upgrade | `company_createPage` / `company_updatePage` / `company_addCapacityPage` / `company_upgrade` |
| 收款账户 | `POST /vms/api/customerAccount/update` | `customerAccount-update` |
| 退款 | `POST /vms/api/order/refund` | `order-refund` |
| 旧收款人配置 | `/mtBeneficiary` saveWxInfo / saveAliInfo / deletePayInfo | `customerAccount-update` |

说明：
- 角色增删改在旧体系里没有登记独立按钮码，沿用菜单码 `role-list`。能管理角色与用户的人即可自我提权——这是旧 RBAC 的固有性质，未改动。
- 旧收款人配置三个入口 Vue 已不引用，但路由仍可达（微信/支付宝收款信息与证书），一并标注。
- 账号自助接口（`/vms/api/account/updateUserName`、`updatePassword`）只操作当前登录人，不标注。

## 三、权限种子（部署前置条件，必须执行）

**不落种子则无人拥有任何权限，标注端点对所有人 403（包括 admin）。**

```
mysql -u<root> -p vms < database/vms_seed_sys_permission.sql
```

内容（全部 `INSERT IGNORE`，幂等，末尾带自检查询）：
1. `T_SYS_ELEMENT`：187 个权限项（`menu-vms.md` 去重后），`ELEMENT_MODULE=0`（VMS 端；移动端菜单不在范围）。
2. 系统管理员角色：`ROLE_ID=990001`，`ROLE_TYPE_SYSTEM` + `ROLE_LEVEL_ADMIN`——旧语义下的一级公司管理员角色，其权限项即角色管理页的"可分配权限全集"。
3. 该角色绑定全部权限项；**新增权限项后重跑脚本即自动补绑**。
4. 种子账号 `admin` 绑定该角色。

自检预期：`elements=187 / role=1 / bound=187 / admin_bind=1`。

**注意事项**
- **admin 须归属一级公司**：角色管理页的权限树按公司层级取管理员角色，admin 无公司归属时该页权限树不可用（权限校验本身不依赖公司）。全新部署需先建一级公司并给 admin 指定。
- **存量用户不会自动获得权限**：其他账号执行种子后对标注端点为 403，需在角色管理页按业务分配角色。这是有意的——授予谁能管理权限应由人决定。
- **旧 JSP 后台重新受权限控制**：`SSOAccessControlFilter` 按 `ELEMENT_URL` 精确匹配，此前因表空形同虚设。落种子后无权限账号访问旧 JSP 页面会被拒（符合旧系统设计）。
- 已进入部署包：`deploy/server/dist/sql/08-sys-permission.sql`（新库初始化自动执行）。

**源数据修正**（`menu-vms.md` 2018 年导出自带缺陷，生成器显式处理并在种子头注释记录）：
- `st-vm-energy-delete` 整行重复，去重；
- `st-vm-theme-delete` 主键 `1040402` 与节能策略-修改撞号，改用 `1040502`（父级 `1040500` 下第 2 个按钮的编号位，原表空闲）。权限码未改。

## 四、验证（本地 8081，修复后构建）

| 场景 | 结果 |
| --- | --- |
| 匿名访问 20 个标注端点 | 17 个 `/vms/api` 端点 401；3 个 `/mtBeneficiary` 旧路由 302（未登录由 Shiro 在更前面重定向，同为拒绝） |
| 无角色账号访问 20 个标注端点 | **20/20 → 403** `{"code":403,"message":"没有操作权限"}` |
| 管理员访问 20 个标注端点 | **20/20 过闸**，均返回正常业务响应；`sysRole/create` 真实创建成功（已清理） |
| 未标注端点 | 无角色账号 `/auth/me`、`/sysRole/list` 照常 200 |
| 审计日志 | ALLOWED / DENIED 行均落 `info.log`；权限查询失败 0 次 |
| 种子 | 连续执行两次结果一致（幂等）；中文名正常 |

回归结果见任务卡执行报告。

## 五、未覆盖（如实登记）

1. **其余约 200 个写端点仍是"登录即可"**（工单、设备、策略、基础数据的增删改）。它们是业务数据操作，不构成提权路径，但低权限账号仍可越权修改业务数据。第二批按域逐批标注，权限码在 `menu-vms.md` 中已有。
2. **读接口未做权限**：如 `/vms/api/sysUser/list`、`/vms/api/settings/types`，登录即可读。旧体系中部分读操作有独立权限码（如 `customer-list`）。
3. **前端不按权限渲染**：`/auth/me` 的 `roles`/`permissions` 仍为空数组（T-1.1 共享授权加载未做），所有登录用户都能看到全部菜单，操作时才收到 403。需让 `/auth/me` 返回真实权限 + 前端据此渲染菜单与按钮。
4. **非管理员不能被授予超出自身的权限**之类的防向上提权规则，旧体系没有，本次未新增。
5. `updatePassword` 不校验旧密码（注释称由前端流程保证），不在本次范围。
