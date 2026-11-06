package com.kdx.mp.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.mp.service.IMpTaskService;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.model.custom.DeliveryTaskJson;
import com.kdx.vms.mq.PublisherPool;
import com.kdx.vms.service.IExtractCodeService;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmChannelService;
import com.kdx.vms.service.IMtVmShelfService;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2016/12/23.
 */
@Controller
@RequestMapping("mp/mpTask/delivery")
public class MpDeliveryTaskController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 没有权限地址*
     */
    private String unauthorizedUrl = "/mp/auth/unauthorized";
    @Autowired
    private IMpTaskService taskService;
    @Autowired
    private IMcsService mcsService;
    @Autowired
    private IMtVmChannelService mtVmChannelService;
    @Autowired
    private IMtVmShelfService mtVmShelfService;
    @Autowired
    private IExtractCodeService extractCodeService;
    private String url = "/mp/mpTask/delivery/list";

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        try {

//        TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            List taskList = taskService.findTaskByOwnerAndDay(context, currentUser.getId(), DateUtils.getTime1(), TTaskVm.TYPE_DELIVERY);
            List<TTaskVm> ongoingTaskList = new ArrayList<>();
            List<TTaskVm> closeTaskList = new ArrayList<>();
            List<TTaskVm> initialTaskList = new ArrayList<>();
            for (Object o : taskList) {
                TTaskVm taskVm = (TTaskVm) o;
                if (taskVm.getTaskVmStatus() == null) continue;
                switch (taskVm.getTaskVmStatus()) {
                    case TTaskVm.STATUS_INITIALIZATION:
                        initialTaskList.add(taskVm);
                        break;
                    case TTaskVm.STATUS_ONGOING:
                        ongoingTaskList.add(taskVm);
                        break;
                    case TTaskVm.STATUS_FINISHED:
                        closeTaskList.add(taskVm);
                        break;
                    case TTaskVm.STATUS_CLOSED:
                        closeTaskList.add(taskVm);
                        break;
                }
            }
            request.setAttribute("ongoingTaskList", ongoingTaskList);
            request.setAttribute("closeTaskList", closeTaskList);
            request.setAttribute("initialTaskList", initialTaskList);
            //获取所有提货信息
            Map<TMtSku, Integer> map = taskService.calculateSku(context, ongoingTaskList);
            Integer count = 0;
            for (TMtSku sku : map.keySet()) {
                count += map.get(sku);
            }
            request.setAttribute("count", count);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/task/delivery/delivery_task_list";
    }

    @RequestMapping("order/list")
    public String orderList(HttpServletRequest request) {
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        List<?> taskList = taskService.findTaskByOwnerAndDay(context, currentUser.getId(), DateUtils.getTime1(), TTaskVm.TYPE_DELIVERY);
        List<TTaskVm> ongoingTaskList = taskList.stream().map(TTaskVm.class::cast).filter(taskVm -> taskVm.getTaskVmStatus().equals(TTaskVm.STATUS_ONGOING)).collect(Collectors.toList());
        Map<TMtSku, Integer> orderMap = taskService.calculateSku(context, ongoingTaskList);
        request.setAttribute("orderMap", orderMap);
        return "mp/task/delivery/delivery_task_order";
    }

    @RequestMapping("detail")
    public String detail(HttpServletRequest request, HttpServletResponse response, Integer taskId) {
        try {

            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            if (!taskVm.getOwner().equals(currentUser.getId())) {
                try {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            request.setAttribute("task", taskVm);
            String html = this.taskService.getChannelMergeDetail(taskVm);
            request.setAttribute("html", html);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/task/delivery/delivery_task_detail";
    }

    @RequestMapping(value = "detail/taskInfo", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String taskInfo(HttpServletRequest request, HttpServletResponse response, Integer taskId) {
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            return JSON.toJSONString(this.taskService.getTaskInfo(taskVm));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "";
    }


    @RequestMapping("order/pickup")
    public
    @ResponseBody
    Map<String, String> orderPickup(HttpServletRequest request, HttpServletResponse response, Integer[] skuIds, Integer[] counts) {

        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        List<?> taskList = taskService.findTaskByOwnerAndDay(context, currentUser.getId(), DateUtils.getTime1(), TTaskVm.TYPE_DELIVERY);
        List<TTaskVm> ongoingTaskList = taskList.stream().map(TTaskVm.class::cast).filter(taskVm -> taskVm.getTaskVmStatus().equals(TTaskVm.STATUS_ONGOING)).collect(Collectors.toList());
        Map<String, String> resMap = taskService.pickup(context, ongoingTaskList, skuIds, counts, currentUser);
        resMap.put("url", url);
        return resMap;
    }


    @RequestMapping("exit")
    public
    @ResponseBody
    Map<String, String> exit(HttpServletRequest request, Integer taskId) {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("url", url);
        resMap.put("type", "success");
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        taskService.updateTask(context, TTaskVm.STATUS_INITIALIZATION, taskId);
        return resMap;
    }

    @RequestMapping("join")
    public
    @ResponseBody
    Map<String, String> join(HttpServletRequest request, Integer[] taskIds) {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("url", url);
        resMap.put("type", "success");
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        taskService.updateTask(context, TTaskVm.STATUS_ONGOING, taskIds);
        return resMap;
    }

    @RequestMapping("ignore")
    public
    @ResponseBody
    Map<String, String> ignore(HttpServletRequest request, HttpServletResponse response, Integer taskId) {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("url", url);
        resMap.put("type", "success");
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
        if (!taskVm.getOwner().equals(currentUser.getId())) {
            try {
                WebUtils.issueRedirect(request, response, unauthorizedUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        taskService.updateTask(context, TTaskVm.STATUS_CLOSED, taskId);
        // 主动通知变更密码
        mcsService.notifyOpenDoorPwd(taskVm.getObjectContext(), taskVm.getMtVm().getId());
        return resMap;
    }


    @RequestMapping("finish")
    public
    @ResponseBody
    Map<String, String> finish(String content, HttpServletRequest request, HttpServletResponse response) {
        logger.info("完成工单 " + content);
        Map<String, String> resMap = new HashMap<>();
        try {
            resMap.put("url", url);
            resMap.put("type", "success");
            DeliveryTaskJson taskJson = Optional.ofNullable(content).map(DeliveryTaskJson::of).orElse(null);
            if (null == taskJson) {
                resMap.put("type", "error");
                return resMap;
            }
            taskJson.formatDate();
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TTaskVm taskVm = Optional.of(taskJson)
                    .map(DeliveryTaskJson::getTaskId)
                    .map(id -> Cayenne.objectForPK(context, TTaskVm.class, id)).orElse(null);
            if (null == taskVm) {
                resMap.put("type", "error");
                resMap.put("message", "工单不存在");
                logger.error(taskJson.getTaskId() + "工单不存在");
                return resMap;
            }
            DeliveryTaskJson task = this.taskService.getTaskInfo(taskVm);//发往前端的json和返回的json，总货道数应该一致
            if (null == task) {
                resMap.put("type", "error");
                resMap.put("message", "工单不存在");
                logger.error(taskJson.getTaskId() + "工单不存在");
                return resMap;
            }
            if (!taskJson.check(task) || !taskJson.checkDate()) {
                resMap.put("type", "error");
                resMap.put("message", "工单验证不通过");
                logger.error(taskJson.getTaskId() + "工单验证不通过");
                return resMap;
            }
            //有时候浏览器会提交两次，为避免提示错误，已完成工单直接回成功
            if (taskVm.getTaskVmStatus().equals(TTaskVm.STATUS_FINISHED)) {
                return resMap;
            }

            if (!taskVm.getTaskVmStatus().equals(TTaskVm.STATUS_ONGOING)) {
                resMap.put("type", "error");
                resMap.put("message", "工单状态错误");
                logger.error(taskJson.getTaskId() + "工单状态错误");
                return resMap;
            }
            if (!taskVm.getOwner().equals(currentUser.getId())) {
                try {
                    WebUtils.issueRedirect(request, response, unauthorizedUrl);
                } catch (IOException e) {
                    logger.error(e.getLocalizedMessage(), e);
                }
                resMap.put("type", "error");
                resMap.put("message", "非本人工单");
                logger.error(taskJson.getTaskId() + "非本人工单");
                return resMap;
            }
            LocalDateTime taskTime = taskVm.getTaskTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime now = LocalDateTime.now();
            if (taskTime.getDayOfMonth() == now.getDayOfMonth()) {
                if (taskTime.getHour() < 22 && now.getHour() >= 22) {
                    resMap.put("type", "error");
                    resMap.put("message", "工单已过期");
                    logger.error(taskJson.getTaskId() + "工单已过期");
                    return resMap;
                }
            } else if (taskTime.plusDays(1).getDayOfMonth() == now.getDayOfMonth()) {
                if (taskTime.getHour() < 22 || now.getHour() >= 22) {
                    resMap.put("type", "error");
                    resMap.put("message", "工单已过期");
                    logger.error(taskJson.getTaskId() + "工单已过期");
                    return resMap;
                }
            } else {
                resMap.put("type", "error");
                resMap.put("message", "工单已过期");
                logger.error(taskJson.getTaskId() + "工单已过期");
                return resMap;
            }
            taskJson = taskService.calculateFillCount(taskJson);
            String result = mcsService.deliveryTaskFinish(taskJson);
            if (!StringUtil.isEmpty(result)) {
                logger.error("mcs完成补货工单失败,错误信息" + result + "taskId" + taskJson.getTaskId());
                resMap.put("type", "error");
            } else {
                if (taskVm.getMtVm().isReserve()) {
                    List<Integer> skuList = taskService.updateReverseStatistics(taskVm, taskJson);
                    String date;
                    int reserveEndTime = Integer.parseInt((String) PropertiesLoader.map.get("reserveEndTime"));
                    if (LocalDateTime.now().getHour() >= reserveEndTime) {
                        date = LocalDate.now().plusDays(1).toString();
                    } else {
                        date = LocalDate.now().toString();
                    }
                    skuList.forEach(skuId -> {
                        Map<String, String> body = new HashMap<>();
                        body.put("vmId", taskVm.getMtVm().getId() + "");
                        body.put("date", date);
                        body.put("skuId", skuId + "");
                        Map<String, Object> map = new HashMap<>();
                        map.put("head", Collections.EMPTY_MAP);
                        map.put("body", body);
                        String message = JSON.toJSONString(map);
                        try {
                            PublisherPool.getInstance().publish(message, (String) PropertiesLoader.map.get("deliveredTopic"));
                        } catch (Exception e) {
                            resMap.put("type", "error");
                            logger.error(e.getLocalizedMessage(), e);
                        }
                    });
                }
                taskVm.getMtVm().updateStockStatus();
                mtVmShelfService.deleteShelfCache(taskVm.getMtVm().getId());//补货完成清除所有人的货道和列表页缓存
                mtVmChannelService.deleteChannelCache(taskVm.getMtVm().getId());
                extractCodeService.createExtractCode(taskVm.getMtVm());
            }
            // 主动通知变更密码
//            mcsService.notifyOpenDoorPwd(taskVm.getObjectContext(), taskVm.getMtVm().getId());
        } catch (Exception e) {
            resMap.put("type", "error");
            logger.error(e.getLocalizedMessage(), e);
        }
        return resMap;
    }

}
