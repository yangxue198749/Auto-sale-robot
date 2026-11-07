-- ============================================================
-- VMS 初始化种子数据（本地 MySQL 开发环境）
-- 配合 vms_schema.sql 使用
-- 生成时间: 2026-11-07
-- 说明：
--   1. 系统用户 vms_system（系统创建/修改数据时的 CTOR/UTOR 归属）
--   2. T_SYS_SETTINGS 设置树：ROOT(ID=0) -> redis/mcs/refund/upload -> 具体键
--   3. Redis 指向本地无密码实例
-- 注意：DataMap 不含初始化数据，本文件为重建，约束/字典数据后续按业务补全。
-- ============================================================

USE vms;

-- ------------------------------------------------------------
-- 1. 系统用户（UserUtils.getVmsSystem 依赖 user_account='vms_system'）
-- ------------------------------------------------------------
INSERT INTO `T_SYS_USER`
  (`USER_ID`, `USER_ACCOUNT`, `USER_NAME`, `USER_PASSWORD`, `LOCK_MARK`, `DELETE_MARK`, `CTIME`, `CTOR`)
VALUES
  (1, 'vms_system', 'vms', NULL, 1, 0, NOW(), 1)
ON DUPLICATE KEY UPDATE `USER_ACCOUNT` = `USER_ACCOUNT`;

-- 管理账号（密码为 md5('1') 大写，与 Md5Util 的大写 hex 输出一致；登录联调时用）
-- md5('1') 大写 = C4CA4238A0B923820DCC509A6F75849B
INSERT INTO `T_SYS_USER`
  (`USER_ID`, `USER_ACCOUNT`, `USER_NAME`, `USER_PASSWORD`, `LOCK_MARK`, `DELETE_MARK`, `CTIME`, `CTOR`)
VALUES
  (1101, 'admin', '管理员', 'C4CA4238A0B923820DCC509A6F75849B', 0, 0, NOW(), 1)
ON DUPLICATE KEY UPDATE `USER_ACCOUNT` = `USER_ACCOUNT`;

-- ------------------------------------------------------------
-- 2. T_SYS_SETTINGS 设置树
--    自关联：ID -> PID；ROOT 固定 ID=0（TSysSettings.ROOT_ID）
-- ------------------------------------------------------------
-- ROOT 节点
INSERT INTO `T_SYS_SETTINGS`
  (`ID`, `PID`, `KEY`, `NAME`, `VALUE`, `CTIME`, `CTOR`)
VALUES
  (0, NULL, 'sys_setting_root', 'ROOT', NULL, NOW(), 1)
ON DUPLICATE KEY UPDATE `KEY` = `KEY`;

-- 一级分组：redis / mcs / refund / upload
INSERT INTO `T_SYS_SETTINGS` (`ID`, `PID`, `KEY`, `NAME`, `VALUE`, `CTIME`, `CTOR`) VALUES
  (100, 0, 'redis',  'redis相关配置', NULL, NOW(), 1),
  (200, 0, 'mcs',    'mcs消息相关配置', NULL, NOW(), 1),
  (300, 0, 'refund', '退款相关配置', NULL, NOW(), 1),
  (400, 0, 'upload', '上传文件相关配置', NULL, NOW(), 1)
ON DUPLICATE KEY UPDATE `KEY` = `KEY`;

-- redis 子项（本地无密码 Redis；MyJedisCluster/RedisConfiguration 读取这些键）
INSERT INTO `T_SYS_SETTINGS` (`ID`, `PID`, `KEY`, `NAME`, `VALUE`, `CTIME`, `CTOR`) VALUES
  (101, 100, 'redis.ip',              'ip',                                   '127.0.0.1', NOW(), 1),
  (102, 100, 'redis.port',            '端口',                                 '6379',      NOW(), 1),
  (103, 100, 'redis.timeout',         '每个节点最大连接超时时间(ms)',          '5000',      NOW(), 1),
  (104, 100, 'redis.maxRedirections', '最大重定向次数',                        '10',        NOW(), 1),
  (105, 100, 'redis.passWord',        '密码',                                 'redis123',  NOW(), 1),
  (106, 100, 'redis.maxIdle',         '最大空闲连接数',                        '100',       NOW(), 1),
  (107, 100, 'redis.minIdle',         '最小空闲连接数',                        '0',         NOW(), 1),
  (108, 100, 'redis.maxTotal',        '最大连接数',                            '1024',      NOW(), 1),
  (109, 100, 'redis.maxWaitMillis',   '获取连接最大等待毫秒',                  '1000',      NOW(), 1),
  (110, 100, 'redis.testOnBorrow',    '获取连接时检查有效性',                  'true',      NOW(), 1),
  (111, 100, 'redis.testOnReturn',    '归还连接时检查有效性',                  'true',      NOW(), 1)
ON DUPLICATE KEY UPDATE `KEY` = `KEY`;

-- mcs 子项（指向本地 MCS mock：tools/mcs_mock.py，返回 success=0；启动方式见 docs/LOCAL_STARTUP.md）
-- 注意：键名必须与 McsConfiguration 的常量一致（驼峰，无点）：mcsUrl/mcsConnectUrl/mcsCallBackUrl
INSERT INTO `T_SYS_SETTINGS` (`ID`, `PID`, `KEY`, `NAME`, `VALUE`, `CTIME`, `CTOR`) VALUES
  (201, 200, 'mcsUrl',         'MCS消息入口',     'http://127.0.0.1:8011/mcs/enter',    NOW(), 1),
  (202, 200, 'mcsConnectUrl',  'MCS注册心跳入口', 'http://127.0.0.1:8011/mcs/connect',  NOW(), 1),
  (203, 200, 'mcsCallBackUrl', 'MCS消息回调地址', 'http://127.0.0.1:18081/api/pushMessage', NOW(), 1)
ON DUPLICATE KEY UPDATE `KEY` = `KEY`;

-- refund 子项（自动退款最大金额，单位分；AddSettings 原值 5000）
INSERT INTO `T_SYS_SETTINGS` (`ID`, `PID`, `KEY`, `NAME`, `VALUE`, `CTIME`, `CTOR`) VALUES
  (301, 300, 'max_refund_money', '自动退款最大金额(分)', '5000', NOW(), 1)
ON DUPLICATE KEY UPDATE `KEY` = `KEY`;

-- upload 子项（AddSettings 原值）
INSERT INTO `T_SYS_SETTINGS` (`ID`, `PID`, `KEY`, `NAME`, `VALUE`, `CTIME`, `CTOR`) VALUES
  (401, 400, 'theme_max_zip_size',      '主题zip包上传大小限制(MB)', '10',  NOW(), 1),
  (402, 400, 'theme_max_preview_size',  '主题预览图上传大小限制(MB)', '3',   NOW(), 1),
  (403, 400, 'sku_max_image_size',      '商品图片上传大小限制(KB)',   '200', NOW(), 1)
ON DUPLICATE KEY UPDATE `KEY` = `KEY`;

-- ------------------------------------------------------------
-- 验证
-- ------------------------------------------------------------
SELECT 'T_SYS_USER' AS tbl, COUNT(*) AS cnt FROM `T_SYS_USER`
UNION ALL
SELECT 'T_SYS_SETTINGS', COUNT(*) FROM `T_SYS_SETTINGS`;
