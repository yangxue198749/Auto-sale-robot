-- ============================================================
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
  (1000000, '运营管理', '/toloadMenu?code=operate-manage', 'operate-manage', 'module', 1, 1, 0, NOW(), 1),
  (1010000, '工作台', NULL, NULL, 'little-module', 1000000, 1, 0, NOW(), 1),
  (1010100, '个人工作台', '/workbench/list', 'personal-workbench', 'menu', 1010000, 1, 0, NOW(), 1),
  (1010101, '今日配送工单', '/workbench/deliveryCount', 'delivery-task-count-today', 'button', 1010100, 1, 0, NOW(), 1),
  (1010102, '今日维护工单', '/workbench/repairCount', 'repair-task-count-today', 'button', 1010100, 2, 0, NOW(), 1),
  (1010103, '今日投放工单', '/workbench/deployCount', 'deploy-task-count-today', 'button', 1010100, 3, 0, NOW(), 1),
  (1010104, '今日撤机工单', '/workbench/withdrawCount', 'withdraw-task-count-today', 'button', 1010100, 4, 0, NOW(), 1),
  (1010105, '今日新增点位', '/workbench/newNodeCount', 'add-node-count-today', 'button', 1010100, 5, 0, NOW(), 1),
  (1010106, '今日新增设备', '/workbench/newVmCount', 'add-vm-count-today', 'button', 1010100, 6, 0, NOW(), 1),
  (1010107, '今日在促销设备', '/workbench/eventVmCount', 'event-vm-count-today', 'button', 1010100, 7, 0, NOW(), 1),
  (1010108, '今日缺货设备', '/workbench/oosVmCount', 'out-stock-vm-count-today', 'button', 1010100, 8, 0, NOW(), 1),
  (1010109, '今日销量', '/workbench/salesVolume', 'order-count-today', 'button', 1010100, 9, 0, NOW(), 1),
  (1010110, '今日销售额', '/workbench/salesAmount', 'sales-volume-today', 'button', 1010100, 10, 0, NOW(), 1),
  (1020000, '工单管理', NULL, NULL, 'little-module', 1000000, 2, 0, NOW(), 1),
  (1020100, '配送工单', '/deliveryTask/list', 'delivery-task', 'menu', 1020000, 1, 0, NOW(), 1),
  (1020101, '新增', '/deliveryTask/addTaskDialog', 'delivery-task-add', 'button', 1020100, 1, 0, NOW(), 1),
  (1020102, '修改', '/deliveryTask/editTaskDialog', 'delivery-task-edit', 'button', 1020100, 2, 0, NOW(), 1),
  (1020103, '取消', '/deliveryTask/cancelTask', 'delivery-task-cancel', 'button', 1020100, 3, 0, NOW(), 1),
  (1020200, '维护工单', '/repairTask/list', 'repair-task', 'menu', 1020000, 3, 0, NOW(), 1),
  (1020201, '新增', '/repairTask/addTaskDialog', 'repair-task-add', 'button', 1020200, 1, 0, NOW(), 1),
  (1020202, '修改', '/repairTask/editTaskDialog', 'repair-task-edit', 'button', 1020200, 2, 0, NOW(), 1),
  (1020203, '取消', '/repairTask/cancelTask', 'repair-task-cancel', 'button', 1020200, 3, 0, NOW(), 1),
  (1020300, '投放工单', '/deployTask/list', 'deploy-task', 'menu', 1020000, 4, 0, NOW(), 1),
  (1020301, '新增', '/deployTask/addTaskDialog', 'deploy-task-add', 'button', 1020300, 1, 0, NOW(), 1),
  (1020302, '修改', '/deployTask/editTaskDialog', 'deploy-task-edit', 'button', 1020300, 2, 0, NOW(), 1),
  (1020303, '取消', '/deployTask/cancelTask', 'deploy-task-cancel', 'button', 1020300, 3, 0, NOW(), 1),
  (1020304, '发布', '/deployTask/publishTaskDialog', 'deploy-task-publish', 'button', 1020300, 4, 0, NOW(), 1),
  (1020400, '撤机工单', '/withdrawTask/list', 'withdraw-task', 'menu', 1020000, 5, 0, NOW(), 1),
  (1020401, '新建', '/withdrawTask/addTaskDialog', 'withdraw-task-add', 'button', 1020400, 1, 0, NOW(), 1),
  (1020402, '修改', '/withdrawTask/editTaskDialog', 'withdraw-task-edit', 'button', 1020400, 2, 0, NOW(), 1),
  (1020403, '取消', '/withdrawTask/cancelTask', 'withdraw-task-cancel', 'button', 1020400, 3, 0, NOW(), 1),
  (1020500, '工单查询', '/searchTask/list', 'search-task', 'menu', 1020000, 6, 0, NOW(), 1),
  (1030000, '设备管理', NULL, NULL, 'little-module', 1000000, 3, 0, NOW(), 1),
  (1030100, '售货机管理', '/mtVm/list', 'vm-manage', 'menu', 1030000, 1, 0, NOW(), 1),
  (1030101, '新建', '/mtVm/addMtVmDialog', 'vm-add', 'button', 1030100, 1, 0, NOW(), 1),
  (1030102, '修改', '/mtVm/editMtVmDialog', 'vm-edit', 'button', 1030100, 2, 0, NOW(), 1),
  (1030103, '货道设置', '/mtVmChannel/list', 'vm-set-channel', 'button', 1030100, 3, 0, NOW(), 1),
  (1030104, '闪购键设置', '/mtVmShortcuts/list', 'vm-set-shortcuts', 'button', 1030100, 4, 0, NOW(), 1),
  (1030105, '走马灯设置', '/mtVmText/list', 'vm-set-text', 'button', 1030100, 5, 0, NOW(), 1),
  (1030106, '节能设置', '/mtVmEnergySave/list', 'vm-set-energy', 'button', 1030100, 6, 0, NOW(), 1),
  (1030107, '价格设置', '/mtVmPrice/list', 'vm-set-price', 'button', 1030100, 7, 0, NOW(), 1),
  (1030108, '查看货道', '/mtVm/showChannelDialog', 'vm-show-channel', 'button', 1030100, 8, 0, NOW(), 1),
  (1030109, '查看列表页', '/mtVm/showSkuSortDialog', 'vm-show-sort', 'button', 1030100, 9, 0, NOW(), 1),
  (1030110, '查看价格', '/mtVm/showMtSkuPriceDialog', 'vm-show-price', 'button', 1030100, 10, 0, NOW(), 1),
  (1030111, '查看策略', '/mtVm/showMtStrategyDialog', 'vm-show-strategy', 'button', 1030100, 11, 0, NOW(), 1),
  (1030112, '销售控制', '/mtVm/changeSale', 'vm-sale-console', 'button', 1030100, 12, 0, NOW(), 1),
  (1030113, '皮肤设置', '/mtVm/mtVmTheme', 'vm-set-theme', 'button', 1030100, 13, 0, NOW(), 1),
  (1040000, '设备策略管理', NULL, NULL, 'little-module', 1000000, 4, 0, NOW(), 1),
  (1040100, '货道策略', '/stVmChannel/list', 'st-vm-channel', 'menu', 1040000, 1, 0, NOW(), 1),
  (1040101, '新增', '/stVmChannel/addStVmChannelDialog', 'st-vm-channel-add', 'button', 1040100, 1, 0, NOW(), 1),
  (1040102, '修改', '/stVmChannel/editStVmChannelDialog', 'st-vm-channel-edit', 'button', 1040100, 2, 0, NOW(), 1),
  (1040103, '删除', '/stVmChannel/deleteStrategy', 'st-vm-channel-delete', 'button', 1040100, 3, 0, NOW(), 1),
  (1040200, '闪购键策略', '/stShortcuts/list', 'st-shortcuts', 'menu', 1040000, 2, 0, NOW(), 1),
  (1040201, '新增', '/stShortcuts/addPage1', 'st-shortcuts-add', 'button', 1040200, 1, 0, NOW(), 1),
  (1040202, '修改', '/stShortcuts/editPage1', 'st-shortcuts-edit', 'button', 1040200, 2, 0, NOW(), 1),
  (1040203, '删除', '/stShortcuts/deleteShortcuts', 'st-shortcuts-delete', 'button', 1040200, 3, 0, NOW(), 1),
  (1040300, '走马灯策略', '/stVmText/list', 'st-vm-text', 'menu', 1040000, 3, 0, NOW(), 1),
  (1040301, '新增', '/stVmText/addStVmTextDialog', 'st-vm-text-add', 'button', 1040300, 1, 0, NOW(), 1),
  (1040302, '修改', '/stVmText/editStVmTextDialog', 'st-vm-text-edit', 'button', 1040300, 2, 0, NOW(), 1),
  (1040303, '删除', '/stVmText/submitDeleteStVmText', 'st-vm-text-delete', 'button', 1040300, 3, 0, NOW(), 1),
  (1040400, '节能策略', '/stVmEnergySave/list', 'st-vm-energy', 'menu', 1040000, 4, 0, NOW(), 1),
  (1040401, '新增', '/stVmEnergySave/addStVmEnergyDialog', 'st-vm-energy-add', 'button', 1040400, 1, 0, NOW(), 1),
  (1040402, '修改', '/stVmEnergySave/editStVmEnergySaveDialog', 'st-vm-energy-edit', 'button', 1040400, 2, 0, NOW(), 1),
  (1040403, '删除', '/stVmEnergySave/deleteStVmStrategy', 'st-vm-energy-delete', 'button', 1040400, 3, 0, NOW(), 1),
  (1040500, '皮肤策略', '/theme/list', 'st-theme-list', 'menu', 1040000, 5, 0, NOW(), 1),
  (1040501, '新增', '/theme/addTheme', 'st-vm-theme-add', 'button', 1040500, 1, 0, NOW(), 1),
  (1040502, '删除', '/theme/deleteTheme', 'st-vm-theme-delete', 'button', 1040500, 2, 0, NOW(), 1),
  (1050000, '价格策略管理', NULL, NULL, 'little-module', 1000000, 5, 0, NOW(), 1),
  (1050100, '价格策略', '/stSkuPrice/list', 'stSkuPrice', 'menu', 1050000, 1, 0, NOW(), 1),
  (1050101, '新增', '/stSkuPrice/add', 'stSkuPrice-add', 'button', 1050100, 1, 0, NOW(), 1),
  (1050102, '修改', '/stSkuPrice/update', 'stSkuPrice-update', 'button', 1050100, 2, 0, NOW(), 1),
  (1050103, '删除', '/stSkuPrice/delete', 'stSkuPrice-delete', 'button', 1050100, 3, 0, NOW(), 1),
  (1050200, '商品促销策略', '/stSkuEvent/list', 'stSkuEvent', 'menu', 1050000, 2, 0, NOW(), 1),
  (1050201, '新增', '/stSkuEvent/add', 'stSkuEvent-add', 'button', 1050200, 1, 0, NOW(), 1),
  (1050202, '修改', '/stSkuEvent/update', 'stSkuEvent-update', 'button', 1050200, 2, 0, NOW(), 1),
  (1050203, '删除', '/stSkuEvent/delete', 'stSkuEvent-delete', 'button', 1050200, 3, 0, NOW(), 1),
  (1050300, '整机促销策略', '/stVmEvent/list', 'stVmEvent', 'menu', 1050000, 3, 0, NOW(), 1),
  (1050301, '新增', '/stVmEvent/add', 'stVmEvent-add', 'button', 1050300, 1, 0, NOW(), 1),
  (1050302, '修改', '/stVmEvent/update', 'stVmEvent-update', 'button', 1050300, 2, 0, NOW(), 1),
  (1050303, '删除', '/stVmEvent/delete', 'stVmEvent-delete', 'button', 1050300, 3, 0, NOW(), 1),
  (1050400, '应用促销策略', '/mtVmEvent/list', 'mtVmEvent', 'menu', 1050000, 4, 0, NOW(), 1),
  (1050401, '应用商品策略', '/mtVmEvent/add/sku', 'mtVmEvent-add-sku', 'button', 1050400, 1, 0, NOW(), 1),
  (1050402, '应用整机策略', '/mtVmEvent/add/vm', 'mtVmEvent-add-vm', 'button', 1050400, 2, 0, NOW(), 1),
  (1050403, '取消', '/mtVmEvent/delete', 'mtVmEvent-delete', 'button', 1050400, 3, 0, NOW(), 1),
  (1060000, '点位管理', NULL, NULL, 'little-module', 1000000, 6, 0, NOW(), 1),
  (1060100, '点位信息', '/mtNode/list', 'mtNode', 'menu', 1060000, 1, 0, NOW(), 1),
  (1060101, '新增', '/mtNode/add', 'mtNode-add', 'button', 1060100, 1, 0, NOW(), 1),
  (1060102, '修改', '/mtNode/update', 'mtNode-update', 'button', 1060100, 2, 0, NOW(), 1),
  (1060103, '删除', '/mtNode/delete', 'mtNode-delete', 'button', 1060100, 3, 0, NOW(), 1),
  (1060104, '配置配送员', '/mtNode/deliveryList', 'mtNode-deliveryList', 'button', 1060100, 4, 0, NOW(), 1),
  (1060105, '配置维修员', '/mtNode/repairList', 'mtNode-repairList', 'button', 1060100, 5, 0, NOW(), 1),
  (1070000, '客户管理', NULL, NULL, 'little-module', 1000000, 7, 0, NOW(), 1),
  (1070100, '客户信息', '/customer/home', 'customer-home', 'menu', 1070000, 1, 0, NOW(), 1),
  (1070101, '查询', '/customer/list', 'customer-list', 'button', 1070100, 1, 0, NOW(), 1),
  (1070102, '新增', '/customer/createPage', 'customer-createPage', 'button', 1070100, 2, 0, NOW(), 1),
  (1070103, '修改', '/customer/updatePage', 'customer-updatePage', 'button', 1070100, 3, 0, NOW(), 1),
  (1070104, '删除', '/customer/delete', 'customer-delete', 'button', 1070100, 4, 0, NOW(), 1),
  (1070200, '合伙人信息', '/partner/home', 'partner-home', 'menu', 1070000, 2, 0, NOW(), 1),
  (1070201, '查询', '/partner/list', 'partner-list', 'button', 1070200, 1, 0, NOW(), 1),
  (1070202, '新增', '/partner/createPage', 'partner-createPage', 'button', 1070200, 2, 0, NOW(), 1),
  (1070203, '修改', '/partner/updatePage', 'partner-updatePage', 'button', 1070200, 3, 0, NOW(), 1),
  (1070204, '删除', '/partner/delete', 'partner-delete', 'button', 1070200, 4, 0, NOW(), 1),
  (2000000, '数据管理', '/toloadMenu?code=data-manage', 'data-manage', 'module', 1, 2, 0, NOW(), 1),
  (2010000, '硬件数据', NULL, NULL, 'little-module', 2000000, 1, 0, NOW(), 1),
  (2010100, '售货机型号', '/vmModel/home', 'vmModel-home', 'menu', 2010000, 1, 0, NOW(), 1),
  (2010101, '新增', '/vmModel/createPage', 'vmModel-createPage', 'button', 2010100, 1, 0, NOW(), 1),
  (2010102, '修改型号', '/vmModel/updatePage', 'vmModel-updatePage', 'button', 2010100, 2, 0, NOW(), 1),
  (2010103, '修改货道', '/vmModelChannel/updatePage', 'vmModel-update-channel', 'button', 2010100, 3, 0, NOW(), 1),
  (2010104, '删除', '/vmModel/delete', 'vmModel-delete', 'button', 2010100, 4, 0, NOW(), 1),
  (2010200, '售货机MAC地址', '/uniqueCode/list', 'uniqueCode', 'menu', 2010000, 1, 0, NOW(), 1),
  (2010201, '解除绑定', '/uniqueCode/unbind', 'uniqueCode-unbind', 'button', 2010200, 1, 0, NOW(), 1),
  (2020000, '商品数据', NULL, NULL, 'little-module', 2000000, 2, 0, NOW(), 1),
  (2020100, '商品类型', '/skuType/home', 'skuType-home', 'menu', 2020000, 1, 0, NOW(), 1),
  (2020101, '新增', '/skuType/create', 'skuType-create', 'button', 2020100, 1, 0, NOW(), 1),
  (2020102, '修改', '/skuType/update', 'skuType-update', 'button', 2020100, 2, 0, NOW(), 1),
  (2020103, '删除', '/skuType/delete', 'skuType-delete', 'button', 2020100, 3, 0, NOW(), 1),
  (2020200, '商品类型详情', '/skuType/listType', 'skuType-list', 'button', 2020100, 4, 0, NOW(), 1),
  (2020201, '新增', '/skuType/createType', 'skuType-create', 'button', 2020200, 5, 0, NOW(), 1),
  (2020202, '修改', '/skuType/updateType', 'skuType-update', 'button', 2020200, 6, 0, NOW(), 1),
  (2020203, '删除', '/skuType/deleteType', 'skuType-delete', 'button', 2020200, 7, 0, NOW(), 1),
  (2020300, '商品信息', '/sku/home', 'sku-home', 'menu', 2020000, 2, 0, NOW(), 1),
  (2020301, '新增', '/sku/createPage', 'sku-createPage', 'button', 2020300, 1, 0, NOW(), 1),
  (2020302, '修改', '/sku/updatePage', 'sku-updatePage', 'button', 2020300, 2, 0, NOW(), 1),
  (2020303, '编辑', '/sku/changePage', 'sku-changePage', 'button', 2020300, 3, 0, NOW(), 1),
  (2030000, '场景类型', NULL, NULL, 'little-module', 2000000, 3, 0, NOW(), 1),
  (2030100, '场景信息', '/businessDistrict/home', 'businessDistrict-home', 'menu', 2030000, 1, 0, NOW(), 1),
  (2030101, '新增', '/businessDistrict/createPage', 'businessDistrict-createPage', 'button', 2030100, 1, 0, NOW(), 1),
  (2030102, '删除', '/businessDistrict/delete', 'businessDistrict-delete', 'button', 2030100, 2, 0, NOW(), 1),
  (2030103, '修改', '/businessDistrict/updatePage', 'businessDistrict-updatePage', 'button', 2030100, 3, 0, NOW(), 1),
  (2040000, '仓库数据', NULL, NULL, 'little-module', 2000000, 4, 0, NOW(), 1),
  (2040100, '仓库信息', '/warehouse/home', 'warehouse-uniteArea', 'menu', 2040000, 1, 0, NOW(), 1),
  (2040101, '关联区域', '/warehouse/uniteAreaPage', 'warehouse-uniteAreaPage', 'button', 2040100, 1, 0, NOW(), 1),
  (2050000, 'sim卡数据', NULL, NULL, 'little-module', 2000000, 5, 0, NOW(), 1),
  (2050100, 'sim卡信息', '/sim/list', 'sim-list', 'menu', 2050000, 1, 0, NOW(), 1),
  (2050101, '新增', '/sim/addPage', 'sim-add', 'button', 2050100, 1, 0, NOW(), 1),
  (2050102, '删除', '/sim/deleteSim', 'sim-delete', 'button', 2050100, 2, 0, NOW(), 1),
  (2050103, '修改', '/sim/editPage', 'sim-edit', 'button', 2050100, 3, 0, NOW(), 1),
  (2060000, '基础数据', NULL, NULL, 'little-module', 2000000, 6, 0, NOW(), 1),
  (2060100, '字典信息', '/sysType/home', 'sysType-home', 'menu', 2060000, 1, 0, NOW(), 1),
  (2060101, '新增', '/sysType/createPage', 'sysType-create', 'button', 2060100, 1, 0, NOW(), 1),
  (2060102, '修改', '/sysType/updatePage', 'sysType-update', 'button', 2060100, 2, 0, NOW(), 1),
  (2060103, '删除', '/sysType/delete', 'sysType-delete', 'button', 2060100, 3, 0, NOW(), 1),
  (2060104, '查询字典详情', '/dictionary/list', 'dictionary-list', 'button', 2060100, 4, 0, NOW(), 1),
  (2060105, '新增', '/dictionary/createPage', 'dictionary-create', 'button', 2060100, 5, 0, NOW(), 1),
  (2060106, '修改', '/dictionary/updatePage', 'dictionary-update', 'button', 2060100, 6, 0, NOW(), 1),
  (2060107, '删除', '/dictionary/delete', 'dictionary-delete', 'button', 2060100, 7, 0, NOW(), 1),
  (3000000, '统计报表', '/toloadMenu?code=statistical-analysis', 'statistical-analysis', 'module', 1, 3, 0, NOW(), 1),
  (3010000, '统计报表', NULL, NULL, 'little-module', 3000000, 1, 0, NOW(), 1),
  (3010100, '销售流水', '/order/list', 'order-list', 'menu', 3010000, 1, 0, NOW(), 1),
  (3010101, '退款', '/order/refund', 'order-refund', 'button', 3010100, 1, 0, NOW(), 1),
  (3010102, '导出', '/order/export', 'order-export', 'button', 3010100, 2, 0, NOW(), 1),
  (3010200, '售货机库存', '/vmStock/list', 'vmStock-list', 'menu', 3010000, 2, 0, NOW(), 1),
  (3010300, '售货机状态', '/vmStatus/list', 'vmStatus-list', 'menu', 3010000, 3, 0, NOW(), 1),
  (3010400, '售货机历史状态', '/vmStatusHis/list', 'vmStatusHis-list', 'menu', 3010000, 4, 0, NOW(), 1),
  (4000000, '财务管理', '/toloadMenu?code=finance-manage', 'finance-manage', 'module', 1, 4, 0, NOW(), 1),
  (4010000, '财务统计', NULL, NULL, 'little-module', 4000000, 1, 0, NOW(), 1),
  (4010100, 'B客户转账', NULL, 'giro-mangage', 'little-module', 4000000, 2, 0, NOW(), 1),
  (4010101, 'B客户转账', '/giro/list', 'giro-list', 'menu', 4010100, 1, 0, NOW(), 1),
  (5000000, '系统管理', '/toloadMenu?code=system-manage', 'system-manage', 'module', 1, 5, 0, NOW(), 1),
  (5010000, '系统管理', NULL, NULL, 'little-module', 5000000, 1, 0, NOW(), 1),
  (5010100, '用户管理', '/user/list', 'user-list', 'menu', 5010000, 1, 0, NOW(), 1),
  (5010101, '新增', '/user/addPage', 'user-add', 'button', 5010100, 1, 0, NOW(), 1),
  (5010102, '删除', '/user/deleteUser', 'user-delete', 'button', 5010100, 2, 0, NOW(), 1),
  (5010103, '修改', '/user/editPage', 'user-edit', 'button', 5010100, 3, 0, NOW(), 1),
  (5010104, '重置密码', '/user/resetPw', 'user-resePw', 'button', 5010100, 4, 0, NOW(), 1),
  (5010200, '角色管理', '/role/list', 'role-list', 'menu', 5010000, 2, 0, NOW(), 1),
  (5010400, '账号设置', '/account/home', 'account-home', 'menu', 5010000, 4, 0, NOW(), 1),
  (5010401, '修改用户名', '/account/updateUserNamePage', 'account-updateUserNamePage', 'button', 5010400, 1, 0, NOW(), 1),
  (5010402, '修改手机号', '/account/updateUserPhonePage', 'account-updateUserPhonePage', 'button', 5010400, 2, 0, NOW(), 1),
  (5010403, '修改密码', '/account/updateUserPasswordPage', 'account-updateUserPasswordPage', 'button', 5010400, 3, 0, NOW(), 1),
  (5010500, '公司管理', '/company/home', 'company_home', 'menu', 5010000, 5, 0, NOW(), 1),
  (5010501, '新增', '/company/createPage', 'company_createPage', 'button', 5010500, 1, 0, NOW(), 1),
  (5010502, '修改', '/company/updatePage', 'company_updatePage', 'button', 5010500, 2, 0, NOW(), 1),
  (5010503, '新增机器', '/company/addCapacityPage', 'company_addCapacityPage', 'button', 5010500, 3, 0, NOW(), 1),
  (5010504, '升级公司', '/company/upgrade', 'company_upgrade', 'button', 5010500, 4, 0, NOW(), 1),
  (5010600, '系统日志', '/sysLog/home', 'sysLog-home', 'menu', 5010000, 6, 0, NOW(), 1),
  (5010700, '系统配置', '/settings/list', 'sysSetting-list', 'menu', 5010000, 7, 0, NOW(), 1),
  (5010701, '新增类型', '/settings/type/add', 'sysSetting-type-add', 'button', 5010700, 1, 0, NOW(), 1),
  (5010702, '新增配置', '/settings/setting/add', 'sysSetting-setting-add', 'button', 5010700, 2, 0, NOW(), 1),
  (5010703, '删除配置', '/settings/setting/delete', 'sysSetting-setting-delete', 'button', 5010700, 3, 0, NOW(), 1),
  (5010704, '更新配置', '/settings/setting/update', 'sysSetting-setting-update', 'button', 5010700, 4, 0, NOW(), 1),
  (5010705, '类型数据', '/settings/type/table', 'sysSetting-type-table', 'button', 5010700, 5, 0, NOW(), 1),
  (5010706, '配置数据', '/settings/setting/table', 'sysSetting-setting-table', 'button', 5010700, 6, 0, NOW(), 1),
  (5010707, '保存数据', '/settings/save', 'sysSetting-save', 'button', 5010700, 7, 0, NOW(), 1),
  (5020000, '支付管理', NULL, NULL, 'little-module', 5000000, 2, 0, NOW(), 1),
  (5020100, '支付管理', '/customerAccount/home', 'customerAccount-home', 'menu', 5020000, 1, 0, NOW(), 1),
  (5020101, '修改', '/customerAccount/update', 'customerAccount-update', 'button', 5020100, 1, 0, NOW(), 1);

-- ---------- 2. 系统管理员角色（一级公司管理员：其权限项即"可分配权限全集"） ----------
-- USER_ACCOUNT 无唯一约束：取 USER_ID 最小的 admin，多个同名账号不会让脚本中断
INSERT IGNORE INTO `T_SYS_ROLE`
  (`ROLE_ID`, `ROLE_NAME`, `ROLE_TYPE`, `ROLE_LEVEL`, `COMPANY_ID`, `ROLE_DESC`, `CTIME`, `CTOR`)
VALUES
  (990001, '系统管理员', 1, 1,
   (SELECT `COMPANY_ID` FROM (SELECT `COMPANY_ID` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin' ORDER BY `USER_ID` LIMIT 1) t),
   '一级公司管理员，拥有全部权限项', NOW(), 1);

-- ---------- 3. 管理员角色绑定全部权限项（只补尚未绑定的，界面编辑过绑定后重跑不会产生重复行） ----------
INSERT IGNORE INTO `T_SYS_ROLE_ELEMENT` (`RE_ID`, `ROLE_ID`, `ELEMENT_ID`, `CTIME`, `CTOR`)
SELECT e.`ELEMENT_ID` + 10000000, 990001, e.`ELEMENT_ID`, NOW(), 1
FROM `T_SYS_ELEMENT` e
WHERE e.`ELEMENT_MODULE` = 0
  AND EXISTS (SELECT 1 FROM `T_SYS_ROLE` r WHERE r.`ROLE_ID` = 990001 AND r.`ROLE_NAME` = '系统管理员')
  AND NOT EXISTS (SELECT 1 FROM `T_SYS_ROLE_ELEMENT` x WHERE x.`ROLE_ID` = 990001 AND x.`ELEMENT_ID` = e.`ELEMENT_ID`);

-- ---------- 4. 种子账号 admin（USER_ID 最小的同名账号）绑定管理员角色 ----------
INSERT IGNORE INTO `T_SYS_USER_ROLE` (`UR_ID`, `USER_ID`, `ROLE_ID`, `CTIME`, `CTOR`)
SELECT 990001, u.`USER_ID`, 990001, NOW(), 1
FROM `T_SYS_USER` u
WHERE u.`USER_ACCOUNT` = 'admin'
  AND u.`USER_ID` = (SELECT MIN(`USER_ID`) FROM (SELECT `USER_ID` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin') t2)
  AND EXISTS (SELECT 1 FROM `T_SYS_ROLE` r WHERE r.`ROLE_ID` = 990001 AND r.`ROLE_NAME` = '系统管理员');

-- ---------- 5. 自检 ----------
-- 预期：elements=187 / role=1 / bound=187 / admin_bind=1
-- 上线关口（不为预期值时必须先处理，见 DEPLOY_RUNBOOK「权限种子前置检查」）：
--   admin_accounts = 1     同名 admin 多于一个时只绑定了 USER_ID 最小的那个
--   level1_roles  = 1      存在旧的一级管理员角色时角色管理页的"可分配权限"取到的是旧角色，须处置
SELECT 'elements' AS item, COUNT(*) AS n FROM `T_SYS_ELEMENT` WHERE `ELEMENT_MODULE` = 0
UNION ALL SELECT 'role', COUNT(*) FROM `T_SYS_ROLE` WHERE `ROLE_ID` = 990001 AND `ROLE_NAME` = '系统管理员'
UNION ALL SELECT 'bound', COUNT(DISTINCT `ELEMENT_ID`) FROM `T_SYS_ROLE_ELEMENT` WHERE `ROLE_ID` = 990001
UNION ALL SELECT 'admin_bind', COUNT(*) FROM `T_SYS_USER_ROLE` ur JOIN `T_SYS_USER` u ON u.`USER_ID` = ur.`USER_ID`
  WHERE u.`USER_ACCOUNT` = 'admin' AND ur.`ROLE_ID` = 990001
UNION ALL SELECT 'admin_accounts', COUNT(*) FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin'
UNION ALL SELECT 'level1_roles', COUNT(*) FROM `T_SYS_ROLE` WHERE `ROLE_LEVEL` = 1
UNION ALL SELECT 'admin_company', IFNULL((SELECT `COMPANY_ID` FROM (SELECT `COMPANY_ID` FROM `T_SYS_USER` WHERE `USER_ACCOUNT` = 'admin' ORDER BY `USER_ID` LIMIT 1) t), -1);

-- ============================================================
-- 清理段（回滚用，按需执行）
-- 注意：若目标库在执行本种子之前就有相同主键的权限项（INSERT IGNORE 未覆盖它们），
--       最后一条 DELETE 会把那些存量行一并删掉——回滚前先按 CTIME/CTOR 核对。
-- ============================================================
-- DELETE FROM `T_SYS_ROLE_ELEMENT` WHERE `ROLE_ID` = 990001;
-- DELETE FROM `T_SYS_USER_ROLE`  WHERE `ROLE_ID` = 990001;
-- DELETE FROM `T_SYS_ROLE`        WHERE `ROLE_ID` = 990001 AND `ROLE_NAME` = '系统管理员';
-- DELETE FROM `T_SYS_ELEMENT`     WHERE `ELEMENT_MODULE` = 0 AND `ELEMENT_ID` IN (1000000,1010000,1010100,1010101,1010102,1010103,1010104,1010105,1010106,1010107,1010108,1010109,1010110,1020000,1020100,1020101,1020102,1020103,1020200,1020201,1020202,1020203,1020300,1020301,1020302,1020303,1020304,1020400,1020401,1020402,1020403,1020500,1030000,1030100,1030101,1030102,1030103,1030104,1030105,1030106,1030107,1030108,1030109,1030110,1030111,1030112,1030113,1040000,1040100,1040101,1040102,1040103,1040200,1040201,1040202,1040203,1040300,1040301,1040302,1040303,1040400,1040401,1040402,1040403,1040500,1040501,1040502,1050000,1050100,1050101,1050102,1050103,1050200,1050201,1050202,1050203,1050300,1050301,1050302,1050303,1050400,1050401,1050402,1050403,1060000,1060100,1060101,1060102,1060103,1060104,1060105,1070000,1070100,1070101,1070102,1070103,1070104,1070200,1070201,1070202,1070203,1070204,2000000,2010000,2010100,2010101,2010102,2010103,2010104,2010200,2010201,2020000,2020100,2020101,2020102,2020103,2020200,2020201,2020202,2020203,2020300,2020301,2020302,2020303,2030000,2030100,2030101,2030102,2030103,2040000,2040100,2040101,2050000,2050100,2050101,2050102,2050103,2060000,2060100,2060101,2060102,2060103,2060104,2060105,2060106,2060107,3000000,3010000,3010100,3010101,3010102,3010200,3010300,3010400,4000000,4010000,4010100,4010101,5000000,5010000,5010100,5010101,5010102,5010103,5010104,5010200,5010400,5010401,5010402,5010403,5010500,5010501,5010502,5010503,5010504,5010600,5010700,5010701,5010702,5010703,5010704,5010705,5010706,5010707,5020000,5020100,5020101);
