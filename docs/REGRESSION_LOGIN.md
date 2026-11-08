# 登录态全量回归报告

日期：2026-11-08
环境：本地 Tomcat 9.0.88（cargo）+ MySQL 8 + 单点 Redis + RabbitMQ + MCS mock
执行人：Claude Code（大聪明授权临时开启验证码旁路）
脚本：`tools/regression-login.ps1`

## 1. 方法

1. 临时将 `login.captcha.date.bypass.enabled` 置为 `true`（仅本机开发实例），重新打包部署。
2. 执行回归脚本，覆盖：
   - 匿名访问 36 个 REST 端点必须 401；
   - `admin` 登录（日期验证码）与 `TOUCHARM_VMS` 会话 Cookie；
   - 登录态下 36 个列表端点 200；
   - 设备级端点（货道/节能/闪购键/走马灯/皮肤入口/设备详情）、型号货道；
   - 各模块详情端点；
   - 写闭环：走马灯策略增改删、价格策略增删、整机促销策略增删、商品促销策略增删、
     货道策略（按型号模板创建→详情→删除）、节能策略（创建→新增明细→详情→删除）、
     促销应用（整机策略应用到设备→详情→取消）；
   - 匿名 POST 必须 401。
3. 回归完成后立即恢复 `login.captcha.date.bypass.enabled=false` 并重新部署，验证安全行为恢复。

## 2. 结果

**103 项断言全部通过（103 passed, 0 failed）。**

安全恢复验证：
- 日期验证码登录返回 `{"code":4,"message":"您输入的验证码有误..."}`；
- `/vms/api/mtVm/list` 无 Cookie 返回 401；
- `/ui` 仍正常 302 到 `/dist/index.html`。

## 3. 回归发现并修复的缺陷

| # | 位置 | 现象 | 修复 |
| --- | --- | --- | --- |
| 1 | `TaskOptionsApiController.getDeliveryStaff` | 一级公司（数据范围为全部）调用配送员接口 500（NPE：staffList 为 null） | 一级公司改为查询全部未删除用户；DAO 空结果不再导致 NPE |
| 2 | `MtVmDaoImpl.findListByCondition` | `showPattern` 为 null 自动拆箱 NPE，设备列表恒为空 | 改为 `Boolean.TRUE.equals(showPattern)` |
| 3 | `MtVmChannelApiController.list` | 设备货道列表 500（Cayenne 路径 `TMtVmChannel.vm` 不存在） | 关系名修正为 `mtVm` |

说明：T-5.01 期间已修复的 LogAdvice 吞异常问题（写接口未登录返回 200 空响应）是本轮鉴权断言的背景修复。

## 4. 测试数据

为让设备级/策略级接口有数据可测，本地执行了两个可重复种子脚本：

- `database/test_data_scope.sql`：公司 90001~90006、用户、设备 90001~90006（脚本自带清理）
- `database/test_model_channels.sql`：型号 9001 的货道模板（1 层 2 道）、闪购键数、温控能力

二者只影响本地开发库，不属于生产数据。

## 5. 未覆盖项

- 皮肤上传（zip+png）未做自动化回归：本地无皮肤数据，且上传后需人工确认预览；
- 闪购键策略的创建/编辑未做写闭环：依赖型号闪购键数量与跳板资源组合，仅验证了列表与资源接口；
- 前端页面级交互（Vue 运行时）未做浏览器回归：本机未启用内置 Browser preview，仅验证 HTTP 层与构建类型检查；
- 工单创建（配送/维护/投放/撤机）未在回归中造数，避免库存与工单状态副作用；相关接口已验证列表/详情与鉴权。

## 6. 结论

登录鉴权、数据范围、九类策略页与设备级配置接口在登录态下工作正常，
三处真实缺陷已修复。验证码旁路已恢复关闭，代码与配置回到安全基线。
