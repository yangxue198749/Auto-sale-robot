# Phase 0 改造记录：Oracle→MySQL、Redis 集群→单点

**日期**：2026-11-07  
**目标**：让 VMS 在本地最小环境（MySQL + Redis + RabbitMQ）可运行，验证数据层兼容性

---

## 一、环境现状（本地）

| 组件 | 版本 | 连接 | 凭据 |
|------|------|------|------|
| MySQL | 8.0.46 | localhost:3306，库 `vms`，85 表 | root / 123456 |
| Redis | 3.0.504（Windows 移植版） | localhost:6379 | 密码 redis123，已开 keyspace 通知 `xE` |
| RabbitMQ | 3.12.13（Erlang 14.2.5） | localhost:5672，管理界面 15672 | vms_user / vms_dev_2026，vhost `/` |

---

## 二、关键发现（决定改造范围的事实）

### 2.1 数据层零 Oracle 方言依赖 ✅
- 全代码库**没有**真正的 Oracle 专有 SQL。
- 最初 grep 到的 `ROWNUM`（8 处）实际是 `ExportExcel.java` 的 Java 变量名（Excel 行计数器），不是 SQL。
- 最初 grep 到的 `DECODE(`（15 处）实际是 `URLDecoder.decode` 等 Java 方法，不是 Oracle DECODE 函数。
- ORM 层全部用 Cayenne 高层 API（`ObjectContext` / `SelectQuery` / `Expression`），Cayenne 按 JDBC 驱动自动识别数据库类型。
- **结论**：换数据库只需改驱动 + 连接串，Java 业务代码零改动。

### 2.2 Redis 原本是集群模式，本地是单点 ⚠️
- 旧代码 `MyJedisCluster` / `MyJedisUtils` 用 `JedisCluster`，启动时发 `CLUSTER` 命令。
- 本地 Redis 3.0.504（Windows 老版本）不支持集群模式，直接报错 `ERR This instance has cluster support disabled`。
- 所有 Redis 调用都收敛在两个包装类内（业务代码只调包装类方法），因此**只改包装类内部即可**。

### 2.3 Redis 连接参数存在数据库里，不在配置文件
- `MyJedisCluster.init()` 从 `T_SYS_SETTINGS` 表读 Redis 配置（`redis.ip` 等键），不是从 `setting.properties`。
- `RedisConfiguration.load()` 对空密码应用默认值 `redis123`（`StringUtil.getStringValue` 把空串当空值）。
- 设置是树形结构：`ROOT(ID=0) → redis/mcs/refund/upload → 具体键`，自关联 `ID → PID`。

### 2.4 MQ 不支持 vhost 配置
- 两个 `ConnectionConfig` 类都不读 vhost，`ConnectionFactory` 用默认 vhost `/`。
- 已给 `vms_user` 授予默认 vhost `/` 的全部权限。

---

## 三、实际改造内容

### 3.1 数据库：Oracle → MySQL

**文件**：
- `vms-model/pom.xml`：`ojdbc8` 换为 `mysql-connector-j:8.3.0`（Oracle 驱动注释保留以备回退）
- `setting.properties`（本地，已 gitignore）：
  - 驱动 `com.mysql.cj.jdbc.Driver`
  - URL `jdbc:mysql://localhost:3306/vms?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true`
- `database/vms_schema.sql`：从 `DataMap.map.xml` 自动生成的 85 张表 DDL（`tools/datamap_to_mysql.py`）
- `database/vms_seed.sql`：种子数据（系统用户、设置树、Redis/MCS/refund/upload 配置）

### 3.2 Redis：JedisCluster → JedisPool（业务代码零改动）

**文件**：
- `vms-service/.../redis/MyJedisCluster.java`：
  - 字段 `JedisCluster JC` → `JedisPool pool`
  - 新增 `buildPool()`（按密码有无选构造器）、`exec(RedisAction)`（统一借还连接）
  - 所有 public 方法签名不变，内部改走 `exec(jedis -> ...)`
  - `subscribe()` 改为从池借独立 Jedis 长连接做阻塞订阅
- `vms-commons/.../util/MyJedisUtils.java`：同样 `JedisCluster` → `JedisPool`，静态方法签名不变
- Redis 设密码 `redis123`（与代码默认值对齐）并 `CONFIG REWRITE` 持久化
- 开启 keyspace 通知 `Ex`（`MyJedisCluster.dealExpire` 订阅 `__keyevent@0__:expired` 必需）

### 3.3 冒烟测试（3 个，全部通过）

| 测试类 | 验证内容 |
|--------|----------|
| `MysqlConnectionSmokeTest` | Cayenne 经 MySQL 驱动连库并查询 |
| `SettingsSeedSmokeTest` | `T_SYS_SETTINGS` 设置树可被 `SysSettingsCache` 读取 |
| `RedisConnectionSmokeTest` | 两个 Redis 包装类在单点上 hash/list/expire 读写正常 |

---

## 四、当前可验证的能力边界

**已验证**：
- 应用数据层（Cayenne）→ MySQL 读写
- Redis 包装类 → 单点 Redis 读写、过期设置
- 配置树加载、系统用户种子
- 全模块编译 + WAR 打包通过

**尚未验证（第一期闭环要解决）**：
- Web 容器启动（Jetty/Tomcat）+ Spring 上下文完整加载
- RabbitMQ 实际收发（队列声明、消费、重试）
- 配送工单完整业务闭环（创建→分配→执行→库存变化）
- 设备通信协议兼容性
- OSS 替代（当前 `AliyunOss*` 读到的是空 AK，上传会失败，需本地文件实现）

---

## 五、待办与风险

1. **DDL 不完整**：`DataMap.map.xml` 只含表/列/主键，**缺外键约束、索引、触发器、初始化字典数据**。业务跑通过程中按需补，已在 seed 文件标注。
2. **Redis 版本老**：3.0.504 是 Windows 移植版，生产建议换 Linux 版 Redis 6/7；当前够开发用。
3. **OSS 已停用**：三个 `AliyunOss*` 类需要本地文件存储实现，闭环涉及图片/附件上传时处理。
4. **MCS 配置是占位**：`mcs.*` 指向 127.0.0.1 不存在的端口，涉及设备消息的功能需后续补 mock 或真实服务。

---

## 六、回滚方式

- pom 驱动：恢复 `vms-model/pom.xml` 中注释的 ojdbc8 依赖即可
- Redis 包装类：`git diff` 可见完整改动，`git checkout` 对应两个文件即可回退到 JedisCluster
- 数据库：`vms_schema.sql` / `vms_seed.sql` 幂等（`ON DUPLICATE KEY UPDATE`），可重复执行
