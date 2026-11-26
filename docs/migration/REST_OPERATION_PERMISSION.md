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

**旧 JSP 控制器的同类接口是绕过路径**（第一次提交后自查发现，已一并处理）：`/role/addRole`、`/role/editRole`、`/role/editElement`、`/role/deleteRole`、`/user/editUser`、`/company/*` 这些写接口的 URL 从未在 `menu-vms.md` 登记，旧 `SSOAccessControlFilter` 一直不拦。实测完整提权链：

| 步骤 | 结果 |
| --- | --- |
| 无角色账号 `POST /role/addRole` | 200，角色创建成功 |
| 无角色账号 `POST /user/editUser`（`roleIdStr=990001`，给自己指派系统管理员角色） | 200"操作成功"，库里成为系统管理员 |
| 该账号再调已加保护的 `POST /vms/api/sysRole/create` | 200——见下方「路径变体」 |

**旧过滤器的路径变体绕过**（第一次终审指出，已在基线实测）：`menu-vms.md` 登记过的旧 URL 由 `SSOAccessControlFilter` 保护，但它按字符串精确匹配查权限码。同一个无角色账号：

| 调用 | 结果 |
| --- | --- |
| `POST /order/refund`、`/user/resetPw`、`/settings/save`（精确路径） | 被旧过滤器拒绝 |
| `POST /order/refund.json`、`/order/refund/` | 进入业务逻辑 |
| `POST /user/resetPw/` | "重置密码成功"，目标账号密码被改成随机值 |
| `POST /settings/save/` | 进入业务逻辑 |
| `POST /customer/update`、`/partner/update`（从未登记） | 进入业务逻辑（可覆写转账/收款账户字段） |

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

### 标注的端点（47 个）
| 域 | 端点 | 权限码 |
| --- | --- | --- |
| 角色管理 | `/vms/api/sysRole` create / edit / bindElements / delete；旧 `/role` addRole / editRole / editElement / deleteRole | `role-list` |
| 用户管理 | `/vms/api/sysUser` create / {id}/update / delete / {id}/resetPwd；旧 `/user` editUser / deleteUser / resetPw | `user-add` / `user-edit` / `user-delete` / `user-resePw` |
| 系统配置 | `GET /vms/api/settings/{pid}/settings`、save、delete；旧 `/settings` type/add、setting/add、setting/update、setting/delete、save | `sysSetting-setting-table` / `sysSetting-save` / `sysSetting-setting-delete` / `sysSetting-type-add` / `sysSetting-setting-add` / `sysSetting-setting-update` |
| 公司管理 | `/vms/api/company` create / update / addCapacity / upgrade；旧 `/company` create / update / addCapacity / upgrade | `company_createPage` / `company_updatePage` / `company_addCapacityPage` / `company_upgrade` |
| 客户 / 合伙人 | 旧 `/customer` create / update / delete；旧 `/partner` create / update / delete | `customer-createPage` / `customer-updatePage` / `customer-delete`；`partner-createPage` / `partner-updatePage` / `partner-delete` |
| 收款账户 | `POST /vms/api/customerAccount/update`；旧 `/customerAccount/update` | `customerAccount-update` |
| 退款 | `POST /vms/api/order/refund`；旧 `/order/refund` | `order-refund` |
| 旧收款人配置 | `/mtBeneficiary` saveWxInfo / saveAliInfo / deletePayInfo | `customerAccount-update` |
| OpenAPI 密钥 | `/openApi` add / update / deleteOpenApiConfirm（**临时码，待拍板**） | `sysSetting-save` |

说明：
- 角色增删改在旧体系里没有登记独立按钮码，沿用菜单码 `role-list`。能管理角色与用户的人即可自我提权——这是旧 RBAC 的固有性质，未改动。
- 客户与合伙人的旧写接口是整实体绑定，会一并覆写转账/收款账户字段，按资金域处理。
- **为什么登记过的旧 URL 也必须标注**：旧 `SSOAccessControlFilter` 按 `ELEMENT_URL` **字符串精确匹配**查权限码。给路径加尾斜杠或 `.json` 后缀后，Spring 4.2 仍路由到同一个方法，而过滤器查不到权限码直接放行。基线实测无角色账号 `POST /user/resetPw/` 返回"重置密码成功"。拦截器按方法校验，对路径变体免疫。
- 旧收款人配置三个入口 Vue 已不引用，但路由仍可达（微信/支付宝收款信息与证书），一并标注。
- 账号自助接口（`/vms/api/account/updateUserName`、`updatePassword`、旧 `/account/*`）只操作当前登录人，不标注。
- 标注了却没写权限码（`@RequirePermission({})`）按配置错误处理，拒绝而不是放行。

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

自检预期：`elements=187 / role=1 / bound=187 / admin_bind=1 / admin_accounts=1 / level1_roles=1`，`admin_company` 为一级公司 ID。后两项不为预期值时先按下文处置，不要带病上线。

**上线前置检查（对接已有数据库时逐条做）**
1. `SELECT COUNT(*) FROM T_SYS_USER WHERE USER_ACCOUNT='admin';` 应为 1。多于 1 个时脚本只把 ID 最小的那个绑成管理员（不会中断），其余同名账号需自行处置。
2. `SELECT ROLE_ID, ROLE_NAME, COMPANY_ID FROM T_SYS_ROLE WHERE ROLE_LEVEL=1;` 应只有 `990001 系统管理员`。角色管理页取"可分配权限"时取查询结果的第一条且没有排序（`SysElementDaoImpl.getElementAllBySystemAdmin`）；库里若有旧的一级管理员角色，取到的是它——若它没绑权限项，权限树为空，谁都分配不了权限。处置：给旧角色补绑权限项，或把它降级/清理，再重跑种子。
3. `admin` 须归属**一级公司**。全新部署由 `database/vms_seed_bootstrap.sql`（部署包 `04b-bootstrap-company.sql`）创建一级公司「总公司（系统内置）」并把无公司的 admin 归属过去；界面上「新建公司」只能建三级公司，没有别的途径产生一级公司。该脚本对已有库是空操作。上线后在「公司管理」核对公司名称与售货机额度（默认 1000000 是不设上限的占位值，下级公司的额度都从总公司划拨）。
4. **受影响账号清单**：除 admin 外的存量账号执行种子后，对全部标注接口是 403。退款、收款账户这类日常操作必须在上生产前列出"岗位 → 角色"分配清单并完成分配，由大聪明确认。

**注意事项**
- **存量用户不会自动获得权限**：其他账号执行种子后对标注端点为 403，需在角色管理页按业务分配角色。这是有意的——授予谁能管理权限应由人决定。
- **旧 JSP 后台重新受权限控制**：`SSOAccessControlFilter` 按 `ELEMENT_URL` 精确匹配，此前因表空形同虚设。落种子后无权限账号访问旧 JSP 页面会被拒（符合旧系统设计）。Vue 前端对这些旧路径零调用，不受影响。
- **一个已知的不对称**（旧过滤器自身的问题，非本次引入）：旧过滤器的权限缓存只在访问旧 JSP 页面时加载，REST 登录会话里是空的。所以管理员用 Vue 登录后直接调**已登记旧 URL 的精确路径**会被旧过滤器拒绝，而走路径变体则由拦截器按数据库放行。这些旧接口 Vue 已不使用；彻底解决需在登录时加载权限缓存（第二批，同时也是前端按权限渲染的前提）。
- **新增权限项后**：重跑种子即自动补绑到管理员角色；只补尚未绑定的，界面改过绑定也不会产生重复行。
- 已进入部署包：`08-sys-permission.sql` 与 `04b-bootstrap-company.sql`（新库初始化按文件名顺序自动执行）。

**源数据修正**（`menu-vms.md` 2018 年导出自带缺陷，生成器显式处理并在种子头注释记录）：
- `st-vm-energy-delete` 整行重复，去重；
- `st-vm-theme-delete` 主键 `1040402` 与节能策略-修改撞号，改用 `1040502`（父级 `1040500` 下第 2 个按钮的编号位，原表空闲）。权限码未改。

## 四、验证（本地 8081，最终构建）

| 场景 | 结果 |
| --- | --- |
| 无角色账号 → 44 个标注端点 | **44/44 被拒**：32 个由拦截器返回 403 `{"code":403,"message":"没有操作权限"}`；12 个已登记旧 URL 的精确路径在到达拦截器前被旧过滤器拒绝 |
| 无角色账号 → 11 个路径变体（尾斜杠、`.json` 后缀、分号路径参数、双斜杠，新旧接口都有） | **11/11 → 403**（未映射到方法的为 404），无一进入业务逻辑。整改前 `/user/resetPw/` 返回"重置密码成功" |
| 管理员 → 44 个标注端点 | **32 个真正过闸**，返回正常业务响应；另 12 个已登记旧 URL 的精确路径被旧过滤器拒绝（REST 会话不带旧权限缓存，见上文"已知的不对称"），其路径变体 3/3 过闸 |
| 管理员 → 路径变体 | 3/3 过闸（`/order/refund.json`、`/user/resetPw/`、`/settings/save/`） |
| 匿名 → 44 个标注端点 | 17 个 `/vms/api` 端点 401；27 个旧路由 302（未登录由 Shiro 在更前面重定向） |
| 未标注端点 | 无角色账号 `/auth/me`、`/sysRole/list`、`/deliveryTask/list` 照常 200 |
| 权限撤销即时生效 | 同一登录会话内：无角色 403 → 数据库授予后 200 → 撤销后 403（无缓存） |
| 种子幂等 | 连续执行两次一致；模拟"界面改过绑定"（换成低位主键）后重跑两次，绑定行数仍 187、无重复 |
| 引导数据对已有库 | 空操作：未覆盖已有的 9001 号公司，未改动 admin 归属 |
| Vue 对已登记旧路径的调用 | 0 处（旧过滤器生效不影响前端功能） |
| 矩阵脚本合计 | 137 / 137 |
| 登录态回归 `regression-login.ps1` | 114 / 114 |
| 浏览器验收 `ui.acceptance.mjs` | 45 / 45 |

说明：管理员探测只用空参数或不存在的 ID，未触发写操作；"空注解即拒绝"的分支没有对应端点，仅编译验证。验证过程中创建的测试角色、角色绑定、测试账号均已清理；本地开发库保留种子数据（187 权限项 + 系统管理员角色 + admin 绑定）。

## 五、未覆盖（如实登记）

**本批次域内仍开放、需要拍板的**
- `/openApi/addOpenApiConfirm`、`updateOpenApiConfirm`、`deleteOpenApiConfirm`：OpenAPI 密钥的增删改。复审建议后已**临时收口**（标 `sysSetting-save`，纯防呆——Vue 零调用，且 Shiro 链对该路由本就重定向 REST 会话，实测三形态均被拒）。正式权限码待确认是否仍有外部系统凭 `T_OPEN_SECRET` 访问后定。
- `/company/exportOrUnStock`：公司域写操作，完全开放，影响低，权限码待确认。
- `/customerAccount/passSMS`：任意登录用户可对任意客户触发短信（网关目前已停用，实际无影响）。
- 旧 `/user/editUser` 同时承担新建与更新，只要求 `user-edit`：只持 `user-add` 的操作员新建用户会 403。放开成任一码即可则同时放开更新，需业务拍板。
- `role-list` 一码兼"看角色页、管角色、绑权限"，持有者可自我提权到全量权限。旧体系固有，建议为角色管理登记独立按钮码。

**其余域（第二批）**
- 其余约 200 个 REST 写端点仍是"登录即可"（工单、设备、策略、基础数据的增删改）。它们是业务数据操作，不构成提权路径，但低权限账号仍可越权修改业务数据。第二批按域逐批标注，权限码在 `menu-vms.md` 中已有。
- 旧 JSP 控制器其余域的写接口同样没有权限：全量审计显示 50 多个旧控制器里有数百个映射的 URL 从未登记。按风险需优先看：设备升级包相关（`/upgradePackage/*`、`/vmBoxUpgrade/*`，可向售货机下发升级包）、`/beneficiaryManage/*`、`/vendout/*`；其中面向设备的接口（`DealVmBoxRequest` 等）涉及设备鉴权，需单独分析，不能简单套用后台权限码。
- 读接口未做权限：如 `/vms/api/sysUser/list`、`/vms/api/settings/types`、旧 `/role/listPage`、`/user/listPage`，登录即可读。
- 前端不按权限渲染：`/auth/me` 的 `roles`/`permissions` 仍为空数组（T-1.1 共享授权加载未做），所有登录用户都能看到全部菜单，操作时才收到 403。需在登录时加载权限缓存、让 `/auth/me` 返回真实权限、前端据此渲染菜单与按钮——这一项同时解决上文"已知的不对称"。
- "非管理员不能被授予超出自身的权限"之类的防向上提权规则，旧体系没有，本次未新增。
- `updatePassword` 不校验旧密码（注释称由前端流程保证），不在本次范围。
