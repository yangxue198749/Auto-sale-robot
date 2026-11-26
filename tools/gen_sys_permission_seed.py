#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""从 menu-vms.md（VMS 端菜单/按钮权限项导出表）生成权限种子 database/vms_seed_sys_permission.sql。

用法（在 vms/ 下）：python tools/gen_sys_permission_seed.py

生成内容：
  1. T_SYS_ELEMENT 全量权限项（module / little-module / menu / button）
  2. 系统管理员角色（旧语义：ROLE_TYPE_SYSTEM + ROLE_LEVEL_ADMIN，即一级公司管理员角色，
     其已绑定的权限项构成"可分配权限全集"，见 SysElementDaoImpl.getElementAllBySystemAdmin）
  3. 管理员角色绑定全部权限项（可重复执行：新增权限项后再跑一次即自动补绑）
  4. 种子账号 admin 绑定管理员角色

主键全部落在高位固定区（990001 / 1000 万偏移），避开 Cayenne 自增（AUTO_PK_SUPPORT 自 10000 起）。
"""
import io
import os
import re

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
MENU_MD = os.path.join(ROOT, 'menu-vms.md')
OUT = os.path.join(ROOT, 'database', 'vms_seed_sys_permission.sql')

ADMIN_ROLE_ID = 990001
ADMIN_BIND_ID = 990001
ROLE_ELEMENT_OFFSET = 10000000
VMS_MODULE = 0  # TSysElement.VMS_MODULE

# menu-vms.md（2018 年导出）自带的主键冲突修正，键为权限码：
#   皮肤策略-删除 st-vm-theme-delete 在源表中主键误写为 1040402（与节能策略-修改撞号），
#   按编号规律（父级 1040500 下第 2 个按钮）应为 1040502，且该号空闲。
# 遇到未在此登记的主键冲突一律报错退出，不静默改号。
ID_OVERRIDES = {'st-vm-theme-delete': 1040502}


def sql_str(value):
    if value is None:
        return 'NULL'
    return "'" + value.replace('\\', '\\\\').replace("'", "''") + "'"


def parse_menu(path):
    rows = []
    with io.open(path, encoding='utf-8') as fh:
        for line in fh:
            line = line.strip()
            if not line or not re.match(r'^\d+\|', line):
                continue  # 表头/空行
            parts = line.split('|')
            if len(parts) != 7:
                raise SystemExit('unexpected menu row (expect 7 columns): %s' % line)
            eid, name, url, code, etype, pid, idx = [p.strip() for p in parts]
            # '#' 与空路径不是可访问地址，存 NULL，避免被 getCodeByUrl 精确匹配到
            url = url if url and url != '#' else None
            code = code or None
            rows.append((int(eid), name, url, code, etype, int(pid), int(idx)))

    # 源表整行重复（如 st-vm-energy-delete 出现两次）：按主键 + 路径 + 权限码去重，保留首次
    deduped, seen = [], set()
    for r in rows:
        key = (r[0], r[2], r[3])
        if key in seen:
            continue
        seen.add(key)
        deduped.append(r)
    rows = deduped

    rows = [(ID_OVERRIDES.get(r[3], r[0]),) + r[1:] for r in rows]

    ids = set(r[0] for r in rows)
    if len(ids) != len(rows):
        dup = sorted(r[0] for r in rows)
        clash = sorted(set(x for x in dup if dup.count(x) > 1))
        raise SystemExit('duplicate ELEMENT_ID after overrides: %s (register in ID_OVERRIDES)' % clash)
    for r in rows:
        if r[5] != 1 and r[5] not in ids:
            raise SystemExit('orphan row %s: parent %s not found' % (r[0], r[5]))
    return rows


HEAD = u"""-- ============================================================
-- VMS 权限体系种子：权限项 + 系统管理员角色 + 种子 admin 绑定
--
-- 生成方式：python tools/gen_sys_permission_seed.py（数据源 menu-vms.md，勿手改本文件）
-- 幂等：全部 INSERT IGNORE，可重复执行；新增权限项后重跑即自动补绑到管理员角色。
--
-- 为什么需要它：REST 操作权限校验（ApiPermissionInterceptor）按
--   用户 -> T_SYS_USER_ROLE -> T_SYS_ROLE_ELEMENT -> T_SYS_ELEMENT.ELEMENT_CODE
-- 判定；不落本种子则无人拥有任何权限，标注了 @RequirePermission 的接口对所有人 403。
--
-- 前置条件：
--   1. 种子账号 admin 须归属**一级公司**（角色管理页的"可分配权限"按公司层级取管理员角色，
--      无公司归属时该页权限树不可用；权限校验本身不依赖公司）。全新部署由
--      vms_seed_bootstrap.sql（部署包 03b）创建一级公司并归属 admin。
--   2. 库中 ROLE_LEVEL=1 的角色应只有本种子创建的「系统管理员」一个：角色管理页取"可分配权限"
--      时取查询结果的第一条且无排序（SysElementDaoImpl.getElementAllBySystemAdmin），若存在旧的
--      一级管理员角色，取到的会是它——执行后看自检 level1_roles，不为 1 时先处置旧角色。
--   3. 其余存量账号不会自动获得权限，须在角色管理页按业务分配（有意为之：谁能管理权限应由人决定）。
--
-- 副作用（符合旧系统设计）：旧 JSP 后台的 SSOAccessControlFilter 按 ELEMENT_URL 精确
--   匹配权限码，本种子落库后旧 JSP 页面访问重新受权限控制（此前因表空而形同虚设）。
--
-- 源数据修正（menu-vms.md 自带缺陷，生成器显式处理）：
--   1. 整行重复的 st-vm-energy-delete 去重；
--   2. st-vm-theme-delete（皮肤策略-删除）主键 1040402 与节能策略-修改撞号，
--      改用 1040502（父级 1040500 下第 2 个按钮的编号位，原表空闲）。权限码未改。
--
-- 回滚：执行文末「清理段」。
-- ============================================================

USE vms;

-- ---------- 1. 权限项（VMS 端，ELEMENT_MODULE=0；移动端菜单不在本种子范围） ----------
INSERT IGNORE INTO `T_SYS_ELEMENT`
  (`ELEMENT_ID`, `ELEMENT_NAME`, `ELEMENT_URL`, `ELEMENT_CODE`, `ELEMENT_TYPE`, `P_ID`, `ELEMENT_INDEX`, `ELEMENT_MODULE`, `CTIME`, `CTOR`)
VALUES
"""

TAIL = u""";

-- ---------- 2. 系统管理员角色（一级公司管理员：其权限项即"可分配权限全集"） ----------
-- USER_ACCOUNT 无唯一约束：取 USER_ID 最小的 admin，多个同名账号不会让脚本中断
INSERT IGNORE INTO `T_SYS_ROLE`
  (`ROLE_ID`, `ROLE_NAME`, `ROLE_TYPE`, `ROLE_LEVEL`, `COMPANY_ID`, `ROLE_DESC`, `CTIME`, `CTOR`)
VALUES
  ({role}, '系统管理员', 1, 1,
   (SELECT `COMPANY_ID` FROM (SELECT `COMPANY_ID` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin' ORDER BY `USER_ID` LIMIT 1) t),
   '一级公司管理员，拥有全部权限项', NOW(), 1);

-- ---------- 3. 管理员角色绑定全部权限项（只补尚未绑定的，界面编辑过绑定后重跑不会产生重复行） ----------
INSERT IGNORE INTO `T_SYS_ROLE_ELEMENT` (`RE_ID`, `ROLE_ID`, `ELEMENT_ID`, `CTIME`, `CTOR`)
SELECT e.`ELEMENT_ID` + {offset}, {role}, e.`ELEMENT_ID`, NOW(), 1
FROM `T_SYS_ELEMENT` e
WHERE e.`ELEMENT_MODULE` = {module}
  AND EXISTS (SELECT 1 FROM `T_SYS_ROLE` r WHERE r.`ROLE_ID` = {role} AND r.`ROLE_NAME` = '系统管理员')
  AND NOT EXISTS (SELECT 1 FROM `T_SYS_ROLE_ELEMENT` x WHERE x.`ROLE_ID` = {role} AND x.`ELEMENT_ID` = e.`ELEMENT_ID`);

-- ---------- 4. 种子账号 admin（USER_ID 最小的同名账号）绑定管理员角色 ----------
INSERT IGNORE INTO `T_SYS_USER_ROLE` (`UR_ID`, `USER_ID`, `ROLE_ID`, `CTIME`, `CTOR`)
SELECT {bind}, u.`USER_ID`, {role}, NOW(), 1
FROM `T_SYS_USER` u
WHERE u.`USER_ACCOUNT` = 'admin'
  AND u.`USER_ID` = (SELECT MIN(`USER_ID`) FROM (SELECT `USER_ID` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin') t2)
  AND EXISTS (SELECT 1 FROM `T_SYS_ROLE` r WHERE r.`ROLE_ID` = {role} AND r.`ROLE_NAME` = '系统管理员');

-- ---------- 5. 自检 ----------
-- 预期：elements={n} / role=1 / bound={n} / admin_bind=1
-- 上线关口（不为预期值时必须先处理，见 DEPLOY_RUNBOOK「权限种子前置检查」）：
--   admin_accounts = 1     同名 admin 多于一个时只绑定了 USER_ID 最小的那个
--   level1_roles  = 1      存在旧的一级管理员角色时角色管理页的"可分配权限"取到的是旧角色，须处置
SELECT 'elements' AS item, COUNT(*) AS n FROM `T_SYS_ELEMENT` WHERE `ELEMENT_MODULE` = {module}
UNION ALL SELECT 'role', COUNT(*) FROM `T_SYS_ROLE` WHERE `ROLE_ID` = {role} AND `ROLE_NAME` = '系统管理员'
UNION ALL SELECT 'bound', COUNT(DISTINCT `ELEMENT_ID`) FROM `T_SYS_ROLE_ELEMENT` WHERE `ROLE_ID` = {role}
UNION ALL SELECT 'admin_bind', COUNT(*) FROM `T_SYS_USER_ROLE` ur JOIN `T_SYS_USER` u ON u.`USER_ID` = ur.`USER_ID`
  WHERE u.`USER_ACCOUNT` = 'admin' AND ur.`ROLE_ID` = {role}
UNION ALL SELECT 'admin_accounts', COUNT(*) FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin'
UNION ALL SELECT 'level1_roles', COUNT(*) FROM `T_SYS_ROLE` WHERE `ROLE_LEVEL` = 1
UNION ALL SELECT 'admin_company', IFNULL((SELECT `COMPANY_ID` FROM (SELECT `COMPANY_ID` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin' ORDER BY `USER_ID` LIMIT 1) t), -1);

-- ============================================================
-- 清理段（回滚用，按需执行）
-- 注意：若目标库在执行本种子之前就有相同主键的权限项（INSERT IGNORE 未覆盖它们），
--       最后一条 DELETE 会把那些存量行一并删掉——回滚前先按 CTIME/CTOR 核对。
-- ============================================================
-- DELETE FROM `T_SYS_ROLE_ELEMENT` WHERE `ROLE_ID` = {role};
-- DELETE FROM `T_SYS_USER_ROLE`  WHERE `ROLE_ID` = {role};
-- DELETE FROM `T_SYS_ROLE`        WHERE `ROLE_ID` = {role} AND `ROLE_NAME` = '系统管理员';
-- DELETE FROM `T_SYS_ELEMENT`     WHERE `ELEMENT_MODULE` = {module} AND `ELEMENT_ID` IN ({ids});
"""


def main():
    rows = parse_menu(MENU_MD)
    if not rows:
        raise SystemExit('no menu rows parsed')

    lines = []
    for r in rows:
        lines.append(u"  (%d, %s, %s, %s, %s, %d, %d, %d, NOW(), 1)" % (
            r[0], sql_str(r[1]), sql_str(r[2]), sql_str(r[3]), sql_str(r[4]), r[5], r[6], VMS_MODULE))
    text = HEAD + u',\n'.join(lines) + TAIL.format(
        role=ADMIN_ROLE_ID, bind=ADMIN_BIND_ID, offset=ROLE_ELEMENT_OFFSET,
        module=VMS_MODULE, n=len(rows), ids=','.join(str(r[0]) for r in rows))

    with io.open(OUT, 'w', encoding='utf-8', newline='\n') as fh:
        fh.write(text)
    print('rows=%d -> %s' % (len(rows), os.path.relpath(OUT, ROOT)))


if __name__ == '__main__':
    main()
