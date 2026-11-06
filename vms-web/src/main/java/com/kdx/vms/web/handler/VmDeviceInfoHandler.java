package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.commons.util.mcs.message.data.DeviceInfo;
import com.kdx.vms.commons.util.mcs.message.data.VersionInfo;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmVersion;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.orm.dao.MtVmVersionDao;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * 设备信息
 * Created by zhangguodong on 2017/7/19.
 */
@Service("vmDeviceInfoHandler")
public class VmDeviceInfoHandler implements ServiceHandler {
    private static final String BODY = "body";

    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IMtVmService mtVmService;
    @Resource
    private MtVmVersionDao mtVmVersionDao;
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
            String innerCode = requestMessage.getData().getInnerCode();
            ObjectContext contextDemp = ObjectContextFactory.getInstance().newObjectContext();
            TMtVm mtVm = mtVmService.findMtVmByInnerCode(contextDemp,innerCode);
            if(null != mtVm){
                Integer extentionCode = requestMessage.getData().getExtentionCode();
                mtVm = mtVm.findExtention(extentionCode);//5.0一拖多
                boolean isChange = false;
                Integer spCount = mtVm.getReportSpCount();
                String channelStructure = mtVm.getChannelStructure();
                Integer toreCount = requestMessage.getData().getDeviceInfo().getButtonCount();
                String  channelCount =  Arrays.toString(requestMessage.getData().getDeviceInfo().getChannelCount());
                if(null != toreCount && !toreCount.equals(spCount)){
                    isChange = true;
                    mtVm.setReportSpCount(requestMessage.getData().getDeviceInfo().getButtonCount());
                }
                if(!StringUtil.isEmpty(channelCount) && !channelCount.equals(channelStructure)){
                    isChange = true;
                    mtVm.setChannelStructure(channelCount);
                }
                if(!isChange){
                    LOGGER.info("售货机闪购键及货道分布无变更");
                    contextDemp.rollbackChanges();
                }
                DeviceInfo deviceInfo = requestMessage.getData().getDeviceInfo();
                List<VersionInfo> versions = null;
                if(null != deviceInfo){
                    versions = deviceInfo.getVersionInfo();
                }
                if(null != versions && versions.size()>0){
                    // 检查版本信息有没有上报过，如果有并且版本信息有更新，覆盖
                    //如果没有上报过，存储
                    for (VersionInfo versionInfo : versions) {
                        if(null != versionInfo.getType()){
                            TMtVmVersion vmVersion =  mtVmVersionDao.fingVersionByVmIdAndType(contextDemp, mtVm.getId(), versionInfo.getType());
                            if(null != vmVersion){
                                //存在覆盖
                                vmVersion.setDeviceVersion(versionInfo.getVersion());
                                vmVersion.setUtime(new Date());
                                vmVersion.setUtor(UserUtils.getVmsSystem().getId());
                            }else{
                                //不存在   新建
                                TMtVmVersion newVersion = contextDemp.newObject(TMtVmVersion.class);
                                newVersion.setCtime(new Date());
                                newVersion.setCtor(UserUtils.getVmsSystem().getId());
                                newVersion.setDeviceType(versionInfo.getType());
                                newVersion.setDeviceVersion(versionInfo.getVersion());
                                newVersion.setMtVm(mtVm);
                            }
                        }
                    }
                    try {
                        contextDemp.commitChanges();
                    } catch (Exception e) {
                        LOGGER.error("存储售货机上报的设备信息失败");
                        LOGGER.error(e.getLocalizedMessage(),e);
                        contextDemp.rollbackChanges();
                    }
                }
            }else{
                LOGGER.error("收到售货机上报设备配置   innerCode = "+ innerCode +"  售货机不存在");
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
        return response;
    }
}
