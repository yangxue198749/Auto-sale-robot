-- ============================================================
-- VMS 关键查询索引补齐（MySQL 8，幂等可重复执行）
--
-- 背景：database/vms_schema.sql 由 Cayenne DataMap 生成，只有列和主键，
--       无任何二级索引。列表/报表类查询（工作台今日指标、四类工单分页、
--       状态历史时间窗、订单流水、系统日志）随数据量增长会退化为全表扫描。
--
-- 范围：仅对已迁移页面实际使用的 WHERE/JOIN 列建索引；
--       不加外键约束（关系由 Cayenne 应用层维护，DDL 加 FK 风险大于收益）。
--
-- 索引选取依据（对应查询代码）：
--   T_TASK_VM      (TASK_TYPE,TASK_TIME)  WorkbenchServiceImpl.findXxxTaskList 今日工单
--                                          + TaskVmDaoImpl.findPage 类型+时间范围
--                  (TASK_VM_STATUS)       findPage 状态过滤
--                  (VM_ID)                工单详情/货道明细 JOIN
--                  (OWNER)                findPage 负责人搜索、配送员工单
--                  (COMPANY_ID)           公司数据范围
--   T_TASK_VM_CHANNEL (TASK_VM_ID)        工单货道明细按工单装载
--   T_MT_VM        (NODE_ID/VM_MODEL_ID/COMPANY_ID) 设备列表筛选与公司范围
--                  (INNER_CODE)           设备编号搜索/精确匹配
--   T_MT_VM_CHANNEL (VM_ID/SKU_ID)        货道配置、库存明细、商品关联
--   T_MT_NODE      (COMPANY_ID)           点位列表公司范围
--   T_MT_SKU       (SKU_TYPE)             商品类型筛选
--   T_VM_STATUS    (VM_ID)                售货机状态页
--   T_VM_STATUS_HIS (VM_ID,VM_TIME)       历史状态页默认 1 小时时间窗
--   T_VM_OPERATION_HIS (VM_ID,CTIME)      运营历史按设备+时间
--   T_SYS_LOG      (USER_ID,LOG_TIME)/(LOG_TIME) 日志页按操作人+时间
--   T_ORDER_MASTER (C_TIME/VM_ID/COMPANY_ID) 订单流水日期/设备/公司筛选
--   T_SYS_USER     (COMPANY_ID)           用户列表公司范围
--
-- 回滚：文末附注释版 DROP INDEX 清单，逐条执行即可。
-- ============================================================

USE vms;

DROP PROCEDURE IF EXISTS add_index_if_missing;
DELIMITER $$
CREATE PROCEDURE add_index_if_missing(IN tbl VARCHAR(64), IN idx VARCHAR(64), IN cols VARCHAR(255))
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.statistics
        WHERE table_schema = DATABASE() AND table_name = tbl AND index_name = idx
    ) THEN
        SET @ddl = CONCAT('CREATE INDEX `', idx, '` ON `', tbl, '` (', cols, ')');
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END$$
DELIMITER ;

-- ---------- 工单域 ----------
CALL add_index_if_missing('T_TASK_VM', 'idx_task_type_time', '`TASK_TYPE`, `TASK_TIME`');
CALL add_index_if_missing('T_TASK_VM', 'idx_task_status', '`TASK_VM_STATUS`');
CALL add_index_if_missing('T_TASK_VM', 'idx_task_vm', '`VM_ID`');
CALL add_index_if_missing('T_TASK_VM', 'idx_task_owner', '`OWNER`');
CALL add_index_if_missing('T_TASK_VM', 'idx_task_company', '`COMPANY_ID`');
CALL add_index_if_missing('T_TASK_VM_CHANNEL', 'idx_tvc_task', '`TASK_VM_ID`');
CALL add_index_if_missing('T_TASK_VM_CHANNEL', 'idx_tvc_channel', '`CHANNEL_ID`');
CALL add_index_if_missing('T_TASK_VM_CHANNEL', 'idx_tvc_sku', '`SKU_ID`');

-- ---------- 设备域 ----------
CALL add_index_if_missing('T_MT_VM', 'idx_vm_node', '`NODE_ID`');
CALL add_index_if_missing('T_MT_VM', 'idx_vm_model', '`VM_MODEL_ID`');
CALL add_index_if_missing('T_MT_VM', 'idx_vm_company', '`COMPANY_ID`');
CALL add_index_if_missing('T_MT_VM', 'idx_vm_inner_code', '`INNER_CODE`');
CALL add_index_if_missing('T_MT_VM_CHANNEL', 'idx_channel_vm', '`VM_ID`');
CALL add_index_if_missing('T_MT_VM_CHANNEL', 'idx_channel_sku', '`SKU_ID`');
CALL add_index_if_missing('T_VM_STATUS', 'idx_vms_vm', '`VM_ID`');
CALL add_index_if_missing('T_VM_STATUS_HIS', 'idx_vmsh_vm_time', '`VM_ID`, `VM_TIME`');
CALL add_index_if_missing('T_VM_OPERATION_HIS', 'idx_voh_vm_ctime', '`VM_ID`, `CTIME`');

-- ---------- 基础数据 ----------
CALL add_index_if_missing('T_MT_NODE', 'idx_node_company', '`COMPANY_ID`');
CALL add_index_if_missing('T_MT_SKU', 'idx_sku_type', '`SKU_TYPE`');
CALL add_index_if_missing('T_SYS_USER', 'idx_user_company', '`COMPANY_ID`');

-- ---------- 日志与订单 ----------
CALL add_index_if_missing('T_SYS_LOG', 'idx_log_user_time', '`USER_ID`, `LOG_TIME`');
CALL add_index_if_missing('T_SYS_LOG', 'idx_log_time', '`LOG_TIME`');
CALL add_index_if_missing('T_ORDER_MASTER', 'idx_order_ctime', '`C_TIME`');
CALL add_index_if_missing('T_ORDER_MASTER', 'idx_order_vm', '`VM_ID`');
CALL add_index_if_missing('T_ORDER_MASTER', 'idx_order_company', '`COMPANY_ID`');

DROP PROCEDURE IF EXISTS add_index_if_missing;

-- ============================================================
-- 回滚清单（按需逐条执行）
-- ============================================================
-- DROP INDEX `idx_task_type_time`   ON `T_TASK_VM`;
-- DROP INDEX `idx_task_status`      ON `T_TASK_VM`;
-- DROP INDEX `idx_task_vm`          ON `T_TASK_VM`;
-- DROP INDEX `idx_task_owner`       ON `T_TASK_VM`;
-- DROP INDEX `idx_task_company`     ON `T_TASK_VM`;
-- DROP INDEX `idx_tvc_task`         ON `T_TASK_VM_CHANNEL`;
-- DROP INDEX `idx_tvc_channel`      ON `T_TASK_VM_CHANNEL`;
-- DROP INDEX `idx_tvc_sku`          ON `T_TASK_VM_CHANNEL`;
-- DROP INDEX `idx_vm_node`          ON `T_MT_VM`;
-- DROP INDEX `idx_vm_model`         ON `T_MT_VM`;
-- DROP INDEX `idx_vm_company`       ON `T_MT_VM`;
-- DROP INDEX `idx_vm_inner_code`    ON `T_MT_VM`;
-- DROP INDEX `idx_channel_vm`      ON `T_MT_VM_CHANNEL`;
-- DROP INDEX `idx_channel_sku`     ON `T_MT_VM_CHANNEL`;
-- DROP INDEX `idx_vms_vm`          ON `T_VM_STATUS`;
-- DROP INDEX `idx_vmsh_vm_time`    ON `T_VM_STATUS_HIS`;
-- DROP INDEX `idx_voh_vm_ctime`    ON `T_VM_OPERATION_HIS`;
-- DROP INDEX `idx_node_company`    ON `T_MT_NODE`;
-- DROP INDEX `idx_sku_type`        ON `T_MT_SKU`;
-- DROP INDEX `idx_user_company`    ON `T_SYS_USER`;
-- DROP INDEX `idx_log_user_time`   ON `T_SYS_LOG`;
-- DROP INDEX `idx_log_time`        ON `T_SYS_LOG`;
-- DROP INDEX `idx_order_ctime`     ON `T_ORDER_MASTER`;
-- DROP INDEX `idx_order_vm`        ON `T_ORDER_MASTER`;
-- DROP INDEX `idx_order_company`   ON `T_ORDER_MASTER`;
