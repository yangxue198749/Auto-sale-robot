-- 系统日志字典种子：saveSysLog(SyslogEnum) 依赖 T_SYS_DICTIONARY 匹配 type_code，
-- 缺失时日志静默不写（历史种子库未包含）。手工 ID 取 900 段，避开 AUTO_PK(10000+)。
DELETE FROM t_sys_dictionary WHERE TYPE_INFO_ID = 901;
DELETE FROM t_sys_type_info WHERE TYPE_INFO_ID = 901;

INSERT INTO t_sys_type_info (TYPE_INFO_ID, TYPE_CODE, TYPE_NAME, TYPE_DESC)
VALUES (901, 'sys_log', '系统日志', '操作日志模块');

INSERT INTO t_sys_dictionary (DICTIONARY_ID, DICTIONARY_CODE, DICTIONARY_NAME, DICTIONARY_DESC, DICTIONARY_STATE, TYPE_INFO_ID)
VALUES
  (9011, 'company_code', '公司管理', '公司管理操作', 1, 901),
  (9012, 'sim_code',     'SIM卡',    'SIM卡操作',    1, 901),
  (9013, 'user_code',    '用户管理', '用户管理操作', 1, 901),
  (9014, 'role_code',    '角色管理', '角色管理操作', 1, 901),
  (9015, 'vm_code',      '售货机',   '售货机/应用促销操作', 1, 901),
  (9016, 'strategy_code','策略管理', '价格/促销策略操作', 1, 901),
  (9017, 'theme',        '皮肤策略', '皮肤策略操作', 1, 901),
  (9018, 'upgrade_package','升级包', '升级包操作',   1, 901),
  (9019, 'node_code',    '点位管理', '点位管理操作', 1, 901),
  (9020, 'workOrder_code','工单管理','工单操作',     1, 901),
  (9021, 'b_mode',       'B模式',    'B模式操作',    1, 901);
