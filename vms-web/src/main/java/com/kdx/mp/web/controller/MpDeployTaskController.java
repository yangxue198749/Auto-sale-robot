package com.kdx.mp.web.controller;

import com.kdx.mp.service.IMpActivateVmService;
import com.kdx.mp.service.IMpTaskService;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.mcs.message.data.Extention;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.service.IMcsService;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2016/12/23.
 */
@Controller
@RequestMapping("mp/mpTask/deploy")
public class MpDeployTaskController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 没有权限地址
     **/
    private String unauthorizedUrl = "/mp/auth/unauthorized";
    @Autowired
    private IMpTaskService taskService;
    @Autowired
    private IMpActivateVmService mpActivateVmService;
    @Autowired
    private IMcsService mcsService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            List taskList = taskService.findFinishTaskByOwnerAndDay(context, currentUser.getId(), DateUtils.getTime1(), TTaskVm.TYPE_DEPLOY);
            List unDealTaskList = taskService.findByOwner(context, currentUser.getId(), TTaskVm.TYPE_DEPLOY);
            List<TTaskVm> closeTaskList = new ArrayList<>();
            List<TTaskVm> initialTaskList = new ArrayList<>();
            for (Object o : taskList) {
                TTaskVm taskVm = (TTaskVm) o;
                switch (taskVm.getTaskVmStatus()) {
                    case TTaskVm.STATUS_FINISHED:
                        closeTaskList.add(taskVm);
                        break;
                    case TTaskVm.STATUS_CLOSED:
                        closeTaskList.add(taskVm);
                        break;
                }
            }
            for (Object o : unDealTaskList) {
                TTaskVm taskVm = (TTaskVm) o;
                switch (taskVm.getTaskVmStatus()) {
                    case TTaskVm.STATUS_INITIALIZATION:
                        initialTaskList.add(taskVm);
                        break;
                    case TTaskVm.STATUS_ONGOING:
                        initialTaskList.add(taskVm);
                        break;
                }
            }
            request.setAttribute("closeTaskList", closeTaskList);
            request.setAttribute("initialTaskList", initialTaskList);
            return "mp/task/deploy/deploy_task_list";
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @RequestMapping("detail")
    public String detail(HttpServletRequest request, HttpServletResponse response, Integer taskId) {
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            if (!taskVm.getOwner().equals(currentUser.getId())) {
                WebUtils.issueRedirect(request, response, unauthorizedUrl);
                return null;
            }
            TMtVm mtVm = taskVm.getMtVm();
            String uniqueCode = mtVm.getUniqueCode();
            if (!StringUtil.isEmpty(uniqueCode)) {
                request.setAttribute("canFinish", "true");
            } else {
                request.setAttribute("canFinish", "false");
            }
            request.setAttribute("vmType", mtVm.getVmType());
            request.setAttribute("task", taskVm);
            return "mp/task/deploy/deploy_task_detail";
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @RequestMapping("finish")
    public
    @ResponseBody
    Map<String, String> finish(HttpServletRequest request, HttpServletResponse response, Integer taskId, String longitude, String latitude, String latitude_mars, String longitude_mars) {
        Map<String, String> resMap = new HashMap<>();
        try {
            resMap.put("type", "success");
            if (null == taskId) {
                resMap.put("type", "error");
                return resMap;
            }
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            if (!taskVm.getOwner().equals(currentUser.getId())) {
                WebUtils.issueRedirect(request, response, unauthorizedUrl);
                resMap.put("type", "error");
                return resMap;
            }
            taskService.deployVm(context, taskVm);
            taskService.updateTask(context, TTaskVm.STATUS_FINISHED, taskId);
            mpActivateVmService.updateNodePosition(context, taskVm.getMtVm().getNode(), latitude, longitude, 0);
            mpActivateVmService.updateNodePosition(context, taskVm.getMtVm().getNode(), latitude_mars, longitude_mars, 1);
            return resMap;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        resMap.put("type", "error");
        return resMap;
    }

    @RequestMapping("finishExtention")
    public
    @ResponseBody
    Map<String, String> finishExtention(HttpServletRequest request, HttpServletResponse response, Integer taskId, String hardwareCode) {
        Map<String, String> resMap = new HashMap<>();
        try {
            resMap.put("type", "success");
            if (null == taskId) {
                resMap.put("type", "error");
                return resMap;
            }
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            if (!taskVm.getOwner().equals(currentUser.getId())) {
                WebUtils.issueRedirect(request, response, unauthorizedUrl);
                resMap.put("type", "error");
                return resMap;
            }
            TMtVm mtVm = taskVm.getMtVm();
            mtVm.setHardwareCode(hardwareCode);
            mtVm.setVmStatus(TMtVm.STATUS_SALE);
            taskVm.setFinishTime(new Date());
            taskVm.setTaskVmStatus(TTaskVm.STATUS_FINISHED);
            List<TMtVm> list = mtVm.getParentVm().getChildrenVm();
            list = list.stream().filter(vm -> vm.getVmStatus() == TMtVm.STATUS_SALE).collect(Collectors.toList());
            List<Extention> extentionList = new ArrayList<>();
            list.forEach(vm -> {
                Extention extention = new Extention();
                extention.setExtentionCode(vm.getVmExtentionNum());
                extentionList.add(extention);
            });
            String result = mcsService.sendDeployVmTaskInfo(context, mtVm.getInnerCode(), null, extentionList);
            if (result.equals("")) {
                context.commitChanges();
                resMap.put("type", "success");
                resMap.put("message", "投放成功");
                return resMap;
            } else {
                context.rollbackChanges();
                resMap.put("type", "error");
                resMap.put("message", "请先激活主机");
                return resMap;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        resMap.put("type", "error");
        return resMap;
    }


}
