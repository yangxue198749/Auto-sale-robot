package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.service.IMcsService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备信息
 * Created by zhangguodong on 2017/7/19.
 */
@Service("vmBaseInfoHandler")
public class VmBaseInfoHandler implements ServiceHandler {
    private static final String BODY = "body";

    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IMcsService mcsService;
    @Override
    public HandleResponse deal(String str) {
        LOGGER.info("收到消息：" + str);
        HandleResponse response = new HandleResponse();
        MessageFactory factory = new MessageFactory();
        HeadMessage requestMessage = null;
        Map<String,Object> res = new HashedMap();
        try {
            Map<String,Object> map = new HashMap<>();
            map = JSON.parseObject(str,Map.class);
            if(!map.containsKey(BODY)){
                LOGGER.error(" miss body ");
                return response;
            }else{
                res = (Map<String, Object>) map.get(BODY);
            }
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage(),e);
            return response;
        }
        try {
            requestMessage = factory.toHeadMessageFormJson(JSON.toJSONString(res));
        } catch (Exception e) {
            LOGGER.error("收到的消息未能正确转换");
        }
        try {
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            mcsService.sendVmBaseInfo(context, requestMessage.getData().getInnerCode(), requestMessage.getFrom(), requestMessage.getSn());
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage() + " 参数错误.");
        }
        return response;
    }
}
