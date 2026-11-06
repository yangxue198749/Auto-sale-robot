package com.kdx.vms.web.handler.card;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.commons.util.mcs.message.data.OutResult;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.enumeration.OrderNotifyStatus;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.mq.utils.StringUtils;
import com.kdx.vms.orm.dao.TaskVmDao;
import com.kdx.vms.service.IMtVmChannelService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.Cayenne;
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
 * Created by zhangguodong on 2017/9/11.
 */
@Service("outResultCardHandler")
public class OutResultCardHandler implements ServiceHandler{
    private static final String BODY = "body";

    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IMtVmService mtVmService;
    @Resource
    private IMtVmChannelService mtVmChannelService;
    @Resource
    private TaskVmDao taskVmDao;
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
                LOGGER.error("收到售货机出货结果请求 innerCode = " + innerCode);
                response.setResult("出货结果不合法");
                return response;
            }
            OutResult outResult = message.getData().getOutResult();
            if(null != outResult){
                String saleSeq = outResult.getOrderId();
                if(!StringUtil.isEmpty(saleSeq)){
                    ObjectContext contextDemp = ObjectContextFactory.getInstance().newObjectContext();
                    TMtVm mtVm = mtVmService.findMtVmByInnerCode(contextDemp,innerCode);
                    if(null == mtVm){
                        LOGGER.error("收到售货机出货结果请求 innerCode = " + innerCode + "售货机不存在");
                        response.setResult("出货结果请求处理结果 innerCode = " + innerCode + "售货机不存在");
                        return response;
                    }
                    Integer extentionCode = message.getData().getExtentionCode();
                    mtVm = mtVm.findExtention(extentionCode);//5.0一拖多
                    synchronized (mtVm.getInnerCode().intern()){
                        String channelCode = outResult.getChannelCode();
                        Integer outStatus = outResult.getOutStatus();
                        Integer stock = outResult.getStock();

                        //订单
                        TOrderMaster orderMaster = contextDemp.newObject(TOrderMaster.class);
                        orderMaster.setCompany(mtVm.getCompany());
//                        orderMaster.setConsigneeId(null); //目前卡还没有和人进行关联


                        orderMaster.setCTime(new Date());
                        orderMaster.setCost(formatOrderCost(outResult, contextDemp));//订单支付情况
                        orderMaster.setCostStatus(TOrderCost.COST_STATUS_SUCCESS);
                        orderMaster.setMtVm(mtVm);
                        orderMaster.setOrderSeq(outResult.getOrderId());
                        orderMaster.setOrderType(TOrderMaster.CARD);//实体卡支付
                        orderMaster.setTotalPrice(outResult.getPrice());
                        orderMaster.setRealTotalPrice(outResult.getPrice());

                        //订单详情
                        TOrderDetail orderDetail = contextDemp.newObject(TOrderDetail.class);
                        orderDetail.setOrder(orderMaster);
                        orderDetail.setNotifyStatus(OrderNotifyStatus.STATUS_PENDING);
                        orderDetail.setSku(Cayenne.objectForPK(contextDemp,TMtSku.class,outResult.getSkuId()));
                        orderDetail.setRealPrice(outResult.getPrice());
                        orderDetail.setSkuPrice(outResult.getPrice());
                        orderDetail.setValidTime(DateUtils.getTime1());

                        //订单详情支付
                        TOrderDetailCost orderDetailCost = contextDemp.newObject(TOrderDetailCost.class);
                        orderDetailCost.setCostAccount(outResult.getCardNumber());
                        orderDetailCost.setCostPrice(outResult.getPrice());
                        orderDetailCost.setCostType(outResult.getPayType());
                        orderDetailCost.setOrderDetail(orderDetail);

                        TOrderMasterAtt orderMasterAtt = contextDemp.newObject(TOrderMasterAtt.class);
                        orderMasterAtt.setMaster(orderMaster);
                        orderMasterAtt.setOrderScene(0);
                        orderMasterAtt.setOrderSources(1);

                        //订单详情通知
                        TOrderDetailNotify orderDetailNotify = contextDemp.newObject(TOrderDetailNotify.class);
//                        orderDetailNotify.setCustomerPublicId(null);
                        orderDetailNotify.setNotifyStatus(OrderNotifyStatus.STATUS_PENDING);
                        orderDetailNotify.setNotifyTime(StringUtil.strToDate(outResult.getOutTime(),StringUtil.Pattern));
                        orderDetailNotify.setDetail(orderDetail);

                        //订单详情出货

                        TOrderDetailVendout orderDetailVendout = contextDemp.newObject(TOrderDetailVendout.class);
                        orderDetailVendout.setDetail(orderDetail);
                        orderDetailVendout.setVendoutTime(StringUtil.strToDate(outResult.getOutTime(),StringUtil.Pattern));
                        orderDetailVendout.setVendoutStatus(outStatus);
                        orderDetail.setVendoutStatus(outStatus);


                        if(!StringUtil.isEmpty(channelCode)){
                            TMtVmChannel outChannel = mtVmChannelService.findByChannelCode(contextDemp, mtVm, channelCode);
                            if(null != outChannel){
                                orderDetailVendout.setVendoutChannel(outChannel);
                                if(null != outStatus && outStatus.equals(TOrderDetailVendout.OUT_STATUS_SUCCESS)){
                                    response.setResult(formatResult(orderDetail.getSku().getId(),orderDetail.getRealPrice(), DateUtils.getTime1(orderDetail.getOrder().getCTime()),orderDetail.getOrder().getCompany().getId()));
                                    //出货成功减少库存
                                    if(outChannel.getCurrNum()>0){
                                        outChannel.setCurrNum(outChannel.getCurrNum() - 1);
                                    }
                                }else if(outStatus.equals(TOrderDetailVendout.OUT_STATUS_FAIL_CHANNEL_BROKEN) ){
                                    //创建自动维修工单，并通知
                                    if(null != mtVm.getNode() && null != mtVm.getNode().getRepair()){
                                        createRepairTask(contextDemp, UserUtils.getVmsSystem(),mtVm,mtVm.getNode().getRepair().getId(),"出货失败,失败原因:货道故障",DateUtils.getTime1(),response);
                                    }
                                }else if(outStatus.equals(TOrderDetailVendout.OUT_STATUS_FAIL_PRINTER_BROKEN)){
                                    if(null != mtVm.getNode() && null != mtVm.getNode().getRepair()){
                                        createRepairTask(contextDemp, UserUtils.getVmsSystem(),mtVm,mtVm.getNode().getRepair().getId(),"出货失败,失败原因:打印机故障",DateUtils.getTime1(),response);
                                    }
                                }
                                if(null != stock){
                                    outChannel.setHasNextStock(stock);
                                }
                            }
                        }
                        try {
                            contextDemp.commitChanges();
                            mtVm.updateStockStatus();
                        } catch (Exception e) {
                            response.setResult(null);
                            contextDemp.rollbackChanges();
                            LOGGER.error("售货机出货结果处理出错  " +e.getLocalizedMessage(),e);
                        }
                    }
                }else{
                    LOGGER.error("收到售货机出货结果请求 saleSeq = " + saleSeq);
                    response.setResult("出货结果不合法");
                    return response;
                }
            }else{
                LOGGER.error("收到售货机出货结果请求 outResult = " + outResult);
                response.setResult("出货结果不合法");
                return response;
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(),e);
        }
        return response;
    }

    private TOrderCost formatOrderCost(OutResult outResult, ObjectContext contextDemp) {
        TOrderCost orderCost = contextDemp.newObject(TOrderCost.class);
        orderCost.setPayStatus(TOrderCost.COST_STATUS_SUCCESS);
        orderCost.setPayPrice(outResult.getPrice());
        orderCost.setPayTime(new Date());
        orderCost.setPayType(outResult.getPayType());
        TOrderCostDetail costDetail = contextDemp.newObject(TOrderCostDetail.class);
        costDetail.setCostAccount(outResult.getCardNumber());
        costDetail.setCostPrice(outResult.getPrice());
        costDetail.setCostType(outResult.getPayType());
        costDetail.setPayOuterSeq(outResult.getOuterTradeNo());
        costDetail.setCost(orderCost);
        return orderCost;
    }


    private void createRepairTask(ObjectContext context,
                                  TSysUser user, TMtVm mtVm, Integer ownerId,String taskRemarks,String taskTime,HandleResponse response) {
        try {

            if(null == ownerId){
                return ;
            }

            if(mtVm.getVmStatus() != TMtVm.STATUS_SALE && mtVm.getVmStatus() != TMtVm.STATUS_UNSLAE){
                LOGGER.error("售货机状态不符合，不创建维修工单");
                return ;
            }
            TTaskVm unDealTask = taskVmDao.getUnDealTaskToday(context,mtVm,TTaskVm.TYPE_REPAIR, DateUtils.getTime1(new Date()));

            TSysUser ownUser = Cayenne.objectForPK(context,TSysUser.class,ownerId);
            if(ownUser == null){
                LOGGER.error("新建维修工单失败,维修负责人不存在");
                return ;
            }
            Date  taskTimeDate = new Date();
            try {
                taskTimeDate = DateUtils.getDate1(taskTime);
            } catch (Exception e) {
                return ;
            }

            if(null == unDealTask){
                unDealTask = context.newObject(TTaskVm.class);
                unDealTask.setCtime(new Date());
            }
            unDealTask.setCtor(user.getId());
            unDealTask.setMtVm(mtVm);
            unDealTask.setOwner(ownerId);
            unDealTask.setTaskLevel(TTaskVm.LEVEL_URGENT);
            unDealTask.setTaskTime(taskTimeDate);
            unDealTask.setTaskTitle("新建维修工单"+ DateUtils.getTime1(unDealTask.getCtime()));
            unDealTask.setTaskType(TTaskVm.TYPE_REPAIR);
            unDealTask.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
            unDealTask.setTaskRemarks(taskRemarks);
            unDealTask.setCompany(ownUser.getCompany());
            context.commitChanges();

            if(!StringUtils.isEmpty(ownUser.getOpenId())){

                Map<String,Object> taskMap = new HashMap<>();
                Map<String,Object> headMap = new HashMap<>();
                Map<String,Object> bodyMap = new HashMap<>();
                bodyMap.put("openId",ownUser.getOpenId());
                bodyMap.put("title","维修任务");
                bodyMap.put("date",DateUtils.getTime1(taskTimeDate));

                taskMap.put("head",headMap);
                taskMap.put("body",bodyMap);
                response.setResult(JSON.toJSONString(taskMap));
            }
        } catch (Exception e) {
            context.rollbackChanges();
            LOGGER.error(e.getLocalizedMessage(),e);
        }
    }
    private String formatResult(Integer skuId,Integer price,String date,Integer companyId) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> bodyMap = new HashMap<>();
        Map<String, Object> headMap = new HashMap<>();
        map.put("head",headMap);
        bodyMap.put("skuId",skuId);
        bodyMap.put("price",price);
        bodyMap.put("date",date);
        bodyMap.put("companyId",companyId);
        map.put(BODY,bodyMap);
        return JSON.toJSONString(map);
    }
}
