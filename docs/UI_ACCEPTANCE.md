# 浏览器级 UI 验收报告

日期：2026-11-08
方式：Playwright（系统 Chrome）驱动打包产物 `/dist/index.html`（Tomcat 8080 同源部署）
脚本：`vms-ui/tests/ui.acceptance.mjs`（全页走查）、`vms-ui/tests/spotcheck.mjs`（数据渲染抽查）
前置：经大聪明授权临时开启验证码旁路，验收后已恢复关闭

## 1. 结果

**28/28 全部通过**：登录 1 项 + 27 个页面路由。

每个页面断言：

- 页面标题（h1）与预期一致；
- 无 console error、无未捕获 pageerror；
- 无 4xx/5xx 的 `/vms/api/**` 业务请求；
- 截图留档（`vms-ui/test-results/screens/`，不入库）。

数据渲染抽查（DOM 行数 + 分页总数）：

| 页面 | 表格行数 | 分页总数 |
| --- | --- | --- |
| 售货机管理 | 7 | Total 7 |
| 商品管理 | 2 | Total 2 |
| 点位管理 | 1 | Total 1 |
| 工单查询 | 7 | Total 7 |
| 配送工单 | 7 | Total 7 |

## 2. 验收发现并修复的问题

| # | 问题 | 修复 |
| --- | --- | --- |
| 1 | 登录页验证码图片写死 `/api` 前缀，生产同源部署 404 | 改用 `API_BASE`（开发 `/api`，生产空） |
| 2 | 商品缺省图 `/vmsimgs/default.png` 不存在，商品页 404 | war 内置 `vmsimgs/default.png`，静态映射增加回落：磁盘 bucket 优先，缺失时用 war 内置 |
| 3 | 无 favicon，页面加载报 404 | 新增 `favicon.svg` 并在 index.html 引用 |

提交：见 git log（feat: 浏览器级 UI 验收 28/28）。

## 3. 覆盖与未覆盖

- 覆盖：登录、27 页路由渲染、JS 零报错、业务 API 零 4xx/5xx、五页数据行渲染。
- 未覆盖：页面内弹窗交互（新建/编辑表单提交）未逐页走查——写路径已由
  `tools/regression-login.ps1`（103 项断言）在 HTTP 层覆盖；
  皮肤上传/闪购键策略写闭环仍属未覆盖项。

## 4. 安全收尾

- `login.captcha.date.bypass.enabled=false` 已恢复并重新部署；
- 实测：日期验证码登录返回 code=4；无 Cookie 访问 API 返回 401；`/ui` 与静态资源正常。
