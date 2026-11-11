# VMS 服务器部署 Runbook（腾讯云轻量 114.132.231.42）

适用：Docker Compose 形态，与既有 ai-company-mcp 容器共存，VMS 对外端口 **8090**。
前置：已在本地执行 `deploy/server/build-deploy-package.ps1` 组装好 `dist/`。

## 0. 服务器上已有、不要动的东西

- 容器 `ai-company-mcp-v2`（任务总线）与 composition-nginx（bus.xzqgh.cn）
- 端口 80/443 被总线占用；VMS 用 **8090**，互不影响

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
