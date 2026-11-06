package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TOrderDetailNotify;

public class TOrderDetailNotify extends _TOrderDetailNotify {
    /**
     * 通知出货:成功
     */
    public static final Integer NOTIFY_STATUS_SUCCESS = 1;
    /**
     * 通知出货:失败
     */
    public static final Integer NOTIFY_STATUS_FAIL = 2;
    /**
     * 通知出货:售货机不在线
     */
    public static final Integer NOTIFY_STATUS_OFFLINE = 3;
    /**
     * 通知出货:售货机回复超时
     */
    public static final Integer NOTIFY_STATUS_TIMEOUT = 4;
    /**
     * 通知出货:服务器通信超时
     */
    public static final Integer NOTIFY_STATUS_SERVER_TIMEOUT = 5;
    /**
     * 通知出货:通知出错
     */
    public static final Integer NOTIFY_STATUS_ERROR = -1;
}
