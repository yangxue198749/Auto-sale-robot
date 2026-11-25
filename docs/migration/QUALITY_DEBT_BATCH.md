# 质量债批次：索引补齐 + 私有文件鉴权下载 + 前端构建优化

日期：2026-11-26 ｜ 任务卡：TASK-1795800000001 ｜ 状态：待 Codex 审查

## 一、数据库索引补齐（database/vms_indexes.sql）

**背景**：DataMap 生成的 85 表 DDL 只有列和主键，零二级索引；工作台今日指标、工单分页、状态历史时间窗等查询随数据量增长退化为全表扫描。

**范围**：12 张热路径表、25 个索引，全部对应已迁移页面的实际 WHERE/JOIN 列（脚本头部有逐条依据注释）。**明确不加外键约束**——关系由 Cayenne 应用层维护，DDL 层加 FK 风险大于收益。

**使用**：
- 本地/存量库：`mysql -uroot -p vms < database/vms_indexes.sql`（幂等，可重复执行；存储过程 `add_index_if_missing` 自动跳过已存在索引）
- 全新部署：docker-entrypoint-initdb.d 按 `06-indexes.sql` 文件名序自动执行（已加入 build-deploy-package.ps1）
- 回滚：脚本文末附注释版 DROP 清单

**验证**：本地应用后 `EXPLAIN SELECT COUNT(*) FROM T_TASK_VM WHERE TASK_TYPE=3 AND TASK_TIME BETWEEN ...` → `key: idx_task_type_time, Extra: Using index`（覆盖扫描）；复跑无报错（幂等）。

## 二、私有 bucket 文件鉴权下载（FilesApiController）

**背景**：OSS→本地存储改造时私有 bucket（微信支付 p12 证书 `pay/wx/<mchId>.p12`，收款人配置页上传）只有写入通道，无 HTTP 读取出口。

**设计决策（安全取舍）**：不做"任意 key 读取"通用端点——支付证书属资金域敏感文件。改为**无参数窄端点**：

```
GET /vms/api/files/private/payCert
```

证书路径由服务端从 `IMtBeneficiaryService.findPayInfo(当前用户)` 推导（该服务本身按用户公司过滤，T-1.5 已验证数据范围），天然携带公司隔离，杜绝跨公司读取与路径探测面。后续私有 bucket 新增文件类型时应各建带范围校验的窄端点，不放宽本端点。

**响应**：200 流式 `application/x-pkcs12` + `Content-Disposition: <mchId>.p12` + `Cache-Control: no-store`；404（未配置收款人/未登记路径/文件缺失）；401（未登录）。

**验证**（本地 8081 实测）：匿名 401 ✓；登录无配置 404 ✓；造数后 200 且与磁盘文件**字节一致** ✓；测试数据已清理。

## 三、前端构建优化（vms-ui）

**改动**：
1. main.ts 移除全量 `app.use(ElementPlus)` 与全量 CSS；
2. vite 接入 `unplugin-auto-import` + `unplugin-vue-components`（ElementPlusResolver）按需注入模板组件/指令/样式；
3. 45 处 `import { ElMessage(, ElMessageBox) } from 'element-plus'` 重写为 `element-plus/es/components/message(-box)/index` 深路径（`import type` 不动，编译期擦除）；
4. `Components({ dts: false })`——开启 dts 会给 el-table 插槽引入严格类型，暴露 40+ 处历史 `DefaultRow` 不匹配，已立为后续技术债，不在本批范围。

**效果**：

| 指标 | 改造前 | 改造后 |
| --- | --- | --- |
| 入口 index.js | 1083.64 kB | 21.65 kB |
| 全量 CSS | 364.59 kB | 19.94 kB（按组件注入） |
| 最大共享 chunk | ~1084 kB（超限警告） | 267.64 kB（无警告） |

**踩坑记录**：
- `manualChunks` 对象形式会把所列包的**入口模块整个拉进依赖图**（曾致 element-plus 全量 947kB）——按需引入后无需 manualChunks，Rollup 默认分包已达标（配置内留注释说明）。
- `cargo.jvmargs` 对 embedded 容器**不生效**（共用 Maven JVM）：本地验证存储根须 `-Dlocal.storage.root=...` 直接传给 mvn。vms-web/pom 已把 `cargo.servlet.port` 参数化（默认 8080，`-Dcargo.servlet.port=8081` 可覆盖，本地 8080 被其他项目占用时的正解）。
- `ui.acceptance.mjs` 原 `toISOString()` 取 UTC 日期，凌晨时段与服务器本地日差一天导致日期验证码绕过失效——已改本地日期。

**回归**：45/45 页浏览器验收通过（ENTRY=http://127.0.0.1:8081/dist/index.html）；安全收尾后部署产物实测 date 登录 code=4、匿名 API 401。
