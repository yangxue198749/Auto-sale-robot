# VMS 发布与部署

版本：v5.1.0（2026-11-09）
适用：本地/测试环境发布，生产发布须由大聪明确认后执行。

> **服务器部署（推荐）**：见 `deploy/server/DEPLOY_RUNBOOK.md` —— Docker Compose 形态
> （MySQL8/Redis7/RabbitMQ/Tomcat 四容器），目标腾讯云轻量 114.132.231.42，对外 8090。
> 组包命令：`deploy/server/build-deploy-package.ps1`。
> 环境差异通过 JVM 系统属性覆盖（PropertiesLoader 支持 -D 优先于 setting.properties）：
> 开发 = cargo.jvmargs（pom），服务器 = Tomcat setenv.sh / compose CATALINA_OPTS。

## 1. 产物构成

| 产物 | 路径 | 说明 |
| --- | --- | --- |
| 后端 war | `vms-web/target/vms-web.war` | Spring MVC + Cayenne + Shiro + JSP 旧后台 |
| 前端静态包 | `vms-ui/dist/` | Vue 3 + Element Plus，hash 路由 |
| 建库脚本 | `database/vms_schema.sql`、`vms_cayenne_pk.sql`、`vms_seed.sql` | MySQL 8 |

打包时若 `vms-ui/dist/index.html` 存在，`with-ui` profile 自动激活，把前端产物打进 war 的 `/dist/`。

## 2. 一键构建

```powershell
powershell -ExecutionPolicy Bypass -File tools/build-release.ps1
```

脚本等价于：

```bash
cd vms-ui && npm ci && npm run build
cd .. && mvn -DskipTests package
```

要求：JDK 17、Maven 3.8+、Node 20+。

## 3. 运行依赖

| 依赖 | 配置位置 | 本地默认 |
| --- | --- | --- |
| MySQL 8 | `vms-web/src/main/resources/setting.properties` → `jdbc.*` | localhost:3306/vms，root/123456 |
| Redis 5+ | 同上 → `redis.*` | localhost:6379，密码 redis123，需 keyspace 通知 `xE` |
| RabbitMQ | 同上 → `mq.*` | 127.0.0.1，vms_user/vms_dev_2026，vhost `/` |
| MCS 接口 | 数据库 `T_SYS_SETTINGS`（mcsUrl/mcsConnectUrl/mcsCallBackUrl） | 本地可用 `tools/mcs_mock.py`（8011 端口） |
| 本地文件存储 | 同上 → `local.storage.root` | `D:/vms-data`（public/private 两个 bucket） |

首次部署顺序：

```bash
mysql -uroot -p vms < database/vms_schema.sql
mysql -uroot -p vms < database/vms_cayenne_pk.sql
mysql -uroot -p vms < database/vms_seed.sql
mysql -uroot -p vms < database/test_data_scope.sql   # 可选：数据范围测试数据
```

## 4. 部署到 Tomcat 9

1. 把 `vms-web.war` 放入 `$CATALINA_HOME/webapps/`，重命名为 `ROOT.war`（若作为根应用）。
2. 设置 `JAVA_OPTS="-Dfile.encoding=UTF-8"`，启动 Tomcat。
3. 入口：
   - 新版前端：`http://<host>:8080/ui`（302 → `/dist/index.html`，hash 路由）
   - 旧版 JSP 后台：`http://<host>:8080/`
   - REST 接口：`http://<host>:8080/vms/api/**`
   - 图片：`http://<host>:8080/vmsimgs/**`

前端与后端同源部署，生产环境 API 直接请求 `/vms/api/**`（开发环境由 Vite `/api` 代理转发）。

## 5. 可选：Nginx 前置

```nginx
server {
    listen 80;
    server_name vms.example.com;
    root /opt/tomcat/webapps/ROOT;

    location /vms/api/ { proxy_pass http://127.0.0.1:8080/vms/api/; }
    location /vmsimgs/ { proxy_pass http://127.0.0.1:8080/vmsimgs/; }
    location /ui { return 302 /dist/index.html; }
    location /dist/ { try_files $uri =404; }
}
```

## 6. 发布检查单

- [ ] `mvn -DskipTests package` 成功，war 内含 `dist/index.html`
- [ ] 访问 `/ui` 返回 200 并跳转到 `#/login`
- [ ] 未登录访问 `/vms/api/mtVm/list` 返回 401
- [ ] 登录后可打开设备/策略各页面
- [ ] 图片可访问（`/vmsimgs/...`）
- [ ] 配送工单创建 → MCS mock 收到下发
- [ ] 日志：`logs/` 无启动异常，Shiro/Cayenne 连接正常

## 7. 回滚

1. 保留上一版本 war：`cp vms-web.war vms-web.war.bak-<日期>`。
2. 回滚时替换 war 并重启 Tomcat；数据库变更均为增量脚本，按需执行反向 SQL。
3. 前端独立回滚：替换 `/dist/` 目录即可，不影响后端。

## 8. 生产部署闸门

按 AGENTS.md 铁律 5：生产部署、真实数据导入、对外承诺、花钱必须由大聪明亲自确认。
Agent 只准备产物与脚本，不自行发布。
