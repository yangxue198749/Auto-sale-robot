package com.kdx.vms.commons.util;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * 系统返回码
 * 0  为正常
 * <0 为系统内部错误编码
 * >0 为业务逻辑错误编码
 * Created by liyang on 16-12-1.
 */
public enum SystemCode {

    SUCCESS(0, "success",  "操作成功"),

    SIM_PHONE_EXITS(121, "error", "SIM卡号码已存在。"),

    COMPANY_DONT_CREATE(131, "error", "失败:三级公司不能创建子公司。"),
    COMPANY_DONT_UPGRADE(132, "error", "失败:二级公司不能升级。"),
    COMPANY_DONT_DELETE(133, "error", "失败:不能删除本公司"),
    COMPANY_DONT_MAXVMCAPACITY(134, "error", "失败:售货机数量不能超过当前未使用售货机数量。"),

    USER_IS_EXIST(141, "error", "失败:账号已存在。"),

    ROLE_CANNOT_DELETE(152, "error", "失败:系统预设和公共角色不能删除。"),

    STRATEGY_NOT_SHORTCUTS(241, "error", "策略主表没有关联闪购键策略。"),

    CONTEXT_IS_NULL(-1, "error", "失败:内部错误。"),
    CONTEXT_COMMIT_FAIL(-2, "error", "失败:提交异常。"),
    OBJECT_IS_NULL(-3, "error", "失败:数据结构错误。"),
    PARAMETER_ERROR(-4, "error", "失败:参数错误。"),
    FAIL(-100, "error", "失败。"),
    BTN_DISABLE(0, "禁用", "操作成功"),
    BTN_ENABLE(0, "启用", "操作成功")
    ;

    private int    code;
    private String type;
    private String message;

    private SystemCode(int code, String type, String message) {
        this.code    = code;
        this.type    = type;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("code"   , code);
        map.put("type"   , type);
        map.put("message", message);
        return map;
    }

}
