# VMS 本地启动指南（Tomcat 9 + MySQL + Redis + RabbitMQ）

**日期**：2026-11-07  
**适用**：Phase 0 已完成数据层改造后的本地开发环境

---

## 一、启动前置（已就绪）

| 组件 | 地址 | 凭据 |
|------|------|------|
| MySQL 8 | localhost:3306，库 `vms`（85 表 + 种子数据） | root / 123456 |
| Redis 3.0 | localhost:6379 | 密码 redis123，keyspace 通知 `xE` |
| RabbitMQ 3.12 | localhost:5672（管理界面 15672） | vms_user / vms_dev_2026，vhost `/` |
| 本地文件存储 | `D:/vms-data`（public/private 两目录） | 无需凭据，HTTP 前缀 `/vmsimgs/` |

> 文件存储根目录必须用无中文、无空格路径（如 D:/vms-data），否则 properties 读取时编码错乱。

RabbitMQ 需预先存在两个持久化 topic 交换机（消费者只声明队列、不声明交换机）：
- `open-api`
- `pay-service`

交换机已通过管理 API 创建；若环境重建，执行：

```bash
# Git Bash 下需 export MSYS_NO_PATHCONV=1，否则 %2F 会被路径转换
export MSYS_NO_PATHCONV=1
for ex in open-api pay-service; do
  curl -s -u vms_user:vms_dev_2026 -X PUT "http://localhost:15672/api/exchanges/%2F/$ex" \
    -H "content-type: application/json" \
    -d '{"type":"topic","durable":true,"auto_delete":false,"internal":false,"arguments":{}}'
done
```

---

## 二、启动命令

### 2.1 先启动 MCS Mock（库存闭环必需）

工单完成时会向 MCS 下发设备指令（货道策略/开门密码等）。本地用 Python mock 替代：

```bash
cd D:/OPC/AI_Company/01_客户项目/Auto-sale-robot/vms
python tools/mcs_mock.py 8011
# 看到 "MCS mock listening on 0.0.0.0:8011" 即可
# 请求日志写入 tools/mcs_mock.log
```

依赖：Python 3（仅标准库，无需 pip install）。`T_SYS_SETTINGS` 的 mcs 分组已指向 127.0.0.1:8011。

### 2.2 启动应用

```bash
cd D:/OPC/AI_Company/01_客户项目/Auto-sale-robot/vms

# 1. 构建并安装所有模块到本地仓库
mvn clean install -DskipTests

# 2. 启动嵌入式 Tomcat 9（cargo 插件，首次自动下载容器）
mvn -pl vms-web cargo:run
```

看到 `Tomcat 9.x Embedded started on port [8080]` 即启动成功。停止：Ctrl+C。

> 注意：必须用仓库根目录的 `mvn -pl vms-web cargo:run`，cargo 需要能解析兄弟模块依赖。

---

## 三、访问与登录

**必须用 127.0.0.1 访问，不要用 localhost**：

Shiro 会话 cookie（`TOUCHARM_VMS`）的 domain 取自 `setting.properties` 的 `login_url=127.0.0.1`。
用 localhost 访问时 cookie 域不匹配，登录后 session 不回传，会被反复 302 到登录页。

- 入口：http://127.0.0.1:8080/login （根路径 `/` 自动 302 到此）
- 账号：`admin`
- 密码：`1`（数据库存大写 MD5：`C4CA4238A0B923820DCC509A6F75849B`）
- 验证码：正常走 `/createCode` 图片；自动化测试可在 `/submitLogin` 传**当天日期（yyyy-MM-dd）** 作为 `validCode` 绕过（代码内置后门，见 `LoginController.submitLogin` 的 `DateUtils.getTime1()` 判断）

### 命令行登录验证（PowerShell，原生维护 session）

```powershell
$today = Get-Date -Format 'yyyy-MM-dd'
$session = New-Object Microsoft.PowerShell.Commands.WebRequestSession
Invoke-WebRequest -Uri 'http://127.0.0.1:8080/createCode' -WebSession $session -UseBasicParsing | Out-Null
$body = @{ userName='admin'; password='1'; validCode=$today }
Invoke-WebRequest -Uri 'http://127.0.0.1:8080/submitLogin' -Method POST -Body $body -WebSession $session -UseBasicParsing
# 期望：{"code":1,"href":"toloadMenu","message":"用户admin登录成功"}
Invoke-WebRequest -Uri 'http://127.0.0.1:8080/workbench/list' -WebSession $session -UseBasicParsing
# 期望：200，标题“工作台 | 售货机管理系统”
```

---

## 四、已验证的启动链路

| 环节 | 结果 |
|------|------|
| Spring 根上下文（spring-config.xml） | 加载成功，设置树 ROOT 节点读取正常 |
| Cayenne → MySQL | 连接、查询正常 |
| Redis 包装类（JedisPool 单点） | init 成功，过期事件订阅线程启动 |
| RabbitMQ 消费者 | 21 个队列自动声明并绑定，无通道错误 |
| Shiro 认证 | 登录、会话、授权拦截正常 |
| DispatcherServlet（spring-mvc.xml） | 初始化成功，JSP 视图渲染正常 |
| 工作台页面 | HTTP 200，业务文案正常 |

---

## 五、启动期已知的非致命问题

1. **两个死队列配置**：`queue.vmStatus.v1`、`queue.doorStatus.v1` 只在 properties 定义，Java 零引用，不会被声明——属遗留配置，非缺陷。
2. **MQ 交换机缺失会刷错误**：若 `open-api`/`pay-service` 不存在，消费者 `queueBind` 报 `NOT_FOUND - no exchange`，通道关闭并大量重试（但不阻止 Web 启动，因 InitFactory 每个消费者都 try/catch）。
3. **OSS 已停用**：`AliyunOss*` 读到空 AK，涉及文件上传的功能会失败，待本地文件存储实现。
4. **MCS 为占位配置**：指向 127.0.0.1 不存在的 8011 端口，设备消息相关功能待补 mock。
5. **首次登录强制改密**：新用户 `LOGIN_TIME` 为空会返回 code=2 要求改密；种子 admin 已预置 LOGIN_TIME 跳过此步。

---

## 六、关键架构事实（供后续改造参考）

- 认证是**传统 Shiro Session + EhCache**，cookie 名 `TOUCHARM_VMS`，不是 JWT——前后端分离时需专门设计 session/token 方案。
- 所有 MQ 消费者初始化都有异常保护，MQ 故障不拖垮 Web 容器。
- Redis 连接参数存数据库表 `T_SYS_SETTINGS`（树形 ID→PID），不在配置文件。
- 应用是 JSP 单体（336 个 JSP），DispatcherServlet 映射 `/`，视图解析走 InternalResourceViewResolver。
