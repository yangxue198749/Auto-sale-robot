package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.mp.commons.util.WeChatConfig;
import com.kdx.mp.util.WeChatUtils;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Created by zhouxing on 2017/11/13.
 */
@Service("RepairWorkHandler")
public class RepairWorkHandler implements ServiceHandler {
    private static final String BODY = "body";
    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public HandleResponse deal(String message) {
        LOGGER.info("收到消息：" + message);
        HandleResponse response = new HandleResponse();
        Map<String,Object> res = new HashedMap();

        try {
            Map<String,Object> map = new HashedMap();
            map = JSON.parseObject(message, Map.class);
            if(!map.containsKey(BODY)){
                LOGGER.error(" miss body ");
                return response;
            }else{
                res = (Map<String, Object>) map.get(BODY);
                Object titleobj = res.get("title");
                Object openidobj = res.get("openId");
                Object dateobj = res.get("date");
                if(titleobj instanceof String && openidobj instanceof String && dateobj instanceof String){
                    String title = String.class.cast(titleobj);
                    String date =  String.class.cast(dateobj);
                    String openId =  String.class.cast(openidobj);
                    WeChatUtils.sendTaskTemplateMessage(openId,title,date, WeChatConfig.MSG_URL +"/mp/mpTask/repair/list");
                }
            }
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage(),e);
            return response;
        }
        return response;
    }
}