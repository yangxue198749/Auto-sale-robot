package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TOrderDetailVendout;
import org.apache.cayenne.Cayenne;

import java.util.Date;

public class TOrderDetailVendout extends _TOrderDetailVendout {
    /**
     * 出货结果:未知，初始值
     */
    public static final int OUT_STATUS_UNKNOWN = -100;
    /**
     * 出货结果:成功
     */
    public static final int OUT_STATUS_SUCCESS = 0;
    /**
     * 出货结果:失败 货道售空
     */
    public static final int OUT_STATUS_FAIL_EMPTY = 1;
    /**
     * 出货结果:失败 货道故障
     */
    public static final int OUT_STATUS_FAIL_CHANNEL_BROKEN = 2;
    /**
     * 出货结果:失败 出货超时
     */
    public static final int OUT_STATUS_FAIL_OUT_TIMEOUT = 3;
    /**
     * 出货结果:失败 通讯超时
     */
    public static final int OUT_STATUS_FAIL_COMMUNICATION_TIMEOUT = -1;
    /**
     * 出货结果:失败 连续支付
     */
    public static final int OUT_STATUS_FAIL_MEANWHILE = 4;
    /**
     * 出货结果:失败 接受订单超时
     */
    public static final int OUT_STATUS_FAIL_ORDER_TIMEOUT = 5;
    /**
     * 出货结果:失败 通讯超时
     */
    public static final int OUT_STATUS_FAIL_BUSY = 6;
    /**
     * 出货结果:失败 电控未上报结果
     */
    public static final int OUT_STATUS_FAIL_NOT_REPORT = 7;
    /**
     * 出货结果:失败 打印机故障
     */
    public static final int OUT_STATUS_FAIL_PRINTER_BROKEN = 8;
    /**
     * 出货结果:失败 货道不存在
     */
    public static final int OUT_STATUS_FAIL_CHANNEL_NOT_EXISTENT = 9;
    /**
     * 出货结果:失败 设备不存在
     */
    public static final int OUT_STATUS_FAIL_DEVICE_NOT_EXISTENT = 10;


    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    @Override
    public void setVendoutStatus(Integer vendoutStatus) {
        super.setVendoutStatus(vendoutStatus);
        super.setUTime(new Date());
    }
}
