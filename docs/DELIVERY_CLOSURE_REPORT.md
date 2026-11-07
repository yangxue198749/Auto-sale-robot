# 配送工单业务闭环验证报告（第一期里程碑）

**日期**：2026-11-07  
**闭环**：登录 → 创建配送工单（含货道明细）→ 业务校验 → 状态流转 → 取消/完成 → MQ 消息 → 重启恢复  
**结论**：✅ 闭环打通，5 个自动化测试 + HTTP 实测全部通过

---

## 一、业务链路与状态机

### 工单状态机（T_TASK_VM.TASK_VM_STATUS）
| 值 | 常量 | 含义 |
|----|------|------|
| 1 | STATUS_INITIALIZATION | 未开始 |
| 2 | STATUS_ONGOING | 进行中（创建配送工单即此态） |
| 4 | STATUS_FINISHED | 完成（设备/MCS 回调驱动） |
| 7 | STATUS_CLOSED | 忽略/关闭（取消） |

### 工单类型（TASK_TYPE）
配送=3（TYPE_DELIVERY）、维护=5、投放=7、撤机=8

### 数据依赖链
`公司(T_MT_COMPANY)` → `点位(T_MT_NODE)` + `机型(T_MT_VM_MODEL)` + `商品(T_MT_SKU)` → `售货机(T_MT_VM)` → `货道(T_MT_VM_CHANNEL)` → `工单(T_TASK_VM)` + `工单货道(T_TASK_VM_CHANNEL)`

---

## 二、过程中发现并解决的真实问题

### 2.1 Cayenne 主键生成表缺失（关键）
- **现象**：首次创建工单报 `Table 'vms.auto_pk_support' doesn't exist`
- **根因**：Cayenne 3.1 不用数据库自增，而用 `AUTO_PK_SUPPORT(TABLE_NAME, NEXT_ID)` 表分配主键；从 DataMap 重建的 DDL 只有业务表，缺这套机制
- **解决**：新增 `database/vms_cayenne_pk.sql`，建表并为全部 85 张业务表初始化主键计数（起始 10000，避开种子数据占用的 9000 段和 0~403 段）
- **机制**：每插入一行，Cayenne 执行 `UPDATE ... SET NEXT_ID=NEXT_ID+1` 取号

### 2.2 开门密码通知走 HTTP 而非 MQ（认知修正）
- 取消工单触发的 `mcsService.notifyOpenDoorPwd` 实际是 **HTTP POST 到 MCS 服务**，不是 RabbitMQ
- MCS 地址当前是占位（127.0.0.1:8011 不存在），故静默失败——属预期，非缺陷
- RabbitMQ 承载的是设备指令/出货结果等异步消息

### 2.3 旧 compiler 插件不支持 try-with-resources
- maven-compiler-plugin 2.3.2 即使 source=1.8 也无法编译 try-with-resources 语法
- 测试代码改用传统 try/finally（后续技术栈升级时统一处理）

---

## 三、验证证据

### 3.1 HTTP 端到端实测（真实容器 + 真实登录）
| 操作 | 接口 | 结果 |
|------|------|------|
| 登录 | POST /submitLogin | `{"code":1,"message":"用户admin登录成功"}` |
| 创建配送工单 | POST /deliveryTask/addTask | `{"type":"success","message":"新建成功"}` |
| 重复创建拦截 | 同上（同机二次） | `当前售货机存在未处理配送工单` |
| 缺负责人校验 | 同上（无 ownerId） | `请选择负责人` |
| 取消工单 | POST /deliveryTask/cancelTask | `取消成功`，状态 2→7 |
| 重复取消拦截 | 同上（二次） | `工单已取消，无法再次操作` |

落库验证：工单 10000，TYPE=3、STATUS=2→7，2 条货道明细（补货前库存快照 3/2、补货数 7/6、容量 10/8）均正确。

### 3.2 自动化测试（5 个，全过）
| 测试类 | 验证内容 |
|--------|----------|
| MysqlConnectionSmokeTest | Cayenne→MySQL 连接查询 |
| SettingsSeedSmokeTest | 设置树加载 |
| RedisConnectionSmokeTest | 两个 Redis 包装类单点读写 |
| RabbitMqSmokeTest | open-api 交换机端到端发布/消费、持久队列声明 |
| DeliveryTaskClosureTest | 工单创建→完成(4)→幂等重复→新上下文重启恢复→货道级联 |

`mvn test -Dtest=...`：Tests run: 5, Failures: 0, Errors: 0

---

## 四、闭环覆盖的异常场景

- **重复消息幂等**：对已完成工单再次提交完成，不新增数据、不报错（DeliveryTaskClosureTest 验证）
- **重启恢复**：提交后用全新 ObjectContext 读取，状态/完成时间/货道均持久化成功
- **业务规则拦截**：同机当日重复工单、缺负责人、重复取消均被正确拒绝
- **MQ 可靠性**：消息发布带 confirm、持久化投递模式（deliveryMode=2）

---

## 五、仍未覆盖 / 待办

1. **真实库存扣减未验证**：`finishDelivery` 不直接写库，而是调 MCS 通讯接口由设备侧保存；本地无 MCS/设备，库存→货道 CURR_NUM 的实际变更需 mock MCS 或设备联调
2. **设备通信协议兼容性**：仍需 VMBox/Android 客户端或实机联调（客户端未改造）
3. **OSS 文件上传**：待本地文件存储实现
4. **MCS 相关功能**：开门密码、货道库存下发等依赖 HTTP MCS 的功能，需 mock 服务
5. **其他工单类型**：维护/投放/撤机的闭环可复用本架构，尚未逐一验证

---

## 六、新增/变更文件

| 文件 | 用途 |
|------|------|
| `database/vms_cayenne_pk.sql` | Cayenne 主键支持表 + 85 表计数初始化 |
| `database/vms_seed_delivery_closure.sql` | 闭环业务种子数据（公司/点位/机型/商品/售货机/货道） |
| `vms-web/src/test/.../RabbitMqSmokeTest.java` | MQ 端到端测试 |
| `vms-web/src/test/.../DeliveryTaskClosureTest.java` | 工单状态机闭环测试 |
