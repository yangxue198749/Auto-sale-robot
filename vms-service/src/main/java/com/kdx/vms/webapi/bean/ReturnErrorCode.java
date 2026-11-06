package com.kdx.vms.webapi.bean;

/**
 * Created by yangshuxi on 2016/12/30.
 */
public enum ReturnErrorCode {
    NO_REQUEST_JSON(101,"未收到推送消息",ReturnJsonObject.ERROR),
    REQUEST_IMPERFECT(102,"推送消息消息头不完整",ReturnJsonObject.ERROR),
    ACCESS_DENIED(103,"秘钥非法",ReturnJsonObject.ERROR),
    DATA_INVALIDATE(104,"数据格式不合法",ReturnJsonObject.ERROR),
    OPERATION_INVALIDATE(105,"操作类型不合法",ReturnJsonObject.ERROR),
    DELETE_INVALIDATE(106,"不支持删除操作",ReturnJsonObject.IGNORE),
    NO_SKU_CODE(107,"缺少skuCode",ReturnJsonObject.ERROR),
    NO_SKU_TYPE_ID(108,"缺少skuTypeId",ReturnJsonObject.ERROR),
    NO_WAREHOUSE_CODE(109,"缺少warehouseCode",ReturnJsonObject.ERROR),
    NO_SKU_TYPE(110,"无法找到对应的商品类型",ReturnJsonObject.ERROR),
    NO_TYPE_CODE(111,"缺少classCode",ReturnJsonObject.ERROR),
    INVALIDATE_TYPE_CODE(112,"classCode长度不合法",ReturnJsonObject.ERROR),
    INVALIDATE_TYPE_CODE_ONE_LEVEL(113,"传入的不是快消品类型",ReturnJsonObject.IGNORE),
    NO_SKU_TYPE_PARENT(114,"无法找到对应父级的商品类型",ReturnJsonObject.ERROR),
    INVALIDATE_SKU_CODE(115,"传入的不是快消品",ReturnJsonObject.IGNORE),
    SUCCESS(0,"成功",ReturnJsonObject.SUCCESS);
    private Integer errorCode;
    private String errorMessage;
    private String type;

    private ReturnErrorCode(Integer errorCode,String errorMessage,String type) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.type = type;
    }

    public Integer getErrorCode() {
        return errorCode;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public String getType() {
        return type;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

}
