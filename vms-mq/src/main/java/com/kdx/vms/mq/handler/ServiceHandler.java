package com.kdx.vms.mq.handler;


import com.kdx.vms.mq.consumer.HandleResponse;

/**
 *
 * 处理消息接口
 * Created by liyang on 2017/6/30.
 */
public interface ServiceHandler {

    /**
     * 处理消息
     * @param message
     * @return
     */
    HandleResponse deal(String message);

}
