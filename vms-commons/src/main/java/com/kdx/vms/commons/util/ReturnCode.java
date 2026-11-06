package com.kdx.vms.commons.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/1/10.
 */
public enum ReturnCode {
    SUCCESS(0, "success",  "操作成功"),


    CONTEXT_IS_NULL(-1, "error", "失败:内部错误。"),
    CONTEXT_COMMIT_FAIL(101, "error", "失败:提交异常。"),
    DELETE_DENY(-99, "error", "失败:有关联数据存在，禁止删除。"),
    OBJECT_IS_NULL(-3, "error", "失败:数据结构错误。"),
    PARAMETER_ERROR(102, "error", "失败:提交数据不完整。"),
    MCS_FAIL(-5, "error", "保存成功"),
    DATA_OVER(103, "error", "失败:百分比打折不可大于100。"),
    REFUND_DENIED(104, "error", "失败:该笔交易不可退款"),
    REFUND_SUCCESS(105, "success", "操作成功，请稍后刷新页面查询退款结果"),
    IMAGE_DENY(206, "error", "上传失败，该文件不是图片文件"),
    IMAGE_SUF(207, "error", "上传失败，只能上传png后缀文件"),
    IMAGE_SUF_NEW(2071, "error", "上传失败，只能上传Png,Jpg后缀文件"),
    IMAGE_SIZE(208, "error", "上传失败，上传文件不能大于200KB"),
    IMAGE_SIZE_300KB(209, "error", "上传失败，上传文件不能大于200KB"),
    ZIP_DENY(306, "error", "上传失败，皮肤包大小不能超过10MB"),
    VIEW_DENY(307, "error", "上传失败，预览图大小不能超过3MB"),
    NO_SELECT_VM(107, "error", "请选择一台或多台售货机。"),
    NO_SELECT_STRATEGY(108, "error", "请选择一条策略。"),
    SAME_SETTING_KEY_EXIST(109, "error", "系统配置key值冲突。"),
    SETTING_MODIFY_DENY(110, "error", "禁止修改的系统配置"),
    FAIL(-100, "error", "失败。")
    ;
    private int    code;
    private String type;
    private String message;

    private ReturnCode(int code, String type, String message) {
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
