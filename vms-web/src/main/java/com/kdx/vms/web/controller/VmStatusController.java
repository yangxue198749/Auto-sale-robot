package com.kdx.vms.web.controller;

import com.kdx.vms.commons.ResponseJson;
import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.SysUserDao;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.TMtVmAlarmSubscriptionService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by liyang on 16-12-15.
 */
@Controller
@RequestMapping(value = "vmStatus")
public class VmStatusController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource()
    private IMtVmService mtVmService;
    @Resource
    private TMtVmAlarmSubscriptionService tMtVmAlarmSubscriptionService;
    @Resource
    private SysUserDao sysUserDao;



    @RequestMapping("list")
    public String list() {
        return "vmStatus/status_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                           Integer pageSize,
                           Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        logger.info("[listPage]");
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        Integer totalSize = 1000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();

            String selectType = request.getParameter("selectType");
            logger.info("selectType:" + selectType);
            String selectValue = request.getParameter("selectValue");
            logger.info("selectText:" + selectValue);
            String netStatus = request.getParameter("netStatus");
            logger.info("netStatus:" + netStatus);
            String vmcStatus = request.getParameter("vmcStatus");
            logger.info("vmcStatus:" + vmcStatus);
            String taskStatus = request.getParameter("taskStatus");
            logger.info("taskStatus:" + taskStatus);
            //查询条件增加温度状态
            String tempStatus = request.getParameter("tempStatus");
            logger.info("tempStatus:" + tempStatus);
            String innerCode = null;
            String nodeName = null;
            if (!StringUtil.isEmpty(selectType)) {
                switch (selectType) {
                    case "1":
                        innerCode = selectValue;
                        break;
                    case "2":
                        nodeName = selectValue;
                        break;
                }
            }
            List list = mtVmService.getMtVmForStatus(sysUser,
                    innerCode,
                    nodeName,
                    StringUtil.isEmpty(netStatus) ? null : Integer.parseInt(netStatus),
                    StringUtil.isEmpty(vmcStatus) ? null : Integer.parseInt(vmcStatus),
                    StringUtil.isEmpty(taskStatus) ? null : Integer.parseInt(taskStatus),
                    StringUtil.isEmpty(tempStatus) ? null : Integer.parseInt(tempStatus),
                    pageSize);
            logger.info("list:" + list);
            if (null != list) {
                totalSize = list.size();
                Integer start = (pageNo - 1) * pageSize;
                Integer end = pageNo * pageSize;
                start = start > list.size() ? list.size() : start;
                end = end > list.size() ? list.size() : end;
                logger.info("start:" + start + ", end:" + end);
                request.setAttribute("vmList", list.subList(start, end));
            }
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "vmStatus/table/status_table";
    }

    @RequestMapping("dispatch")
    @ResponseBody
    public Map<String, Object> dispatch(Integer[] ids, HttpServletRequest request) {
        ObjectContext context = TokenUtil.getToken().getObjectContext();
        Map<String, Object> respMap = new HashMap<>();
        try {
            if (CheckUtils.check(ids)) {
                List<TVmStatus> list = Arrays.stream(ids).map(id -> Cayenne.objectForPK(context, TMtVm.class, id)).filter(vm -> null != vm).map(TMtVm::getVmStatusLog).collect(Collectors.toList());
                if (list.contains(null)) {
                    respMap.put("type", "error");
                    respMap.put("message", "所选机器未投放，无法派单");
                    return respMap;
                }
                list.forEach(tVmStatus -> tVmStatus.setTaskStatus(TVmStatus.TASK_STATUS_DISPATCH));
                context.commitChanges();
                respMap.put("type", "success");
                respMap.put("message", "派单成功");
                return respMap;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
        }
        respMap.put("type", "error");
        respMap.put("message", "派单失败");
        return respMap;
    }

    @RequestMapping("unDispatch")
    @ResponseBody
    public Map<String, Object> unDispatch(Integer[] ids, HttpServletRequest request) {
        ObjectContext context = TokenUtil.getToken().getObjectContext();
        Map<String, Object> respMap = new HashMap<>();
        try {
            if (CheckUtils.check(ids)) {
                List<TVmStatus> list = Arrays.stream(ids).map(id -> Cayenne.objectForPK(context, TMtVm.class, id)).filter(vm -> null != vm).map(TMtVm::getVmStatusLog).filter(status -> null != status).collect(Collectors.toList());
                list.forEach(tVmStatus -> tVmStatus.setTaskStatus(TVmStatus.TASK_STATUS_NORMAL));
                context.commitChanges();
                respMap.put("type", "success");
                respMap.put("message", "取消派单成功");
                return respMap;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
        }
        respMap.put("type", "error");
        respMap.put("message", "取消派单失败");
        return respMap;
    }

    @RequestMapping("getCompanyUserList")
    public String getCompanyUserList() {
        return "vmStatus/dialog/alarm_waraning_dialog";
    }


    /**
     * 告警订阅选择通知用户列表
     *
     * @return
     */
    @RequestMapping(value = "companyUserListData", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String companyUserListData() {
        ObjectContext context = TokenUtil.getToken().getObjectContext();
        TSysUser tSysUser = TokenUtil.getToken().getSysUser();
        Map<String, Object> map = new HashMap<>();
        List<Map<String, Object>> alarmList = new ArrayList<>();
        List<Map<String, Object>> unAlarmList = new ArrayList<>();
        try {
            //公司用户
            List<TSysUser> tSysUsers = tMtVmAlarmSubscriptionService.findUserByCompanyId(context, tSysUser.getCompany().getId());
            //订阅用户
            List<TMtVmAlarmSubscription> subscriptionList = tMtVmAlarmSubscriptionService.findByCompanyId(context, tSysUser.getCompany().getId());
            if(null != subscriptionList && subscriptionList.size() >0){
                //未订阅用户
                List<TSysUser> tSysUserList = tSysUsers.stream().filter(t -> !subscriptionList.contains(t.getId())).collect(Collectors.toList());
                tSysUserList.forEach(user -> {
                    Map<String, Object> config = new HashMap<>();
                    config.put("id", user.getId());
                    config.put("name", user.getUserName());
                    unAlarmList.add(config);
                });
                subscriptionList.forEach(tMtVmAlarmSubscription -> {
                    Map<String, Object> subscribeUser = new HashMap<>();
                    subscribeUser.put("id", tMtVmAlarmSubscription.getUserId());
                    subscribeUser.put("name", sysUserDao.findUserByPk(context,tMtVmAlarmSubscription.getUserId()).getUserName());
                    alarmList.add(subscribeUser);
                });
            }else{
                tSysUsers.forEach(user -> {
                    Map<String, Object> config = new HashMap<>();
                    config.put("id", user.getId());
                    config.put("name", user.getUserName());
                    unAlarmList.add(config);
                });
            }

            map.put("userList",alarmList);
            map.put("subscribeUser",unAlarmList);
            return new ResponseJson(true, "", map).toJson();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
        }
        return new ResponseJson(false, "系统内部异常", map).toJson();
    }

    /**
     * 保存告警订阅用户信息
     *
     *
     * @return
     */
    @RequestMapping(value = "saveAlertSysUser", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String saveAlertSysUser(@RequestParam(required = false, value = "userList[]") List<String> userList) {
        try {
            ObjectContext context = TokenUtil.getToken().getObjectContext();
            TSysUser tSysUser = TokenUtil.getToken().getSysUser();
            TMtCompany company = tSysUser.getCompany();
            String msg = tMtVmAlarmSubscriptionService.addTMtVmAlarmSubscription(context, userList, company.getId());
            if(null != msg){
                return new ResponseJson(false, msg, null).toJson();
            }
            return new ResponseJson(true, "", null).toJson();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
        }
        return new ResponseJson(false, "内部错误", null).toJson();
    }


}
