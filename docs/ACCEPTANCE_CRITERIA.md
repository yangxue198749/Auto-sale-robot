# VMS 开发环境验收标准

**用途**：WorkBuddy 安装完成后的最终验收检查  
**验收人**：大聪明 / Claude Code  
**验收方式**：命令行测试 + 预期结果比对

---

## 一、验收测试命令（一键执行）

将以下命令复制到命令行执行，**所有测试必须通过**：

```bash
@echo off
echo ========================================
echo VMS 开发环境验收测试
echo ========================================
echo.

echo [1/8] MySQL 版本检查
mysql --version
if %errorlevel% neq 0 (
    echo [FAIL] MySQL 未安装或不在 PATH 中
) else (
    echo [PASS] MySQL 已安装
)
echo.

echo [2/8] MySQL 数据库和表检查
mysql -u root -p你的密码 -e "USE vms; SELECT COUNT(*) AS table_count FROM information_schema.tables WHERE table_schema = 'vms';" 2>nul | findstr "85"
if %errorlevel% neq 0 (
    echo [FAIL] vms 数据库不存在或表数量不是 85
) else (
    echo [PASS] vms 数据库已创建，85 个表已导入
)
echo.

echo [3/8] Redis 连接测试
redis-cli ping | findstr "PONG"
if %errorlevel% neq 0 (
    echo [FAIL] Redis 未运行或无法连接
) else (
    echo [PASS] Redis 正常运行
)
echo.

echo [4/8] Erlang 版本检查
erl -version 2>&1 | findstr "Erlang"
if %errorlevel% neq 0 (
    echo [FAIL] Erlang 未安装或环境变量未配置
) else (
    echo [PASS] Erlang 已安装
)
echo.

echo [5/8] RabbitMQ 服务状态检查
rabbitmqctl status 2>&1 | findstr "Status of node"
if %errorlevel% neq 0 (
    echo [FAIL] RabbitMQ 未运行
) else (
    echo [PASS] RabbitMQ 服务正常运行
)
echo.

echo [6/8] RabbitMQ 管理插件检查
rabbitmq-plugins list 2>&1 | findstr "rabbitmq_management.*\[E\*\]"
if %errorlevel% neq 0 (
    echo [FAIL] RabbitMQ 管理插件未启用
) else (
    echo [PASS] RabbitMQ 管理插件已启用
)
echo.

echo [7/8] 端口监听检查（3306, 6379, 5672, 15672）
netstat -an | findstr ":3306.*LISTENING"
if %errorlevel% neq 0 (
    echo [FAIL] MySQL 端口 3306 未监听
) else (
    echo [PASS] MySQL 端口 3306 正常
)

netstat -an | findstr ":6379.*LISTENING"
if %errorlevel% neq 0 (
    echo [FAIL] Redis 端口 6379 未监听
) else (
    echo [PASS] Redis 端口 6379 正常
)

netstat -an | findstr ":5672.*LISTENING"
if %errorlevel% neq 0 (
    echo [FAIL] RabbitMQ 端口 5672 未监听
) else (
    echo [PASS] RabbitMQ 端口 5672 正常
)

netstat -an | findstr ":15672.*LISTENING"
if %errorlevel% neq 0 (
    echo [FAIL] RabbitMQ 管理端口 15672 未监听
) else (
    echo [PASS] RabbitMQ 管理端口 15672 正常
)
echo.

echo [8/8] RabbitMQ 管理界面可访问性测试
curl -s -o nul -w "%%{http_code}" http://localhost:15672 | findstr "200"
if %errorlevel% neq 0 (
    echo [FAIL] RabbitMQ 管理界面无法访问
) else (
    echo [PASS] RabbitMQ 管理界面可访问
)
echo.

echo ========================================
echo 验收测试完成
echo ========================================
```

**注意**：将 `你的密码` 替换为实际的 MySQL root 密码。

---

## 二、逐项验收标准

### 2.1 MySQL 验收

**测试命令**：
```bash
mysql -u root -p -e "USE vms; SHOW TABLES;" | find /c "T_"
```

**预期结果**：
- 输出数字：**85**

**手动验证**：
```bash
mysql -u root -p
USE vms;
SHOW TABLES;
# 应该看到 85 个表，以 T_ 开头
```

**通过标准**：
- ✅ `vms` 数据库存在
- ✅ 表数量 = 85
- ✅ 关键表存在：`T_TASK_VM`, `T_MT_VM`, `T_MT_SKU`, `T_SYS_USER`

---

### 2.2 Redis 验收

**测试命令**：
```bash
redis-cli ping
```

**预期结果**：
- 输出：`PONG`

**手动验证**：
```bash
redis-cli
127.0.0.1:6379> SET test "hello"
OK
127.0.0.1:6379> GET test
"hello"
127.0.0.1:6379> DEL test
(integer) 1
```

**通过标准**：
- ✅ Redis 服务运行
- ✅ 可以读写数据
- ✅ 端口 6379 监听

---

### 2.3 Erlang 验收

**测试命令**：
```bash
erl -version
```

**预期结果**：
- 输出包含：`Erlang (SMP,ASYNC_THREADS) (BEAM) emulator version 14.x.x`

**手动验证**：
```bash
erl
1> halt().
# 应该能进入 Erlang shell 并退出
```

**通过标准**：
- ✅ `erl` 命令可执行
- ✅ 版本号显示正常
- ✅ 环境变量 `ERLANG_HOME` 已配置

---

### 2.4 RabbitMQ 验收

**测试命令 1 - 服务状态**：
```bash
rabbitmqctl status
```

**预期结果**：
- 输出包含：`Status of node rabbit@<hostname>`
- 无错误信息

**测试命令 2 - 管理插件**：
```bash
rabbitmq-plugins list | findstr "rabbitmq_management"
```

**预期结果**：
- 输出包含：`rabbitmq_management` 且标记为 `[E*]`（已启用）

**测试命令 3 - 管理界面**：
```bash
curl -I http://localhost:15672
```

**预期结果**：
- HTTP 状态码：**200** 或 **302**

**手动验证**：
1. 浏览器打开：http://localhost:15672
2. 用 `guest` / `guest` 登录
3. 应该看到 RabbitMQ 管理界面

**通过标准**：
- ✅ RabbitMQ 服务运行
- ✅ 管理插件已启用
- ✅ 管理界面可访问
- ✅ 可以用默认账号登录
- ✅ 端口 5672 和 15672 监听

---

## 三、连接信息验收

安装完成后，WorkBuddy 必须提供以下信息：

### 3.1 MySQL 连接信息

| 参数 | 值 | 验证命令 |
|------|-----|----------|
| Host | `localhost` | - |
| Port | `3306` | `netstat -an \| findstr :3306` |
| Database | `vms` | `mysql -u root -p -e "SHOW DATABASES;"` |
| Username | `root`（或 `vms_user`） | - |
| Password | 用户提供的密码 | 能登录即可 |

**验收**：
```bash
mysql -h localhost -P 3306 -u root -p密码 -e "USE vms; SELECT 1;"
# 应该返回 1
```

### 3.2 Redis 连接信息

| 参数 | 值 | 验证命令 |
|------|-----|----------|
| Host | `localhost` | - |
| Port | `6379` | `netstat -an \| findstr :6379` |
| Password | 无（默认） | - |

**验收**：
```bash
redis-cli -h localhost -p 6379 ping
# 应该返回 PONG
```

### 3.3 RabbitMQ 连接信息

| 参数 | 值 | 验证命令 |
|------|-----|----------|
| Host | `localhost` | - |
| AMQP Port | `5672` | `netstat -an \| findstr :5672` |
| Management Port | `15672` | `netstat -an \| findstr :15672` |
| Username | `guest`（或 `vms_user`） | - |
| Password | `guest`（或提供的密码） | - |
| Virtual Host | `/`（或 `vms`） | `rabbitmqctl list_vhosts` |

**验收**：
```bash
rabbitmqctl list_vhosts
# 应该包含 / 或 vms
```

---

## 四、验收报告模板

WorkBuddy 完成后填写：

```markdown
# VMS 开发环境验收报告

**安装日期**：2026-11-07
**安装人**：WorkBuddy

## 验收结果

| 序号 | 测试项 | 结果 | 备注 |
|------|--------|------|------|
| 1 | MySQL 版本 | ✅ PASS / ❌ FAIL | |
| 2 | MySQL 数据库（85 表） | ✅ PASS / ❌ FAIL | |
| 3 | Redis 连接 | ✅ PASS / ❌ FAIL | |
| 4 | Erlang 版本 | ✅ PASS / ❌ FAIL | |
| 5 | RabbitMQ 服务 | ✅ PASS / ❌ FAIL | |
| 6 | RabbitMQ 管理插件 | ✅ PASS / ❌ FAIL | |
| 7 | 端口监听（4 个） | ✅ PASS / ❌ FAIL | |
| 8 | RabbitMQ 管理界面 | ✅ PASS / ❌ FAIL | |

## 连接信息

### MySQL
- Host: 
- Port: 
- Database: `vms`
- Username: 
- Password: 

### Redis
- Host: 
- Port: 
- Password: 

### RabbitMQ
- Host: 
- AMQP Port: 
- Management Port: 
- Username: 
- Password: 
- Virtual Host: 

## 问题记录

（如有问题，记录在此）

## 验收结论

- [ ] 全部通过，可以进入下一步
- [ ] 部分通过，需要修复以下问题：

---

**验收人签字**：__________  
**日期**：__________
```

---

## 五、验收流程

1. **WorkBuddy 执行安装**（按 INSTALLATION_GUIDE.md）
2. **WorkBuddy 执行验收测试**（本文档第一节的一键测试脚本）
3. **WorkBuddy 填写验收报告**（本文档第四节模板）
4. **大聪明 / Claude Code 审核验收报告**
5. **验收通过** → 进入 Phase 0 下一步（配置应用、启动服务）
6. **验收不通过** → WorkBuddy 修复问题，重新验收

---

## 六、常见失败原因

| 测试项 | 常见失败原因 | 解决方法 |
|--------|--------------|----------|
| MySQL 表数量不对 | SQL 文件未完全执行 | 重新执行 `vms_schema.sql` |
| Redis 无法连接 | Redis 服务未启动 | `net start Redis` |
| Erlang 命令找不到 | 环境变量未配置 | 重新设置 `ERLANG_HOME` 和 `Path`，重启终端 |
| RabbitMQ 服务未运行 | 安装不完整 | 重新安装 RabbitMQ |
| RabbitMQ 管理界面无法访问 | 管理插件未启用 | `rabbitmq-plugins enable rabbitmq_management` |
| 端口未监听 | 服务未启动 | 检查服务状态，必要时重启 |

---

**文档版本**：v1.0  
**创建时间**：2026-11-07  
**配套文档**：INSTALLATION_GUIDE.md
