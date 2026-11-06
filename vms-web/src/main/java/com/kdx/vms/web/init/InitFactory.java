package com.kdx.vms.web.init;

import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.mq.config.ConnectionConfig;
import com.kdx.vms.mq.consumer.ConsumerQueueFactory;
import com.kdx.vms.web.handler.*;
import com.kdx.vms.web.handler.card.OutResultCardHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;

/**
 * 系统初始化调用
 * Created by zhangguodong on 2017/7/18.
 */
public class InitFactory {
    Logger LOGGER = LoggerFactory.getLogger(getClass());


    @Resource
    private OutResultHandler outResultHandler;
    @Resource
    private SkuCfgReqHandler skuCfgReqHandler;
    @Resource
    private SpCfgReqHandler spCfgReqHandler;
    @Resource
    private MtEnergyReqHandler mtEnergyReqHandler;
    @Resource
    private OpenDoorPwdReqHandler openDoorPwdReqHandler;
    @Resource
    private SimCodeHandler simCodeHandler;
    @Resource
    private ChannelPolicyHandler channelPolicyHandler;
    @Resource
    private AreaHandler areaHandler;
    @Resource
    private BCustomerHandler bCustomerHandler;
    @Resource
    private VmDeviceInfoHandler vmDeviceInfoHandler;
    @Resource
    private VmBaseInfoHandler vmBaseInfoHandler;
    @Resource
    private OutResultReserveHandler outResultReserveHandler;
    @Resource
    private OutResultCardHandler outResultCardHandler;
    @Resource
    private RepeatTextReqHandler repeatTextReqHandler;
    @Resource
    private RepairWorkHandler repairWorkHandler;
    @Resource
    private SkuOrderPolicyHandler skuOrderPolicyHandler;
    @Resource
    private ReportExportHandler reportExportHandler;
    @Resource
    private MtEnergyReqNewHandler mtEnergyReqNewHandler;
    @Resource
    private MtPortalReqHandler mtPortalReqHandler;
    @Resource
    private SkuInfoHandler skuInfoHandler;
    /**初始化mq关**/
    private void init(){
        ConnectionConfig.setHOST((String)PropertiesLoader.map.get("mq_host"));
        ConnectionConfig.setPORT(Integer.parseInt((String)PropertiesLoader.map.get("mq_port")));
        ConnectionConfig.setUserName((String) PropertiesLoader.map.get("mq_user_name"));
        ConnectionConfig.setPassWord((String) PropertiesLoader.map.get("mq_pass_word"));

        ConnectionConfig.setPoolSize(Integer.parseInt(String.valueOf(PropertiesLoader.map.get("adms_pool_size"))));
        //发给adms的消息
        initArea();
        //发给adms的消息
        initBCustomer(); 

        ConnectionConfig.setPoolSize(Integer.parseInt(String.valueOf(PropertiesLoader.map.get("pool_size"))));
        //出货结果
        initImmediateOutResult();
        initReserveOutResult();
        //商品信息请求
        initSkuCfgReq();
        //闪购键
        initSpCfgReq();
        //走马灯
        initRepeatTextReq();
        //节能
        initMtEnergyReq();
        //开门密码
        initOpenDoorPwdReq();
        //sim卡
        initSimCode();
        //货道
        initChannelPolicy();
        //设备版本信息
        initVmDeviceInfo();
        //设备信息
        initVmBaseInfo();
        
        //地推出货结果
        initPromotionOutResultHandler();
        //刷卡取货出货结果处理
        initOutResultCardHandler();
        //接收维修工单信息，推送给维修员
        initRepairWork();
        //货架信息
        initSkuOrderPolicy();
        //报表导出
        initExportInfo();
        //节能消息（一拖多）
        initMtEnergyReqNew();
        //快捷入口
        initMtPortalReq();
        //商品信息5.0.0a
        initskuInfoReq();
    }


    /**area**/
    private void initArea() {
        String areaQueueName = (String) PropertiesLoader.map.get("areaQueueName");
        String areaRequestTopic = (String) PropertiesLoader.map.get("areaRequestTopic");
        String areaResponseTopic = (String) PropertiesLoader.map.get("areaResponseTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("area-handler init start   queueName = " + areaQueueName  + ",requestTopic = " + areaRequestTopic +",responseTopic = "+areaResponseTopic+ ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(areaHandler);
            consumerQueueFactory.createConsumer(areaQueueName,areaRequestTopic,areaResponseTopic,responseExchangeName);
            LOGGER.info("area-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**bCustomer**/
    private void initBCustomer() {
        String bCustomerQueueName = (String) PropertiesLoader.map.get("bCustomerQueueName");
        String bCustomerRequestTopic = (String) PropertiesLoader.map.get("bCustomerRequestTopic");
        String bCustomerResponseTopic = (String) PropertiesLoader.map.get("bCustomerResponseTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("bCustomer-handler init start   queueName = " + bCustomerQueueName  + ",requestTopic = "+",responseTopic = "+bCustomerResponseTopic+  ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(bCustomerHandler);
            consumerQueueFactory.createConsumer(bCustomerQueueName,bCustomerRequestTopic,bCustomerResponseTopic,responseExchangeName);
            LOGGER.info("bCustomer-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }
    /**初始化即售出货结果**/
    private void initImmediateOutResult() {
        String outResultQueueName = (String) PropertiesLoader.map.get("outResultImmediateQueueName");
        String outResultRequestTopic = (String) PropertiesLoader.map.get("outResultImmediateRequestTopic");
        String outResultResponseTopic = (String) PropertiesLoader.map.get("outResultImmediateResponseTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("out-result-immediate-handler init start   queueName = " + outResultQueueName  + ",requestTopic = " + outResultRequestTopic + ",responseTopic = " + outResultResponseTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(outResultHandler);
            consumerQueueFactory.createConsumer(outResultQueueName,outResultRequestTopic,outResultResponseTopic,responseExchangeName);
            LOGGER.info("out-result-immediate-handler init success");

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**初始化预售出货结果**/
    private void initReserveOutResult() {
        //地推和预定公用一个出货结果处理
        String outResultQueueName = (String) PropertiesLoader.map.get("outResultReserveQueueName");
        String outResultRequestTopic = (String) PropertiesLoader.map.get("outResultReserveRequestTopic");
        String outResultResponseTopic = (String) PropertiesLoader.map.get("outResultImmediateResponseTopic");

        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("out-result-reserve-handler init start   queueName = " + outResultQueueName  + ",requestTopic = " + outResultRequestTopic  + ",responseTopic = " + outResultResponseTopic +",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(outResultReserveHandler);
            consumerQueueFactory.createConsumer(outResultQueueName,outResultRequestTopic,outResultResponseTopic,responseExchangeName);
            LOGGER.info("out-result-reserve-handler init success");

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**初始化商品信息请求**/
    private void initSkuCfgReq() {
        String skuCfgReqQueueName = (String) PropertiesLoader.map.get("skuCfgReqQueueName");
        String skuCfgReqRequestTopic = (String) PropertiesLoader.map.get("skuCfgReqRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("skuCfgReq-handler init start   queueName = " + skuCfgReqQueueName  + ",requestTopic = " + skuCfgReqRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(skuCfgReqHandler);
            consumerQueueFactory.createConsumer(skuCfgReqQueueName,skuCfgReqRequestTopic,responseExchangeName);
            LOGGER.info("skuCfgReq-handler init success");

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }
    /**闪购键**/
    private void initSpCfgReq(){
        String spCfgReqQueueName = (String) PropertiesLoader.map.get("spCfgReqQueueName");
        String spCfgReqRequestTopic = (String) PropertiesLoader.map.get("spCfgReqRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("spCfgReq-handler init start   queueName = " + spCfgReqQueueName  + ",requestTopic = " + spCfgReqRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(spCfgReqHandler);
            consumerQueueFactory.createConsumer(spCfgReqQueueName,spCfgReqRequestTopic,responseExchangeName);
            LOGGER.info("spCfgReq-handler init success");

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**走马灯**/
    private void initRepeatTextReq() {
        String repeatTextReqQueueName = (String) PropertiesLoader.map.get("repeatTextReqQueueName");
        String repeatTextReqRequestTopic = (String) PropertiesLoader.map.get("repeatTextReqRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("repeatTextReq-handler init start   queueName = " + repeatTextReqQueueName  + ",requestTopic = " + repeatTextReqRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(repeatTextReqHandler);
            consumerQueueFactory.createConsumer(repeatTextReqQueueName,repeatTextReqRequestTopic,responseExchangeName);
            LOGGER.info("repeatTextReq-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }
    /**节能**/
    private void initMtEnergyReq() {
        String mtEnergyReqQueueName = (String) PropertiesLoader.map.get("mtEnergyReqQueueName");
        String mtEnergyReqRequestTopic = (String) PropertiesLoader.map.get("mtEnergyReqRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("mtEnergyReq-handler init start   queueName = " + mtEnergyReqQueueName  + ",requestTopic = " + mtEnergyReqRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(mtEnergyReqHandler);
            consumerQueueFactory.createConsumer(mtEnergyReqQueueName,mtEnergyReqRequestTopic,responseExchangeName);
            LOGGER.info("mtEnergyReq-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**开门密码**/
    private void initOpenDoorPwdReq() {
        String openDoorPwdReqQueueName = (String) PropertiesLoader.map.get("openDoorPwdReqQueueName");
        String openDoorPwdReqRequestTopic = (String) PropertiesLoader.map.get("openDoorPwdReqRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("openDoorPwdReq-handler init start   queueName = " + openDoorPwdReqQueueName  + ",requestTopic = " + openDoorPwdReqRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(openDoorPwdReqHandler);
            consumerQueueFactory.createConsumer(openDoorPwdReqQueueName,openDoorPwdReqRequestTopic,responseExchangeName);
            LOGGER.info("openDoorPwdReq-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**sim卡**/
    private void initSimCode() {
        String simCodeQueueName = (String) PropertiesLoader.map.get("simCodeQueueName");
        String simCodeRequestTopic = (String) PropertiesLoader.map.get("simCodeRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("simCode-handler init start   queueName = " + simCodeQueueName  + ",requestTopic = " + simCodeRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(simCodeHandler);
            consumerQueueFactory.createConsumer(simCodeQueueName,simCodeRequestTopic,responseExchangeName);
            LOGGER.info("simCode-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**货道**/
    private void initChannelPolicy() {
        String channelPolicyQueueName = (String) PropertiesLoader.map.get("channelPolicyQueueName");
        String channelPolicyRequestTopic = (String) PropertiesLoader.map.get("channelPolicyRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("channelPolicy-handler init start   queueName = " + channelPolicyQueueName  + ",requestTopic = " + channelPolicyRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(channelPolicyHandler);
            consumerQueueFactory.createConsumer(channelPolicyQueueName,channelPolicyRequestTopic,responseExchangeName);
            LOGGER.info("channelPolicy-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**设备版本信息**/
    private void initVmDeviceInfo() {
        String vmDeviceInfoQueueName = (String) PropertiesLoader.map.get("vmDeviceInfoQueueName");
        String vmDeviceInfoRequestTopic = (String) PropertiesLoader.map.get("vmDeviceInfoRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("vmDeviceInfo-handler init start   queueName = " + vmDeviceInfoQueueName  + ",requestTopic = " + vmDeviceInfoRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(vmDeviceInfoHandler);
            consumerQueueFactory.createConsumer(vmDeviceInfoQueueName,vmDeviceInfoRequestTopic,responseExchangeName);
            LOGGER.info("vmDeviceInfo-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**设备信息**/
    private void initVmBaseInfo() {
        String vmBaseInfoQueueName = (String) PropertiesLoader.map.get("vmBaseInfoQueueName");
        String vmBaseInfoRequestTopic = (String) PropertiesLoader.map.get("vmBaseInfoRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("vmBaseInfo-handler init start   queueName = " + vmBaseInfoQueueName  + ",requestTopic = " + vmBaseInfoRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(vmBaseInfoHandler);
            consumerQueueFactory.createConsumer(vmBaseInfoQueueName,vmBaseInfoRequestTopic,responseExchangeName);
            LOGGER.info("vmBaseInfo-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    private void initPromotionOutResultHandler() {
        //地推和预定公用一个出货结果处理
        String promotionOutResultQueueName = (String) PropertiesLoader.map.get("promotionOutResultQueueName");
        String promotionOutResultRequestTopic = (String) PropertiesLoader.map.get("promotionOutResultRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("promotionOutResult-handler init start   queueName = " + promotionOutResultQueueName  + ",requestTopic = " + promotionOutResultRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(outResultReserveHandler);
            consumerQueueFactory.createConsumer(promotionOutResultQueueName,promotionOutResultRequestTopic,responseExchangeName);
            LOGGER.info("promotionOutResult-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    private void initOutResultCardHandler() {
        String outResultCardQueueName = (String) PropertiesLoader.map.get("outResultCardQueueName");
        String outResultCardRequestTopic = (String) PropertiesLoader.map.get("outResultCardRequestTopic");
        String outResultResponseTopic = (String) PropertiesLoader.map.get("outResultImmediateResponseTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("cardOutResult-handler init start   queueName = " + outResultCardQueueName  + ",requestTopic = " + outResultCardRequestTopic + ",responseTopic = " + outResultResponseTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(outResultCardHandler);
            consumerQueueFactory.createConsumer(outResultCardQueueName,outResultCardRequestTopic,outResultResponseTopic,responseExchangeName);
            LOGGER.info("cardOutResult-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }
    private void initSkuOrderPolicy() {
        String skuOrderPolicyQueueName = (String) PropertiesLoader.map.get("skuOrderPolicyQueueName");
        String skuOrderPolicyRequestTopic = (String) PropertiesLoader.map.get("skuOrderPolicyRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("skuOrderPolicy-handler init start   queueName = " + skuOrderPolicyQueueName  + ",requestTopic = " + skuOrderPolicyRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(skuOrderPolicyHandler);
            consumerQueueFactory.createConsumer(skuOrderPolicyQueueName,skuOrderPolicyRequestTopic,responseExchangeName);
            LOGGER.info("skuOrderPolicy-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    private void initRepairWork() {
        String repairWorkQueueName = (String) PropertiesLoader.map.get("repairWorkQueueName");
        String repairWorkRequestTopic = (String) PropertiesLoader.map.get("repairWorkRequestTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("repairWork-handler init start   queueName = " + repairWorkQueueName  + ",requestTopic = " + repairWorkRequestTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(repairWorkHandler);
            consumerQueueFactory.createConsumer(repairWorkQueueName,repairWorkRequestTopic,responseExchangeName);
            LOGGER.info("repairWork-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**报表导出**/
    private void initExportInfo() {
        String exportResultQueueName = (String) PropertiesLoader.map.get("exportResultQueueName");
        String exportResponseTopic = (String) PropertiesLoader.map.get("exportResponseTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("payExchangeName");
        LOGGER.info("exportInfo-handler init start   queueName = " + exportResultQueueName  + ",requestTopic = " + exportResponseTopic + ",responseExchangeName = " + responseExchangeName);

        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(reportExportHandler);
            consumerQueueFactory.createConsumer(exportResultQueueName,exportResponseTopic,responseExchangeName);
            LOGGER.info("exportInfo-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**
     * 节能（5.0.0a 一拖多）
     */
    private void initMtEnergyReqNew() {
        String mtEnergyReqQueueName = (String) PropertiesLoader.map.get("mtEnergyReqQueueNameNew");
        String mtEnergyReqRequestTopic = (String) PropertiesLoader.map.get("mtEnergyComboReqTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("repairWork-handler init start   queueName = " + mtEnergyReqQueueName  + ",requestTopic = " + mtEnergyReqRequestTopic + ",responseExchangeName = " + responseExchangeName);
        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(mtEnergyReqNewHandler);
            consumerQueueFactory.createConsumer(mtEnergyReqQueueName,mtEnergyReqRequestTopic,responseExchangeName);
            LOGGER.info("mtEnergyReqNew-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**
     * 快捷入口
     */
    private void initMtPortalReq() {
        String mtPortalCfgReqQueueName = (String) PropertiesLoader.map.get("mtPortalCfgReqQueueName");
        String mtPortalCfgReqTopic = (String) PropertiesLoader.map.get("mtPortalCfgReqTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("mtPortalReq-handler init start   queueName = " + mtPortalCfgReqQueueName  + ",requestTopic = " + mtPortalCfgReqTopic + ",responseExchangeName = " + responseExchangeName);
        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(mtPortalReqHandler);
            consumerQueueFactory.createConsumer(mtPortalCfgReqQueueName,mtPortalCfgReqTopic,responseExchangeName);
            LOGGER.info("mtPortalReq-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

    /**
     * 商品信息
     * 5.0.0a
     */
    private void initskuInfoReq() {
        String skuInfoReqQueueName = (String) PropertiesLoader.map.get("skuInfoReqQueueName");
        String skuInfoReqTopic = (String) PropertiesLoader.map.get("skuInfoReqTopic");
        String responseExchangeName = (String) PropertiesLoader.map.get("responseExchangeName");
        LOGGER.info("repairWork-handler init start   queueName = " + skuInfoReqQueueName  + ",requestTopic = " + skuInfoReqTopic + ",responseExchangeName = " + responseExchangeName);
        try {
            ConsumerQueueFactory consumerQueueFactory = new ConsumerQueueFactory(skuInfoHandler);
            consumerQueueFactory.createConsumer(skuInfoReqQueueName,skuInfoReqTopic,responseExchangeName);
            LOGGER.info("skuInfoReq-handler init success");
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }

}
