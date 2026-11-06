package com.kdx.mp.service.impl;

import com.kdx.mp.service.IMpTaskService;
import com.kdx.mp.vo.TaskChannel;
import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.DeliveryObject;
import com.kdx.vms.model.custom.DeliveryTaskJson;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.orm.dao.MtVmChannelDao;
import com.kdx.vms.orm.dao.ReserveStatisticsDao;
import com.kdx.vms.orm.dao.TaskVmDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by yangshuxi on 2016/12/21.
 */
@Service
public class MpTaskServiceImpl implements IMpTaskService {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Autowired
    TaskVmDao taskVmDao;
    @Autowired
    MtVmChannelDao mtVmChannelDao;
    @Autowired
    ReserveStatisticsDao reserveStatisticsDao;

    @Override
    public List findByOwner(ObjectContext context, Integer userId, Integer taskType) {
        return taskVmDao.findByOwner(context, userId, taskType);
    }

    @Override
    public List findTaskByOwnerAndDay(ObjectContext context, Integer userId, String day, Integer taskType) {
        return taskVmDao.findTaskByOwnerAndDay(context, userId, day, taskType);
    }

    @Override
    public List findFinishTaskByOwnerAndDay(ObjectContext context, Integer userId, String day, Integer taskType) {
        return taskVmDao.findFinishTaskByOwnerAndDay(context, userId, day, taskType);
    }

    @Override
    public Map<TMtSku, Integer> calculateSku(ObjectContext context, List<TTaskVm> list) {
        Map<TMtSku, Integer> resMap = new HashMap<>();
        for (TTaskVm taskVm : list) {
            if (IntegerUtils.isPositiveInteger(taskVm.getAlreadyPickup())) continue;
            if (taskVm.isManualEditTask()) {
                List<TTaskVmChannel> taskVmChannels = taskVm.getTaskVmChannel();
                for (TTaskVmChannel taskVmChannel : taskVmChannels) {
                    if (taskVmChannel.getTaskType() != TTaskVmChannel.CHANNEL_TYPE_FILL) continue;
                    TMtSku sku = taskVmChannel.getSku();
                    Integer count = taskVmChannel.getSkuCount();
                    if (count != null) {
                        if (null == resMap.get(sku)) {
                            resMap.put(sku, count);
                        } else {
                            resMap.put(sku, resMap.get(sku) + count);
                        }
                    }
                }
            } else {
                List<TTaskVmChannel> taskVmChannels = taskVm.getTaskVmChannel();
                for (TTaskVmChannel taskVmChannel : taskVmChannels) {
                    if (taskVmChannel.getTaskType() != TTaskVmChannel.CHANNEL_TYPE_FILL) continue;
                    TMtSku sku = taskVmChannel.getSku();
                    if (null != taskVmChannel.getMaxCount() && null != taskVmChannel.getBeforeDeliveryStock()) {
                        Integer count = taskVmChannel.getMaxCount() - taskVmChannel.getBeforeDeliveryStock();
                        if (null == resMap.get(sku)) {
                            resMap.put(sku, count);
                        } else {
                            resMap.put(sku, resMap.get(sku) + count);
                        }
                    }
                }
            }
        }
//        for (TMtSku sku : resMap.keySet()) {
//            resMap.put(sku, divide(resMap.get(sku), StringUtil.getIntValue(sku.getPackingSpecification(),1)));
//        }
        return resMap;
    }

    private Integer divide(Integer num1, Integer num2) {
        BigDecimal decimal1 = new BigDecimal(num1);
        BigDecimal decimal2 = new BigDecimal(num2);
        return decimal1.divide(decimal2, 0, BigDecimal.ROUND_UP).intValue();
    }

    @Override
    public void updateTask(ObjectContext context, Integer status, Integer... taskIds) {
        if (null != taskIds && taskIds.length > 0) {
            try {
                for (Integer taskId : taskIds) {
                    TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
                    taskVm.setTaskVmStatus(status);
                    if (status.equals(TTaskVm.STATUS_FINISHED)) {
                        taskVm.setFinishTime(new Date());
                    }
                }
                context.commitChanges();
            } catch (Exception e) {
                logger.error(e.getLocalizedMessage(), e);
                context.rollbackChanges();
            }
        }
    }

    @Override
    public void withdrawVm(ObjectContext context, TTaskVm taskVm) {
        TMtVm mtVm = taskVm.getMtVm();
        mtVm.setVmStatus(TMtVm.STATUS_UNDEPLOY);
        mtVm.setUniqueCode(null);
        if (mtVm.getVmType().equals(TMtVmModel.COMBINATORIAL_EXTENTION)) {
            mtVm.setDeleteMark(DeleteMark.DELETE.getCode());
        } else if (mtVm.getVmType().equals(TMtVmModel.COMBINATORIAL_MAIN)) {
            List<TMtVm> list = mtVm.getChildrenVm();
            list.forEach(vm -> vm.setDeleteMark(DeleteMark.DELETE.getCode()));
            List<TTaskVm> taskList = list.stream()
                    .map(vm -> taskVmDao.findDeployTaskByVm(vm))
                    .flatMap(List::stream)
                    .map(TTaskVm.class::cast)
                    .collect(Collectors.toList());
            taskList.forEach(taskVm1 -> taskVm1.setTaskVmStatus(TTaskVm.STATUS_CLOSED));

        }
    }

    @Override
    public void deployVm(ObjectContext context, TTaskVm taskVm) {
        TMtVm mtVm = taskVm.getMtVm();
        mtVm.setVmStatus(TMtVm.STATUS_SALE);
    }

    @Override
    public void updateVmChannel(ObjectContext context, Integer taskId) {
        try {
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            TMtVm mtVm = taskVm.getMtVm();
            List<TMtVmChannel> mtVmChannels = mtVm.getChannelInfo();
            for (TMtVmChannel mtVmChannel : mtVmChannels) {
                if (mtVmChannel.getChannelStatus() != 0) {
                    mtVmChannel.setChannelStatus(0);
                }
            }
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
        }
    }

    @Override
    public Map<String, Object> getChannelDetail(ObjectContext context, TTaskVm tTaskVm) {
        Map<String, Object> resMap = new HashMap<>();
        try {
            List<TTaskVmChannel> list = tTaskVm.getTaskVmChannel();
            //4.0
            List<TaskChannel> voList = list.stream().map(TaskChannel::newInstance).collect(Collectors.toList());
            Map<String, TaskChannel> voMap = voList.stream().collect(Collectors.toMap(TaskChannel::getChannelCode, taskChannel -> taskChannel));
            //4.0 end
            TMtVm mtVm = tTaskVm.getMtVm();
            Map<String, TMtVmChannel> channelInfoMap = mtVmChannelDao.getMtVmChannelInfoMap(mtVm);
            TMtVmModel vmModel = Cayenne.objectForPK(context, TMtVmModel.class, mtVm.getVmModelId());
            if (mtVm.hasNextChannel()) {
                Map<String, TMtVmChannelNext> channelNextMap = mtVmChannelDao.getMtVmChannelNextMap(mtVm);
                resMap = getAllTask(channelInfoMap, voMap, channelNextMap, vmModel);
            } else {
                resMap = getFillTask(channelInfoMap, voMap, vmModel);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return resMap;
    }

    @Override
    public Map<Integer, Integer> finishDelivery(ObjectContext context, TTaskVm taskVm, Integer[] currNums, Integer[] channelIds) {
        Map map = taskVm.getMtVm().getChannelNext();
        map.size();
        logger.info(new Date() + "工单id=" + taskVm.getTaskVmId() + "补货完成，当前应用货到信息快照=" + map);
        Map<Integer, Integer> currNumMap = new HashMap<>();
        try {
            for (int i = 0; i < channelIds.length; i++) {
//                TMtVmChannel mtVmChannel = Cayenne.objectForPK(context, TMtVmChannel.class, channelIds[i]);
//                mtVmChannel.setCurrNum(currNums[i]);
                currNumMap.put(channelIds[i], currNums[i]);
            }
            //不保存到数据库，调用MCS通讯接口，由接口执行保存
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
        }
        return currNumMap;
    }

    @Override
    public Map<String, String> pickup(ObjectContext context, List<TTaskVm> ongoingList, Integer[] skuIds, Integer[] counts, TSysUser sysUser) {
        Map<String, String> resMap = new HashMap<>();
        try {
            if (ongoingList == null || ongoingList.isEmpty() || skuIds == null || counts == null || skuIds.length != counts.length)
                return null;
            ongoingList.stream().filter(taskVm -> !IntegerUtils.isPositiveInteger(taskVm.getAlreadyPickup())).forEach(taskVm -> taskVm.setAlreadyPickup(TTaskVm.ALREADY_PICKUP_TRUE));
            for (int i = 0; i < skuIds.length; i++) {
                if (IntegerUtils.isPositiveInteger(skuIds[i]) && IntegerUtils.isPositiveInteger(counts[i])) {
                    TTaskPickupHis taskPickupHis = context.newObject(TTaskPickupHis.class);
                    taskPickupHis.setPickupSkuId(skuIds[i]);
                    taskPickupHis.setPickupCount(counts[i]);
                    taskPickupHis.setPickupUserId(sysUser.getId());
                    taskPickupHis.setPickupTime(new Date());
                }
            }
            context.commitChanges();
            resMap.put("type", "success");
            return resMap;
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage());
            resMap.put("type", "error");
            return resMap;
        }
    }

    @Override
    public TTaskVm getUnDealTaskToday(ObjectContext context, TMtVm mtVm, Integer taskType, String day) {
        return taskVmDao.getUnDealTaskToday(context, mtVm, taskType, day);
    }

    @Override
    public void calculateMaxCapacity(ObjectContext context, TMtVm mtVm, List<TaskChannel> voList, TTaskVm taskVm) {
        try {
            List list = reserveStatisticsDao.findTodayReserveStatisticsByVm(context, mtVm);
            if (null != list && list.size() > 0) {
                for (Object o : list) {
                    TReserveStatistics statistics = (TReserveStatistics) o;
                    Integer skuId = Optional.ofNullable(statistics).map(TReserveStatistics::getSku).map(TMtSku::getId).orElse(null);
                    Integer count = statistics.getPendingNum();
                    for (TaskChannel taskChannel : voList) {
                        if (taskChannel.getSkuId().equals(skuId)) {
                            Integer lack;
                            if (!taskChannel.isChangeSku() && taskVm.getTaskLevel() != TTaskVm.LEVEL_AUTO) {
                                //自动工单或者该货道修改过，则当前货道余量按0算
                                lack = taskChannel.getMaxCapacity() - taskChannel.getCurrCapacity();
                            } else {
                                lack = taskChannel.getMaxCapacity();
                            }
                            if (count >= lack) {
                                count = count - lack;
                                taskChannel.setTargetCapacity(taskChannel.getMaxCapacity());
                            } else {
                                if (!taskChannel.isChangeSku() && taskVm.getTaskLevel() != TTaskVm.LEVEL_AUTO) {
                                    taskChannel.setTargetCapacity(taskChannel.getCurrCapacity() + count);
                                } else {
                                    taskChannel.setTargetCapacity(count);
                                }
                                count = 0;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    @Override
    public List<Integer> updateReverseStatistics(TTaskVm taskVm, DeliveryTaskJson deliveryTaskJson) {
        List<Integer> skuList = new ArrayList<>();
        logger.info("更新预定单数量 vmId=" + Optional.of(taskVm).map(TTaskVm::getMtVm).map(TMtVm::getId).orElse(null));
        ObjectContext context = taskVm.getObjectContext();
        try {
            TMtVm mtVm = taskVm.getMtVm();
            List<TReserveStatistics> list = reserveStatisticsDao.findTodayReserveStatisticsByVm(context, mtVm)
                    .stream().map(TReserveStatistics.class::cast).collect(Collectors.toList());
            List<DeliveryObject> channelList = deliveryTaskJson.getFillList();
            List<DeliveryObject> adjustList = deliveryTaskJson.getAdjustList();
            List<DeliveryObject> cleanList = deliveryTaskJson.getCleanList();
            channelList.addAll(adjustList);
            if (!isFirst(taskVm) && CheckUtils.check(cleanList)) {
                channelList.addAll(cleanList);
            }
            Map<Integer, List<DeliveryObject>> map = channelList.stream().collect(Collectors.groupingBy(DeliveryObject::getSkuId));
            if (CheckUtils.check(list) && CheckUtils.check(channelList)) {
                list.forEach(
                        tReserveStatistics -> {
                            Integer skuId = Optional.of(tReserveStatistics).map(TReserveStatistics::getSku).map(TMtSku::getId).orElse(null);
                            Integer count = map.get(skuId) == null ? 0 : map.get(skuId).stream().mapToInt(DeliveryObject::getFillCount).sum();//商品实补总量\
                            Integer clear = map.get(skuId) == null ? 0 : map.get(skuId).stream().mapToInt(DeliveryObject::getClearCount).sum();
                            tReserveStatistics.setPendingNum(tReserveStatistics.getPendingNum() - count + clear);
                            tReserveStatistics.setDistributionNum(tReserveStatistics.getDistributionNum() + count - clear);
                            if (count > 0) skuList.add(skuId);
                        });
                context.commitChanges();
            }

        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
        }
        return skuList;
    }

    @Override
    public String getChannelMergeDetail(TTaskVm taskVm) {
        try {
            TMtVm mtVm = taskVm.getMtVm();
            TMtVmModel vmModel = Cayenne.objectForPK(taskVm.getObjectContext(), TMtVmModel.class, mtVm.getVmModelId());
            Map<String, TMtVmChannel> channelInfoMap = mtVmChannelDao.getMtVmChannelInfoMap(mtVm);
            if (mtVm.hasNextChannel()) {
                return getAllTask(channelInfoMap, mtVm.getChannelNext(), vmModel);
            } else {
                return getFillTask(channelInfoMap, vmModel);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public DeliveryTaskJson getTaskInfo(TTaskVm taskVm) {
        List<DeliveryObject> allList = new ArrayList<>();
        if (taskVm.getTaskVmStatus() == TTaskVm.STATUS_FINISHED) {
            allList = taskVm
                    .getTaskVmChannel()
                    .stream()
                    .map(DeliveryObject::ofFinish)
                    .filter(CheckUtils::check)
                    .collect(Collectors.toList());
        } else {
            TMtVm mtVm = taskVm.getMtVm();
            taskVmDao.refreshChannel(taskVm);//强行刷新货道信息
            boolean hasNext = mtVm.hasNextChannel();
            Map<String, TMtVmChannelNext> channelNextMap = mtVm.getChannelNext();
            Map<TMtSku, TReserveStatistics> map = new HashMap<>();
            if (taskVm.getMtVm().isReserve()) {
                Map<TMtSku, List<TReserveStatistics>> listMap = reserveStatisticsDao.findTodayReserveStatisticsByVm(taskVm.getObjectContext(), taskVm.getMtVm())
                        .stream()
                        .map(TReserveStatistics.class::cast)
                        .collect(Collectors.groupingBy(TReserveStatistics::getSku));
                for (TMtSku sku : listMap.keySet()) {
                    List<TReserveStatistics> list = listMap.get(sku);
                    if (CheckUtils.check(list)) {
                        map.put(sku, list.get(0));
                    }
                }
            }
            logger.error(hasNext + "");
            boolean isFirst = this.isFirst(taskVm);
            List<DeliveryObject> manualList = new ArrayList<>();
            if (taskVm.isManualEditTask()) {
                manualList = taskVm
                        .getTaskVmChannel()
                        .stream()
                        .map(channel -> {
                            if (hasNext) {
                                TMtVmChannelNext channelNext = channelNextMap.get(channel.getChannel().getChannelCode());
                                return DeliveryObject.ofManualChange(channel, channelNext);
                            }
                            return DeliveryObject.ofManualWithoutChange(channel);
                        })
                        .filter(CheckUtils::check)
                        .flatMap(List::stream)
                        .collect(Collectors.toList());
            }
            allList.addAll(manualList);
            List<String> channelCodeList = manualList.stream().map(DeliveryObject::getChannelCode).collect(Collectors.toList());
            List<TMtVmChannel> channelList = mtVm.getChannelInfo();
            List<DeliveryObject> fullList = channelList
                    .stream()
                    .sorted()
                    .filter(channel -> !channelCodeList.contains(channel.getChannelCode()))
                    .map(channel -> {
                        if (hasNext) {
                            TMtVmChannelNext channelNext = channelNextMap.get(channel.getChannelCode());
                            return DeliveryObject.ofFullChange(channel, channelNext, map, isFirst);
                        }
                        return DeliveryObject.ofFullWithoutChange(channel, map, isFirst);
                    })
                    .filter(CheckUtils::check)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());
            allList.addAll(fullList);
        }
        DeliveryTaskJson deliveryTaskJson = DeliveryTaskJson.newInstance();
        deliveryTaskJson
                .setFillList(allList.stream().filter(deliveryObject -> deliveryObject.getType() == TTaskVmChannel.CHANNEL_TYPE_FILL).sorted().collect(Collectors.toList()))
                .setCleanList(allList.stream().filter(deliveryObject -> deliveryObject.getType() == TTaskVmChannel.CHANNEL_TYPE_CLEAN).sorted().collect(Collectors.toList()))
                .setAdjustList(allList.stream().filter(deliveryObject -> deliveryObject.getType() == TTaskVmChannel.CHANNEL_TYPE_ADJUST).sorted().collect(Collectors.toList()))
                .setTaskId(taskVm.getTaskVmId())
                .setTaskStatus(taskVm.getTaskVmStatus());
        taskVm.getObjectContext().rollbackChanges();
        return deliveryTaskJson;
    }

    @Override
    public DeliveryTaskJson calculateFillCount(DeliveryTaskJson deliveryTaskJson) {
        List<DeliveryObject> fillList = Optional.of(deliveryTaskJson)
                .map(DeliveryTaskJson::getFillList)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(DeliveryObject::calculateFillCount)
                .collect(Collectors.toList());
        List<DeliveryObject> adjustList = Optional.of(deliveryTaskJson)
                .map(DeliveryTaskJson::getAdjustList)
                .map(List::stream)
                .orElse(Stream.empty())
                .map(DeliveryObject::calculateFillCount)
                .collect(Collectors.toList());
        deliveryTaskJson.setFillList(fillList).setAdjustList(adjustList);
        return deliveryTaskJson;
    }


    /**
     * 返回0为无任务，返回1为合并，返回-1为拆分，返回2为拆分+合并，处理时>0显示合并，后续如果需求变更可分别判断1和2
     *
     * @param oldChannel
     * @param newChannel
     * @return
     */
    private Integer checkMerge(TMtVmChannel oldChannel, TMtVmChannelNext newChannel) {
        Integer qty1 = oldChannel.getMergeQty();
        Integer qty2 = newChannel.getMergeQty();
        if (qty1 != 1 && qty2 == 1) return -1;
        if (qty1 == 0 && qty2 > 1) return 2;
        if (qty1 > 1 && qty2 > 1 && qty2 < qty1) return -1;
        if (qty1 >= 1 && qty2 > qty1) return 1;
        return 0;
    }

    private boolean checkClean(TMtVmChannel oldChannel, TMtVmChannelNext newChannel) {
        if (oldChannel.getSku() != null && newChannel.getSku() == null) {
            return true;
        } else if (oldChannel.getSku() != null && !oldChannel.getSku().getId().equals(newChannel.getSku().getId())) {
            return true;
        }
        return false;
    }


    /**
     * 货道无变化
     *
     * @param channelInfoMap
     * @param voMap
     * @param vmModel
     * @return
     */
    private Map<String, Object> getFillTask(Map<String, TMtVmChannel> channelInfoMap, Map<String, TaskChannel> voMap, TMtVmModel vmModel) {
        StringBuilder builder = new StringBuilder();
        List<TaskChannel> voList = new ArrayList<>();
        Map<String, Object> resMap = new HashMap<>();
        int row = vmModel.getChannelLayerQty();
        String col = vmModel.getChannelPerLayer();
        String cols[] = col.split(",");
        for (int i = 1; i <= row; i++) {
            builder.append("<ul>");
            int colInt = Integer.parseInt(cols[i - 1]);
            Integer demp = Integer.parseInt(cols[i - 1]);//当前行有多少列
            for (int n = 1; n <= colInt; n++) {
                String channelCode = i + "-" + n;
                String text = "";
                if (null != voMap.get(channelCode)) {
                    TaskChannel taskChannel = voMap.get(channelCode);
                    taskChannel.setMaxCapacity(channelInfoMap.get(channelCode).getMaxNum());
                    taskChannel.setTargetCapacity(channelInfoMap.get(channelCode).getMaxNum());
                    voList.add(taskChannel);
                }
                Integer mergeQty;
                mergeQty = channelInfoMap.get(channelCode).getMergeQty();
                if (mergeQty == 0) continue;
                builder.append(" <li class='w").append(100 / demp * mergeQty).append("'><p>").append(channelCode).append("</p><p>").append(text).append("</p></li>");
                builder.append("</li>");
            }
            builder.append("</ul>");
        }
        resMap.put("1", builder.toString());
        resMap.put("3", voList);
        return resMap;
    }

    /**
     * 货道有变化
     *
     * @param channelInfoMap
     * @param voMap
     * @param vmModel
     * @return
     */
    private Map<String, Object> getAllTask(Map<String, TMtVmChannel> channelInfoMap, Map<String, TaskChannel> voMap, Map<String, TMtVmChannelNext> channelNextMap, TMtVmModel vmModel) {
        List<TaskChannel> voList = new ArrayList<>();
        Map<String, Object> resMap = new HashMap<>();
        List<TMtVmChannel> cleanList = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        int row = vmModel.getChannelLayerQty();
        String col = vmModel.getChannelPerLayer();
        String cols[] = col.split(",");
        for (int i = 1; i <= row; i++) {
            builder.append("<ul>");
            int colInt = Integer.parseInt(cols[i - 1]);
            Integer demp = Integer.parseInt(cols[i - 1]);//当前行有多少列
            for (int n = 1; n <= colInt; n++) {
                String channelCode = i + "-" + n;
                String text = "";
                TaskChannel taskChannel = null;
                if (null != voMap.get(channelCode)) {
                    taskChannel = voMap.get(channelCode);
                    taskChannel.setMaxCapacity(channelNextMap.get(channelCode).getMaxNum());
                    taskChannel.setTargetCapacity(channelNextMap.get(channelCode).getMaxNum());
                }
                if (checkClean(channelInfoMap.get(channelCode), channelNextMap.get(channelCode))) {
                    cleanList.add(channelInfoMap.get(channelCode));
                    if (null != voMap.get(channelCode) && taskChannel != null) {
                        taskChannel.setChangeSku(true);
                    }
                }
                int merge = checkMerge(channelInfoMap.get(channelCode), channelNextMap.get(channelCode));
                Integer mergeQty;
                mergeQty = channelNextMap.get(channelCode).getMergeQty();
                if (mergeQty == 0) continue;
                StringBuilder liStr = new StringBuilder();
                switch (merge) {
                    case 0:
                        liStr.append("<li class='w").append(100 / demp * mergeQty).append("'>");
                        break;
                    case -1:
                        text += "拆";
                        liStr.append("<li class='w").append(100 / demp * mergeQty).append(" cover_green").append("'>");
                        break;
                    case 1:
                        text += "合";
                        liStr.append("<li class='w").append(100 / demp * mergeQty).append(" cover_green").append("'>");
                        break;
                    case 2:
                        text += "合";
                        liStr.append("<li class='w").append(100 / demp * mergeQty).append(" cover_green").append("'>");
                        break;
                    default:
                        liStr.append("<li class='w").append(100 / demp * mergeQty).append("'>");
                        break;
                }
                builder.append(liStr).append("<p>").append(channelCode).append("</p><p>").append(text).append("</p></li>");
                builder.append("</li>");
                if (null != taskChannel) {
                    voList.add(taskChannel);
                }
//                }
            }
            builder.append("</ul>");
        }
        resMap.put("1", builder.toString());
        resMap.put("2", cleanList);
        resMap.put("3", voList);
        return resMap;
    }


    /**
     * 货道无变化
     *
     * @param channelInfoMap
     * @param vmModel
     * @return
     */
    private String getFillTask(Map<String, TMtVmChannel> channelInfoMap, TMtVmModel vmModel) {
        StringBuilder builder = new StringBuilder();
        int row = vmModel.getChannelLayerQty();
        String col = vmModel.getChannelPerLayer();
        String cols[] = col.split(",");
        for (int i = 1; i <= row; i++) {
            builder.append("<ul>");
            int colInt = Integer.parseInt(cols[i - 1]);
            for (int n = 1; n <= colInt; n++) {
                String channelCode = i + "-" + n;
                String text = "";
                Integer mergeQty;
                mergeQty = channelInfoMap.get(channelCode).getMergeQty();
                if (mergeQty == 0) continue;
                builder.append(" <li class='w").append(100 / colInt * mergeQty).append("'><p>").append(channelCode).append("</p><p>").append(text).append("</p></li>");
                builder.append("</li>");
            }
            builder.append("</ul>");
        }
        return builder.toString();
    }

    /**
     * 货道有变化
     *
     * @param channelInfoMap
     * @param vmModel
     * @return
     */
    private String getAllTask(Map<String, TMtVmChannel> channelInfoMap, Map<String, TMtVmChannelNext> channelNextMap, TMtVmModel vmModel) {
        StringBuilder builder = new StringBuilder();
        int row = vmModel.getChannelLayerQty();
        String col = vmModel.getChannelPerLayer();
        String cols[] = col.split(",");
        for (int i = 1; i <= row; i++) {
            builder.append("<ul>");
            int colInt = Integer.parseInt(cols[i - 1]);
            for (int n = 1; n <= colInt; n++) {
                String channelCode = i + "-" + n;
                String text = "";
                int merge = checkMerge(channelInfoMap.get(channelCode), channelNextMap.get(channelCode));
                Integer mergeQty;
                mergeQty = channelNextMap.get(channelCode).getMergeQty();
                if (mergeQty == 0) continue;
                StringBuilder liStr = new StringBuilder();
                switch (merge) {
                    case 0:
                        liStr.append("<li class='w").append(100 / colInt * mergeQty).append("'>");
                        break;
                    case -1:
                        text += "拆";
                        liStr.append("<li class='w").append(100 / colInt * mergeQty).append(" cover_green").append("'>");
                        break;
                    case 1:
                        text += "合";
                        liStr.append("<li class='w").append(100 / colInt * mergeQty).append(" cover_green").append("'>");
                        break;
                    case 2:
                        text += "合";
                        liStr.append("<li class='w").append(100 / colInt * mergeQty).append(" cover_green").append("'>");
                        break;
                    default:
                        liStr.append("<li class='w").append(100 / colInt * mergeQty).append("'>");
                        break;
                }
                builder.append(liStr).append("<p>").append(channelCode).append("</p><p>").append(text).append("</p></li>");
                builder.append("</li>");

            }
            builder.append("</ul>");
        }
        return builder.toString();
    }

    private boolean isFirst(TTaskVm taskVm) {
        List list = taskVmDao.searchFinishTask(taskVm.getObjectContext(), taskVm.getMtVm(), TTaskVm.TYPE_DELIVERY);
        if (taskVm.getTaskVmStatus() == TTaskVm.STATUS_FINISHED) {
            return CheckUtils.check(list) && list.size() == 1;
        }
        return !CheckUtils.check(list);
    }

}
