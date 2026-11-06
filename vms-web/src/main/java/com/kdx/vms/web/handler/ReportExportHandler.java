package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtExport;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;


/**
 * Created by guoyang on 2018/1/11.
 */
@Service("reportExport")
public class ReportExportHandler implements ServiceHandler {
    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Override
    public HandleResponse deal(String message) {
        LOGGER.info("receive export data ：" + message);
        HandleResponse response = new HandleResponse();
        try {
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            Map<String, Object> map = JSON.parseObject(message,Map.class);

            Map<String, Object> bodyMap = (Map<String, Object>) map.get("body");
            if(null == bodyMap){
                LOGGER.warn("receive export report map data is null. message=" + message);
                return response;
            }
            Integer exportId = (Integer) bodyMap.get("exportId");
            if (null == exportId) {
                LOGGER.warn("receive export exportId is null, message=" + message);
                return response;
            }
            TMtExport export = Cayenne.objectForPK(context, TMtExport.class,exportId);
            if(null == export){
                LOGGER.warn("receive export exportId is null, message=" + message);
                return response;
            }
            String fileName = (String) bodyMap.get("fileName");
            if(fileName != null){
                export.setAddress(fileName);
            }else{
                context.deleteObject(export);
            }
            context.commitChanges();
        } catch (Exception e) {
            LOGGER.error("receive export data is exception,cause by=" + e.getMessage() + ",message=" + message,e);
        }
        return response;
    }
}
