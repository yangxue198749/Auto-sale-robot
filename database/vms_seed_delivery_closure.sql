-- ============================================================
-- VMS 配送工单闭环 - 最小业务种子数据
-- 依赖：vms_schema.sql（表结构）+ vms_seed.sql（系统用户/设置树）
-- 目标：构造 公司→点位→机型→商品→售货机→货道 完整链路，
--       使 admin 能创建配送工单并走通状态流转。
-- 日期：2026-11-07
-- 说明：幂等，可重复执行；ID 固定在 9000+ 段，避免与历史数据冲突。
-- ============================================================

USE vms;

-- 1. 公司（一级公司，LEVEL_ONE；admin 归属此公司）
INSERT INTO `T_MT_COMPANY`
  (`COMPANY_ID`, `COMPANY_LEVEL`, `COMPANY_NAME`, `DELETE_MARK`, `CTIME`, `CTOR`, `MAX_VM_CAPACITY`)
VALUES
  (9001, 1, '闭环测试公司', 0, NOW(), 1, 999)
ON DUPLICATE KEY UPDATE `COMPANY_NAME` = VALUES(`COMPANY_NAME`);

-- 2. 点位（售货机投放点）
INSERT INTO `T_MT_NODE`
  (`NODE_ID`, `NODE_NAME`, `NODE_ADDRESS`, `COMPANY_ID`, `CTIME`, `CTOR`)
VALUES
  (9001, '闭环测试点位', '测试地址 1 号', 9001, NOW(), 1)
ON DUPLICATE KEY UPDATE `NODE_NAME` = VALUES(`NODE_NAME`);

-- 3. 售货机机型
INSERT INTO `T_MT_VM_MODEL`
  (`MODEL_ID`, `MODEL_NAME`, `MODEL_TYPE`, `DELETE_MARK`, `CTIME`, `CTOR`)
VALUES
  (9001, '闭环测试机型', 0, 0, NOW(), 1)
ON DUPLICATE KEY UPDATE `MODEL_NAME` = VALUES(`MODEL_NAME`);

-- 4. 商品（SKU）
INSERT INTO `T_MT_SKU`
  (`SKU_ID`, `SKU_CODE`, `SKU_NAME`, `SKU_UNIT`, `CTIME`, `CTOR`, `OFFER_PRICE`)
VALUES
  (9001, 'SKU9001', '测试矿泉水', '瓶', NOW(), 1, 200),
  (9002, 'SKU9002', '测试可乐',   '罐', NOW(), 1, 350)
ON DUPLICATE KEY UPDATE `SKU_NAME` = VALUES(`SKU_NAME`);

-- 5. 售货机（在售状态，归属公司+点位+机型）
--    VM_STATUS 见 TMtVm 常量；STOCK_STATUS 正常
INSERT INTO `T_MT_VM`
  (`VM_ID`, `INNER_CODE`, `VM_DESC`, `COMPANY_ID`, `NODE_ID`, `VM_MODEL_ID`,
   `VM_STATUS`, `VM_TYPE`, `STOCK_STATUS`, `DELETE_MARK`, `CTIME`, `CTOR`)
VALUES
  (9001, 'INNER9001', '闭环测试售货机 1 号', 9001, 9001, 9001,
   1, 0, 1, 0, NOW(), 1)
ON DUPLICATE KEY UPDATE `VM_DESC` = VALUES(`VM_DESC`);

-- 6. 售货机货道（2 条，分别放两种商品，当前库存低于容量，便于补货）
INSERT INTO `T_MT_VM_CHANNEL`
  (`CHANNEL_ID`, `VM_ID`, `SKU_ID`, `CHANNEL_CODE`, `CURR_NUM`, `MAX_CAPACITY`,
   `CHANNEL_STATUS`, `CTIME`, `CTOR`)
VALUES
  (9001, 9001, 9001, 'A01', 3, 10, 0, NOW(), 1),
  (9002, 9001, 9002, 'A02', 2, 8,  0, NOW(), 1)
ON DUPLICATE KEY UPDATE `CURR_NUM` = VALUES(`CURR_NUM`);

-- 7. admin 用户关联到公司（创建工单时 user.getCompany() 不能为空）
UPDATE `T_SYS_USER` SET `COMPANY_ID` = 9001, `DELETE_MARK` = 0
WHERE `USER_ACCOUNT` = 'admin';

-- 8. 验证
SELECT 'company' AS tbl, COUNT(*) AS cnt FROM `T_MT_COMPANY` WHERE COMPANY_ID = 9001
UNION ALL SELECT 'node',     COUNT(*) FROM `T_MT_NODE` WHERE NODE_ID = 9001
UNION ALL SELECT 'model',    COUNT(*) FROM `T_MT_VM_MODEL` WHERE MODEL_ID = 9001
UNION ALL SELECT 'sku',      COUNT(*) FROM `T_MT_SKU` WHERE SKU_ID IN (9001,9002)
UNION ALL SELECT 'vm',       COUNT(*) FROM `T_MT_VM` WHERE VM_ID = 9001
UNION ALL SELECT 'channel',  COUNT(*) FROM `T_MT_VM_CHANNEL` WHERE VM_ID = 9001
UNION ALL SELECT 'admin_co', COUNT(*) FROM `T_SYS_USER` WHERE USER_ACCOUNT='admin' AND COMPANY_ID=9001;
