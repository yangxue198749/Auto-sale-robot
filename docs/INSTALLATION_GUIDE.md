# VMS 开发环境安装指南

**目标**：搭建 VMS 系统的最小运行环境，用于 Phase 0 业务验证  
**执行人**：WorkBuddy  
**验收人**：大聪明 / Claude Code

---

## 一、安装清单

| 序号 | 软件 | 版本要求 | 安装目录 | 用途 |
|------|------|----------|----------|------|
| 1 | MySQL | 8.0+ | 默认（已有） | 业务数据库 |
| 2 | Redis | 6.0+ | 默认（已有） | 缓存 + 业务逻辑 |
| 3 | Erlang OTP | 26.x | `C:\Program Files\Erlang OTP` | RabbitMQ 运行时 |
| 4 | RabbitMQ Server | 3.12+ | `C:\Program Files\RabbitMQ Server` | 消息队列 |

---

## 二、详细安装步骤

### 2.1 MySQL（已有，只需建库建表）

**验证现有 MySQL**：
```bash
mysql --version
# 应该显示 mysql  Ver 8.x.x 或更高
```

**建库建表**：
```bash
# 登录 MySQL
mysql -u root -p

# 在 MySQL 命令行中执行：
source D:/OPC/AI_Company/01_客户项目/Auto-sale-robot/vms/database/vms_schema.sql

# 验证
USE vms;
SHOW TABLES;
# 应该看到 85 个表

# 创建测试用户（可选，生产环境建议）
CREATE USER 'vms_user'@'localhost' IDENTIFIED BY 'vms_password_2026';
GRANT ALL PRIVILEGES ON vms.* TO 'vms_user'@'localhost';
FLUSH PRIVILEGES;
```

**验收标准**：
- ✅ MySQL 服务正常运行
- ✅ `vms` 数据库已创建
- ✅ 85 个表已创建
- ✅ 可用 `SHOW TABLES;` 验证

---

### 2.2 Redis（已有，只需验证）

**验证现有 Redis**：
```bash
redis-cli ping
# 应该返回 PONG
```

**检查 Redis 配置**：
```bash
redis-cli CONFIG GET port
# 默认是 6379

redis-cli CONFIG GET bind
# 应该是 127.0.0.1（本地访问）
```

**验收标准**：
- ✅ Redis 服务正常运行
- ✅ 可以用 `redis-cli` 连接
- ✅ 端口 6379 可用

---

### 2.3 Erlang OTP（新装）

**下载安装**：
1. 访问：https://www.erlang.org/downloads
2. 下载：**Windows 64-bit Binary File**
   - 文件名：`otp_win64_26.2.x.exe`（选最新版本）
3. 运行安装程序
4. 安装目录：`C:\Program Files\Erlang OTP`（默认）
5. 安装选项：全部默认，勾选"Create Start Menu shortcuts"

**配置环境变量**：
1. 右键"此电脑" → 属性 → 高级系统设置 → 环境变量
2. 新建系统变量：
   - 变量名：`ERLANG_HOME`
   - 变量值：`C:\Program Files\Erlang OTP`
3. 编辑系统变量 `Path`，添加：
   - `%ERLANG_HOME%\bin`
4. 点击"确定"保存

**验证安装**：
```bash
# 打开新的命令行窗口（必须新开，才能加载环境变量）
erl -version
# 应该显示：Erlang (SMP,ASYNC_THREADS) (BEAM) emulator version 14.x.x

# 或者
erl +V
# 应该显示版本号
```

**验收标准**：
- ✅ Erlang 安装成功
- ✅ `ERLANG_HOME` 环境变量已设置
- ✅ `erl -version` 命令可执行

---

### 2.4 RabbitMQ Server（新装）

**下载安装**：
1. 访问：https://www.rabbitmq.com/download.html
2. 下载：**RabbitMQ for Windows**
   - 文件名：`rabbitmq-server-3.12.x.exe`（选最新版本）
3. 运行安装程序
4. 安装目录：`C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.x`（默认）
5. 安装选项：全部默认

**验证服务启动**：
```bash
# 检查 RabbitMQ 服务状态
rabbitmqctl status

# 应该显示节点信息、运行状态等
```

**启用管理插件**：
```bash
# 启用 Web 管理界面
rabbitmq-plugins enable rabbitmq_management

# 应该显示：
# Enabling plugins on node rabbit@<hostname>:
# rabbitmq_management
```

**验证管理界面**：
1. 浏览器打开：http://localhost:15672
2. 默认账号：`guest`
3. 默认密码：`guest`
4. 应该能看到 RabbitMQ 管理界面

**创建 VMS 专用用户（可选，推荐）**：
```bash
# 创建用户
rabbitmqctl add_user vms_user vms_password_2026

# 设置权限（vms 虚拟主机）
rabbitmqctl add_vhost vms
rabbitmqctl set_permissions -p vms vms_user ".*" ".*" ".*"

# 设置管理员标签（可选）
rabbitmqctl set_user_tags vms_user administrator
```

**验收标准**：
- ✅ RabbitMQ 服务正常运行
- ✅ `rabbitmqctl status` 命令可执行
- ✅ 管理插件已启用
- ✅ 浏览器可访问 http://localhost:15672
- ✅ 可以用 `guest/guest` 登录

---

## 三、整体验收测试

### 3.1 服务状态检查

```bash
# 检查所有服务
echo "=== MySQL ===" && mysql --version
echo "=== Redis ===" && redis-cli ping
echo "=== Erlang ===" && erl -version
echo "=== RabbitMQ ===" && rabbitmqctl status | head -5
```

**期望输出**：
```
=== MySQL ===
mysql  Ver 8.x.x for Win64 on x86_64 (MySQL Community Server)
=== Redis ===
PONG
=== Erlang ===
Erlang (SMP,ASYNC_THREADS) (BEAM) emulator version 14.x.x
=== RabbitMQ ===
Status of node rabbit@<hostname> ...
Runtime
...
```

### 3.2 数据库连接测试

```bash
# MySQL
mysql -u root -p -e "USE vms; SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = 'vms';"
# 应该返回 85

# Redis
redis-cli SET test_key "hello" && redis-cli GET test_key
# 应该返回 "hello"

# RabbitMQ（检查虚拟主机）
rabbitmqctl list_vhosts
# 应该包含 / 和 vms（如果创建了）
```

### 3.3 端口检查

```bash
# 检查所有必需端口
netstat -an | findstr ":3306 :6379 :5672 :15672"
```

**期望输出**：
```
TCP    0.0.0.0:3306    0.0.0.0:0    LISTENING  (MySQL)
TCP    0.0.0.0:6379    0.0.0.0:0    LISTENING  (Redis)
TCP    0.0.0.0:5672    0.0.0.0:0    LISTENING  (RabbitMQ AMQP)
TCP    0.0.0.0:15672   0.0.0.0:0    LISTENING  (RabbitMQ Management)
```

---

## 四、安装完成后需要提供的信息

安装完成后，请提供以下信息给 Claude Code：

1. **MySQL 连接信息**：
   - Host: `localhost`
   - Port: `3306`（默认）
   - Database: `vms`
   - Username: `root`（或创建的 `vms_user`）
   - Password: `你的root密码`（或 `vms_password_2026`）

2. **Redis 连接信息**：
   - Host: `localhost`
   - Port: `6379`（默认）
   - Password: 无（默认）

3. **RabbitMQ 连接信息**：
   - Host: `localhost`
   - Port: `5672`（AMQP 协议）
   - Management Port: `15672`
   - Username: `guest`（或创建的 `vms_user`）
   - Password: `guest`（或 `vms_password_2026`）
   - Virtual Host: `/`（或 `vms`）

---

## 五、常见问题

### Q1: Erlang 安装后 `erl` 命令找不到
**解决**：
- 确认环境变量 `ERLANG_HOME` 和 `Path` 已正确设置
- 重新打开命令行窗口（环境变量需要重启终端才能生效）
- 检查路径：`C:\Program Files\Erlang OTP\bin\erl.exe` 是否存在

### Q2: RabbitMQ 服务无法启动
**解决**：
```bash
# 查看错误日志
rabbitmqctl status

# 重启服务
net stop RabbitMQ && net start RabbitMQ
```

### Q3: 无法访问 RabbitMQ 管理界面
**解决**：
```bash
# 确认插件已启用
rabbitmq-plugins list | grep management

# 如果未启用，重新启用
rabbitmq-plugins enable rabbitmq_management

# 重启服务
net stop RabbitMQ && net start RabbitMQ
```

### Q4: MySQL 执行 SQL 文件报错
**解决**：
- 确认文件路径使用正斜杠 `/` 或双反斜杠 `\\`
- 确认 MySQL 用户有 CREATE DATABASE 权限
- 检查 SQL 文件编码（应该是 UTF-8）

---

## 六、验收检查清单

安装完成后，逐项确认：

- [ ] MySQL 8.0+ 已安装并运行
- [ ] `vms` 数据库已创建，85 个表已导入
- [ ] Redis 6.0+ 已安装并运行
- [ ] Erlang OTP 26.x 已安装，`erl -version` 可执行
- [ ] RabbitMQ 3.12+ 已安装并运行
- [ ] RabbitMQ 管理插件已启用
- [ ] 浏览器可访问 http://localhost:15672
- [ ] 所有必需端口（3306, 6379, 5672, 15672）可监听
- [ ] 已提供 MySQL/Redis/RabbitMQ 连接信息

**全部打勾后，通知 Claude Code 继续下一步。**

---

## 七、安装目录总结

| 软件 | 安装目录 | 数据目录 |
|------|----------|----------|
| MySQL | 默认（已有） | 默认（已有） |
| Redis | 默认（已有） | 默认（已有） |
| Erlang OTP | `C:\Program Files\Erlang OTP` | - |
| RabbitMQ | `C:\Program Files\RabbitMQ Server\rabbitmq_server-3.12.x` | `%APPDATA%\RabbitMQ` |

---

**文档版本**：v1.0  
**创建时间**：2026-11-07  
**适用项目**：VMS 系统改造 Phase 0
