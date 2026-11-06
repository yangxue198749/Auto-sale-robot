package com.kdx.vms.service.impl;

import com.kdx.mp.commons.util.WeChatConfig;
import com.kdx.mp.util.WeChatUtils;
import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.sendMessage.UcPaasUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.*;
import com.kdx.vms.service.ISysLogService;
import com.kdx.vms.service.ITaskVmService;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 工单实现类
 *
 * @author zhangguodong
 */
@Service("taskVmService")
public class TaskVmServiceImpl implements ITaskVmService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private TaskVmDao taskVmDao;
    @Resource
    private MtVmDao mtVmDao;
    @Resource
    private SysUserDao sysUserDao;
    @Resource
    private MtNodeDao mtNodeDao;
    @Resource
    private ISysLogService sysLogService;
    @Resource
    private ReserveStatisticsDao reserveStatisticsDao;
    @Resource
    private MtVmChannelDao mtVmChannelDao;

    @Override
    public List<TTaskVm> findPage(ObjectContext context, TSysUser user,
                                  Integer searchField, String searchFieldValue, Integer taskVmStatus,
                                  String startTime, String endTime, Integer taskType,
                                  Integer pageSize) {
        try {
            return taskVmDao.findPage(context, user, searchField, searchFieldValue, taskVmStatus, startTime, endTime, taskType, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public String cancelTask(ObjectContext context, TSysUser sysUser,
                             Integer[] ids) {
        String errMsg = null;
        try {
            if (null == ids) {
                return "请选择工单";
            }
            for (Integer taskVmId : ids) {
                TTaskVm taskVm = taskVmDao.findByPk(context, taskVmId);
                if (null != taskVm) {
                    if (taskVm.getTaskVmStatus() == TTaskVm.STATUS_CLOSED) {
                        context.rollbackChanges();
                        return "工单已取消，无法再次操作";
                    }
                    if (taskVm.getTaskVmStatus() != TTaskVm.STATUS_FINISHED && taskVm.getTaskVmStatus() != TTaskVm.STATUS_CLOSED) {
                        taskVm.setUtime(new Date());
                        taskVm.setUtor(sysUser.getId());
                        taskVm.setTaskVmStatus(TTaskVm.STATUS_CLOSED);
                    }
                    /*  5.0.0a 取消维护工单不发送短信通知
                    if (taskVm.getTaskType() == TTaskVm.TYPE_REPAIR) {
                        TSysUser oldOwnUser = sysUserDao.findUserByPk(context, taskVm.getOwner());
                        sendCancelRepairMsg(taskVm, oldOwnUser);
                    }*/
                }
            }
            context.commitChanges();
            ObjectContext logContext = ObjectContextFactory.getInstance().newObjectContext();
            try {
                sysLogService.saveSysLog("取消工单" + Arrays.asList(ids), TSysLog.UPDATE, SyslogEnum.WORKORDER_CODE, sysUser.getId(), logContext);
            } catch (Exception e) {
                logContext.rollbackChanges();
                logger.error(e.getLocalizedMessage(), e);
            }
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public List mtVmPage(ObjectContext context, TSysUser user,
                         String searchNodeName, String searchInnerCode, Integer pageSize, Integer vmStatus) {
        try {
            return mtVmDao.findPage(context, user, searchNodeName, searchInnerCode, pageSize, vmStatus);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TTaskVm findByPk(ObjectContext context, Integer taskVmId) {
        try {
            return taskVmDao.findByPk(context, taskVmId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public String editTaskVm(ObjectContext context, TSysUser sysUser,
                             Integer taskVmId, Integer editOwnerId, String remarks, String taskTime) {
        String errMsg = null;
        try {
            if (null == taskVmId) {
                return "请选择工单";
            }
            if (null == editOwnerId) {
                return "请选择负责人";
            }
            TTaskVm taskVm = taskVmDao.findByPk(context, taskVmId);
            if (null == taskVm) {
                return "请选择工单";
            }
            TSysUser ownUser = sysUserDao.findUserByPk(context, editOwnerId);
            if (ownUser == null) {
                return "任务负责人不存在";
            }
            if (taskVm.getTaskVmStatus() == TTaskVm.STATUS_INITIALIZATION || taskVm.getTaskVmStatus() == TTaskVm.STATUS_ONGOING) {
                boolean change = false;
                Integer oldOwner = taskVm.getOwner();
                if (!editOwnerId.equals(oldOwner)) {
                    change = true;
                }
                TSysUser oldOwnUser = null;
                if (null != oldOwner) {
                    oldOwnUser = sysUserDao.findUserByPk(context, oldOwner);
                }
                Date taskTimeDate = new Date();
                try {
                    taskTimeDate = StringUtil.strToDate(taskTime, StringUtil.YMDPattern);
                } catch (Exception e) {
                    return "执行时间错误";
                }
                taskVm.setOwner(editOwnerId);
                taskVm.setUtime(new Date());
                taskVm.setUtor(sysUser.getId());
                taskVm.setTaskRemarks(remarks);
                if (null != taskTime) {
                    taskVm.setTaskTime(taskTimeDate);
                }
                context.commitChanges();
                ObjectContext logContext = ObjectContextFactory.getInstance().newObjectContext();
                try {
                    sysLogService.saveSysLog("修改工单" + taskVm.getTaskVmId(), TSysLog.UPDATE, SyslogEnum.WORKORDER_CODE, sysUser.getId(), logContext);
                } catch (Exception e) {
                    logContext.rollbackChanges();
                    logger.error(e.getLocalizedMessage(), e);
                }
                /*if (taskVm.getTaskType() == TTaskVm.TYPE_REPAIR && change) {
                    if (null != oldOwnUser && !StringUtil.isEmpty(oldOwnUser.getUserPhone())) {
                        sendCancelRepairMsg(taskVm, oldOwnUser);
                    }
					if(!StringUtil.isEmpty(ownUser.getUserPhone())){
						sendCreateRepairMsg(taskVm.getTaskRemarks(), taskVm.getMtVm(), taskVm, ownUser.getUserPhone());
					}
                }*/
            } else {
                return "只能修改未完成或者未取消的工单";
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    private void sendCancelRepairMsg(TTaskVm taskVm, TSysUser oldOwnUser) {
        try {
            String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";//
            String token = "430765a9bfb6177ea8627bd9c280a975";
            String appId = "2c046def4a4443d795ac8f96094798e8";//应用Id
            String templateId = "29015";//维修模板Id：26941；取消维修模板Id:29015
            String param = taskVm.getTaskVmId() + "," + taskVm.getMtVm().getInnerCode();
            UcPaasUtil.templateSMS(accountSid, token, appId, templateId, oldOwnUser.getUserPhone(), param);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }

    }

    @Override
    public String createRepairTask(ObjectContext context,
                                   TSysUser user, Integer vmId, Integer ownerId, String taskRemarks, String taskTime) {
        String errMsg = null;
        try {
            if (null == vmId) {
                return "请选择售货机";
            }
            if (null == ownerId) {
                return "请选择任务负责人";
            }
            TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
            if (null == mtVm) {
                logger.error("新建售货机维修工单失败，售货机不存在 vmId = " + vmId);
                return "请选择存在的售货机";
            }
            if (mtVm.getVmStatus() != TMtVm.STATUS_SALE && mtVm.getVmStatus() != TMtVm.STATUS_UNSLAE) {
                return "售货机状态不符";
            }
            TTaskVm unDealTask = taskVmDao.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_REPAIR, null);
            if (null != unDealTask) {
                logger.error("新建售货机维修工单失败,当前售货机存在未处理维修工单  unDealTask = " + unDealTask);
                return "当前售货机存在未处理维修工单";
            }
            TSysUser ownUser = sysUserDao.findUserByPk(context, ownerId);
            if (ownUser == null) {
                return "任务负责人不存在";
            }
            Date taskTimeDate = new Date();
            try {
                taskTimeDate = StringUtil.strToDate(taskTime, StringUtil.YMDPattern);
            } catch (Exception e) {
                return "执行时间错误";
            }
            TTaskVm newTask = context.newObject(TTaskVm.class);
            newTask.setCtime(new Date());
            newTask.setCtor(user.getId());
            newTask.setMtVm(mtVm);
            newTask.setOwner(ownerId);
            newTask.setTaskLevel(TTaskVm.LEVEL_URGENT);
            newTask.setTaskTime(taskTimeDate);
            newTask.setTaskTitle("新建维修工单" + StringUtil.dateToStr(newTask.getCtime(), StringUtil.Pattern));
            newTask.setTaskType(TTaskVm.TYPE_REPAIR);
            newTask.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
            newTask.setTaskRemarks(taskRemarks);
            newTask.setCompany(ownUser.getCompany());
            context.commitChanges();

            //记录日志
            ObjectContext logContext = ObjectContextFactory.getInstance().newObjectContext();
            try {
                sysLogService.saveSysLog(newTask.getTaskTitle(), TSysLog.INSERT, SyslogEnum.WORKORDER_CODE, user.getId(), logContext);
            } catch (Exception e) {
                logContext.rollbackChanges();
                logger.error(e.getLocalizedMessage(), e);
            }
            String mobile = ownUser.getUserPhone();
//			if(!StringUtil.isEmpty(mobile)){
//				//短信通知
//				sendCreateRepairMsg(taskRemarks, mtVm, newTask, mobile);
//			}
            if (!StringUtil.isEmpty(ownUser.getOpenId())) {
                WeChatUtils.sendTaskTemplateMessage(ownUser.getOpenId(), "维护任务", StringUtil.dateToStr(taskTimeDate, StringUtil.YMDPattern), WeChatConfig.MSG_URL + "/mp/mpTask/repair/list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    private void sendCreateRepairMsg(String taskRemarks, TMtVm mtVm,
                                     TTaskVm newTask, String mobile) {
        try {
            TMtNode mtNode = mtVm.getNode();
            String accountSid = "a862b6dffef1ef8f5b6797ac62c50a96";//
            String token = "430765a9bfb6177ea8627bd9c280a975";
            String appId = "2c046def4a4443d795ac8f96094798e8";//应用Id
            String templateId = "26941";//维修模板Id：26941；取消维修模板Id:29015
            //新的维护任务：{1}；售货机编号：{2}；点位：{3}；执行日期：{4}；详情：{5}
            String param = newTask.getTaskVmId() + "," + mtVm.getInnerCode() + "," + mtNode.getNodeName() + "," + StringUtil.dateToStr(newTask.getTaskTime(), StringUtil.YMDPattern) + "," + taskRemarks;
            UcPaasUtil.templateSMS(accountSid, token, appId, templateId, mobile, param);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }

    }

    @Override
    public String createwithdrawTask(ObjectContext context,
                                     TSysUser user, Integer vmId, Integer ownerId, String taskTime) {
        String errMsg = null;
        try {
            if (null == vmId) {
                return "请选择售货机";
            }
            if (null == ownerId) {
                return "请选择任务负责人";
            }
            TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
            if (null == mtVm) {
                logger.error("新建售货机撤机工单失败，售货机不存在 vmId = " + vmId);
                return "请选择存在的售货机";
            }
            if (mtVm.getVmStatus() != TMtVm.STATUS_UNSLAE) {
                return "售货机状态不符";
            }
            TTaskVm unDealTask = taskVmDao.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_WITHDRAW, null);
            if (null != unDealTask) {
                logger.error("新建售货机撤机工单失败,当前售货机存在未处理撤机工单  unDealTask = " + unDealTask);
                return "当前售货机存在未处理撤机工单";
            }
            Date taskTimeDate = new Date();
            try {
                taskTimeDate = StringUtil.strToDate(taskTime, StringUtil.YMDPattern);
            } catch (Exception e) {
                return "执行时间错误";
            }
            TTaskVm newTask = context.newObject(TTaskVm.class);
            newTask.setCtime(new Date());
            newTask.setCtor(user.getId());
            newTask.setMtVm(mtVm);
            newTask.setOwner(ownerId);
            newTask.setTaskLevel(TTaskVm.LEVEL_URGENT);
            newTask.setTaskTime(taskTimeDate);
            newTask.setTaskTitle("新建撤机工单" + StringUtil.dateToStr(newTask.getCtime(), StringUtil.Pattern));
            newTask.setTaskType(TTaskVm.TYPE_WITHDRAW);
            newTask.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
            newTask.setCompany(user.getCompany());
            context.commitChanges();
            //记录日志
            ObjectContext logContext = ObjectContextFactory.getInstance().newObjectContext();
            try {
                sysLogService.saveSysLog(newTask.getTaskTitle(), TSysLog.INSERT, SyslogEnum.WORKORDER_CODE, user.getId(), logContext);
            } catch (Exception e) {
                logContext.rollbackChanges();
                logger.error(e.getLocalizedMessage(), e);
            }
            TSysUser ownUser = sysUserDao.findUserByPk(context, ownerId);
            if (!StringUtil.isEmpty(ownUser.getOpenId())) {
                WeChatUtils.sendTaskTemplateMessage(ownUser.getOpenId(), "撤机任务", StringUtil.dateToStr(taskTimeDate, StringUtil.YMDPattern), WeChatConfig.MSG_URL + "/mp/mpTask/withdraw/list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public String createdeployTask(ObjectContext context,
                                   TSysUser user, Integer vmId, Integer ownerId, String taskTime, Integer nodeId) {
        String errMsg = null;
        try {
            if (null == vmId) {
                return "请选择售货机";
            }
            if (null == ownerId) {
                return "请选择任务负责人";
            }
            if (null == nodeId) {
                return "请选择点位";
            }
            TMtVm mtVm = mtVmDao.findByVmId(context, vmId);
            if (null == mtVm) {
                logger.error("新建售货机投放工单失败，售货机不存在 vmId = " + vmId);
                return "请选择存在的售货机";
            }
            if (mtVm.getVmStatus() != TMtVm.STATUS_UNDEPLOY) {
                return "售货机状态不符";
            }
            TMtNode mtNode = Cayenne.objectForPK(context, TMtNode.class, nodeId);
            if (mtNode == null) {
                logger.error("新建售货机投放工单失败，点位不存在 nodeId = " + nodeId);
                return "请选择存在的点位";
            }
            TTaskVm unDealTask = taskVmDao.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_DEPLOY, null);
            if (null != unDealTask) {
                logger.error("新建售货机投放工单失败,当前售货机存在未处理投放工单  unDealTask = " + unDealTask);
                return "当前售货机存在未处理投放工单";
            }
            Date taskTimeDate = new Date();
            try {
                taskTimeDate = StringUtil.strToDate(taskTime, StringUtil.YMDPattern);
            } catch (Exception e) {
                return "执行时间错误";
            }
            TTaskVm newTask = context.newObject(TTaskVm.class);
            newTask.setCtime(new Date());
            newTask.setCtor(user.getId());
            newTask.setMtVm(mtVm);
            newTask.setOwner(ownerId);
            newTask.setTaskLevel(TTaskVm.LEVEL_URGENT);
            newTask.setTaskTime(taskTimeDate);
            newTask.setTaskTitle("新建投放工单" + StringUtil.dateToStr(newTask.getCtime(), StringUtil.Pattern));
            newTask.setTaskType(TTaskVm.TYPE_DEPLOY);
            newTask.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
            newTask.setCompany(user.getCompany());
            mtVm.setNode(mtNode);
            context.commitChanges();
            //记录日志
            ObjectContext logContext = ObjectContextFactory.getInstance().newObjectContext();
            try {
                sysLogService.saveSysLog(newTask.getTaskTitle(), TSysLog.INSERT, SyslogEnum.WORKORDER_CODE, user.getId(), logContext);
            } catch (Exception e) {
                logContext.rollbackChanges();
                logger.error(e.getLocalizedMessage(), e);
            }
            TSysUser ownUser = sysUserDao.findUserByPk(context, ownerId);
            if (!StringUtil.isEmpty(ownUser.getOpenId())) {
                WeChatUtils.sendTaskTemplateMessage(ownUser.getOpenId(), "投放任务", StringUtil.dateToStr(taskTimeDate, StringUtil.YMDPattern), WeChatConfig.MSG_URL + "/mp/mpTask/deploy/list");
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public String publishTask(ObjectContext context, TSysUser user,
                              Integer[] taskVmId, Integer own, String taskTime) {
        String errMsg = null;
        try {
            if (null == taskVmId) {
                return "请选择工单";
            }
            if (null == own) {
                return "请选择负责人";
            }
            List<TTaskVm> taskList = Arrays.stream(taskVmId)
                    .map(id -> Cayenne.objectForPK(context, TTaskVm.class, id))
                    .filter(taskVm -> null != taskVm)
                    .collect(Collectors.toList());
            if (!CheckUtils.check(taskList) || taskList.size() < taskVmId.length) {
                return "请选择工单";
            }
            TSysUser ownUser = sysUserDao.findUserByPk(context, own);
            if (ownUser == null) {
                return "任务负责人不存在";
            }
            if (taskList.stream().anyMatch(taskVm -> taskVm.getTaskVmStatus() != TTaskVm.STATUS_INITIALIZATION)) {
                return "只能发布未发布的工单";
            }
            ObjectContext logContext = ObjectContextFactory.getInstance().newObjectContext();
            taskList.forEach(taskVm -> {
                taskVm.setOwner(own);
                taskVm.setTaskTime(StringUtil.strToDate(taskTime, StringUtil.YMDPattern));
                taskVm.setUtime(new Date());
                taskVm.setUtor(user.getId());
                taskVm.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
                sysLogService.saveSysLog("发布工单" + taskVm.getTaskVmId(), TSysLog.UPDATE, SyslogEnum.WORKORDER_CODE, user.getId(), logContext);
            });
            context.commitChanges();
            if (!StringUtil.isEmpty(ownUser.getOpenId())) {
                WeChatUtils.sendTaskTemplateMessage(ownUser.getOpenId(), "投放任务", StringUtil.dateToStr(StringUtil.strToDate(taskTime, StringUtil.YMDPattern), StringUtil.YMDPattern), WeChatConfig.MSG_URL + "/mp/mpTask/deploy/list");
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public List mtNodePage(ObjectContext context, String searchNodeName, Integer pageSize, TSysUser user) {
        try {
            return mtNodeDao.getMtNodeList(context, null, null, null, searchNodeName, null, user.getCompany(), null, null, null, null, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TTaskVm> exportTaskByIds(ObjectContext context,
                                         Integer[] taskVmIds) {
        try {
            List<TTaskVm> list = new ArrayList<TTaskVm>();
            for (Integer taskVmId : taskVmIds) {
                list.add(taskVmDao.findByPk(context, taskVmId));
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public boolean hasUnDealTask(ObjectContext context, TMtVm mtVm, Integer taskType) {
        TTaskVm unDealTask = taskVmDao.getUnDealTaskToday(context, mtVm, taskType, StringUtil.dateToStr(new Date(), StringUtil.YMDPattern));
        if (null != unDealTask) {
            return true;
        }
        return false;
    }

    @Override
    public boolean hasUnDealRepairTask(ObjectContext context, TMtVm mtVm) {
        TTaskVm unDealTask = taskVmDao.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_REPAIR, null);
        return null != unDealTask;
    }

    @Override
    public String createDeliveryTaskByIds(ObjectContext context, TSysUser user, String[] vmIds, Integer ownerId, Integer deliveryPattern, List<TaskChannelModel> taskChannelModels) {
        String errMsg = null;
        try {
            if (null == vmIds || vmIds.length == 0) {
                return "请选择售货机";
            }
            if (null == ownerId) {
                return "请选择任务负责人";
            }
            for (int i = 0; i < vmIds.length; i++) {
                TMtVm mtVm = mtVmDao.findByVmId(context, Integer.parseInt(vmIds[i]));
                if (null == mtVm) {
                    logger.error("新建售货机配送工单失败，售货机不存在 vmId = " + Integer.parseInt(vmIds[i]));
                    return "请选择存在的售货机";
                }
                TTaskVm unDealTask = taskVmDao.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_DELIVERY, StringUtil.dateToStr(new Date(), StringUtil.YMDPattern));
                if (null != unDealTask) {
                    logger.error("新建售货机配送工单失败,当前售货机存在未处理配送工单  unDealTask = " + unDealTask);
                    return "当前售货机存在未处理配送工单";
                }

                TTaskVm newTask = context.newObject(TTaskVm.class);
                newTask.setCtime(new Date());
                newTask.setCtor(user.getId());
                newTask.setMtVm(mtVm);
                newTask.setOwner(ownerId);
                newTask.setTaskLevel(TTaskVm.LEVEL_URGENT);
                newTask.setTaskTime(new Date());
                newTask.setTaskTitle("新建补货工单" + StringUtil.dateToStr(newTask.getCtime(), StringUtil.Pattern));
                newTask.setTaskType(TTaskVm.TYPE_DELIVERY);
                newTask.setTaskVmStatus(TTaskVm.STATUS_ONGOING);
                newTask.setCompany(user.getCompany());
                newTask.setDeliveryPattern(deliveryPattern);

                if (null != taskChannelModels && taskChannelModels.size() > 0 && deliveryPattern.equals(1)) {
                    for (TaskChannelModel taskChannelModel : taskChannelModels) {
                        TTaskVmChannel taskVmChannel = context.newObject(TTaskVmChannel.class);

                        taskVmChannel.setCtor(UserUtils.getVmsSystem().getId());
                        taskVmChannel.setSku(Cayenne.objectForPK(context, TMtSku.class, taskChannelModel.getSkuId()));
                        taskVmChannel.setChannel(Cayenne.objectForPK(context, TMtVmChannel.class, taskChannelModel.getChannelId()));
                        taskVmChannel.setCtime(new Date());
                        taskVmChannel.setTaskVm(newTask);
                        taskVmChannel.setBeforeDeliveryStock(taskChannelModel.getCurrNum());

                        if (null != taskChannelModel.getRealClear() && null != taskChannelModel.getRealCount() && taskChannelModel.getRealClear() == 0 && taskChannelModel.getRealCount() == 0) {
                            taskVmChannel.setTaskType(TTaskVmChannel.CHANNEL_TYPE_ADJUST);
                            taskVmChannel.setClearCount(taskChannelModel.getRealClear());
                            taskVmChannel.setSkuCount(taskChannelModel.getRealCount());
                        } else {
                            taskVmChannel.setTaskType(TTaskVmChannel.CHANNEL_TYPE_FILL);
                            taskVmChannel.setClearCount(taskChannelModel.getRealClear());
                            taskVmChannel.setSkuCount(taskChannelModel.getRealCount());
                        }

                        taskVmChannel.setMaxCount(taskChannelModel.getMaxNum());
                    }
                    context.commitChanges();
                } else {
                    context.commitChanges();
                }
                ObjectContext logContext = ObjectContextFactory.getInstance().newObjectContext();
                try {
                    sysLogService.saveSysLog(newTask.getTaskTitle(), TSysLog.INSERT, SyslogEnum.WORKORDER_CODE, user.getId(), logContext);
                } catch (Exception e) {
                    logContext.rollbackChanges();
                    logger.error(e.getLocalizedMessage(), e);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public List searchChannelByVmId(ObjectContext context, Integer vmId) {
        try {
            List<TaskChannelModel> list = new ArrayList<>();
            TMtVm tMtVm = mtVmDao.findByVmId(context, vmId);
            if (tMtVm.hasNextChannel()) {
                Map<String, TMtVmChannelNext> map = tMtVm.getChannelNext();
                for (TMtVmChannel tMtVmChannel : tMtVm.getChannelInfo()) {
                    TMtVmChannelNext mtVmChannelNext = map.get(tMtVmChannel.getChannelCode());
                    if (null != mtVmChannelNext) {
                        if (null == tMtVmChannel.getSku() && null != mtVmChannelNext.getSku()) {
                            if (TMtSku.RESERVE_TRUE.equals(mtVmChannelNext.getSku().getReserve())) {
                                continue;
                            }
                            TaskChannelModel taskChannelModel = createChannelModel(tMtVmChannel.getId(), tMtVmChannel.getChannelCode(), mtVmChannelNext.getMaxNum(),
                                    mtVmChannelNext.getSku().getImgUrl(), mtVmChannelNext.getSku().getId(), mtVmChannelNext.getSku().getSkuName(),
                                    TaskChannelModel.DEFAULT_REALCOUNT, TMtVmChannelNext.DEFAULT_CURRNUM);
                            list.add(taskChannelModel);
                            continue;
                        } else if (null != tMtVmChannel.getSku() && null != mtVmChannelNext.getSku()) {
                            if (tMtVmChannel.getSku().getId().equals(mtVmChannelNext.getSku().getId())) {
                                if (TMtSku.RESERVE_TRUE.equals(mtVmChannelNext.getSku().getReserve())) {
                                    continue;
                                }
                                TaskChannelModel taskChannelModel = new TaskChannelModel();
                                taskChannelModel.setChannelId(tMtVmChannel.getId());
                                taskChannelModel.setMaxNum(mtVmChannelNext.getMaxNum());
                                taskChannelModel.setChannelCode(tMtVmChannel.getChannelCode());
                                taskChannelModel.setImgUrl(tMtVmChannel.getSku().getImgUrl());
                                taskChannelModel.setSkuId(tMtVmChannel.getSku().getId());
                                taskChannelModel.setSkuName(tMtVmChannel.getSku().getSkuName());
                                taskChannelModel.setCurrNum(tMtVmChannel.getCurr());
                                taskChannelModel.setRealCount(TMtVmChannelNext.DEFAULT_CURRNUM);
                                if (tMtVmChannel.getTempMaxCapacity() > mtVmChannelNext.getMaxNum()) {
                                    if (tMtVmChannel.getCurr() > mtVmChannelNext.getMaxNum()) {
                                        taskChannelModel.setRealClear(tMtVmChannel.getCurr() - mtVmChannelNext.getMaxNum());
                                        taskChannelModel.setRealCount(TMtVmChannelNext.DEFAULT_CURRNUM);
                                    }
                                }
                                list.add(taskChannelModel);
                                continue;
                            } else {
                                if (TMtSku.RESERVE_TRUE.equals(mtVmChannelNext.getSku().getReserve())) {
                                    continue;
                                }
                                TaskChannelModel taskChannelModel = createChannelModel(tMtVmChannel.getId(), tMtVmChannel.getChannelCode(), mtVmChannelNext.getMaxNum(),
                                        mtVmChannelNext.getSku().getImgUrl(), mtVmChannelNext.getSku().getId(), mtVmChannelNext.getSku().getSkuName(),
                                        TaskChannelModel.DEFAULT_REALCOUNT, TMtVmChannelNext.DEFAULT_CURRNUM);
                                list.add(taskChannelModel);
                                continue;
                            }
                        }
                    }
                }
                return list;
            } else {
                List<TMtVmChannel> tMtVmChannels = mtVmChannelDao.findByVmId(context, vmId);
                for (TMtVmChannel tMtVmChannel : tMtVmChannels) {
                    if (TMtSku.RESERVE_TRUE.equals(tMtVmChannel.getSku().getReserve())) {
                        continue;
                    }
                    TaskChannelModel taskChannelModel = createChannelModel(tMtVmChannel.getId(), tMtVmChannel.getChannelCode(), tMtVmChannel.getMaxNum(),
                            tMtVmChannel.getSku().getImgUrl(), tMtVmChannel.getSku().getId(), tMtVmChannel.getSku().getSkuName(), tMtVmChannel.getCurr(),
                            TMtVmChannelNext.DEFAULT_CURRNUM);
                    list.add(taskChannelModel);
                }
                return list;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    private TaskChannelModel createChannelModel(Integer channelId, String channelCode, Integer maxNum, String imgUrl, Integer skuId, String skuName, Integer currNum, Integer realCount) {
        TaskChannelModel taskChannelModel = new TaskChannelModel();
        taskChannelModel.setChannelId(channelId);
        taskChannelModel.setChannelCode(channelCode);
        taskChannelModel.setMaxNum(maxNum);
        taskChannelModel.setImgUrl(imgUrl);
        taskChannelModel.setSkuId(skuId);
        taskChannelModel.setSkuName(skuName);
        taskChannelModel.setCurrNum(currNum);
        taskChannelModel.setRealCount(realCount);
        return taskChannelModel;
    }

}
