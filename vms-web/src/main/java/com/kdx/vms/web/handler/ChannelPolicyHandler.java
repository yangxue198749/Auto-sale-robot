package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 货道
 * Created by zhangguodong on 2017/7/19.
 */
@Service("channelPolicyHandler")
public class ChannelPolicyHandler implements ServiceHandler {
    private static final String BODY = "body";
    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IMtVmService mtVmService;
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
            long sn = requestMessage.getSn();
            String innerCode = requestMessage.getData().getInnerCode();
            TMtVm mtVm = mtVmService.findMtVmByInnerCode(context,innerCode);
            if(mtVm != null){
                Integer extentionCode = requestMessage.getData().getExtentionCode();
                mtVm = mtVm.findExtention(extentionCode);//5.0一拖多
                //发送货道数据
                mcsService.sendChannelPolicy(mtVm,true,sn);

            }else{
                LOGGER.error("收到售货机货道请求   innerCode = "+ innerCode +"  售货机不存在");
                return response;
            }
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage(),e);
        }
        return response;
    }
}
