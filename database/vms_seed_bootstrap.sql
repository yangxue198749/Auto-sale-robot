-- ============================================================
-- VMS 引导数据：一级（系统内置）公司 + 种子账号 admin 归属
--
-- 为什么需要：数据范围、公司管理、角色管理的"可分配权限"都以一级公司为根，
--   而界面上的「新建公司」只能建三级公司（MtCompanyServiceImpl.addCompany），
--   全新部署没有任何途径产生一级公司，admin 也没有公司归属。
--
-- 幂等：可重复执行。对已有数据库通常不产生任何变化——
--   9001 号已存在时不覆盖；只有在 admin 当前没有公司、且 9001 号正是本脚本创建的
--   一级公司（名称匹配）时，才把 admin 归属过去。
--
-- 上线后请在「公司管理」核对：公司名称、售货机额度。
--   默认额度 1000000 是"不设上限"的占位值（代码里额度会直接拆箱比较，不能为空），
--   下级公司的机器额度都从本公司划拨，请按实际规模调整。
--
-- 回滚：
--   UPDATE `T_SYS_USER` SET `COMPANY_ID` = NULL WHERE `USER_ACCOUNT` = 'admin' AND `COMPANY_ID` = 9001;
--   DELETE FROM `T_MT_COMPANY` WHERE `COMPANY_ID` = 9001 AND `COMPANY_NAME` = '总公司（系统内置）';
--   （仅当本公司是本脚本创建的、且其下尚无下级公司与设备时才可删除）
-- ============================================================

USE vms;

INSERT IGNORE INTO `T_MT_COMPANY`
  (`COMPANY_ID`, `COMPANY_LEVEL`, `COMPANY_NAME`, `DELETE_MARK`, `MAX_VM_CAPACITY`, `CTIME`, `CTOR`)
VALUES
  (9001, 1, '总公司（系统内置）', 0, 1000000, NOW(), 1);

UPDATE `T_SYS_USER` u
  JOIN `T_MT_COMPANY` c ON c.`COMPANY_ID` = 9001
SET u.`COMPANY_ID` = 9001
WHERE u.`USER_ACCOUNT` = 'admin' AND u.`COMPANY_ID` IS NULL
  AND u.`USER_ID` = (SELECT `uid` FROM (SELECT MIN(`USER_ID`) AS `uid` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin') t)
  AND c.`COMPANY_LEVEL` = 1 AND c.`COMPANY_NAME` = '总公司（系统内置）';

-- 自检：admin_company 为一级公司 ID 即正常（全新部署应为 9001；对接已有库时应为原有的一级公司）
SELECT 'bootstrap_company' AS item, COUNT(*) AS n FROM `T_MT_COMPANY`
  WHERE `COMPANY_ID` = 9001 AND `COMPANY_LEVEL` = 1 AND `COMPANY_NAME` = '总公司（系统内置）'
UNION ALL SELECT 'admin_company', IFNULL((SELECT `COMPANY_ID` FROM (SELECT `COMPANY_ID` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin' ORDER BY `USER_ID` LIMIT 1) t), -1)
UNION ALL SELECT 'admin_company_is_level1', IFNULL((SELECT c.`COMPANY_LEVEL` FROM `T_SYS_USER` u JOIN `T_MT_COMPANY` c ON c.`COMPANY_ID` = u.`COMPANY_ID`
  WHERE u.`USER_ACCOUNT` = 'admin' ORDER BY u.`USER_ID` LIMIT 1), 0);
