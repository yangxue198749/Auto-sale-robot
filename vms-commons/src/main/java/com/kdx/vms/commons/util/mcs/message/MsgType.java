package com.kdx.vms.commons.util.mcs.message;

/**
 *
 * Created by liyang on 2016/11/9.
 */
public enum MsgType {

    CHANNELCFG("channelCfg"),
    SKUCFG("skuCfg"),
    SPCFG("spCfg"),
    REPEATTEXT("repeatText"),
    SKUPRICE("skuPrice"),
    MTENERGY("mtEnergy"),
    MTENERGYCOMBO("mtEnergyCombo"),
    OUTSKU("outSku"),
 
    REGISTER("register"),
    HEART("heart"),
    OPENDOORPWD("openDoorPwd"),
    /**
     * 货道信息请求
     */
    CHANNELCFGSREQ("channelCfgsReq"),
    /**
     * 商品信息请求
     */
    SKUCFGREQ("skuCfgReq"),
    /**
     * 闪购键请求
     */
    SPCFGREQ("spCfgReq"),
    /**
     * 走马灯请求
     */
    REPEATTEXTREQ("repeatTextReq"),
    
    /**
     * 节能信息请求
     */
    MTENERGYREQ("mtEnergyReq"),
    /**
     * 出货结果
     */
    OUTRESULT("outResult"),
    /**
     * 开门密码请求
     */
    OPENDOORPWDREQ("openDoorPwdReq"),
    /**
     * 售货机状态上报
     */
    VMSTATUS("vmStatus"),
    /**
     * sim卡串上报
     */
    SIMCODE("simCode"),
    /**
     * 下发innercode
     */
    INNERCODEREQ("innerCodeReq"),
    /**
     * 维修工单完成通知
     */
    FIXVMTASK("fixVmTask"),
    /**
     * 维修工单完成通知
     */
    DOORSTATUS("doorStatus"),

    /**
     * 货道配置
     */
    CHANNELPOLICY("channelPolicy"),
    /** 客户信息 */
    BCUSTOMER("bCustomer"),
    /** 基本信息 */
    AREA("area"),
    /** 售货机基础信息 */
    VM_BASE_INFO("vmBaseInfo"),

    /**
     * 货道库存
     */
    SUPPLYPOLICY("supplyPolicy"),

    /**
     * 售货机配置信息
     */
    VMDEVICEINFO("vmDeviceInfo"),
    /**
     * 打开全部格子门
     */
    OPENALLBOXREQ("openAllBoxReq"),
    /**
     * 货架
     */
    SKUORDERPOLICY("skuOrderPolicy"),
    /**
     * 投放
     */
    DEPLOYTASK("deployVmTask"),
    /**
     * 撤机
     */
    WITHDRAWTASK("withdrawVmTask"),
    /**
     * 快捷入口
     */
    PORTALCFG("portalCfg"),

    /**
     * 商品信息5.0.0a
     */
    SKUINFO("skuInfo");


    private String name;

    private MsgType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
