package com.kdx.mp.web.controller;

import com.kdx.mp.service.IMpActivateVmService;
import com.kdx.mp.service.IMpTaskService;
import com.kdx.mp.util.WeChatUtils;
import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.web.oauth.UserMap;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
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
import java.util.stream.Stream;

/**
 * Created by yangshuxi on 2016/12/22.
 */
@RequestMapping("mp/activate")
@Controller
public class MpActivateVmController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMpActivateVmService mpActivateVmService;
    @Autowired
    private IMcsService mcsService;
    @Autowired
    private IMpTaskService taskService;

    @RequestMapping("reg")
    public String register(HttpServletResponse response, HttpServletRequest request, String uniqueCode) {
//        TSysUser currentUser= (TSysUser) request.getSession().getAttribute("mpUser");
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            if (null == currentUser || StringUtil.isEmpty(uniqueCode)) {
                return null;
            }
            uniqueCode = uniqueCode.replaceAll("\n", "");
            ObjectContext context = currentUser.getObjectContext();
            //判断uniqueCode是否存在，如果存在则进入直接激活流程
            TMtVm mtVm = mpActivateVmService.checkActivated(context, uniqueCode);
            if (mtVm != null) {
                TTaskVm taskVm = taskService.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_DEPLOY, null);
                if (null != taskVm) {
                    taskVm.setFinishTime(new Date());
                    taskVm.setOwner(currentUser.getId());
                    taskVm.setTaskVmStatus(TTaskVm.STATUS_FINISHED);
//                taskVm.setUtor(currentUser.getId());
                }
                mtVm.setVmStatus(TMtVm.STATUS_SALE);
                String result = mcsService.sendInnerCodeToMcs(mtVm, uniqueCode);
                request.setAttribute("vmId", mtVm.getId());
                if (result.equals("")) {
                    context.commitChanges();
                } else {
                    context.rollbackChanges();
                }
                return "mp/activate/active_result";
            }
            List taskList = taskService.findByOwner(context, currentUser.getId(), TTaskVm.TYPE_DEPLOY);
            List<TTaskVm> list = new ArrayList<>();
            if (CheckUtils.check(taskList)) {
                Stream<?> stream = taskList.stream();
                list = stream.map(TTaskVm.class::cast).filter(taskVm -> taskVm.getMtVm().getVmType() != TMtVmModel.COMBINATORIAL_EXTENTION).collect(Collectors.toList());
            }
            request.setAttribute("tasks", list);
            request.setAttribute("uniqueCode", uniqueCode);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mp/activate/active";
    }

    @RequestMapping("active")
    public
    @ResponseBody
    Map<String, Object> active(HttpServletResponse response, HttpServletRequest request, String uniqueCode, Integer taskId, String hardwareCode) {
        Map<String, Object> map = new HashMap<>();
        String type = "";
        String message = "";
//        TSysUser currentUser= (TSysUser) request.getSession().getAttribute("mpUser");
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        if (null == currentUser || StringUtil.isEmpty(uniqueCode) || StringUtil.isEmpty(hardwareCode) || !IntegerUtils.isPositiveInteger(taskId)) {
            type = "error";
            message = "数据提交不完整";
            map.put("type", type);
            map.put("message", message);
            return map;
        }
        ObjectContext context = currentUser.getObjectContext();
        try {
            TTaskVm taskVm = Cayenne.objectForPK(context, TTaskVm.class, taskId);
            TMtVm mtVm = taskVm.getMtVm();
            mtVm.setHardwareCode(hardwareCode);
            mtVm.setUniqueCode(uniqueCode);
            mtVm.setVmStatus(TMtVm.STATUS_SALE);
            taskVm.setFinishTime(new Date());
            taskVm.setTaskVmStatus(TTaskVm.STATUS_FINISHED);
//            taskVm.setUtor(currentUser.getId());
            String result = mcsService.sendInnerCodeToMcs(mtVm, uniqueCode);
            if (result.equals("")) {
                context.commitChanges();
            } else {
                context.rollbackChanges();
            }
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage());
            type = "error";
            message = "绑定失败";
            map.put("type", type);
            map.put("message", message);
            return map;
        }

        type = "success";
        message = "成功";
        map.put("type", type);
        map.put("message", message);
        return map;
    }

    @RequestMapping("result")
    public String result(HttpServletRequest request, Integer taskId) {
        try {

            if (null != taskId) {
                TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
                TTaskVm taskVm = Cayenne.objectForPK(currentUser.getObjectContext(), TTaskVm.class, taskId);
                if (null != taskVm) {
                    TMtVm mtVm = taskVm.getMtVm();
                    request.setAttribute("vmId", mtVm.getId());
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

        return "mp/activate/active_result";
    }

    @RequestMapping("config")
    public
    @ResponseBody
    Map<String, Object> config(String url) {
        logger.info("get jssdk:url=" + url);
        Map<String, Object> map = WeChatUtils.getJsSdkConfig(url);
        return map;
    }

    @RequestMapping("mars")
    public
    @ResponseBody
    Map<String, Object> mars(Integer vmId, String latitude, String longitude) {
        logger.info("update mars position : vmId = " + vmId + "latitude = " + latitude + "longitude" + longitude);
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            TMtVm mtVm = Cayenne.objectForPK(currentUser.getObjectContext(), TMtVm.class, vmId);
            TMtNode mtNode = mtVm.getNode();
            mpActivateVmService.updateNodePosition(currentUser.getObjectContext(), mtNode, latitude, longitude, 1);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @RequestMapping("earth")
    public
    @ResponseBody
    Map<String, Object> earth(Integer vmId, String latitude, String longitude) {
        logger.info("update earth position : vmId = " + vmId + "latitude = " + latitude + "longitude" + longitude);
        try {
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            TMtVm mtVm = Cayenne.objectForPK(currentUser.getObjectContext(), TMtVm.class, vmId);
            TMtNode mtNode = mtVm.getNode();
            mpActivateVmService.updateNodePosition(currentUser.getObjectContext(), mtNode, latitude, longitude, 0);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
