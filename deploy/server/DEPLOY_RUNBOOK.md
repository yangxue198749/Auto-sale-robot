# VMS 服务器部署 Runbook（腾讯云轻量 114.132.231.42）

适用：Docker Compose 形态，与既有 ai-company-mcp 容器共存，VMS 对外端口 **8090**。
前置：已在本地执行 `deploy/server/build-deploy-package.ps1` 组装好 `dist/`。

## 0. 服务器上已有、不要动的东西

- 容器 `ai-company-mcp-v2`（任务总线）与 composition-nginx（bus.xzqgh.cn）
- 端口 80/443 被总线占用；**宿主机 8080 为美业占用**
- VMS 只对外发布 **8090**（compose 映射 8090:容器内部端口，容器网络与宿主机隔离，
  内部端口不与宿主机任何服务冲突），互不影响

## 1. 上传部署包

本地（Git Bash）：

```bash
cd vms/deploy/server/dist
tar czf vms-deploy.tar.gz .
scp vms-deploy.tar.gz root@114.132.231.42:/opt/vms/
```

服务器：

```bash
mkdir -p /opt/vms && cd /opt/vms && tar xzf vms-deploy.tar.gz
```

## 2. 修改两处配置（必须）

```bash
cd /opt/vms
cp .env.example .env && vi .env          # 改三个密码
vi sql/07-server-overrides.sql           # 改 redis.passWord（= .env 的 REDIS_PASSWORD）
                                         # 改 mcsCallBackUrl 的 IP 为 114.132.231.42
```

## 3. 启动

```bash
docker compose up -d
docker compose ps                 # 四个容器 Up / healthy
docker compose logs vms-app | grep -i "Server startup"   # 出现即启动完成
```

首次启动 MySQL 自动执行 `sql/` 下全部脚本建库（仅首次；重启不重复执行）。

**权限种子（上线前置，详见 `docs/migration/REST_OPERATION_PERMISSION.md`）**：`sql/08-sys-permission.sql`
写入权限项、系统管理员角色，并把种子账号 `admin` 绑定到该角色。REST 接口与旧 JSP 接口的角色/用户/公司/
系统配置/客户与合伙人/收款账户/退款等管理操作按权限校验——**没有这份数据，admin 也会被拒（403）**。
全新部署由上面的初始化自动执行（`04b` 先建一级公司并把 admin 归属过去，`08` 再建权限）；
**对接已有数据库时必须手工执行一次**，并先做下面的检查：

```bash
# 0. 前置检查（只读）
docker compose exec -T mysql mysql -u root -p"$MYSQL_ROOT_PASSWORD" vms -e "
SELECT COUNT(*) admin_accounts FROM T_SYS_USER WHERE USER_ACCOUNT='admin';        -- 须为 1
SELECT ROLE_ID, ROLE_NAME, COMPANY_ID FROM T_SYS_ROLE WHERE ROLE_LEVEL=1;          -- 须只有 990001 系统管理员（执行后）
SELECT u.USER_ACCOUNT, u.COMPANY_ID, c.COMPANY_LEVEL FROM T_SYS_USER u LEFT JOIN T_MT_COMPANY c ON c.COMPANY_ID=u.COMPANY_ID WHERE u.USER_ACCOUNT='admin';  -- COMPANY_LEVEL 须为 1
SELECT USER_ACCOUNT, USER_NAME, COMPANY_ID FROM T_SYS_USER WHERE DELETE_MARK=0 AND USER_ACCOUNT<>'admin';  -- 存量账号清单：逐个确定角色
SELECT USER_ACCOUNT, COUNT(*) n FROM T_SYS_USER WHERE DELETE_MARK=0 GROUP BY USER_ACCOUNT HAVING n>1;      -- 同名账号须先处置"
# 1. admin 若没有公司且库里没有一级公司：先建一级公司（名称、额度上线后在「公司管理」核对）
docker compose exec -T mysql mysql -u root -p"$MYSQL_ROOT_PASSWORD" vms < sql/04b-bootstrap-company.sql
# 2. 权限种子（幂等，可重复）
docker compose exec -T mysql mysql -u root -p"$MYSQL_ROOT_PASSWORD" vms < sql/08-sys-permission.sql
#    自检应为 elements=187 / role=1 / bound=187 / admin_bind=1 / admin_accounts=1 / level1_roles=1
# 3. 切流量之前：用「存量账号清单」在「角色管理」给每个岗位分配角色并实测。
#    退款、收款账户是日常操作，漏分配会直接变成 403。分配清单须经大聪明确认。
```

菜单或权限项变更后重跑 `08`，新权限项自动补绑到管理员角色（不会产生重复绑定）。

## 4. 防火墙开放 8090

- 腾讯云控制台 → 轻量应用服务器 → 防火墙 → 添加规则：TCP 8090
- 或 `ufw allow 8090/tcp`（若启用 ufw）

## 5. 验证清单

```bash
# 服务器上
curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:8090/ui          # 302
curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:8090/dist/index.html  # 200
curl -s -o /dev/null -w "%{http_code}\n" http://127.0.0.1:8090/vms/api/mtVm/list # 401（未登录）
```

本地浏览器：`http://114.132.231.42:8090/ui` → admin 登录（验证码图片）→ 逐页点查。

## 6. 首次登录后必做

- [ ] 「账号管理」修改 admin 密码
- [ ] 确认 admin 已归属**一级公司**（全新部署为「总公司（系统内置）」，请在「公司管理」核对名称与售货机额度；对接已有库时见上文前置检查第 3 条）
- [ ] 实测越权拦截：建一个不分配任何角色的测试账号登录，
      `POST /vms/api/sysRole/create`、`POST /role/addRole`、`POST /user/editUser` 均应提示「没有操作权限」，
      再试带尾斜杠的 `POST /user/resetPw/`、带后缀的 `POST /order/refund.json` 同样应被拒；测完删除该账号
- [ ] 「系统管理 → 系统设置」检查 redis 配置（应已指向 vms-redis）
- [ ] 删除 `webapps/examples`、`docs`、`host-manager`、`manager`（compose 挂载单个 war，本形态默认不存在，跳过）

## 7. 日常运维

```bash
docker compose logs -f vms-app --tail 200   # 看应用日志
docker compose restart vms-app              # 重启应用
docker compose down && docker compose up -d # 全套重启（数据卷保留）
```

## 8. 回滚

```bash
# 保留上一版 war 再上传新版；回滚 = 恢复旧 ROOT.war + docker compose restart vms-app
docker compose down
cp ROOT.war.bak-YYYYMMDD ROOT.war
docker compose up -d
```

## 9. 已知约束（试运行期）

- 真实售货机未接入前，MCS 地址保持 mock；设备接入时改 T_SYS_SETTINGS 的 mcsUrl/mcsConnectUrl 并把回调指向公网 IP
- 短信网关已停用：手机号修改/短信验证类功能不可用（页面已标注）
- B客户转账页为旧系统未完成功能，未迁移
