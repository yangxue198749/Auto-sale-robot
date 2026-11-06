package com.kdx.mp.service;

import com.kdx.mp.vo.TaskChannel;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.custom.DeliveryTaskJson;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/21.
 */
public interface IMpTaskService {
    /**
     * 显示未完成的
     *
     * @param context
     * @param userId
     * @param taskType
     * @return
     */
    public List findByOwner(ObjectContext context, Integer userId, Integer taskType);

    /**
     * 显示所有
     *
     * @param context
     * @param userId
     * @param day
     * @param taskType
     * @return
     */
    public List findTaskByOwnerAndDay(ObjectContext context, Integer userId, String day, Integer taskType);

    public List findFinishTaskByOwnerAndDay(ObjectContext context, Integer userId, String day, Integer taskType);

    /**
     * 计算件数
     *
     * @param context
     * @param list
     * @return
     */
    public Map<TMtSku, Integer> calculateSku(ObjectContext context, List<TTaskVm> list);

    public void updateTask(ObjectContext context, Integer status, Integer... taskIds);

    public void withdrawVm(ObjectContext context, TTaskVm taskVm);

    public void deployVm(ObjectContext context, TTaskVm taskVm);

    public void updateVmChannel(ObjectContext context, Integer taskId);

    public Map<String, Object> getChannelDetail(ObjectContext context, TTaskVm taskVm);

    public Map<Integer, Integer> finishDelivery(ObjectContext context, TTaskVm taskVm, Integer[] currNums, Integer[] channelIds);

    public Map<String, String> pickup(ObjectContext context, List<TTaskVm> ongoingList, Integer[] skuIds, Integer[] counts, TSysUser sysUser);

    TTaskVm getUnDealTaskToday(ObjectContext context, TMtVm mtVm, Integer taskType, String day);

    public void calculateMaxCapacity(ObjectContext context, TMtVm mtVm, List<TaskChannel> voList, TTaskVm taskVm);

    public List<Integer> updateReverseStatistics(TTaskVm taskVm, DeliveryTaskJson deliveryTaskJson);

    public String getChannelMergeDetail(TTaskVm taskVm);

    public DeliveryTaskJson getTaskInfo(TTaskVm taskVm);

    public DeliveryTaskJson calculateFillCount(DeliveryTaskJson deliveryTaskJson);


}
