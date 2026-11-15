# 工作台 REST API

日期：2026-11-15 ｜ 任务卡：TASK-1795100000001 ｜ 状态：待 Codex 审查

## 端点

```
GET /vms/api/workbench/summary
```

鉴权：与既有 `/vms/api/**` 一致——Shiro 放行，controller 内 `requireAuth()` 显式校验，未登录 401 JSON。

## 响应

```json
{
  "date": "2026-11-15",
  "delivery": { "ongoing": 0, "finished": 0, "closed": 0 },
  "repair":   { "ongoing": 0, "finished": 0, "closed": 0 },
  "deploy":   { "ongoing": 0, "finished": 0, "closed": 0 },
  "withdraw": { "ongoing": 0, "finished": 0, "closed": 0 },
  "newNodeCount": 0,
  "newVmCount": 0,
  "eventVmCount": 0,
  "oosVmCount": 1,
  "salesVolume": 0,
  "salesAmount": "0.00"
}
```

## 口径说明（与旧 WorkbenchController 完全一致）

| 字段 | 来源（IWorkbenchService） | 口径 |
| --- | --- | --- |
| delivery/repair/deploy/withdraw | findXxxTaskList | 今日该类工单按状态计数：`ongoing` = 未开始(1)+进行中(2) 合并（旧 Map key=1）、`finished` = 完成(4)、`closed` = 关闭(7)；含公司数据范围 |
| newNodeCount | findNewNode | 今日新增点位 |
| newVmCount | findNewVm | 今日新增设备 |
| eventVmCount | findEventVm | 今日在促销设备 |
| oosVmCount | findOutOfStockVm | 今日缺货设备 |
| salesVolume | findSalesVolume | 今日销量（Integer） |
| salesAmount | findSalesAmount | 今日销售额（String，如 "0.00"） |

复用 service 原方法，业务逻辑零改动；旧 JSP 端点（`/workbench/*`）保留未动。

## 前端

- `vms-ui/src/api/workbench.ts`：类型 + `getWorkbenchSummary()`
- `vms-ui/src/views/Home.vue`：10 指标卡片（四类工单卡含三态明细），全部 router-link 跳对应管理页；加载/错误/重试态

## 验证

- 匿名 401 / 登录态 200（本地实测，含 `oosVmCount:1` 真实数据命中）
- `tests/ui.acceptance.mjs` 45/45 全过（bypass 临时窗口内执行，事后已恢复 false 并以部署产物实测 code=4）
