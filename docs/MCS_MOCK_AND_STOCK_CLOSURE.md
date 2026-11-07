# MCS Mock 服务与库存扣减闭环报告

**日期**：2026-11-07  
**目标**：补齐配送工单闭环的最后一环——库存实际扣减/补货落库  
**结论**：✅ 打通，货道 CURR_NUM 真实更新，MCS 设备指令通知链路验证通过

---

## 一、关键认知修正

### 1.1 库存写入其实是本地 DB 操作，不依赖 MCS 落库
- 补货完成入口 `McsServiceImpl.deliveryTaskFinish(DeliveryTaskJson)`
- 真正的库存写入在 `dealFillTaskList`：`mtVmChannel.setCurrNum(fill.getMarginCount())` 后由 Cayenne commit 到本地 MySQL
- MCS 的角色是**工单完成后向售货机下发指令**（货道策略、商品、价格、开门密码），不负责库存持久化
- 所以只要 MCS 接口返回标准成功响应，整条链路即可走通

### 1.2 MCS 配置键名是驼峰，不是点分（踩坑）
- `McsConfiguration` 期望的键：`mcsUrl` / `mcsConnectUrl` / `mcsCallBackUrl`（驼峰无点）
- 最初按 Redis 风格写成 `mcs.url`（带点），导致 `map.get("mcsUrl")` 取到 null，回退默认内网地址 `192.168.101.231`，报 Connection reset
- **教训**：配置键名必须与 Java 常量逐字对齐，不能想当然套用其他模块的命名风格

### 1.3 MCS 成功响应格式
- `MessageResult.success`：**0 代表成功**，非 0 为失败
- mock 对所有 POST 返回 `{"success":0,"sn":"mock","from":"mcs-mock"}`

---

## 二、MCS Mock 实现

**文件**：`tools/mcs_mock.py`（Python 标准库，零第三方依赖）

- 监听 8011，对所有 POST 返回 `{"success":0}`
- 记录每次请求的路径和 `msgType` 到 `tools/mcs_mock.log`
- GET 提供健康检查
- 启动：`python tools/mcs_mock.py 8011`

### 工单完成时实际下发的设备指令（mock 日志实测）
| msgType | 含义 |
|---------|------|
| supplyPolicy | 货道库存/补货策略下发 |
| skuOrderPolicy | 商品排序策略下发 |
| openDoorPwd | 开门密码通知 |

---

## 三、验证证据

### StockDeductionClosureTest（Spring 完整上下文）
通过 `ClassPathXmlApplicationContext("spring-config.xml")` 加载完整装配的 `IMcsService`，避免手动注入 6+ 个 DAO：

```
[STOCK] before: A01=3, A02=2
[STOCK] deliveryTaskFinish error=''
[STOCK] after: A01=10, A02=8
[STOCK] OK: stock deduction persisted, task finished, MCS notified
```

- 货道 A01 库存 3→10、A02 库存 2→8，真实落库
- 工单状态 进行中(2)→完成(4)，完成时间写入
- MCS mock 收到 supplyPolicy / skuOrderPolicy / openDoorPwd 三类指令
- 测试自动恢复库存、清理工单，可重复执行

### 全部 6 个集成测试
`Tests run: 6, Failures: 0, Errors: 0`
1. MysqlConnectionSmokeTest — Cayenne→MySQL
2. SettingsSeedSmokeTest — 设置树
3. RedisConnectionSmokeTest — Redis 单点包装类
4. RabbitMqSmokeTest — MQ 端到端收发
5. DeliveryTaskClosureTest — 工单状态机/幂等/重启恢复
6. StockDeductionClosureTest — 库存真实扣减 + MCS 通知

---

## 四、配置变更

`T_SYS_SETTINGS` mcs 分组（PID=200）：
| KEY | VALUE |
|-----|-------|
| mcsUrl | http://127.0.0.1:8011/mcs/enter |
| mcsConnectUrl | http://127.0.0.1:8011/mcs/connect |
| mcsCallBackUrl | http://127.0.0.1:18081/api/pushMessage |

种子 SQL `vms_seed.sql` 已同步修正键名。

---

## 五、配送工单闭环完整度

| 环节 | 状态 |
|------|------|
| 登录授权 | ✅ |
| 创建工单（含货道明细） | ✅ |
| 业务校验（重复/缺负责人/重复取消） | ✅ |
| 状态机（进行中→完成/关闭） | ✅ |
| 库存真实落库 | ✅（本次打通） |
| MQ 消息收发 | ✅ |
| MCS 设备指令通知 | ✅（mock） |
| 幂等/重启恢复 | ✅ |
| 真实设备协议兼容 | ⏳ 仍需 VMBox/Android 或实机联调 |

**除真实设备联调外，配送工单的服务端业务闭环已 100% 打通。**

---

## 六、待办

1. 真实设备/客户端联调（mock 只验证了消息格式契约，未验证物理设备行为）
2. OSS 文件上传的本地实现（图片/附件仍不可用）
3. 维护/投放/撤机三类工单闭环（架构可复用）
4. mock 目前对所有消息统一返回成功，若后续要测失败重试，需扩展按 msgType 注入故障
