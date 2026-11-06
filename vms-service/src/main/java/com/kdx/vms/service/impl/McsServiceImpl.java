package com.kdx.vms.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.kdx.vms.commons.McsConfiguration;
import com.kdx.vms.commons.util.*;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.commons.util.mcs.message.MessageResult;
import com.kdx.vms.commons.util.mcs.message.MsgType;
import com.kdx.vms.commons.util.mcs.message.data.*;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.custom.DeliveryObject;
import com.kdx.vms.model.custom.DeliveryTaskJson;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.orm.dao.*;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service("mcsService")
public class McsServiceImpl implements IMcsService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private TaskVmDao taskVmDao;
    @Resource
    private MyJedisCluster myJedisCluster;
    @Resource
    private MtVmChannelDao mtVmChannelDao;

    @Resource
    private MtVmDao mtVmDao;
    @Resource
    private MtCustomerDao mtCustomerDao;
    @Resource
    private MtAreaDao mtAreaDao;
    @Resource
    private MtBusinessDistrictDao mtBusinessDistrictDao;
    @Resource
    private MtNodeDao mtNodeDao;
    @Resource
    private MtVmSkuSortDao mtVmSkuSortDao;

    static MessageFactory factory = new MessageFactory();
    private static String urLPrefix = (String) PropertiesLoader.map.get("imgUrlPrefix");

    @Override
    public String deliveryTaskFinish(DeliveryTaskJson deliveryTaskJson) {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        String errorMessage = "";
        try {
            logger.info("收到补货完成请求, taskVmId = " + JSON.toJSONString(deliveryTaskJson));
            //查询出当前配送工单
            TTaskVm tkTaskVm = taskVmDao.findByPk(context, deliveryTaskJson.getTaskId());

            if (null != tkTaskVm) {
                //工单存在，校验工单是否满足完成要求
                errorMessage = checkTkTaskVm(tkTaskVm);
            } else {
                //没查到工单
                logger.error("完成工单失败,配送工单不存在 taskVmId = " + deliveryTaskJson.getTaskId());
                errorMessage = "没有查到此工单";
            }

            if (StringUtil.isEmpty(errorMessage)) {
                TMtVm mtVm = tkTaskVm.getMtVm();
                boolean hasNext = mtVm.hasNextChannel();//是否有将要应用货道

                Map<String, TMtVmChannel> currChannelMap = null;
                Map<String, TTaskVmChannel> taskVmChannelMap = null;
                Map<String, TTaskVmChannel> taskVmChannelCleanMap = new HashMap<>();

                List<TMtVmChannel> currChannels = mtVm.getChannelInfo();
                Map<String, TMtVmChannelNext> nextChannelMap = mtVm.getChannelNext();

                currChannelMap = nextToCurrChannel(mtVm, currChannels, nextChannelMap, hasNext);//即将应用转化为当前
                taskVmChannelMap = taskVmChannelListToMap(tkTaskVm.getTaskVmChannel(), taskVmChannelCleanMap);
                //先持久化，再发给售货机

                //1.处理清货
                dealCleanTask(deliveryTaskJson, tkTaskVm, currChannelMap, taskVmChannelCleanMap);

                //2处理补货
                dealFillTaskList(currChannelMap, tkTaskVm, taskVmChannelMap, deliveryTaskJson);

                //3.处理调量
                dealAdJustTask(deliveryTaskJson, tkTaskVm, currChannelMap, taskVmChannelMap);


                errorMessage += finishDistributionWorkOrder(context, tkTaskVm);

                if (StringUtil.isEmpty(errorMessage)) {
                    if (hasNext) {
                        //货道有变更
                        //					// 1.下发新的全量商品
                        errorMessage += sendSkuToMcs(context, mtVm, true, false, null);
                        //5.0.0a新协议
                        // TODO: 2018/3/26 新商品协议
                        errorMessage += sendSkuInfoToMcs(context, mtVm, true, false, null);
                        Thread.sleep(1);
                        //					//2.下发新的全量商品价格
                        errorMessage += sendSkuPriceToMcs(context, mtVm, true);
                        Thread.sleep(1);
                        //					//3.下发新的全量货道数据
                        logger.info("当前售货机存在将要应用货道信息，下发货道数据,innerCode = " + mtVm.getInnerCode());
                        errorMessage += sendChannelPolicy(mtVm, false, null);
                    }
                    //下发库存
                    Thread.sleep(1);
                    errorMessage += sendSupplyPolocy(mtVm, null);

                    errorMessage += sendSkuOrder(context, mtVm.getInnerCode(), null);

                    getOpenDoorPwd(context, mtVm.getId(), null);
                } else {
                    return errorMessage;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            errorMessage = "配送工单完成失败";
        }
        return errorMessage;
    }

    /**
     * 处理调量
     *
     * @param deliveryTaskJson
     * @param tkTaskVm
     * @param currChannelMap
     */
    private void dealAdJustTask(DeliveryTaskJson deliveryTaskJson, TTaskVm tkTaskVm, Map<String, TMtVmChannel> currChannelMap, Map<String, TTaskVmChannel> taskVmChannelMap) {
        List<DeliveryObject> adJustList = deliveryTaskJson.getAdjustList();

        if (null != adJustList && adJustList.size() > 0) {
            for (DeliveryObject adJust : adJustList) {
                TMtVmChannel mtVmChannel = currChannelMap.get(adJust.getChannelCode());
                mtVmChannel.setCurrNum(adJust.getMarginCount());
                mtVmChannel.setHasNextStock(TMtVmChannel.DISTRIBUTION_INIT);
                TTaskVmChannel taskVmChannel = taskVmChannelMap.get(adJust.getChannelCode());
                if (null == taskVmChannel) {
                    taskVmChannel = tkTaskVm.getObjectContext().newObject(TTaskVmChannel.class);
                    taskVmChannel.setTaskType(TTaskVmChannel.CHANNEL_TYPE_ADJUST);
                    taskVmChannel.setChannel(mtVmChannel);
                    taskVmChannel.setBeforeDeliveryStock(adJust.getCurrCount());
                    taskVmChannel.setCtor(tkTaskVm.getOwner());
                    taskVmChannel.setCtime(new Date());
                    taskVmChannel.setMaxCount(mtVmChannel.getTempMaxCapacity());
                    taskVmChannel.setSku(mtVmChannel.getSku());
                    taskVmChannel.setTaskVm(tkTaskVm);
                }
                taskVmChannel.setRealClear(adJust.getClearCount());
                taskVmChannel.setAfterDeliveryStock(adJust.getMarginCount());
                taskVmChannel.setRealCount(adJust.getFillCount());
                if (null != adJust.getManufactureDate()) {
                    mtVmChannel.setManufactureDate(DateUtils.getDate1(adJust.getManufactureDate()));
                } else {
                    mtVmChannel.setManufactureDate(null);
                    mtVmChannel.setManufactureEndDate(null);
                    mtVmChannel.setBatchNo(null);
                }
                mtVmChannel.setBatchNo(adJust.getBatchNum());
                if (null != mtVmChannel.getManufactureDate() && null != mtVmChannel.getSku() && null != mtVmChannel.getSku().getValidityType() && null != mtVmChannel.getSku().getSkuShelfLife()) {
                    if (mtVmChannel.getSku().getValidityType() == 0) {
                        //天
                        mtVmChannel.setManufactureEndDate(DateUtils.plusDay3(mtVmChannel.getManufactureDate(), mtVmChannel.getSku().getSkuShelfLife()));
                    } else if (mtVmChannel.getSku().getValidityType() == 1) {
                        //月
                        mtVmChannel.setManufactureEndDate(DateUtils.plusMonth1(mtVmChannel.getManufactureDate(), mtVmChannel.getSku().getSkuShelfLife()));
                    }
                }

            }
        }
    }

    /**
     * 处理清货
     *
     * @param deliveryTaskJson
     * @param taskVmChannelMap
     */
    private void dealCleanTask(DeliveryTaskJson deliveryTaskJson, TTaskVm taskVm, Map<String, TMtVmChannel> currChannelMap, Map<String, TTaskVmChannel> taskVmChannelMap) {
        List<DeliveryObject> cleanList = deliveryTaskJson.getCleanList();
        if (null != cleanList && cleanList.size() > 0) {
            for (DeliveryObject clean : cleanList) {
                TMtVmChannel mtVmChannel = currChannelMap.get(clean.getChannelCode());
                mtVmChannel.setCurrNum(0);
                mtVmChannel.setHasNextStock(TMtVmChannel.DISTRIBUTION_INIT);
                TTaskVmChannel taskVmChannel = taskVmChannelMap.get(clean.getChannelCode());
                if (null == taskVmChannel) {
                    taskVmChannel = taskVm.getObjectContext().newObject(TTaskVmChannel.class);
                    taskVmChannel.setTaskType(TTaskVmChannel.CHANNEL_TYPE_CLEAN);
                    taskVmChannel.setChannel(mtVmChannel);
                    taskVmChannel.setBeforeDeliveryStock(clean.getCurrCount());
                    taskVmChannel.setClearCount(clean.getClearCount());
                    taskVmChannel.setCtor(taskVm.getOwner());
                    taskVmChannel.setCtime(new Date());
                    taskVmChannel.setMaxCount(mtVmChannel.getAdjustMaxCapacity());
                    taskVmChannel.setSku(Cayenne.objectForPK(taskVm.getObjectContext(), TMtSku.class, clean.getSkuId()));
                    taskVmChannel.setTaskVm(taskVm);
                }
                taskVmChannel.setRealClear(clean.getClearCount());
                taskVmChannel.setAfterDeliveryStock(0);
            }
        }
    }

    /**
     * 处理补货
     *
     * @param currChannelMap
     * @param taskVmChannelMap
     * @param deliveryTaskJson
     */
    private void dealFillTaskList(Map<String, TMtVmChannel> currChannelMap, TTaskVm taskVm, Map<String, TTaskVmChannel> taskVmChannelMap, DeliveryTaskJson deliveryTaskJson) {
        List<DeliveryObject> fillList = deliveryTaskJson.getFillList();//补货
        if (null != fillList && fillList.size() > 0) {
            for (DeliveryObject fill : fillList) {

                TMtVmChannel mtVmChannel = currChannelMap.get(fill.getChannelCode());

                TTaskVmChannel taskVmChannel = taskVmChannelMap.get(fill.getChannelCode());
                if (null == taskVmChannel) {
                    taskVmChannel = taskVm.getObjectContext().newObject(TTaskVmChannel.class);
                    taskVmChannel.setTaskType(TTaskVmChannel.CHANNEL_TYPE_FILL);
                    taskVmChannel.setChannel(mtVmChannel);
                    taskVmChannel.setBeforeDeliveryStock(fill.getCurrCount());
                    taskVmChannel.setClearCount(fill.getClearCount());
                    taskVmChannel.setCtor(taskVm.getOwner());
                    taskVmChannel.setCtime(new Date());
                    taskVmChannel.setMaxCount(mtVmChannel.getAdjustMaxCapacity());
                    taskVmChannel.setSku(Cayenne.objectForPK(taskVm.getObjectContext(), TMtSku.class, fill.getSkuId()));
                    taskVmChannel.setTaskVm(taskVm);
                }
                taskVmChannel.setAfterDeliveryStock(fill.getMarginCount());
                taskVmChannel.setRealClear(fill.getClearCount());
                taskVmChannel.setRealCount(fill.getFillCount());


                mtVmChannel.setCurrNum(fill.getMarginCount());
                mtVmChannel.setHasNextStock(TMtVmChannel.DISTRIBUTION_INIT);
                if (null != fill.getManufactureDate()) {
                    mtVmChannel.setManufactureDate(DateUtils.getDate1(fill.getManufactureDate()));
                } else {
                    mtVmChannel.setManufactureDate(null);
                    mtVmChannel.setManufactureEndDate(null);
                    mtVmChannel.setBatchNo(null);
                }
                mtVmChannel.setBatchNo(fill.getBatchNum());
                if (null != mtVmChannel.getManufactureDate() && null != mtVmChannel.getSku() && null != mtVmChannel.getSku().getValidityType() && null != mtVmChannel.getSku().getSkuShelfLife()) {
                    if (mtVmChannel.getSku().getValidityType() == 0) {
                        //天
                        mtVmChannel.setManufactureEndDate(DateUtils.plusDay3(mtVmChannel.getManufactureDate(), mtVmChannel.getSku().getSkuShelfLife()));
                    } else if (mtVmChannel.getSku().getValidityType() == 1) {
                        //月
                        mtVmChannel.setManufactureEndDate(DateUtils.plusMonth1(mtVmChannel.getManufactureDate(), mtVmChannel.getSku().getSkuShelfLife()));
                    }
                }
            }

        }
    }

    /**
     * 补货工单详情转换
     *
     * @param taskVmChannels
     * @return
     */
    private Map<String, TTaskVmChannel> taskVmChannelListToMap(List<TTaskVmChannel> taskVmChannels, Map<String, TTaskVmChannel> taskVmChannelCleanMap) {
        Map<String, TTaskVmChannel> taskVmChannelMap = new HashMap<>();
        if (null != taskVmChannels && taskVmChannels.size() > 0) {
            for (TTaskVmChannel taskVmChannel : taskVmChannels) {
                if (null != taskVmChannel.getTaskType() && TTaskVmChannel.CHANNEL_TYPE_CLEAN == taskVmChannel.getTaskType()) {
                    taskVmChannelCleanMap.put(taskVmChannel.getChannel().getChannelCode(), taskVmChannel);
                } else {
                    taskVmChannelMap.put(taskVmChannel.getChannel().getChannelCode(), taskVmChannel);
                }
            }
        }
        return taskVmChannelMap;
    }


    /**
     * 下一次货道转化为当前货道
     *
     * @param mtVm
     * @param currChannels
     * @param nextChannelMap
     * @param hasNext        是否需要转化
     * @return
     */
    private Map<String, TMtVmChannel> nextToCurrChannel(TMtVm mtVm, List<TMtVmChannel> currChannels, Map<String, TMtVmChannelNext> nextChannelMap, boolean hasNext) {

        Map<String, TMtVmChannel> currChannelMap = new HashMap<>();
        if (hasNext) {
            logger.info("售货机存在将要应用策略或者必须发送当前货道");
            for (TMtVmChannel tMtVmChannel : currChannels) {

                //修改当前应用货道信息
                TMtVmChannelNext next = nextChannelMap.get(tMtVmChannel.getChannelCode());
                tMtVmChannel.setMergeQty(next.getMergeQty());

                tMtVmChannel.setMergePid(getMergePIdByNext(mtVm, next, mtVm.getObjectContext()));
                tMtVmChannel.setSku(next.getSku());
                tMtVmChannel.setMaxCapacity(next.getAdjustMaxCapacity());
                tMtVmChannel.setAdjustMaxCapacity(next.getAdjustMaxCapacity());
                TMtSku nextSku = next.getSku();
                TMtSku currSku = tMtVmChannel.getSku();
                if ((null == nextSku || null == currSku) || !currSku.getId().equals(nextSku.getId())) {
                    //如果商品有变更
                    tMtVmChannel.setManufactureEndDate(null);
                    tMtVmChannel.setManufactureDate(null);
                    tMtVmChannel.setBatchNo(null);
                    tMtVmChannel.setHasNextStock(TMtVmChannel.DISTRIBUTION_INIT);
                }

                //修改将要应用货道信息
                next.setMergeQty(1);
                next.setMergePid(null);
                next.setMaxCapacity(next.getFixedCapacity() != null ? next.getFixedCapacity() : 0);
                next.setAdjustMaxCapacity(0);
                next.setSku(null);
                next.setUtime(new Date());
                currChannelMap.put(tMtVmChannel.getChannelCode(), tMtVmChannel);
            }
        } else {
            for (TMtVmChannel tMtVmChannel : currChannels) {
                currChannelMap.put(tMtVmChannel.getChannelCode(), tMtVmChannel);
            }
        }
        return currChannelMap;
    }

    private String sendSupplyPolocy(TMtVm mtVm, Long sn) {
        String errMsg = "";

        HeadMessage message = factory.createHeadMessage();

        packMessage(mtVm.getInnerCode(), message, true, sn);

        message.getData().setMsgType(MsgType.SUPPLYPOLICY);

        formatSupplyPolicy(message, mtVm.getChannelInfo());

        String headJson = factory.toJson(message);
        try {
            logger.info("sendSupplyPolocy 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendSupplyPolocy 返回结果 :" + result);

            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "发送货道库存数据失败";
                logger.error("发送货道库存数据失败，MCS无返回结果");
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "发送货道库存数据失败";
                    logger.error("发送货道库存失败，MCS返回信息为发送失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            errMsg = "发送货道库存数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + e.getLocalizedMessage(), e);
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            errMsg = "JSONException 发送货道库存数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + jsonE.getLocalizedMessage(), jsonE);
        }
        return errMsg;
    }


    /**
     * 完成投放工单
     *
     * @param context
     * @param tkTaskVm
     * @return
     */
    private String finishDistributionWorkOrder(ObjectContext context,
                                               TTaskVm tkTaskVm) {
        String errMsg = "";
        try {
            tkTaskVm.setUtime(new Date());
            tkTaskVm.setUtor(UserUtils.getVmsSystem().getId());
            tkTaskVm.setFinishTime(new Date());
            tkTaskVm.setTaskVmStatus(TTaskVm.STATUS_FINISHED);
            context.commitChanges();
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            errMsg = "补货工单完成失败";
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }


    @Override
    public String sendSkuPriceToMcs(ObjectContext context, TMtVm mtVm, boolean isMustSend) {
        String errMsg = "";

        HeadMessage message = factory.createHeadMessage();

        packMessage(mtVm.getInnerCode(), message, true, null);

        message.getData().setMsgType(MsgType.SKUPRICE);
        //根据商品信息及商品的促销信息组装商品价格信息

        boolean isEventTime = formatSkuPrice(context, message, mtVm);

        message.getData().setSkuCfgs(null);
        String headJson = factory.toJson(message);
        try {
            if (!isEventTime && !isMustSend) {
                logger.info("不在促销时间范围内,不发送");
            } else {
                logger.info("sendSkuPriceToMcs 入参 :" + headJson);
                String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
                logger.info("sendSkuPriceToMcs 返回结果 :" + result);
                MessageResult messageResult = factory.toMessageResult(result);
                if (null == messageResult) {
                    errMsg = "发送商品价格失败，MCS无返回结果";
                } else {
                    if (0 != messageResult.getSuccess()) {
                        errMsg = "发送商品价格失败，MCS返回信息为发送失败";
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            errMsg = "发送商品价格失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + e.getLocalizedMessage(), e);
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            logger.error(jsonE.getMessage());
            errMsg = "JSONException 发送商品价格失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + jsonE.getLocalizedMessage(), jsonE);
        }
        return errMsg;
    }


    private boolean formatSkuPrice(ObjectContext context, HeadMessage message, TMtVm mtVm) {
        boolean isEnevtTime = false;//是否在促销时间范围内
        //List<TMtVmSkuSort> skuSortList = mtVm.getSkuSort();
        List<TMtVmSkuSort> skuSortList = mtVmSkuSortDao.findSkuSort(mtVm).stream().map(TMtVmSkuSort.class::cast).collect(Collectors.toList());
        if (null == skuSortList || skuSortList.size() == 0) {
            logger.info("拼装商品价格,售货机不存在商品 vmId = " + mtVm.getId());
            return isEnevtTime;
        }
        Map<Integer, TMtVmPrice> priceMap = new HashMap<Integer, TMtVmPrice>();
        Map<Integer, Integer> skuPriceMap = new HashMap<Integer, Integer>();

        //5.0.0a
        List<TMtVmPrice> prices = null;
        switch (mtVm.getVmType()) {
            case TMtVmModel.COMBINATORIAL_SINGLE:
                prices = mtVm.getPrice();
                break;
            case TMtVmModel.COMBINATORIAL_MAIN:
                prices = mtVm.getPrice();
                break;
            case TMtVmModel.COMBINATORIAL_EXTENTION:
                prices = mtVm.getParentVm().getPrice();
                break;
        }


        formatSkuConfigBySortAndPrice(context, mtVm, skuSortList, priceMap, prices, message, skuPriceMap);

        Integer vmId = mtVm.getId();
        Date currTime = new Date();//当前时间
        //计算商品价格
        List<SkuPrice> priceList = message.getData().getSkuPrices();
        //整机促销
        TMtVmEventMaster mtVmEvent = null;
        //商品促销
        TMtVmEventMaster mtSkuEvent = null;
        //List<TMtVmEventMaster> eventList = mtVm.getEvent();

        //5.0.0a
        List<TMtVmEventMaster> eventList = null;
        switch (mtVm.getVmType()) {
            case TMtVmModel.COMBINATORIAL_SINGLE:
                eventList = mtVm.getEvent();
                break;
            case TMtVmModel.COMBINATORIAL_MAIN:
                eventList = mtVm.getEvent();
                break;
            case TMtVmModel.COMBINATORIAL_EXTENTION:
                eventList = mtVm.getParentVm().getEvent();
                break;
        }


        if (eventList != null && eventList.size() > 0) {
            for (TMtVmEventMaster tMtVmEventMaster : eventList) {
                if (tMtVmEventMaster.getEventType() == TMtVmEventMaster.EVENT_TYPE_VM) {
                    mtVmEvent = tMtVmEventMaster;
                } else if (tMtVmEventMaster.getEventType() == TMtVmEventMaster.EVENT_TYPE_SKU) {
                    mtSkuEvent = tMtVmEventMaster;
                }
            }
        }


        if (null != mtVmEvent) {
            //存在售货机整机促销策略
            logger.info("售货机存在整机促销策略,vmId = " + vmId + "整机策略:" + mtVmEvent);
            Date endDate = mtVmEvent.getEndDate();
            String endTimeStr = mtVmEvent.getEndTime();

            Date startDate = mtVmEvent.getBeginDate();
            String startTimeStr = mtVmEvent.getStartTime();

            if (endDate == null) {
                logger.error("售货机整机促销策略中结束日期为空,vmId = " + vmId);
                return isEnevtTime;
            }
            if (StringUtil.isEmpty(endTimeStr)) {
                logger.error("售货机整机促销策略中结束时间为空,vmId = " + vmId);
                return isEnevtTime;
            }
            if (startDate == null) {
                logger.error("售货机整机促销策略中开始日期为空,vmId = " + vmId);
                return isEnevtTime;
            }
            if (StringUtil.isEmpty(startTimeStr)) {
                logger.error("售货机整机促销策略中开始时间为空,vmId = " + vmId);
                return isEnevtTime;
            }

            String startDateStr = StringUtil.dateToStr(startDate, StringUtil.YMDPattern);
            String endDateStr = StringUtil.dateToStr(endDate, StringUtil.YMDPattern);
            //经过计算得出的开始时间字符串，例：2016-11-23 11:23 -->  20161123112300
            String beginTimeStr = StringUtil.dateToStr(StringUtil.strToDate(startDateStr + " " + startTimeStr + ":00", "yyyy-MM-dd HH:mm:ss"), "yyyyMMddHHmmss");
            //经过计算得出的停止时间字符串，例：2016-11-23 11:23 -->  20161123112300
            String stopTimeStr = StringUtil.dateToStr(StringUtil.strToDate(endDateStr + " " + endTimeStr + ":00", "yyyy-MM-dd HH:mm:ss"), "yyyyMMddHHmmss");

            //当前时间字符串     例：2016-11-23 11:23:45-->  20161123112345
            String currTimeStr = StringUtil.dateToStr(currTime, "yyyyMMddHHmmss");

//			Long currYMDLong = Long.parseLong(currTimeStr.substring(0, 8));//当前时间年月日   例如20161123
            Long currYMDLong = getCurrYMDLong(currTimeStr);
            Long currHMSLong = getCurrTimeLong(currTimeStr);

//			Long currHMSLong = Long.parseLong(currTimeStr.substring(8));//当前时间时分秒 112345
            Long beginYMDLong = Long.parseLong(beginTimeStr.substring(0, 8));//开始时间年月日
            Long beginHMSLong = Long.parseLong(beginTimeStr.substring(8));//开始时间时分秒
            Long stopYMDLong = Long.parseLong(stopTimeStr.substring(0, 8));//结束时间年月日
            Long stopHMSLong = Long.parseLong(stopTimeStr.substring(8));//结束时间时分秒
            Long stopYHDHMSLong = Long.parseLong(stopTimeStr);//结束时间年月日时分秒

            //判断当前时间是不是出于促销时间段，计算促销结果
            //如果当前时间已经比最后一次促销结束时间晚，不再处理
            //如果当前时间的促销不是最后一次促销，把下一次促销时间写到redis中
            if (beginYMDLong <= currYMDLong && currYMDLong <= stopYMDLong) {
                //年月日处于促销期
                if (currHMSLong < beginHMSLong) {
                    String nextStartTimeStr = "";
                    if (beginHMSLong.toString().length() != 6) {
                        int index = 6 - beginHMSLong.toString().length();
                        String demp = "";
                        for (int i = 0; i < index; i++) {
                            demp += "0";
                        }
                        nextStartTimeStr = beginYMDLong.toString() + demp + beginHMSLong.toString();
                    } else {
                        nextStartTimeStr = beginYMDLong.toString() + beginHMSLong.toString();
                    }
                    Date nextStartTime = StringUtil.strToDate(nextStartTimeStr, "yyyyMMddHHmmss");
                    long mills = Math.abs(nextStartTime.getTime() - currTime.getTime());

                    logger.info("key = " + CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId() + "  ");
                    logger.info("过期时间 = " + (mills / 1000 == 0 ? 1 : mills / 1000));

                    myJedisCluster.setPexpireKey(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId(),
                            CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId(),
                            (int) mills / 1000 == 0 ? 1 : (int) mills / 1000);

                } else if (beginHMSLong <= currHMSLong && currHMSLong < stopHMSLong) {
                    //时分秒处于促销期

                    //计算本次结束时间
                    String nextStopTimeStr = "";
                    if (stopHMSLong.toString().length() != 6) {
                        int index = 6 - stopHMSLong.toString().length();
                        String demp = "";
                        for (int i = 0; i < index; i++) {
                            demp += "0";
                        }
                        nextStopTimeStr = currYMDLong.toString() + demp + stopHMSLong.toString();
                    } else {
                        nextStopTimeStr = currYMDLong.toString() + stopHMSLong.toString();
                    }

                    Date nextEndTime = StringUtil.strToDate(nextStopTimeStr, "yyyyMMddHHmmss");
                    //将本次结束时间和当前时间的差，存到redis
                    long mills = Math.abs(nextEndTime.getTime() - currTime.getTime());

                    logger.info("key = " + CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId() + "  ");
                    logger.info("过期时间 = " + (mills / 1000 == 0 ? 1 : mills / 1000));

                    myJedisCluster.setPexpireKey(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId(),
                            CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId(),
                            (int) mills / 1000 == 0 ? 1 : (int) mills / 1000);

                    List<TMtVmEvent> evemtDetails = mtVmEvent.getEvents();

                    if (null != evemtDetails && evemtDetails.size() > 0) {
                        logger.info("存在售货机整机促销详情  vmId = " + vmId + " evemtDetails " + evemtDetails);
                        TMtVmEvent evemtDetail = evemtDetails.get(0);
                        Integer discountType = evemtDetail.getDiscountType();//促销类型 1:百分比;2:绝对值
                        Integer discountValue = evemtDetail.getDiscountValue();
                        if (1 == discountType) {
                            //1:百分比
                            logger.info("存在售货机促销，促销类型 百分比 discountValue = " + discountValue + ".vmId = " + vmId);
                            for (SkuPrice skuPrice : priceList) {
                                Double dempDouble = skuPrice.getPrice() * discountValue / 100.0;
                                skuPrice.setPrice(dempDouble.intValue());
                                skuPrice.setIsDiscount(true);
                            }
                        } else if (2 == discountType) {
                            //2:绝对值
                            logger.info("存在售货机促销，促销类型 绝对值 discountValue = " + discountValue + ".vmId = " + vmId);
                            for (SkuPrice skuPrice : priceList) {
                                skuPrice.setPrice(discountValue);
                                skuPrice.setIsDiscount(true);
                            }
                        } else {
                            logger.error("没有匹配的促销类型，discountType = " + discountType + ",enentId = " + mtVmEvent.getEventId());
                        }
                    }
                    isEnevtTime = true;//在促销时间范围内
                } else if (currHMSLong >= stopHMSLong) {
                    //超出结束时间了，计算出下次开始时间写入redis
                    Long nextBeginYMDLong = currYMDLong + 1;
                    String nextBeginYMDHMSStr = "";
                    if (beginHMSLong.toString().length() != 6) {
                        int index = 6 - beginHMSLong.toString().length();
                        String demp = "";
                        for (int i = 0; i < index; i++) {
                            demp += "0";
                        }
                        nextBeginYMDHMSStr = nextBeginYMDLong.toString() + demp + beginHMSLong.toString();
                    } else {
                        nextBeginYMDHMSStr = nextBeginYMDLong.toString() + beginHMSLong.toString();
                    }
                    //判断下一次开始时间是不是已经超过了最终结束时间
                    if (Long.parseLong(nextBeginYMDHMSStr) <= stopYHDHMSLong) {
                        logger.error("下一次开始时间在有效时间范围内，将下次开始时间写入redis");
                        Date nextStartTime = StringUtil.strToDate(nextBeginYMDHMSStr, "yyyyMMddHHmmss");
                        long mills = Math.abs(nextStartTime.getTime() - currTime.getTime());

                        logger.info("key = " + CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId() + "  ");
                        logger.info("过期时间 = " + (mills / 1000 == 0 ? 1 : mills / 1000));

                        myJedisCluster.setPexpireKey(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId(),
                                CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtVmEvent.getId(),
                                (int) mills / 1000 == 0 ? 1 : (int) mills / 1000);
                    } else {
                        logger.error("下一次开始时间不在有效时间范围内，不记录下次开始时间");
                    }
                }
            }
        }
        if (null != mtSkuEvent) {
            //存在售货机商品促销策略
            logger.info("售货机存在商品促销策略,vmId = " + mtVm.getId() + "商品促销策略:" + mtSkuEvent);


            //当前售货机有商品促销，获取商品促销详情
            Date endDate = mtSkuEvent.getEndDate();
            String endTimeStr = mtSkuEvent.getEndTime();

            Date startDate = mtSkuEvent.getBeginDate();
            String startTimeStr = mtSkuEvent.getStartTime();

            if (endDate == null) {
                logger.error("售货机商品促销策略中结束日期为空,vmId = " + vmId);
                return isEnevtTime;
            }
            if (StringUtil.isEmpty(endTimeStr)) {
                logger.error("售货机商品促销策略中结束时间为空,vmId = " + vmId);
                return isEnevtTime;
            }
            if (startDate == null) {
                logger.error("售货机商品促销策略中开始日期为空,vmId = " + vmId);
                return isEnevtTime;
            }
            if (StringUtil.isEmpty(startTimeStr)) {
                logger.error("售货机商品促销策略中开始时间为空,vmId = " + vmId);
                return isEnevtTime;
            }

            String startDateStr = StringUtil.dateToStr(startDate, StringUtil.YMDPattern);
            String endDateStr = StringUtil.dateToStr(endDate, StringUtil.YMDPattern);
            //经过计算得出的开始时间字符串，例：2016-11-23 11:23 -->  20161123112300
            String beginTimeStr = StringUtil.dateToStr(StringUtil.strToDate(startDateStr + " " + startTimeStr + ":00", "yyyy-MM-dd HH:mm:ss"), "yyyyMMddHHmmss");
            //经过计算得出的停止时间字符串，例：2016-11-23 11:23 -->  20161123112300
            String stopTimeStr = StringUtil.dateToStr(StringUtil.strToDate(endDateStr + " " + endTimeStr + ":00", "yyyy-MM-dd HH:mm:ss"), "yyyyMMddHHmmss");

            //当前时间字符串     例：2016-11-23 11:23:45-->  20161123112345
            String currTimeStr = StringUtil.dateToStr(currTime, "yyyyMMddHHmmss");

//			Long currYMDLong = Long.parseLong(currTimeStr.substring(0, 8));//当前时间年月日   例如20161123
            Long currYMDLong = getCurrYMDLong(currTimeStr);
            Long currHMSLong = getCurrTimeLong(currTimeStr);
//			Long currHMSLong = Long.parseLong(currTimeStr.substring(8));//当前时间时分秒 112345
            Long beginYMDLong = Long.parseLong(beginTimeStr.substring(0, 8));//开始时间年月日
            Long beginHMSLong = Long.parseLong(beginTimeStr.substring(8));//开始时间时分秒
            Long stopYMDLong = Long.parseLong(stopTimeStr.substring(0, 8));//结束时间年月日
            Long stopHMSLong = Long.parseLong(stopTimeStr.substring(8));//结束时间时分秒
            Long stopYHDHMSLong = Long.parseLong(stopTimeStr);//结束时间年月日时分秒
            if (beginYMDLong <= currYMDLong && currYMDLong <= stopYMDLong) {
                //年月日处于促销期
                if (currHMSLong < beginHMSLong) {
                    //计算距离开始促销还有多久，写入redis
                    String nextStartTimeStr = beginYMDLong.toString() + beginHMSLong.toString();
                    Date nextStartTime = StringUtil.strToDate(nextStartTimeStr, "yyyyMMddHHmmss");
                    long mills = Math.abs(nextStartTime.getTime() - currTime.getTime());

                    logger.info("key = " + CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId() + "  ");
                    logger.info("过期时间 = " + (mills / 1000 == 0 ? 1 : mills / 1000));

                    myJedisCluster.setPexpireKey(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId(),
                            CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId(),
                            (int) mills / 1000 == 0 ? 1 : (int) mills / 1000);
                } else if (beginHMSLong <= currHMSLong && currHMSLong < stopHMSLong) {

                    String nextStopTimeStr = "";
                    if (stopHMSLong.toString().length() != 6) {
                        int index = 6 - stopHMSLong.toString().length();
                        String demp = "";
                        for (int i = 0; i < index; i++) {
                            demp += "0";
                        }
                        nextStopTimeStr = currYMDLong.toString() + demp + stopHMSLong.toString();
                    } else {
                        nextStopTimeStr = currYMDLong.toString() + stopHMSLong.toString();
                    }
                    Date nextEndTime = StringUtil.strToDate(nextStopTimeStr, "yyyyMMddHHmmss");
                    //将本次结束时间和当前时间的差，存到redis
                    long mills = Math.abs(nextEndTime.getTime() - currTime.getTime());

                    logger.info("key = " + CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId() + "  ");
                    logger.info("过期时间 = " + (mills / 1000 == 0 ? 1 : mills / 1000));

                    myJedisCluster.setPexpireKey(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId(),
                            CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId(),
                            (int) mills / 1000 == 0 ? 1 : (int) mills / 1000);


                    List<TMtVmEvent> mdseEventDetailList = mtSkuEvent.getEvents();
                    if (mdseEventDetailList == null || mdseEventDetailList.size() == 0) {
                        logger.error("根据售货机查询商品促销信息为空，vmId = " + vmId + "商品促销主键eventId = " + mtSkuEvent.getEventId());
                    } else {
                        Map<Integer, TMtVmEvent> mdseEventMap = new HashMap<Integer, TMtVmEvent>();
                        for (TMtVmEvent stEventMdse : mdseEventDetailList) {
                            if (stEventMdse.getSku() != null) {
                                mdseEventMap.put(stEventMdse.getSku().getId(), stEventMdse);
                            }
                        }
                        for (SkuPrice skuPrice : priceList) {
                            if (mdseEventMap.containsKey(skuPrice.getSkuId())) {
                                logger.error("商品促销策略里面存在,skuId = " + skuPrice.getSkuId());
                                TMtVmEvent stEventMdseDemp = mdseEventMap.get(skuPrice.getSkuId());
                                Integer discountType = stEventMdseDemp.getDiscountType();
                                Integer discountValue = stEventMdseDemp.getDiscountValue();
                                if (null == discountValue || 0 == discountValue) {
                                    logger.error("商品促销策略里面存在,skuId = " + skuPrice.getSkuId() + ",discountValue = " + discountValue);
                                    continue;
                                }
                                Integer oldSkuPrice = skuPriceMap.get(skuPrice.getSkuId());
                                logger.info("商品促销略里面存在,skuId = " + skuPrice.getSkuId() + "未计算商品促销策略之前价格 oldPrice = " + oldSkuPrice);
                                if (1 == discountType) {
                                    //百分比
                                    skuPrice.setIsDiscount(true);
                                    Double dempDouble = oldSkuPrice * discountValue / 100.0;
                                    skuPrice.setPrice(dempDouble.intValue());
                                } else if (2 == discountType) {
                                    //绝对值
                                    skuPrice.setIsDiscount(true);
                                    skuPrice.setPrice(discountValue);
                                } else {
                                    logger.error("商品促销策略里面存在,skuId = " + skuPrice.getSkuId() + ",但是没有匹配的促销类型 discountType = " + discountType);
                                }
                                logger.info("商品促销略里面存在,skuId = " + skuPrice.getSkuId() + "计算商品促销策略之后价格 skuPrice = " + skuPrice.getPrice());
                            }
                        }
                    }
                    isEnevtTime = true;//在促销时间范围内
                } else if (currHMSLong >= stopHMSLong) {
                    //超出结束时间了，计算出下次开始时间写入redis
                    Long nextBeginYMDLong = currYMDLong + 1;
                    String nextBeginYMDHMSStr = "";
                    if (beginHMSLong.toString().length() != 6) {
                        int index = 6 - beginHMSLong.toString().length();
                        String demp = "";
                        for (int i = 0; i < index; i++) {
                            demp += "0";
                        }
                        nextBeginYMDHMSStr = nextBeginYMDLong.toString() + demp + beginHMSLong.toString();
                    } else {
                        nextBeginYMDHMSStr = nextBeginYMDLong.toString() + beginHMSLong.toString();
                    }

                    Date nextStartTime = StringUtil.strToDate(nextBeginYMDHMSStr, "yyyyMMddHHmmss");
                    if (Long.parseLong(nextBeginYMDHMSStr) <= stopYHDHMSLong) {
                        logger.info("下一次开始时间在有效时间范围内，将下次开始时间写入redis");
                        long mills = Math.abs(nextStartTime.getTime() - currTime.getTime());

                        logger.info("key = " + CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId() + "  ");
                        logger.info("过期时间 = " + (mills / 1000 == 0 ? 1 : mills / 1000));

                        myJedisCluster.setPexpireKey(CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId(),
                                CacheKeysUtil.VMS_MDSE_SALE_EVENT_TASK + ":" + vmId + ":" + mtSkuEvent.getId(),
                                (int) mills / 1000 == 0 ? 1 : (int) mills / 1000);
                    } else {
                        logger.info("下一次开始时间不在有效时间范围内，不将下次开始时间写入redis");
                    }
                }
            }

        }

        return isEnevtTime;
    }


    /**
     * 获取当前年月日
     *
     * @param currTimeStr
     * @return
     */
    private Long getCurrYMDLong(String currTimeStr) {
        try {
            Integer currss = Integer.parseInt(currTimeStr.substring(12));//当前秒数

            //如果秒大于45 按照60算
            if (currss >= 45) currss = 60;
            String timeStr = currTimeStr.substring(0, 12) + currss + "";

            String time = StringUtil.dateToStr(StringUtil.strToDate(timeStr, "yyyyMMddHHmmss"), "yyyyMMddHHmmss");
            return Long.parseLong(time.substring(0, 8));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }


    /**
     * 获取当前时间时分秒
     *
     * @param currTimeStr
     * @return
     */
    private Long getCurrTimeLong(String currTimeStr) {
        try {
            Integer currss = Integer.parseInt(currTimeStr.substring(12));//当前秒数

            //如果秒大于45 按照60算
            if (currss >= 45) currss = 60;
            String timeStr = currTimeStr.substring(0, 12) + currss + "";

            String time = StringUtil.dateToStr(StringUtil.strToDate(timeStr, "yyyyMMddHHmmss"), "yyyyMMddHHmmss");
            return Long.parseLong(time.substring(8));

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }


    /**
     * 拼装商品信息
     *
     * @param context
     * @param mtVm
     * @param skuSortList
     * @param priceMap
     * @param mtVmPrice
     * @param skuPriceMap
     */
    private void formatSkuConfigBySortAndPrice(ObjectContext context,
                                               TMtVm mtVm, List<TMtVmSkuSort> skuSortList,
                                               Map<Integer, TMtVmPrice> priceMap, List<TMtVmPrice> mtVmPrice,
                                               HeadMessage message, Map<Integer, Integer> skuPriceMap) {
        if (null != mtVmPrice) {
            for (TMtVmPrice tMtVmPrice : mtVmPrice) {
                priceMap.put(tMtVmPrice.getSkuId(), tMtVmPrice);
            }
            for (TMtVmSkuSort tMtVmSkuSort : skuSortList) {
                TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, tMtVmSkuSort.getSkuId());
                if (sku != null) {
                    SkuPrice skuPrice = new SkuPrice();
                    skuPrice.setIsDiscount(false);
                    skuPrice.setSkuId(sku.getId());

                    if (!priceMap.containsKey(tMtVmSkuSort.getSkuId())) {
                        skuPrice.setPrice(sku.getOfferPrice());
                        logger.info("售货机应用了商品价格策略,vm_id = " + mtVm.getId() + ",因为价格策略中没有商品标识，skuId = " + sku.getId() + ",所以该商品在未计算促销策略之前建议零售价为：" + skuPrice.getPrice());
                    } else {
                        skuPrice.setPrice(priceMap.get(tMtVmSkuSort.getSkuId()).getRealPrice());
                        logger.info("售货机应用了商品价格策略,vm_id = " + mtVm.getId() + ",因为价格策略中存在商品标识，skuId = " + sku.getId() + ",所以该商品在未计算促销策略之前建议零售价为：" + skuPrice.getPrice());
                    }
                    skuPriceMap.put(sku.getId(), skuPrice.getPrice());
                    message.getData().addSkuPrices(skuPrice);
                }
            }
        } else {
            for (TMtVmSkuSort tMtVmSkuSort : skuSortList) {
                TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, tMtVmSkuSort.getSkuId());
                if (sku != null) {
                    SkuPrice skuPrice = new SkuPrice();
                    skuPrice.setIsDiscount(false);
                    skuPrice.setSkuId(sku.getId());
                    skuPrice.setPrice(sku.getOfferPrice());
                    logger.info("售货机没有应用商品价格策略,vm_id = " + mtVm.getId() + ",skuId = " + sku.getId() + ",所以该商品建议零售价为：" + sku.getOfferPrice());
                    skuPriceMap.put(sku.getId(), skuPrice.getPrice());
                    message.getData().addSkuPrices(skuPrice);
                }
            }
        }
    }


    @Override
    public String sendSkuToMcs(ObjectContext context, TMtVm mtVm, boolean isCommit, boolean isMustCurr, Long sn) {
        String errMsg = "";
        HeadMessage message = factory.createHeadMessage();

        packMessageNew(mtVm.getInnerCode(), message, true, sn);

        message.getData().setMsgType(MsgType.SKUCFG);

        mdseSortToMCSSKU(context, message, mtVm, isMustCurr);

        String headJson = factory.toJson(message);
        try {
            logger.info("sendSkuToMcs 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendSkuToMcs 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "发送商品信息失败";
                logger.error("发送商品信息失败，MCS无返回结果   innerCode = " + mtVm.getInnerCode());
            } else {
                if (0 != messageResult.getSuccess()) {
                    logger.error("发送商品信息失败，MCS返回信息为发送失败   innerCode = " + mtVm.getInnerCode());
                } else {
                    if (isCommit) {
                        try {
                            context.commitChanges();
                        } catch (Exception e) {
                            e.printStackTrace();
                            context.rollbackChanges();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            errMsg = "发送商品数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + e.getLocalizedMessage(), e);
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            logger.error(jsonE.getMessage());
            errMsg = "JSONException 发送商品信息失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + jsonE.getLocalizedMessage(), jsonE);
        }
        return errMsg;
    }

    @Override
    public String sendInnerCodeToMcs(TMtVm mtVm, String uniqueCode) {
        String innerCode = mtVm.getInnerCode();

        HeadMessage message = factory.createHeadMessage();
        String errMsg = "";
        packMessageForMacAddress(uniqueCode, message, false);
        try {
            message.getData().setMsgType(MsgType.INNERCODEREQ);
            int vmType = 0;//单机还是主机，默认单机
            vmType = null == mtVm.getVmType() ? 0 : mtVm.getVmType() - 1;
            message.getData().setInnerCode(innerCode);
            message.getData().setVmType(vmType);
            String headJson = factory.toJson(message);
            logger.info("sendInnerCodeToVmBox 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendInnerCodeToVmBox 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "发送innerCode失败";
                logger.error("发送innerCode失败，MCS无返回结果   innerCode = " + innerCode);
            } else {
                if (0 != messageResult.getSuccess() && 4 != messageResult.getSuccess()) {
                    errMsg = "发送innerCode失败";
                    logger.error("发送innerCode失败，MCS返回信息为发送失败   innerCode = " + innerCode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            errMsg = "发送innerCode失败，innerCode = " + innerCode + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + e.getLocalizedMessage(), e);
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            logger.error(jsonE.getMessage());
            errMsg = "JSONException 发送innerCode失败，innerCode = " + innerCode + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + jsonE.getLocalizedMessage(), jsonE);
        }
        return errMsg;
    }


    /**
     * 拼装列表页信息
     *
     * @param message
     * @param mtVm
     */
    private void mdseSortToMCSSKU(ObjectContext context, HeadMessage message, TMtVm mtVm, boolean isMustCurr) {

        try {
            List<TMtVmPrice> mtVmPrice = mtVm.getPrice();
            List<TMtVmSkuSort> currSortList = null;
            Set<Integer> skuSet = new HashSet<>();
            if (!isMustCurr) {
                currSortList = updateVmShelf(context, mtVm);
            } else {
                currSortList = mtVm.getSkuSort();
            }
            if (mtVmPrice != null && mtVmPrice.size() > 0) {
                Map<Integer, TMtVmPrice> priceMap = new HashMap<Integer, TMtVmPrice>();
                for (TMtVmPrice tMtVmPrice : mtVmPrice) {
                    priceMap.put(tMtVmPrice.getSkuId(), tMtVmPrice);
                }
                if (currSortList != null && currSortList.size() > 0) {

                    for (TMtVmSkuSort tMtVmSkuSort : currSortList) {
                        TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, tMtVmSkuSort.getSkuId());
                        if (sku != null && !skuSet.contains(sku.getId())) {

                            SkuCfg skuCfg = new SkuCfg();

                            formatSkuSort(tMtVmSkuSort, sku, skuCfg);

                            if (!priceMap.containsKey(tMtVmSkuSort.getSkuId())) {
                                skuCfg.setOfferPrice(sku.getOfferPrice());
                                logger.info("售货机应用了商品价格策略,vm_id = " + mtVm.getId() + ",因为价格策略中没有商品标识，skuId = " + sku.getId() + ",所以该商品在未计算促销策略之前建议零售价为：" + sku.getOfferPrice());
                            } else {
                                skuCfg.setOfferPrice(priceMap.get(tMtVmSkuSort.getSkuId()).getRealPrice());
                                logger.info("售货机应用了商品价格策略,vm_id = " + mtVm.getId() + ",因为价格策略中存在商品标识，skuId = " + sku.getId() + ",所以该商品在未计算促销策略之前建议零售价为：" + skuCfg.getOfferPrice());
                            }
                            message.getData().addSkuCfgs(skuCfg);
                            skuSet.add(sku.getId());
                        }
                    }
                }
            } else {
                for (TMtVmSkuSort tMtVmSkuSort : currSortList) {
                    TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, tMtVmSkuSort.getSkuId());
                    if (sku != null && !skuSet.contains(sku.getId())) {
                        SkuCfg skuCfg = new SkuCfg();
                        formatSkuSort(tMtVmSkuSort, sku, skuCfg);

                        skuCfg.setOfferPrice(sku.getOfferPrice());
                        logger.info("售货机没有应用商品价格策略,vm_id = " + mtVm.getId() + ",skuId = " + sku.getId() + ",所以该商品建议零售价为：" + sku.getOfferPrice());
                        message.getData().addSkuCfgs(skuCfg);
                        skuSet.add(sku.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    private void formatSkuSort(TMtVmSkuSort tMtVmSkuSort, TMtSku sku,
                               SkuCfg skuCfg) {
        skuCfg.setClassId(sku.getSkuType().getId());
        skuCfg.setClassName(sku.getSkuType().getTypeName());
        skuCfg.setDesc(sku.getSkuDesc());
        skuCfg.setIndex(tMtVmSkuSort.getSort());
        skuCfg.setSkuId(sku.getId());
        skuCfg.setSkuImageUrl(sku.getImgUrl());
        skuCfg.setSkuName(sku.getSkuName());
        skuCfg.setUnit(sku.getSkuUnit());
        skuCfg.setBrand(sku.getSkuProducer());
        skuCfg.setSaleType(null == sku.getReserve() ? 0 : sku.getReserve());
        skuCfg.setGeneralizeType(null == sku.getGeneralizeType() ? 0 : sku.getGeneralizeType());
    }


    private List<TMtVmSkuSort> updateVmShelf(ObjectContext context, TMtVm mtVm) {
        TMtVm vm;
        if (null != mtVm.getVmType() && mtVm.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
            vm = mtVm.getParentVm();
        } else {
            vm = mtVm;
        }
        List<TMtVm> list = vm.getChildrenVm()
                .stream()
                .filter(tMtVm -> tMtVm.getDeleteMark().equals(DeleteMark.EXIST.getCode()))
                .collect(Collectors.toList());
        list.add(vm);
        logger.info(list.stream().map(TMtVm::getInnerCode).collect(Collectors.toList()) + "更新列表页");
        List<Integer> channelSkuList = list.parallelStream()
                .map(TMtVm::getChannelInfo)
                .flatMap(List::stream)
                .filter(channel -> channel.getSku() != null)
                .map(TMtVmChannel::getSku)
                .map(TMtSku::getId)
                .distinct()
                .collect(Collectors.toList());

        List<Integer> shelfSkuList = vm.getSkuSort().parallelStream()
                .sorted((o1, o2) -> o1.getSort() - o2.getSort())
                .map(TMtVmSkuSort::getSkuId)
                .distinct()
                .collect(Collectors.toList());
        if (channelSkuList.equals(shelfSkuList)) {
            return vm.getSkuSort();
        }
        List<Integer> addList = channelSkuList.stream().filter(skuId -> !shelfSkuList.contains(skuId)).distinct().collect(Collectors.toList());
        logger.info("列表页增加商品" + addList);
        List<Integer> removeList = shelfSkuList.stream().filter(skuId -> !channelSkuList.contains(skuId)).collect(Collectors.toList());
//        shelfSkuList.removeAll(removeList);
//        shelfSkuList.addAll(addList);
        List<TMtVmSkuSort> shelfList = vm.getSkuSort();
        List delList = shelfList.stream().filter(sort -> removeList.contains(sort.getSkuId())).collect(Collectors.toList());
        context.deleteObjects(delList);

        shelfList.sort((o1, o2) -> o1.getSort() - o2.getSort());
        int begin = CheckUtils.check(shelfList) ? shelfList.get(shelfList.size() - 1).getSort() : 0;
        for (int i = begin; i < addList.size() + begin; i++) {
            TMtVmSkuSort sort = context.newObject(TMtVmSkuSort.class);
            sort.setCtor(UserUtils.getVmsSystem().getId());
            sort.setCtime(new Date());
            sort.setMtVm(vm);
            sort.setSkuId(addList.get(i - begin));
            sort.setSort(i + 1);
            shelfList.add(sort);
            logger.info("列表页增加商品" + sort.getSkuId());
        }
        try {
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
        }
        return shelfList;
    }

    private String sendChannelInfoToMcs(TMtVm mtVm, boolean isMuseCurr, Long sn, Map<Integer, Integer> currNumMap) {
        String errMsg = "";
        HeadMessage message = factory.createHeadMessage();

        packMessage(mtVm.getInnerCode(), message, true, sn);

        message.getData().setMsgType(MsgType.CHANNELCFG);
        stVmChannelToMCSChanenl(message, mtVm, isMuseCurr, currNumMap);
        String headJson = factory.toJson(message);
        try {
            logger.info("sendChannelInfoToMcs 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendChannelInfoToMcs 返回结果 :" + result);

            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "发送货到数据失败";
                logger.error("发送货到数据失败，MCS无返回结果");
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "发送货到数据失败";
                    logger.error("发送货到数据失败，MCS返回信息为发送失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            errMsg = "发送货道数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + e.getLocalizedMessage(), e);
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            errMsg = "JSONException 发送货道数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + jsonE.getLocalizedMessage(), jsonE);
        }
        return errMsg;
    }


    @Override
    public String pushText(ObjectContext context, List<Integer> vmIdList, Long sn) {
        StringBuffer buffer = new StringBuffer();
        try {
            if (null != vmIdList && vmIdList.size() > 0) {
                logger.info("收到走马灯请求请求, vmIdList = " + Arrays.toString(vmIdList.toArray()));
                int length = vmIdList.size();
                for (int i = 0; i < length; i++) {
                    Integer vmId = vmIdList.get(i);
                    TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
                    if (null == mtVm) {
                        logger.info("根据vmId没有查到售货机信息，vmId = " + vmId);
                        continue;
                    }
                    TMtVmText mtVmText = mtVm.getMtText();
                    if (null == mtVmText) {
                        logger.info("根据vmId没有查到走马灯信息,vmId = " + vmId);
                        continue;
                    }

                    String startTimeStr = StringUtil.dateToStr(mtVmText.getBeginDate(), StringUtil.YMDPattern);
                    String endTimeStr = StringUtil.dateToStr(mtVmText.getEndDate(), StringUtil.YMDPattern);

                    //将走马灯发给MCS
                    HeadMessage message = factory.createHeadMessage();

                    packMessage(mtVm.getInnerCode(), message, true, sn);

                    message.getData().setMsgType(MsgType.REPEATTEXT);
                    RepeatText repeatText = new RepeatText();
                    repeatText.setStartTime(StringUtil.isEmpty(startTimeStr) ? "00:00:00" : startTimeStr + " 00:00:00");
                    repeatText.setEndTime(StringUtil.isEmpty(endTimeStr) ? "23:59:59" : endTimeStr + " 23:59:59");
                    repeatText.setText(mtVmText.getTextContents());
                    message.getData().setRepeatText(repeatText);
                    String headJson = factory.toJson(message);
                    logger.info("pushText 入参 :" + headJson);
                    String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.readTimeout, WebUtils.readTimeout);
                    logger.info("pushText 返回结果 :" + result);
                    MessageResult messageResult = factory.toMessageResult(result);
                    if (null == messageResult) {
                        logger.info("发送走马灯信息失败，MCS无返回结果  innerCode = " + mtVm.getInnerCode());
                        buffer.append("应用走马灯信息失败，售货机编码:" + mtVm.getInnerCode());
                    } else {
                        if (0 != messageResult.getSuccess()) {
                            buffer.append("应用走马灯信息失败，售货机编码:" + mtVm.getInnerCode());
                            logger.info("发送走马灯信息失败，MCS返回信息为发送失败  innerCode = " + mtVm.getInnerCode());
                        }
                    }
                }
            } else {
                logger.info("收到走马灯请求请求, vmIdList 为空");
                return "请选择售货机";
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException 发送走马灯信息失败 " + e.getLocalizedMessage(), e);
            buffer.append("应用走马灯失败!!");
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            buffer.append("发送走马灯信息失败");
            logger.error("JSONException 发送走马灯信息失败 " + jsonE.getLocalizedMessage(), jsonE);
        }
        return buffer.toString();
    }

    @Override
    public String pushShortCuts(ObjectContext context, List<Integer> vmIdList, Long sn) {
        StringBuffer buffer = new StringBuffer();
        try {
            if (null != vmIdList && vmIdList.size() > 0) {
                logger.info("收到闪购键请求, vmIdList = " + Arrays.toString(vmIdList.toArray()));
                int length = vmIdList.size();
                for (int i = 0; i < length; i++) {
                    Integer vmId = vmIdList.get(i);
                    TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
                    if (null == mtVm) {
                        logger.error("pushShortCuts,根据vmId获取售货机信息失败，vmId = " + vmId);
                        continue;
                    }
                    String innerCode = mtVm.getInnerCode();//售货机编号
                    List<TMtVmShortcuts> shortCutsLists = mtVm.getShortcuts();
                    if (null != shortCutsLists && shortCutsLists.size() > 0) {
                        //将售货机闪购键信息转换之后推送给MCS

                        HeadMessage message = factory.createHeadMessage();

                        packMessage(innerCode, message, true, sn);

                        shortCutsToMCSSpInfo(message, shortCutsLists);

                        message.getData().setMsgType(MsgType.SPCFG);
                        String headJson = factory.toJson(message);
                        try {
                            logger.info("pushShortCuts 入参 :" + headJson);
                            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
                            logger.info("pushShortCuts 返回结果 :" + result);
                            MessageResult messageResult = factory.toMessageResult(result);
                            if (null == messageResult) {
                                logger.error("应用闪购键失败 MCS无返回结果 innerCode = " + mtVm.getInnerCode());
                                buffer.append("应用闪购键失败 innerCode = " + mtVm.getInnerCode());

                            } else {
                                if (0 != messageResult.getSuccess()) {
                                    logger.error("发送闪购键信息失败，MCS返回信息为发送失败innerCode = " + mtVm.getInnerCode());
                                    buffer.append("应用闪购键失败 innerCode = " + mtVm.getInnerCode());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            buffer.append("应用闪购键失败");
                            logger.error(" IOException 应用闪购键信息失败   " + e.getLocalizedMessage(), e);
                        } catch (JSONException jsonE) {
                            jsonE.printStackTrace();
                            buffer.append("应用闪购键失败");
                            logger.error("JSONException 应用闪购键信息失败 " + jsonE.getLocalizedMessage(), jsonE);
                        }
                    } else {
                        logger.error("pushShortCuts 根据vmId未查到，闪购键信息，vmId = " + vmId);
                        continue;
                    }
                }
            } else {
                logger.info("收到闪购键请求, vmIdList 为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" Exception 应用闪购键失败" + e.getLocalizedMessage(), e);
            buffer.append("应用闪购键失败");
        }
        return buffer.toString();
    }

    @Override
    public String pushEnergy(ObjectContext context, List<Integer> vmIdList, Long sn) {
        StringBuffer buffer = new StringBuffer();
        try {
            if (null != vmIdList && vmIdList.size() > 0) {
                int length = vmIdList.size();
                TMtVm firstMtVm = mtVmDao.findByVmId(context, vmIdList.get(0));
                Integer vmModelId = firstMtVm.getVmModelId();
                TMtVmModel mtVmModel = Cayenne.objectForPK(context, TMtVmModel.class, vmModelId);
                for (int i = 0; i < length; i++) {
                    Integer vmId = vmIdList.get(i);
                    TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
                    if (null != mtVm) {
                        List<TMtVmEnergySave> energySaveList = mtVm.getEnergySave();
                        if (null != energySaveList) {
                            //拼装信息，调用发送信息方法
                            //通知MCS节能信息
                            //old下发消息
                            HeadMessage oldMessage = factory.createHeadMessage();
                            packMessage(mtVm.getInnerCode(), oldMessage, true, sn);
                            oldMessage.getData().setTempBoxCount(mtVmModel.getTempBoxCount());
                            oldMessage.getData().setMsgType(MsgType.MTENERGY);
                            mtVmEnergyToMCSEnerg(oldMessage, energySaveList);
                            String oldHeadJson = factory.toJson(oldMessage);
                            //old
                            logger.info("pushEnergy 入参 :" + oldHeadJson);
                            String oldResult = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, oldHeadJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
                            logger.info("pushEnergy 返回结果 :" + oldResult);
                            MessageResult oldMessageResult = factory.toMessageResult(oldResult);
                            if (null == oldMessageResult) {
                                logger.info("发送节能信息失败，MCS无返回结果  innerCode = " + mtVm.getInnerCode());
                                buffer.append("应用节能信息失败，售货机编码:" + mtVm.getInnerCode());
                            } else {
                                if (0 != oldMessageResult.getSuccess()) {
                                    logger.info("发送节能信息失败，MCS返回信息为发送失败  innerCode = " + mtVm.getInnerCode());
                                    buffer.append("应用节能信息失败，售货机编码:" + mtVm.getInnerCode());
                                }
                            }
                        } else {
                            logger.info(" 发送节能策略  根据vmId没有查到未同步完成的节能信息，vmId=" + vmId);
                        }

                    } else {
                        logger.info(" 发送节能策略  根据vmId没有查到售货机信息，vmId=" + vmId);
                    }
                }
            } else {
                buffer.append("请选择售货机");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException 发送节能信息失败 " + e.getLocalizedMessage(), e);
            buffer.append("应用节能失败！！！");
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            buffer.append("应用节能失败！！！");
            logger.error("JSONException 发送节能信息失败 " + jsonE.getLocalizedMessage(), jsonE);
        }
        return buffer.toString();
    }

    @Override
    public String getOpenDoorPwd(ObjectContext context, Integer vmId, Long sn) {
        logger.info("收到获取开门密码请求,vmId = " + vmId);
        String pwd = getPwd(6);
        try {
            TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
            TMtVmLock lock = mtVm.getLock();
            if (lock == null) {
                lock = context.newObject(TMtVmLock.class);
                lock.setCtor(UserUtils.getVmsSystem().getId());
                lock.setCtime(new Date());
                lock.setLockKey("111111");
                lock.setMtVm(mtVm);
                try {

                    context.commitChanges();
                } catch (Exception e) {
                    context.rollbackChanges();
                    logger.error("获取开门密码,检测到售货机没有开门密,尝试插入开门密码失败   vmId = " + vmId);
                }
            }
            HeadMessage message = factory.createHeadMessage();

            packMessage(mtVm.getInnerCode(), message, false, sn);

            message.getData().setMsgType(MsgType.OPENDOORPWD);
            message.getData().setOpenDoorPwd(pwd);
            String headJson = factory.toJson(message);

            logger.info("getOpenDoorPwd 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("getOpenDoorPwd 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                logger.info("发送开门密码信息失败，MCS无返回结果");
                return lock.getLockKey();
            } else {
                if (0 != messageResult.getSuccess()) {
                    logger.info("发送开门密码信息失败，MCS返回信息为发送失败");
                    return lock.getLockKey();
                } else {
                    lock.setLockKey(pwd);
                    try {
                        context.commitChanges();
                    } catch (Exception e) {
                        context.rollbackChanges();
                        logger.error(" 获取开门密码，保存数据失败  " + e.getLocalizedMessage(), e);
                    }
                    return pwd;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            logger.error(jsonE.getMessage());
            logger.error("JSONException 发送开门密码失败 " + jsonE.getMessage());
        }
        return pwd;
    }

    public void notifyOpenDoorPwd(ObjectContext context, Integer vmId) {
        logger.info("主动通知开门密码,vmId = " + vmId);
        String pwd = getPwd(6);
        try {
            TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
            if (null == mtVm) {
                logger.error("售货机不存在");
                return;
            }
            HeadMessage message = factory.createHeadMessage();
            packMessage(mtVm.getInnerCode(), message, true, SystemUtils.createSn());
            message.getData().setMsgType(MsgType.OPENDOORPWD);
            message.getData().setOpenDoorPwd(pwd);
            String headJson = factory.toJson(message);

            logger.info("notifyOpenDoorPwd入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("notifyOpenDoorPwd返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null != messageResult && 0 == messageResult.getSuccess()) {
                TMtVmLock lock = mtVm.getLock();
                if (lock == null) {
                    lock = context.newObject(TMtVmLock.class);
                    lock.setCtor(UserUtils.getVmsSystem().getId());
                    lock.setCtime(new Date());
                    lock.setLockKey("111111");
                    lock.setMtVm(mtVm);
                } else {
                    lock.setLockKey(pwd);
                    lock.setUtime(new Date());
                    lock.setUtor(UserUtils.getVmsSystem().getId());
                }
                context.commitChanges();
            } else {
                logger.info("通知mcs失败");
            }
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    /**
     * 拼装HeadMessage 信息
     *
     * @param innerCode  售货机编号
     * @param message    消息
     * @param needReSend 是否需要重发
     */
    private void packMessage(String innerCode, HeadMessage message, boolean needReSend, Long sn) {
        message.setFrom("vms");
        message.setTo("vmBox");
        message.setNeedResend(needReSend);
        message.setSn(null != sn ? sn : System.currentTimeMillis());
//        message.getData().setInnerCode(innerCode);
        message.getData().setVersionNumber(getVersionNumber());
        //5.0一拖多
        if (innerCode.contains("-")) {
            message.getData().setInnerCode(innerCode.split("-")[0]);
            message.getData().setExtentionCode(StringUtil.getIntValue(innerCode.split("-")[1], 1));
        } else {
            message.getData().setInnerCode(innerCode);
            message.getData().setExtentionCode(1);
        }
    }

    /**
     * 拼装HeadMessageNew 信息
     * version 5.0.0a
     *
     * @param innerCode  售货机编号
     * @param message    消息
     * @param needReSend 是否需要重发
     */
    private void packMessageNew(String innerCode, HeadMessage message, boolean needReSend, Long sn) {
        message.setFrom("vms");
        message.setTo("vmBox");
        message.setNeedResend(needReSend);
        message.setSn(null != sn ? sn : System.currentTimeMillis());
        message.getData().setVersionNumber(getVersionNumber());
        if (innerCode.contains("-")) {
            message.getData().setInnerCode(innerCode.split("-")[0]);
        } else {
            message.getData().setInnerCode(innerCode);
        }
    }


    private synchronized long getVersionNumber() {
        long ts = System.currentTimeMillis();
        long base = 99990000000000000L;
        try {
            TimeUnit.MILLISECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return base + ts;
    }

    /**
     * 拼装HeadMessage 信息
     *
     * @param macAddress 售货机编号
     * @param message    消息
     * @param needReSend 是否需要重发
     */
    private void packMessageForMacAddress(String macAddress, HeadMessage message, boolean needReSend) {
        message.setFrom("vms");
        message.setTo("vmBox");
        message.setNeedResend(needReSend);
        message.setSn(new Date().getTime());
        message.getData().setMacAddress(macAddress);
    }

    /**
     * 检验配送工单状态
     *
     * @param taskVm
     * @return
     * @author zhangguodong
     */
    private String checkTkTaskVm(TTaskVm taskVm) {
        String errorMessage = "";
        try {
            Integer taskVmStatus = taskVm.getTaskVmStatus();
            if (null == taskVmStatus || TTaskVm.STATUS_ONGOING != taskVmStatus) {
                logger.error("完成配送工单失败,配送工单状态不符,taskVm = " + taskVm);
                errorMessage = "配送工单状态不符";
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            errorMessage = "完成工单失败！";
        }
        return errorMessage;
    }

    public void stVmChannelToMCSChanenl(HeadMessage message, TMtVm mtVm, boolean isMustCurr, Map<Integer, Integer> currNumMap) {
        try {
            List<TMtVmChannel> channelList = mtVm.getChannelInfo();
            if (mtVm.hasNextChannel() && !isMustCurr) {
                logger.info("售货机存在将要应用策略或者必须发送当前货道   isMustCurr = " + isMustCurr);
                Map<String, TMtVmChannelNext> nexts = mtVm.getChannelNext();
                for (TMtVmChannel tMtVmChannel : channelList) {

                    //修改当前应用货道信息
                    TMtVmChannelNext next = nexts.get(tMtVmChannel.getChannelCode());
                    tMtVmChannel.setMergeQty(next.getMergeQty());


                    tMtVmChannel.setMergePid(getMergePIdByNext(mtVm, next, mtVm.getObjectContext()));
                    tMtVmChannel.setSku(next.getSku());
                    tMtVmChannel.setMaxCapacity(next.getMaxNum());

                    //修改将要应用货道信息
                    next.setMergeQty(1);
                    next.setMergePid(null);
                    next.setMaxCapacity(next.getFixedCapacity() != null ? next.getFixedCapacity() : 0);
                    next.setSku(null);
                    next.setUtime(new Date());

                    //组装发给mcs的货道信息
                    if (tMtVmChannel.getMergeQty() != null && tMtVmChannel.getMergeQty() != 0) {
                        ChannelCfg channelCfg = new ChannelCfg();
                        channelCfg.setChannelCode(tMtVmChannel.getChannelCode());
                        Integer max = tMtVmChannel.getMaxNum();
                        channelCfg.setMaxCapcity(max);
                        channelCfg.setSkuId(tMtVmChannel.getSku() != null ? tMtVmChannel.getSku().getId() : 0);
                        message.getData().addChannelCfgs(channelCfg);
                        if (null != currNumMap) {
                            if (currNumMap.containsKey(tMtVmChannel.getId())) {
                                message.getData().addSupply(tMtVmChannel.getChannelCode(), currNumMap.get(tMtVmChannel.getId()));
                                tMtVmChannel.setCurrNum(currNumMap.get(tMtVmChannel.getId()));
                            } else {
                                message.getData().addSupply(tMtVmChannel.getChannelCode(), tMtVmChannel.getCurrNum() != null ? tMtVmChannel.getCurrNum() : 0);
                            }
                        } else {
                            message.getData().addSupply(tMtVmChannel.getChannelCode(), tMtVmChannel.getCurrNum() != null ? tMtVmChannel.getCurrNum() : 0);
                        }
                    }
                }

            } else {
                for (TMtVmChannel tMtVmChannel : channelList) {
                    if (tMtVmChannel.getMergeQty() != null && tMtVmChannel.getMergeQty() != 0) {
                        ChannelCfg channelCfg = new ChannelCfg();
                        channelCfg.setChannelCode(tMtVmChannel.getChannelCode());
                        Integer max = tMtVmChannel.getMaxNum();
                        channelCfg.setMaxCapcity(max);
                        channelCfg.setSkuId(tMtVmChannel.getSku() != null ? tMtVmChannel.getSku().getId() : 0);
                        message.getData().addChannelCfgs(channelCfg);
                        if (null != currNumMap) {
                            if (currNumMap.containsKey(tMtVmChannel.getId())) {
                                message.getData().addSupply(tMtVmChannel.getChannelCode(), currNumMap.get(tMtVmChannel.getId()));
                                tMtVmChannel.setCurrNum(currNumMap.get(tMtVmChannel.getId()));
                            } else {
                                message.getData().addSupply(tMtVmChannel.getChannelCode(), tMtVmChannel.getCurrNum() != null ? tMtVmChannel.getCurrNum() : 0);
                            }
                        } else {
                            message.getData().addSupply(tMtVmChannel.getChannelCode(), tMtVmChannel.getCurrNum() != null ? tMtVmChannel.getCurrNum() : 0);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }

    }

    /**
     * 获取Pid,根据将要应用货道
     *
     * @param mtVm
     * @param next
     * @param objectContext
     * @return
     */
    private Integer getMergePIdByNext(TMtVm mtVm, TMtVmChannelNext next,
                                      ObjectContext objectContext) {
        if (null != next.getMergePid()) {
            TMtVmChannelNext pMtVmChannelNext = Cayenne.objectForPK(objectContext, TMtVmChannelNext.class, next.getMergePid());
            String channelCode = pMtVmChannelNext.getChannelCode();
            TMtVmChannel pChannelInfo = mtVmChannelDao.findByChannelCode(objectContext, mtVm, channelCode);
            if (null != pChannelInfo) return pChannelInfo.getId();
        }
        return null;
    }


    /**
     * 转换闪购键信息
     *
     * @param message
     * @param shortCutsLists
     */
    public void shortCutsToMCSSpInfo(HeadMessage message,
                                     List<TMtVmShortcuts> shortCutsLists) {
        for (TMtVmShortcuts shortCuts : shortCutsLists) {
            SpCfg spCfg = new SpCfg();
            if (null != shortCuts.getBindType()) {
                if (TMtVmShortcuts.BIND_TYPE_SKU == shortCuts.getBindType()) {
                    TMtSku sku = shortCuts.getSku();
                    spCfg.setSkuId(sku != null ? sku.getId() : 0);
                    spCfg.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                    spCfg.setBindUrl("null");
                } else if (TMtVmShortcuts.BIND_TYPE_URL == shortCuts.getBindType()) {
                    spCfg.setBindType(TMtVmShortcuts.BIND_TYPE_URL);
                    spCfg.setBindUrl(urLPrefix + shortCuts.getResource().getResourceUrl());
                    spCfg.setSkuId(0);
                } else {
                    TMtSku sku = shortCuts.getSku();
                    spCfg.setSkuId(sku != null ? sku.getId() : 0);
                    spCfg.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                    spCfg.setBindUrl("null");
                }
            } else {
                TMtSku sku = shortCuts.getSku();
                spCfg.setSkuId(sku != null ? sku.getId() : 0);
                spCfg.setBindType(TMtVmShortcuts.BIND_TYPE_SKU);
                spCfg.setBindUrl("null");
            }

            spCfg.setSpCode(shortCuts.getShortcutsCode());
            message.getData().addSpCfgs(spCfg);
        }
    }

    public static String getPwd(int size) {
        Random random = new Random();

        StringBuffer result = new StringBuffer();

        for (int i = 0; i < size; i++) {
            result.append(random.nextInt(10));
        }
        return result.toString();
    }

    /**
     * 将VMS节能信息转换为MCS节能信息
     *
     * @param message
     * @param mtVmEnergySaveList 节能信息集合
     */
    public static void mtVmEnergyToMCSEnerg(HeadMessage message, List<TMtVmEnergySave> mtVmEnergySaveList) {
        for (TMtVmEnergySave mtVmEnergySave : mtVmEnergySaveList) {
            MtEnergy mtEnergy = new MtEnergy();
            //因为数据库中存储的是    10:00  时：分  格式
            mtEnergy.setStartTime(mtVmEnergySave.getStartTime() + ":00");
            mtEnergy.setEndTime(mtVmEnergySave.getEndTime() + ":00");
            mtEnergy.setTargetValue(null == mtVmEnergySave.getTargetValue() ? 0 : mtVmEnergySave.getTargetValue());
            mtEnergy.setEnergyType(mtVmEnergySave.getEnergySaveType());
            mtEnergy.setTempBoxNo(mtVmEnergySave.getTempBoxNo());
            message.getData().addMtEnergys(mtEnergy);
        }
    }

    /**
     * 将VMS节能信息转换为MCS节能信息
     * version 5.0.0a
     *
     * @param
     * @param mtVmEnergySaveList 节能信息集合
     */
    public static void mtVmEnergyToMCSEnergNew(String innerCode, Integer tempBoxCount, HeadMessage message, List<TMtVmEnergySave> mtVmEnergySaveList) {
        Extentions extention = new Extentions();
        if (innerCode.contains("-")) {
            extention.setExtentionCode(StringUtil.getIntValue(innerCode.split("-")[1], 1));
        } else {
            extention.setExtentionCode(1);
        }
        extention.setTempBoxCount(tempBoxCount);
        if (null != mtVmEnergySaveList && mtVmEnergySaveList.size() > 0) {
            for (TMtVmEnergySave mtVmEnergySave : mtVmEnergySaveList) {
                MtEnergy mtEnergy = new MtEnergy();
                //因为数据库中存储的是    10:00  时：分  格式
                mtEnergy.setStartTime(mtVmEnergySave.getStartTime() + ":00");
                mtEnergy.setEndTime(mtVmEnergySave.getEndTime() + ":00");
                mtEnergy.setTargetValue(null == mtVmEnergySave.getTargetValue() ? 0 : mtVmEnergySave.getTargetValue());
                mtEnergy.setEnergyType(mtVmEnergySave.getEnergySaveType());
                mtEnergy.setTempBoxNo(mtVmEnergySave.getTempBoxNo());
                extention.addMtEnergys(mtEnergy);
            }
        }/*else {
//            MtEnergy mtEnergy = new MtEnergy();
//            extention.addMtEnergys(mtEnergy);
        }*/

        message.getData().addExtentions(extention);
    }


    @Override
    public String finishRepairTask(String innerCode) {
        String errMsg = "";
        try {
            HeadMessage message = factory.createHeadMessage();

            packMessage(innerCode, message, true, null);

            message.getData().setMsgType(MsgType.FIXVMTASK);

            String headJson = factory.toJson(message);

            logger.info("finishRepairTask 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("finishRepairTask 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "完成维修工单失败，MCS无返回结果";
                logger.error(errMsg + ",innerCode = " + innerCode);
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "完成维修工单失败，MCS返回信息为发送失败";
                    logger.error(errMsg + ",innerCode = " + innerCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    /**
     * 拼装货道库存数据
     *
     * @param message
     * @param channelList
     */
    private void formatSupplyPolicy(HeadMessage message, List<TMtVmChannel> channelList) {
        try {
            for (TMtVmChannel tMtVmChannel : channelList) {
                message.getData().addSupply(tMtVmChannel.getChannelCode(), tMtVmChannel.getCurrNum() != null ? tMtVmChannel.getCurrNum() : 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    @Override
    public String sendChannelPolicy(TMtVm mtVm, boolean isMustCurr,
                                    Long sn) {
        String errMsg = "";
        HeadMessage message = factory.createHeadMessage();

        packMessage(mtVm.getInnerCode(), message, true, sn);

        message.getData().setMsgType(MsgType.CHANNELPOLICY);

        stVmChannelToMCSChannelPolicy(message, mtVm);

        String headJson = factory.toJson(message);
        try {
            logger.info("sendChannelPolicy 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendChannelPolicy 返回结果 :" + result);

            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "发送货到数据失败";
                logger.error("发送货到数据失败，MCS无返回结果");
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "发送货到数据失败";
                    logger.error("发送货到数据失败，MCS返回信息为发送失败");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            errMsg = "发送货道数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + e.getLocalizedMessage(), e);
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            errMsg = "JSONException 发送货道数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + jsonE.getLocalizedMessage(), jsonE);
        }
        return errMsg;
    }


    /**
     * 将vms中的货道信息转换为售货机端的ChannelPolicy
     *
     * @param message
     * @param mtVm
     */
    private void stVmChannelToMCSChannelPolicy(HeadMessage message, TMtVm mtVm) {
        try {
            List<TMtVmChannel> channelList = mtVm.getChannelInfo();
            for (TMtVmChannel tMtVmChannel : channelList) {
                if (tMtVmChannel.getMergeQty() != null && tMtVmChannel.getMergeQty() != 0) {
                    ChannelCfg channelCfg = new ChannelCfg();
                    channelCfg.setChannelCode(tMtVmChannel.getChannelCode());
                    Integer max = tMtVmChannel.getMaxNum();
                    channelCfg.setMaxCapcity(max);
                    channelCfg.setSkuId(tMtVmChannel.getSku() != null ? tMtVmChannel.getSku().getId() : 0);
                    channelCfg.setBatchNumber(StringUtil.isEmpty(tMtVmChannel.getBatchNo()) ? "" : tMtVmChannel.getBatchNo());
                    channelCfg.setProductDate(null == tMtVmChannel.getManufactureDate() ? "" : DateUtils.getTime1(tMtVmChannel.getManufactureDate()));
                    channelCfg.setExpiryDate(null == tMtVmChannel.getManufactureEndDate() ? "" : DateUtils.getTime1(tMtVmChannel.getManufactureEndDate()));
                    message.getData().addChannelCfgs(channelCfg);
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String sendArea(ObjectContext context, String form, Long sn) {
        if (null == context || StringUtil.isEmpty(form) || null == sn) return "参数错误";
        HeadMessage headMessage = factory.createHeadMessage();
        headMessage.setFrom("vms");
        headMessage.setTo(form);
        headMessage.setSn(sn);
        headMessage.setNeedResend(true);
        headMessage.getData().setMsgType(MsgType.AREA);

        AreaRelation areaRelation = headMessage.getData().AreaRelationInstance();

        List vmList = mtVmDao.getAllVm(context);
        for (Object obj : vmList) {
            TMtVm mtVm = (TMtVm) obj;
            areaRelation.addVm(mtVm.getInnerCode(), mtVm.getNode() == null ? null : mtVm.getNode().getId());
        }
        List customerList = mtCustomerDao.findCustomerB(context);
        for (Object obj : customerList) {
            TMtCustomer mtCustomer = (TMtCustomer) obj;
            areaRelation.addCustomer(mtCustomer.getId());
        }
        List nodeList = mtNodeDao.getAllNode(context, true);
        for (Object obj : nodeList) {
            TMtNode mtNode = (TMtNode) obj;
            Integer customerId = 0;
            TMtCompany company = mtNode.getCompany();
            if (null != company) {
                TMtCustomer customer = company.getCustomer();
                if (null != customer) {
                    customerId = customer.getId();
                }
            }
            areaRelation.addNode(mtNode.getId(),
                    mtNode.getNodeName(),
                    mtNode.getNodeAddress(),
                    mtNode.getBusinessDistrictId(),
                    mtNode.getCityId(),
                    customerId);
        }
        List areaList = mtAreaDao.getAllArea(context);
        for (Object obj : areaList) {
            TMtArea mtArea = (TMtArea) obj;
            TMtArea pMtArea = mtArea.getParentArea();
            Integer pid = null != pMtArea ? pMtArea.getId() : 0;
            areaRelation.addArea(mtArea.getId(), mtArea.getAreaName(), pid, mtArea.getAreaLevel());
        }
        List businessDistricList = mtBusinessDistrictDao.getAllBusinessDistrict(context);
        for (Object obj : businessDistricList) {
            TMtBusinessDistrict mtBusinessDistrict = (TMtBusinessDistrict) obj;
            areaRelation.addBusinessArea(mtBusinessDistrict.getId(), mtBusinessDistrict.getBusinessDistrictName());
        }
        String headJson = factory.toJson(headMessage);

        return headJson;
    }

    @Override
    public String sendBCustomer(ObjectContext context, String phone, String form, Long sn) {
        if (null == context || StringUtil.isEmpty(phone) || StringUtil.isEmpty(form) || null == sn) return "参数错误";
        TMtCustomer customer = mtCustomerDao.findCustomerByPhone(context, phone);
        HeadMessage message = factory.createHeadMessage();
        message.setFrom("vms");
        message.setTo(form);
        message.setNeedResend(true);
        message.setSn(sn);
        message.getData().setMsgType(MsgType.BCUSTOMER);
        if (null != customer) {
            message.getData().setId(customer.getId() + "");
        }
        String headJson = factory.toJson(message);

        return headJson;
    }

    public String sendVmBaseInfo(ObjectContext context, String innerCode, String form, Long sn) {
        if (null == context || StringUtil.isEmpty(innerCode) || StringUtil.isEmpty(form)) return "参数错误";
        String errMsg = null;
        TMtVm mtVm = mtVmDao.findByInnerCode(context, innerCode);
        String nodeName = null;
        String nodeAddress = null;
        int printer = 0;
        int reserve = 0;//是否预定，默认为否
        int formatCode = 3;
        int vmType = 0;//单机还是主机，默认单机
        if (null != mtVm) {
            TMtNode mtNode = mtVm.getNode();
            printer = null == mtVm.getPrinter() ? 0 : mtVm.getPrinter();
            reserve = null == mtVm.getReserve() ? 0 : mtVm.getReserve();
            vmType = null == mtVm.getVmType() ? 0 : mtVm.getVmType() - 1;
            if (null != mtNode) {
                nodeAddress = mtNode.getNodeAddress();
                nodeName = mtNode.getNodeName();
            }
            TMtVmThemeFormat themeFormat = mtVm.getThemeFormat();
            if (null != themeFormat) {
                formatCode = mtVm.getThemeFormat().getThemeFormat();
            }
        }
        HeadMessage message = factory.createHeadMessage();
        message.getData().setMsgType(MsgType.VM_BASE_INFO);
        List<TMtPayType> mtPayTypes = mtVm.getMtPayType();
        int length = mtPayTypes.size();
        String[] supportedPayTypes = new String[length];
        for (int i = 0; i < length; i++) {
            supportedPayTypes[i] = mtPayTypes.get(i).getCode() + "";
        }
        message.getData().setVmBaseInfo(nodeName, nodeAddress, printer, formatCode, reserve, supportedPayTypes, vmType);
        packMessage(innerCode, message, true, sn);
        String headJson = factory.toJson(message);
        try {
            logger.info("vmBaseInfo入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("vmBaseInfo返回结果 :" + result);

            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "发送售货机基本信息失败";
                logger.error("发送售货机基本信息失败，MCS无返回结果");
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "发送售货机基本信息失败";
                    logger.error("发送售货机基本信息失败，MCS返回信息为发送失败");
                }
            }
        } catch (IOException | JSONException e) {
            errMsg = "发送售货机基本信息数据失败 " + headJson;
            logger.error(e.getLocalizedMessage() + " " + errMsg, e);
        }
        return errMsg;
    }

    @Override
    public String openAllBox(String innerCode) {
        String errMsg = "";
        try {
            HeadMessage message = factory.createHeadMessage();

            packMessage(innerCode, message, false, null);

            message.getData().setMsgType(MsgType.OPENALLBOXREQ);

            String headJson = factory.toJson(message);

            logger.info("openAllBoxReq 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("openAllBoxReq 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "打开全部格子门，MCS无返回结果";
                logger.error(errMsg + ",innerCode = " + innerCode);
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "打开全部格子门，MCS返回信息为发送失败";
                    logger.error(errMsg + ",innerCode = " + innerCode);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String sendSkuOrder(ObjectContext context, String innerCode, Long sn) {
        String errMsg = "";
        try {
            HeadMessage message = factory.createHeadMessage();
            List<SkuOrder> skuOrders = new ArrayList<>();
            TMtVm mtVm = mtVmDao.findByInnerCode(context, innerCode);
            if (null != mtVm) {
                //5.0一拖多
//                TMtVm vm;
//                if (null != mtVm.getVmType() && mtVm.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
//                    vm = mtVm.getParentVm();
//                } else {
//                    vm = mtVm;
//                }
                List<TMtVmSkuSort> list = mtVmSkuSortDao.findSkuSort(mtVm).stream().map(TMtVmSkuSort.class::cast).collect(Collectors.toList());
                list.forEach(tMtVmSkuSort -> skuOrders.add(new SkuOrder(tMtVmSkuSort.getSkuId(), tMtVmSkuSort.getSort())));
                message.getData().setSkuOrder(skuOrders);
                packMessage(innerCode, message, true, sn);
                message.getData().setMsgType(MsgType.SKUORDERPOLICY);
                String headJson = factory.toJson(message);

                logger.info("sendSkuOrderPolicy 入参 :" + headJson);
                String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
                logger.info("sendSkuOrderPolicy 返回结果 :" + result);
                MessageResult messageResult = factory.toMessageResult(result);
                if (null == messageResult) {
                    errMsg = "下发货架，MCS无返回结果";
                    logger.error(errMsg + ",innerCode = " + innerCode);
                } else {
                    if (0 != messageResult.getSuccess()) {
                        errMsg = "下发货架，MCS返回信息为发送失败";
                        logger.error(errMsg + ",innerCode = " + innerCode);
                    }
                }
            } else {
                logger.error("下发货架,售货机不存在 innerCode = " + innerCode);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String sendWithdrawVmTaskInfo(ObjectContext context, String innerCode, Long sn) {
        String errMsg = "";
        try {
            HeadMessage message = factory.createHeadMessage();
            packMessage(innerCode, message, true, sn);
            message.getData().setMsgType(MsgType.WITHDRAWTASK);
            String headJson = factory.toJson(message);
            logger.info("sendWithdrawVmTaskInfo 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendWithdrawVmTaskInfo 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "下发撤机工单，MCS无返回结果";
                logger.error(errMsg + ",innerCode = " + innerCode);
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "下发撤机工单，MCS返回信息为发送失败";
                    logger.error(errMsg + ",innerCode = " + innerCode);
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String dealWithdraw(ObjectContext context, TMtVm mtVm) {
        try {
            this.updateVmShelf(context, mtVm);
            this.sendSkuToMcs(context, mtVm, false, true, null);
            this.sendSkuPriceToMcs(context, mtVm, true);
            this.sendSkuOrder(context, mtVm.getInnerCode(), null);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public String pushEnergyNew(ObjectContext context, List<Integer> vmIdList, Long sn) {
        StringBuffer buffer = new StringBuffer();
        try {
            if (null != vmIdList && vmIdList.size() > 0) {
                int length = vmIdList.size();
                TMtVm firstMtVm = mtVmDao.findByVmId(context, vmIdList.get(0));
                Integer vmModelId = firstMtVm.getVmModelId();
                TMtVmModel mtVmModel = Cayenne.objectForPK(context, TMtVmModel.class, vmModelId);
                for (int i = 0; i < length; i++) {
                    Integer vmId = vmIdList.get(i);
                    TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
                    if (null != mtVm) {
                        List<TMtVmEnergySave> energySaveList = mtVm.getEnergySave();
                        if (null != energySaveList) {
                            //拼装信息，调用发送信息方法
                            //通知MCS节能信息
                            //new下发消息
                            HeadMessage newMessage = factory.createHeadMessage();
                            packMessageNew(mtVm.getInnerCode(), newMessage, true, sn);
                            newMessage.getData().setMsgType(MsgType.MTENERGYCOMBO);

                            switch (mtVm.getVmType()) {
                                case TMtVmModel.COMBINATORIAL_EXTENTION:
                                    TMtVm mtVm1 = mtVm.getParentVm();
                                    TMtVmModel tMtVmModel = Cayenne.objectForPK(context, TMtVmModel.class, mtVm1.getVmModelId());
                                    mtVmEnergyToMCSEnergNew(mtVm1.getInnerCode(), tMtVmModel.getTempBoxCount(), newMessage, mtVm1.getEnergySave());
                                    List<TMtVm> tMtVms = mtVm1.getChildrenVm();
                                    if (null != tMtVms && tMtVms.size() > 0) {
                                        List<TMtVm> list = tMtVms.stream().filter(tMtVm -> tMtVm.getDeleteMark().equals(DeleteMark.EXIST.getCode())).collect(Collectors.toList());
                                        for (TMtVm mtVm2 : list) {
                                            TMtVmModel mtVmModel1 = Cayenne.objectForPK(context, TMtVmModel.class, mtVm2.getVmModelId());
                                            mtVmEnergyToMCSEnergNew(mtVm2.getInnerCode(), mtVmModel1.getTempBoxCount(), newMessage, mtVm2.getEnergySave());
                                        }
                                    }
                                    break;
                                case TMtVmModel.COMBINATORIAL_MAIN:
                                    List<TMtVm> mtVms = mtVm.getChildrenVm();
                                    mtVmEnergyToMCSEnergNew(mtVm.getInnerCode(), mtVmModel.getTempBoxCount(), newMessage, energySaveList);
                                    if (null != mtVms && mtVms.size() > 0) {
                                        List<TMtVm> list = mtVms.stream().filter(tMtVm -> tMtVm.getDeleteMark().equals(DeleteMark.EXIST.getCode())).collect(Collectors.toList());
                                        for (TMtVm tMtVm : list) {
                                            TMtVmModel mtVmModel1 = Cayenne.objectForPK(context, TMtVmModel.class, tMtVm.getVmModelId());
                                            mtVmEnergyToMCSEnergNew(tMtVm.getInnerCode(), mtVmModel1.getTempBoxCount(), newMessage, tMtVm.getEnergySave());
                                        }
                                    }
                                    break;
                                case TMtVmModel.COMBINATORIAL_SINGLE:
                                    mtVmEnergyToMCSEnergNew(mtVm.getInnerCode(), mtVmModel.getTempBoxCount(), newMessage, energySaveList);
                                    break;
                            }

                            //mtVmEnergyToMCSEnergNew(mtVm.getInnerCode(), mtVmModel.getTempBoxCount(), newMessage, energySaveList);
                            String newHeadJson = factory.toJson(newMessage);
                            //new
                            logger.info("pushEnergy 入参 :" + newHeadJson);
                            String newResult = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, newHeadJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
                            logger.info("pushEnergy 返回结果 :" + newResult);
                            MessageResult newMessageResult = factory.toMessageResult(newResult);
                            if (null == newMessageResult) {
                                logger.info("发送节能信息失败，MCS无返回结果  innerCode = " + mtVm.getInnerCode());
                                buffer.append("应用节能信息失败，售货机编码:" + mtVm.getInnerCode());
                            } else {
                                if (0 != newMessageResult.getSuccess()) {
                                    logger.info("发送节能信息失败，MCS返回信息为发送失败  innerCode = " + mtVm.getInnerCode());
                                    buffer.append("应用节能信息失败，售货机编码:" + mtVm.getInnerCode());
                                }
                            }
                        } else {
                            logger.info(" 发送节能策略  根据vmId没有查到未同步完成的节能信息，vmId=" + vmId);
                        }

                    } else {
                        logger.info(" 发送节能策略  根据vmId没有查到售货机信息，vmId=" + vmId);
                    }
                }
            } else {
                buffer.append("请选择售货机");
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException 发送节能信息失败 " + e.getLocalizedMessage(), e);
            buffer.append("应用节能失败！！！");
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            buffer.append("应用节能失败！！！");
            logger.error("JSONException 发送节能信息失败 " + jsonE.getLocalizedMessage(), jsonE);
        }
        return buffer.toString();
    }

    @Override
    public String sendEntrance(ObjectContext context, List<Integer> vmIdList, Long sn) {
        StringBuffer buffer = new StringBuffer();
        try {
            if (null != vmIdList && vmIdList.size() > 0) {
                int length = vmIdList.size();
                for (int i = 0; i < length; i++) {
                    Integer vmId = vmIdList.get(i);
                    TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
                    if (null != mtVm) {
                        List<TMtVmThemeEntrance> entrances = mtVm.getEntrances();
                        if (null != entrances) {
                            //拼装信息，调用发送信息方法
                            HeadMessage message = factory.createHeadMessage();
                            packMessageNew(mtVm.getInnerCode(), message, true, sn);
                            message.getData().setMsgType(MsgType.PORTALCFG);
                            mtVmEntrancesToMCS(message, entrances);
                            String headJson = factory.toJson(message);
                            logger.info("sendEntrance 入参 :" + headJson);
                            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
                            logger.info("sendEntrance 返回结果 :" + result);
                            MessageResult newMessageResult = factory.toMessageResult(result);
                            if (null == newMessageResult) {
                                logger.info("发送入口信息失败，MCS无返回结果  innerCode = " + mtVm.getInnerCode());
                                buffer.append("应用入口信息失败，售货机编码:" + mtVm.getInnerCode());
                            } else {
                                if (0 != newMessageResult.getSuccess()) {
                                    logger.info("发送入口信息失败，MCS返回信息为发送失败  innerCode = " + mtVm.getInnerCode());
                                    buffer.append("应用入口信息失败，售货机编码:" + mtVm.getInnerCode());
                                }
                            }
                        } else {
                            logger.info(" 发送入口配置  根据vmId没有查到未同步完成的节能信息，vmId=" + vmId);
                        }
                    } else {
                        logger.info(" 发送入口配置  根据vmId没有查到售货机信息，vmId=" + vmId);
                    }
                }
            } else {
                buffer.append("请选择售货机");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("IOException 发送入口配置信息失败 " + e.getLocalizedMessage(), e);
            buffer.append("入口配置失败！！！");
        }
        return buffer.toString();
    }

    @Override
    public String sendDeployVmTaskInfo(ObjectContext context, String innerCode, Long sn, List<Extention> list) {
        String errMsg = "";
        try {
            HeadMessage message = factory.createHeadMessage();
            packMessage(innerCode, message, true, sn);
            message.getData().setMsgType(MsgType.DEPLOYTASK);
            message.getData().setExtentionList(list);
            String headJson = factory.toJson(message);
            logger.info("sendDeployVmTaskInfo 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendDeployVmTaskInfo 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "下发副机投放工单，MCS无返回结果";
                logger.error(errMsg + ",innerCode = " + innerCode);
            } else {
                if (0 != messageResult.getSuccess()) {
                    errMsg = "下发副机投放工单，MCS返回信息为发送失败";
                    logger.error(errMsg + ",innerCode = " + innerCode);
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String sendSkuInfoToMcs(ObjectContext context, TMtVm mtVm, boolean isCommit, boolean isMustCurr, Long sn) {
        String errMsg = "";
        HeadMessage message = factory.createHeadMessage();

        packMessageNew(mtVm.getInnerCode(), message, true, sn);

        message.getData().setMsgType(MsgType.SKUINFO);

        mdseSortToMCSSkuNew(context, message, mtVm, isMustCurr);

        String headJson = factory.toJson(message);
        try {
            logger.info("sendSkuInfoToMcs 入参 :" + headJson);
            String result = WebUtils.doPost(McsConfiguration.getInstance().getUrl(), WebUtils.DEFAULT_CHARSET, headJson.getBytes(WebUtils.DEFAULT_CHARSET), WebUtils.connectTimeout, WebUtils.readTimeout);
            logger.info("sendSkuInfoToMcs 返回结果 :" + result);
            MessageResult messageResult = factory.toMessageResult(result);
            if (null == messageResult) {
                errMsg = "发送商品信息失败";
                logger.error("发送商品信息失败，MCS无返回结果   innerCode = " + mtVm.getInnerCode());
            } else {
                if (0 != messageResult.getSuccess()) {
                    logger.error("发送商品信息失败，MCS返回信息为发送失败   innerCode = " + mtVm.getInnerCode());
                } else {
                    if (isCommit) {
                        try {
                            context.commitChanges();
                        } catch (Exception e) {
                            e.printStackTrace();
                            context.rollbackChanges();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            errMsg = "发送商品数据失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + e.getLocalizedMessage(), e);
        } catch (JSONException jsonE) {
            jsonE.printStackTrace();
            logger.error(jsonE.getMessage());
            errMsg = "JSONException 发送商品信息失败，innerCode = " + mtVm.getInnerCode() + ",sn = " + message.getSn() + ";";
            logger.error(errMsg + jsonE.getLocalizedMessage(), jsonE);
        }
        return errMsg;
    }

    /**
     * 5.0.0a
     * 拼装列表页信息
     *
     * @param message
     * @param mtVm
     */
    private void mdseSortToMCSSkuNew(ObjectContext context, HeadMessage message, TMtVm mtVm, boolean isMustCurr) {

        try {
            List<TMtVmPrice> mtVmPrice = mtVm.getPrice();
            List<TMtVmSkuSort> currSortList = null;
            Set<Integer> skuSet = new HashSet<>();
            if (!isMustCurr) {
                currSortList = updateVmShelf(context, mtVm);
            } else {
                currSortList = mtVm.getSkuSort();
            }

            if (mtVmPrice != null && mtVmPrice.size() > 0) {
                Map<Integer, TMtVmPrice> priceMap = new HashMap<Integer, TMtVmPrice>();
                for (TMtVmPrice tMtVmPrice : mtVmPrice) {
                    priceMap.put(tMtVmPrice.getSkuId(), tMtVmPrice);
                }
                if (currSortList != null && currSortList.size() > 0) {
                    for (TMtVmSkuSort tMtVmSkuSort : currSortList) {
                        TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, tMtVmSkuSort.getSkuId());
                        if (sku != null && !skuSet.contains(sku.getId())) {
                            SkuCfg skuCfg = new SkuCfg();
                            formatSkuSortNew(tMtVmSkuSort, sku, skuCfg);
                            if (!priceMap.containsKey(tMtVmSkuSort.getSkuId())) {
                                skuCfg.setOfferPrice(sku.getOfferPrice());
                                logger.info("售货机应用了商品价格策略,vm_id = " + mtVm.getId() + ",因为价格策略中没有商品标识，skuId = " + sku.getId() + ",所以该商品在未计算促销策略之前建议零售价为：" + sku.getOfferPrice());
                            } else {
                                skuCfg.setOfferPrice(priceMap.get(tMtVmSkuSort.getSkuId()).getRealPrice());
                                logger.info("售货机应用了商品价格策略,vm_id = " + mtVm.getId() + ",因为价格策略中存在商品标识，skuId = " + sku.getId() + ",所以该商品在未计算促销策略之前建议零售价为：" + skuCfg.getOfferPrice());
                            }
                            message.getData().addSkuCfgs(skuCfg);
                            skuSet.add(sku.getId());
                        }
                    }
                }
            } else {
                for (TMtVmSkuSort tMtVmSkuSort : currSortList) {
                    TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, tMtVmSkuSort.getSkuId());
                    if (sku != null && !skuSet.contains(sku.getId())) {
                        SkuCfg skuCfg = new SkuCfg();
                        formatSkuSortNew(tMtVmSkuSort, sku, skuCfg);

                        skuCfg.setOfferPrice(sku.getOfferPrice());
                        logger.info("售货机没有应用商品价格策略,vm_id = " + mtVm.getId() + ",skuId = " + sku.getId() + ",所以该商品建议零售价为：" + sku.getOfferPrice());
                        message.getData().addSkuCfgs(skuCfg);
                        skuSet.add(sku.getId());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    private void formatSkuSortNew(TMtVmSkuSort tMtVmSkuSort, TMtSku sku,
                                  SkuCfg skuCfg) {
        skuCfg.setClassId(sku.getSkuType().getId());
        skuCfg.setClassName(sku.getSkuType().getTypeName());
        skuCfg.setDesc(sku.getSkuDesc());
        skuCfg.setIndex(tMtVmSkuSort.getSort());
        skuCfg.setSkuId(sku.getId());
        //5.0.0a
        List<SkuImageUrl> skuImageUrls = new ArrayList<>();
        List<TMtSkuImg> tMtSkuImgs = sku.getSkuImg();
        List<TMtSkuImg> tMtSkuImgList = tMtSkuImgs.stream().filter(tMtSkuImg -> 1 == tMtSkuImg.getSource()).collect(Collectors.toList());
        for (TMtSkuImg skuImg : tMtSkuImgList) {
            SkuImageUrl skuImageUrl = new SkuImageUrl();
            skuImageUrl.setIndex(skuImg.getImgIndex());
            //发送给mcs时，1是普通图，2是详情图
            switch (skuImg.getDetailDiagram()) {
                case TMtSkuImg.NO_IMG_MASTER_URL:
                    skuImageUrl.setType(1);
                    break;
                case TMtSkuImg.IMG_MASTER_URL:
                    skuImageUrl.setType(2);
                    break;
            }
            skuImageUrl.setUrl(skuImg.getSkuImgUrl());
            skuImageUrls.add(skuImageUrl);
        }
        skuCfg.setSkuImageUrls(skuImageUrls);
        skuCfg.setSkuName(sku.getSkuName());
        skuCfg.setUnit(sku.getSkuUnit());
        skuCfg.setBrand(sku.getSkuProducer());
        skuCfg.setSaleType(null == sku.getReserve() ? 0 : sku.getReserve());
        skuCfg.setGeneralizeType(null == sku.getGeneralizeType() ? 0 : sku.getGeneralizeType());
    }


    /**
     * 将VMS入口配置信息转换为MCS入口信息
     * version 5.0.0a
     *
     * @param message
     * @param tMtVmThemeEntrances 入口信息集合
     */
    public static void mtVmEntrancesToMCS(HeadMessage message, List<TMtVmThemeEntrance> tMtVmThemeEntrances) {
        message.getData().initPortalcfgs();
        for (TMtVmThemeEntrance themeEntrance : tMtVmThemeEntrances) {
            Portalcfg portalcfg = new Portalcfg();
            portalcfg.setPortalCode(themeEntrance.getEntranceIndex());
            portalcfg.setBindUrl(urLPrefix + themeEntrance.getResource().getResourceUrl());
            portalcfg.setImgUrl(themeEntrance.getSkuImgUrl());
            message.getData().addPortalcfgs(portalcfg);
        }
    }

}