package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.model.bean.auto._TVmStatus;

public class TVmStatus extends _TVmStatus {

    public final static int TIME_OUT_MINUTE = 2;

    public final static int STATUS_TRUE = 1;
    public final static int STATUS_FALSE = 2;

    public static int TASK_STATUS_NORMAL = 0;
    public static int TASK_STATUS_DISPATCH = 1;

    public String getTaskStatusName() {
        return CheckUtils.check(getTaskStatus()) ? "已派修" : "";
    }

}
