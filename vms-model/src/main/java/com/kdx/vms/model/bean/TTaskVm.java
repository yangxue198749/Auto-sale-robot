package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.model.bean.auto._TTaskVm;
import org.apache.cayenne.Cayenne;

public class TTaskVm extends _TTaskVm {

    /**
     * TASK_VM_STATUS:任务配送售货机状态 1未开始 2进行中 4完成 7忽略/关闭
     * TASK_LEVEL:任务级别 5常规任务 3临时任务 1紧急任务
     * TASK_TYPE:3补货 5维修 7投放 8撤机
     */

    /**
     * 补货工单
     */
    public static int TYPE_DELIVERY = 3;
    /**
     * 维修工单
     */
    public static int TYPE_REPAIR = 5;
    /**
     * 投放工单
     */
    public static int TYPE_DEPLOY = 7;
    /**
     * 撤机工单
     */
    public static int TYPE_WITHDRAW = 8;

    /**
     * 未开始
     */
    public final static int STATUS_INITIALIZATION = 1;
    /**
     * 进行中
     */
    public final static int STATUS_ONGOING = 2;
    /**
     * 完成
     */
    public final static int STATUS_FINISHED = 4;
    /**
     * 忽略/关闭
     */
    public final static int STATUS_CLOSED = 7;

    /**
     * 紧急任务
     */
    public final static int LEVEL_URGENT = 1;
    /**
     * 临时任务
     */
    public final static int LEVEL_TEMPORARY = 3;
    /**
     * 常规任务
     */
    public final static int LEVEL_ROUTINE = 5;
    /**
     * 自动创建任务
     */
    public final static int LEVEL_AUTO = 6;

    public final static int ALREADY_PICKUP_TRUE = 1;

    public Integer getTaskVmId() {
        return Cayenne.intPKForObject(this);
    }

    public boolean isManualEditTask() {
        return CheckUtils.check(getDeliveryPattern()) && getDeliveryPattern() == 1;
    }
}
