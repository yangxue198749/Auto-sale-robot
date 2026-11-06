package com.kdx.vms.web.handler;

import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * bCustomer
 * Created by zhangguodong on 2017/7/19.
 */
@Service("bCustomerHandler")
public class BCustomerHandler implements ServiceHandler {
    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IMcsService mcsService;
    @Override
    public HandleResponse deal(String res) {
        LOGGER.info("收到消息：" + res);
        HandleResponse response = new HandleResponse();
        MessageFactory factory = new MessageFactory();
        HeadMessage requestMessage = null;
        try {
            requestMessage = factory.toHeadMessageFormJson(res);
        } catch (Exception e) {
            LOGGER.error("收到的消息未能正确转换");
        }
        try {
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            String result = mcsService.sendBCustomer(context, requestMessage.getData().getPhone(), requestMessage.getFrom(),  requestMessage.getSn());
            LOGGER.info("bCustomer result = " + result);
            response.setResult(result);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
        return response;
    }
}
