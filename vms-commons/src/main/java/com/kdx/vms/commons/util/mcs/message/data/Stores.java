package com.kdx.vms.commons.util.mcs.message.data;

/**
 * 商品预定信息
 * @author zhangguodong
 *
 */
public class Stores {

	/**
	 * 预定失效日期
	 */
	private String expire;
	
	/**
	 * 预定编码
	 */
	private String code;

	public String getExpire() {
		return expire;
	}

	public void setExpire(String expire) {
		this.expire = expire;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
