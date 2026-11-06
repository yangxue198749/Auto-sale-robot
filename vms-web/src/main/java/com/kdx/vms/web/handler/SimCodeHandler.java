package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.model.bean.TMtSim;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * sim卡
 * Created by zhangguodong on 2017/7/19.
 */
@Service("simCodeHandler")
public class SimCodeHandler implements ServiceHandler {
    private static final String BODY = "body";

    Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Resource
    private IMtVmService mtVmService;
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
            ObjectContext contextDemp = ObjectContextFactory.getInstance().newObjectContext();
            String innerCode = requestMessage.getData().getInnerCode();
            String simCode = requestMessage.getData().getSimCode();

            TMtVm mtVm = mtVmService.findMtVmByInnerCode(contextDemp,innerCode);
            if(mtVm != null){
                boolean isCommit =false;
                TMtSim sim = mtVm.getSim();
                if(null == sim ){
                    if(!StringUtil.isEmpty(simCode)){
                        isCommit = true;
                        sim = contextDemp.newObject(TMtSim.class);
                        sim.setCtime(new Date());
                        sim.setCtor(UserUtils.getVmsSystem().getId());
                        sim.setDeleteMark(0);
                        sim.setMtVm(mtVm);
                        sim.setSimNumber(simCode);
                    }
                }else{
                    if(!StringUtil.isEmpty(simCode) && !simCode.equals(sim.getSimNumber())){
                        //sim卡编码不为空，且已改变，修改
                        isCommit = true;
                        sim.setSimNumber(simCode);
                    }
                }
                if(isCommit){
                    try {
                        contextDemp.commitChanges();
                    } catch (Exception e) {
                        contextDemp.rollbackChanges();
                        LOGGER.error(e.getLocalizedMessage(),e);
                    }
                }
            }else{
                LOGGER.error("收到售货机上报sim卡编码请求   innerCode = "+ innerCode +"  售货机不存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
