-- 客户/合伙人字典种子：客户类型、客户模式、合伙人类型
-- （customerType/customerMode/partnerType 存的是字典 ID，缺失时旧页面下拉为空）
DELETE FROM t_sys_dictionary WHERE TYPE_INFO_ID IN (902, 903, 904);
DELETE FROM t_sys_type_info WHERE TYPE_INFO_ID IN (902, 903, 904);

INSERT INTO t_sys_type_info (TYPE_INFO_ID, TYPE_CODE, TYPE_NAME, TYPE_DESC) VALUES
  (902, 'customer_type', '客户类型', '客户类型字典'),
  (903, 'customer_mode', '客户模式', '客户模式字典'),
  (904, 'partner_type',  '合伙人类型', '合伙人类型字典');

INSERT INTO t_sys_dictionary (DICTIONARY_ID, DICTIONARY_CODE, DICTIONARY_NAME, DICTIONARY_DESC, DICTIONARY_STATE, TYPE_INFO_ID) VALUES
  (9101, 'customer_enterprise', '企业客户', '企业客户', 1, 902),
  (9102, 'customer_personal',   '个人客户', '个人客户', 1, 902),
  (9103, 'mode_normal', '普通模式', '普通模式', 1, 903),
  (9104, 'mode_b',      'B模式',    'B模式',    1, 903),
  (9105, 'partner_personal', '个人合伙', '个人合伙', 1, 904),
  (9106, 'partner_company',  '企业合伙', '企业合伙', 1, 904);
