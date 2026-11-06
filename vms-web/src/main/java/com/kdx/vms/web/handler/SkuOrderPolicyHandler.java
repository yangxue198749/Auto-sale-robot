package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.service.IMcsService;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 处理货架请求
 * Created by zhangguodong on 2017/11/27.
 */
@Service("skuOrderPolicyHandler")
public class SkuOrderPolicyHandler implements ServiceHandler{
    Logger LOGGER = LoggerFactory.getLogger(getClass());
    private static final String BODY = "body";

    @Resource
    private IMcsService mcsService;
    @Override
    public HandleResponse deal(String str) {

        LOGGER.info("收到消息：" + str);
        HandleResponse response = new HandleResponse();
        HeadMessage message = null;
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
            message = new MessageFactory().toHeadMessageFormJson(JSON.toJSONString(res));
        } catch (Exception e) {
            LOGGER.error("收到的消息未能正确转换");
            return response;
        }
        try {
            String innerCode = message.getData().getInnerCode();
            if(StringUtil.isEmpty(innerCode)){
                LOGGER.error("收到售货机货架请求 innerCode = " + innerCode);
                return response;
            }
            mcsService.sendSkuOrder(ObjectContextFactory.getInstance().newObjectContext(),innerCode,message.getSn());
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage(),e);
            return response;
        }
        return response;
    }
}
