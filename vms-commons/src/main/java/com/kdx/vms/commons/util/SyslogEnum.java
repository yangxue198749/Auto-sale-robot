package com.kdx.vms.commons.util;

public enum SyslogEnum {
	
	/** 公司 */
	COMPANY_CODE("company_code"),
	
	/** sim卡 */
	SIM_CODE("sim_code"),
    /** user */
    USER_CODE("user_code"),
    /** sim卡 */
    ROLE_CODE("role_code"),
	/** 售货机 */
	MT_VM_CODE("vm_code"),
	/** 价格策略 */
	ST_SKU_PRICE("strategy_code"),
	/** 促销策略 */
	ST_VM_EVENT("strategy_code"),
	/** 策略 */
	ST_VM_STRATEGY("strategy_code"),
	/** 应用促销策略 */
	MT_VM_EVENT_CODE("vm_code"),
    /** 皮肤策略*/
    MT_THEME("theme"),
    /** 升级包管理*/
    MT_UPGRADE_PACKAGE("upgrade_package"),
	/**
	 * 点位
	 */
	NODE_CODE("node_code"),
	/**
	 * 工单管理
	 */
	WORKORDER_CODE("workOrder_code"),
	
	/** B模式 */
	B_MODE("b_mode")
	;
	
	
	
	
	private String type_code;
	
	
	private SyslogEnum(String type_code) {
		this.type_code = type_code;
	}

	public String getType_code() {
		return type_code;
	}

	public void setType_code(String type_code) {
		this.type_code = type_code;
	}
}
