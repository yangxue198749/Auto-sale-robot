package com.kdx.mp.web.controller;

import com.kdx.mp.service.IMpTaskService;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVmModel;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/28.
 */
@Controller
@RequestMapping("mp/mpTask/withdraw")
public class MpWithdrawTaskController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 没有权限地址
     **/
    private String unauthorizedUrl = "/mp/auth/unauthorized";
    @Autowired
    private IMpTaskService taskService;
    @Autowired
    private IMcsService mcsService;
    private String url = "/mp/mpTask/withdraw/list";

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            List taskList = taskService.findFinishTaskByOwnerAndDay(context, currentUser.getId(), DateUtils.getTime1(), TTaskVm.TYPE_WITHDRAW);
            List unDealTaskList = taskService.findByOwner(context, currentUser.getId(), TTaskVm.TYPE_WITHDRAW);
            List<TTaskVm> ongoingTaskList = new ArrayList<>();
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
                        ongoingTaskList.add(taskVm);
                        break;
                }
            }
            request.setAttribute("ongoingTaskList", ongoingTaskList);
            request.setAttribute("closeTaskList", closeTaskList);
            request.setAttribute("initialTaskList", initialTaskList);
            return "mp/task/withdraw/withdraw_task_list";
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @RequestMapping("exit")
    public
    @ResponseBody
    Map<String, String> exit(HttpServletRequest request, Integer taskId) {
        Map<String, String> resMap = new HashMap<>();
        try {
            resMap.put("url", url);
            resMap.put("type", "success");
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            taskService.updateTask(context, TTaskVm.STATUS_INITIALIZATION, taskId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return resMap;
    }

    @RequestMapping("join")
    public
    @ResponseBody
    Map<String, String> join(HttpServletRequest request, Integer[] taskIds) {
        Map<String, String> resMap = new HashMap<>();
        try {
            resMap.put("url", url);
            resMap.put("type", "success");
//        TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            taskService.updateTask(context, TTaskVm.STATUS_ONGOING, taskIds);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return resMap;
    }

    @RequestMapping("detail")
    public String detail(HttpServletRequest request, HttpServletResponse response, Integer taskId) {
//        TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
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
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/task/withdraw/withdraw_task_detail";
    }

    @RequestMapping("finish")
    public
    @ResponseBody
    Map<String, String> finish(HttpServletRequest request, HttpServletResponse response, Integer taskId) {
        Map<String, String> resMap = new HashMap<>();
        try {
            resMap.put("url", url);
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
            taskService.withdrawVm(context, taskVm);
            String errorMsg = mcsService.sendWithdrawVmTaskInfo(context, taskVm.getMtVm().getInnerCode(), null);
            if (StringUtil.isEmpty(errorMsg)) {
                taskService.updateTask(context, TTaskVm.STATUS_FINISHED, taskId);
                if (taskVm.getMtVm().getVmType().equals(TMtVmModel.COMBINATORIAL_EXTENTION)) {
                    //更新列表页，发送新的商品，列表页，价格信息
                    mcsService.dealWithdraw(context, taskVm.getMtVm());
                }
                return resMap;
            }
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        resMap.put("type", "error");
        return resMap;
    }
}
