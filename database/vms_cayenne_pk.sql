-- ============================================================
-- Cayenne 3.1 自动主键支持表（MySQLPkGenerator 依赖）
--
-- 背景：Cayenne 不依赖数据库自增，而是用 AUTO_PK_SUPPORT 表为每张
--       业务表分配主键。原 Oracle 环境有此表，从 DataMap 重建的 DDL
--       缺失，导致首次 insert 报 Table 'vms.auto_pk_support' doesn't exist。
--
-- 表名大小写：Cayenne 用 db-entity 名（大写 T_ 开头）作为 TABLE_NAME，
--            MySQL 默认 lower_case_table_names=1 不区分大小写，此处统一大写。
--
-- 起始 ID：种子数据已占用 9000 段（业务数据）和 0~403（设置树），
--         主键计数统一从 10000 起，避免与显式插入的 ID 冲突。
-- ============================================================

USE vms;

CREATE TABLE IF NOT EXISTS `AUTO_PK_SUPPORT` (
  `TABLE_NAME` CHAR(100) NOT NULL,
  `NEXT_ID` BIGINT NOT NULL,
  UNIQUE KEY `UK_AUTO_PK_TABLE_NAME` (`TABLE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 为所有业务表初始化主键计数（幂等：已存在则不覆盖）
INSERT IGNORE INTO `AUTO_PK_SUPPORT` (`TABLE_NAME`, `NEXT_ID`)
SELECT t.table_name, 10000
FROM information_schema.tables t
WHERE t.table_schema = 'vms'
  AND t.table_name <> 'auto_pk_support';

-- 验证
SELECT COUNT(*) AS pk_table_rows FROM `AUTO_PK_SUPPORT`;
SELECT `TABLE_NAME`, `NEXT_ID` FROM `AUTO_PK_SUPPORT`
WHERE `TABLE_NAME` IN ('T_TASK_VM', 'T_TASK_VM_CHANNEL', 'T_SYS_LOG', 'T_MT_VM')
ORDER BY `TABLE_NAME`;
