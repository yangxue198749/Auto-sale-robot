package com.kdx.vms.commons.util;

public enum SysTypeinfoEnum {
	
	/** 系统日志 */
	SYS_LOG("sys_log"),
	
	/** 货道类型 */
	CHANNEL_TYPE("channel_type"),
	
	/** 公司缩写 */
	COMPANY_INIT("company_init"),
	
	/** 售货机外观 */
	MACHINE_VIEW("machine_view"),
	
	/** 温控模式 */
	TEMPERATURE_HANDLER_MODE("temperature_handler_mode"),
	
	/** 屏种类 */
	SCREEN_TYPE("screen_type"),
	
	/** 屏尺寸 */
	SCREEN_SIZE("screen_size"),
	
	/** 出货结构 */
	VEND_OUT_ARCHITECTURE("vend_out_architecture"),
	
	/** 制冷设备 */
	COOLING_HDID("cooling_hdid"),
	
	/** 加热设备 */
	HITTING_HDID("hitting_hdid"),
	
	/** 云台设备 */
	ELEVATOR_PLATFORM_HDID("elevator_platform_hdid"),
	
	/** 出货口 */
	VEND_OUT_TYPE("vend_out_type"),
	
	/** 外接卡 */
	RFID_CARD("rfid_card"),
	
	/** 合伙人类型 */
	PARTNER_TYPE("partner_type"),
	
	/** 客户类型 */
	CUSTOMER_TYPE("customer_type"),
	
	/** 客户模式 */
	CUSTOMER_MODE("customer_mode"),
	
	/** 转账账号类型 */
	GIRO_ACCOUNT_TYPE("giro_account_type"),

	/** 机型分类 */
	MODEL_TYPE("model_type");

	
	
	

	private String type_code;
	
	
	private SysTypeinfoEnum(String type_code) {
		this.type_code = type_code;
	}

	public String getType_code() {
		return type_code;
	}

	public void setType_code(String type_code) {
		this.type_code = type_code;
	}
	
	
}
