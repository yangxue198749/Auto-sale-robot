package com.kdx.vms.web.handler;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.mcs.message.HeadMessage;
import com.kdx.vms.commons.util.mcs.message.MessageFactory;
import com.kdx.vms.commons.util.mcs.message.data.OutResult;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.enumeration.OrderNotifyStatus;
import com.kdx.vms.model.enumeration.OrderRefundType;
import com.kdx.vms.mq.consumer.HandleResponse;
import com.kdx.vms.mq.handler.ServiceHandler;
import com.kdx.vms.mq.utils.StringUtils;
import com.kdx.vms.orm.dao.OrderDetailDao;
import com.kdx.vms.orm.dao.TaskVmDao;
import com.kdx.vms.service.IMtVmChannelService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.IRefundService;
import com.kdx.vms.utils.SysSettingsCache;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 预售出货结果
 * Created by zhangguodong on 2017/8/10.
 */
@Service("outResultReserveHandler")
public class OutResultReserveHandler implements ServiceHandler {
    private static final String BODY = "body";

    Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Resource
    private IMtVmService mtVmService;
    @Resource
    private IMtVmChannelService mtVmChannelService;
    @Resource
    private TaskVmDao taskVmDao;
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private IRefundService refundService;
    private List<Integer> refundList = Arrays.asList(-1,2,3,4,5,6,8,9);
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
                        TOrderDetail orderDetail = orderDetailDao.findByDetailId(contextDemp,Integer.parseInt(saleSeq));
                        if(null != orderDetail){
                            //订单不为空
                            Integer outStatus = outResult.getOutStatus();
                            String channelCode = outResult.getChannelCode();
                            Integer stock = outResult.getStock();
                            if(null != outStatus){
                                if(TOrderDetailVendout.OUT_STATUS_FAIL_EMPTY == outStatus){
                                    //修改通知状态为    售空，目的是可以做到售空的情况可以重复通知
                                    TimeUnit.MILLISECONDS.sleep(500);
                                    orderDetail = orderDetailDao.findByDetailId(contextDemp,Integer.parseInt(saleSeq));
                                    TOrderDetailNotify notify = orderDetailDao.findNotify(contextDemp,orderDetail.getId());
                                    if(null == notify){
                                        LOGGER.error("没有查到通知结果,orderDetailId = "+ orderDetail.getId());
                                    }
                                    notify.setNotifyStatus(OrderNotifyStatus.STATUS_SOLD_OUT);
                                    orderDetail.setNotifyStatus(OrderNotifyStatus.STATUS_SOLD_OUT);
                                }else {
                                    TOrderDetailVendout vendOut = contextDemp.newObject(TOrderDetailVendout.class);
                                    vendOut.setVendoutTime(StringUtil.strToDate(outResult.getOutTime(),StringUtil.Pattern));
                                    orderDetail.setVendout(vendOut);
                                    orderDetail.setVendoutStatus(outStatus);
                                    vendOut.setVendoutStatus(outStatus);
                                    //2017-10-23 add
                                    if(!StringUtil.isEmpty(channelCode)){
                                        TMtVmChannel outChannel = mtVmChannelService.findByChannelCode(contextDemp, mtVm, channelCode);
                                        if(null != outChannel){
                                            vendOut.setVendoutChannel(outChannel);
                                            if(outStatus.equals(TOrderDetailVendout.OUT_STATUS_SUCCESS)){
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
                                }
                                try {
                                    contextDemp.commitChanges();
                                    mtVm.updateStockStatus();
                                    if(refundList.contains(outStatus)){
                                        if(orderDetail.getRealPrice() < Integer.valueOf(SysSettingsCache.getSetting("max_refund_money","refund","5000"))){
                                            dealRefund(Integer.parseInt(saleSeq),outStatus, OrderRefundType.AUTO_GOOD,orderDetail.getOrder().getOrderType());
                                        }
                                    }
                                }catch (Exception e){
                                    response.setResult(null);
                                    contextDemp.rollbackChanges();
                                    LOGGER.error(e.getLocalizedMessage(),e);
                                }
                            }else{
                                LOGGER.error("reserveOutResult  outStatus is null ,outResult =  " + res);
                            }
                        } else{
                            LOGGER.error("订单不存在  innerCode = " + innerCode + ",orderGoodId = " + saleSeq);
                            return response;
                        }
                    }
                }else{
                    LOGGER.error("收到售货机出货结果请求 出货结果不合法 saleSeq = " + saleSeq);
                    return response;
                }
            }else{
                LOGGER.error("收到售货机出货结果请求 出货结果不合法 outResult = " + outResult);
                return response;
            }
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage(),e);
        }
        return response;
    }
    private void dealRefund(Integer orderDetailId, Integer outStatus, OrderRefundType refundType,String orderType) {
        try {
            if(TOrderMaster.RESERVE.equals(orderType)){
                refundService.launchRefund(ObjectContextFactory.getInstance().newObjectContext(),refundType, (String) PropertiesLoader.map.get("reserveRefundTopic"),orderDetailId);
            }
        }catch (Exception e){
            LOGGER.error(e.getLocalizedMessage(),e);
        }
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
