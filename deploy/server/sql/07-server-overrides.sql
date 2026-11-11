-- 服务器部署覆盖：Redis 主机/密码 + MCS 回调地址
-- 部署前必须手工修改下面三处 EDIT-ME（与 .env 保持一致）：
--   1. vms-redis 密码 → .env 的 REDIS_PASSWORD
--   2. mcsCallBackUrl 的 IP → 服务器公网 IP（设备端回调用）
USE vms;

-- Redis：容器内主机名 + 与 .env REDIS_PASSWORD 一致的密码
UPDATE T_SYS_SETTINGS SET VALUE = 'vms-redis'   WHERE KEY = 'redis.ip';
UPDATE T_SYS_SETTINGS SET VALUE = '6379'        WHERE KEY = 'redis.port';
UPDATE T_SYS_SETTINGS SET VALUE = 'EDIT-ME-同.env的REDIS_PASSWORD' WHERE KEY = 'redis.passWord';

-- MCS：设备未接入前先留 mock 地址；接入真实 MCS 时改成实际入口
-- mcsCallBackUrl 必须是设备能访问到的地址：EDIT-ME-公网IP
UPDATE T_SYS_SETTINGS SET VALUE = 'http://EDIT-ME-公网IP:8090/api/pushMessage' WHERE KEY = 'mcsCallBackUrl';
