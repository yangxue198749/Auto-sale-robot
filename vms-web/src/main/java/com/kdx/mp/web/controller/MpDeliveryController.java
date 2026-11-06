package com.kdx.mp.web.controller;

import com.kdx.mp.service.IMpTaskService;
import com.kdx.mp.service.IMpVmStatusService;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TTaskVm;
import com.kdx.vms.service.ITaskVmService;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/4/28.
 */
@Controller
@RequestMapping("mp/delivery")
public class MpDeliveryController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 没有权限地址*
     */
    private String unauthorizedUrl = "/mp/auth/unauthorized";
    @Autowired
    private IMpVmStatusService mpVmStatusService;
    @Autowired
    private IMpTaskService mpTaskService;
    @Autowired
    private ITaskVmService taskVmService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        return "mp/delivery/vm_list";
    }

    @RequestMapping("page")
    @ResponseBody
    public Map<String, Object> page(HttpServletRequest request,String innerCode) {
        Map<String, Object> map = new HashMap<>();
        try {
//            TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
            TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
            ObjectContext context = currentUser.getObjectContext();
            List list = mpVmStatusService.findVmList(currentUser,innerCode);
            String html = createHtml(list);
            map.put("html", html);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return map;
    }

    @RequestMapping("check")
    @ResponseBody
    public Map<String, Object> check(HttpServletRequest request, Integer vmId) {
//        TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        TMtCompany company = currentUser.getCompany();
        ObjectContext context = currentUser.getObjectContext();
        Map<String, Object> map = new HashMap<>();
        try {
            if (IntegerUtils.isPositiveInteger(vmId)) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
                if (null != mtVm) {
                    if (mtVm.getCompany().getId().equals(company.getId())) {
                        TTaskVm unDealTask = mpTaskService.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_DELIVERY, StringUtil.dateToStr(new Date(), StringUtil.YMDPattern));
                        if (null != unDealTask) {
                            if (unDealTask.getTaskVmStatus() == TTaskVm.STATUS_ONGOING) {
                                map.put("type", "confirm");
                                map.put("message", "\n" +
                                        "检测到该贩售机有正在进行的补货任务，\n" +
                                        "是否忽略之前任务，并立即为其补货？");
                                map.put("taskId", unDealTask.getTaskVmId());
                                return map;
                            } else {
                                return create(request, unDealTask.getTaskVmId(), vmId);
                            }
                        }
                        return create(request, null, vmId);
                    } else {
                        map.put("type", "error");
                        map.put("message", "抱歉，该设备不属于您所属的公司，您没有权限访问");
                        return map;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @RequestMapping("create")
    @ResponseBody
    public Map<String, Object> create(HttpServletRequest request, Integer taskId, Integer vmId) {
//        TSysUser currentUser = (TSysUser) request.getSession().getAttribute("mpUser");
        TSysUser currentUser = UserMap.getUser(SecurityUtils.getSubject().getSession().getId());
        ObjectContext context = currentUser.getObjectContext();
        Map<String, Object> map = new HashMap<>();
        try {
            if (IntegerUtils.isPositiveInteger(taskId)) {
                TTaskVm unDealTask = Cayenne.objectForPK(context, TTaskVm.class, taskId);
                if (null != unDealTask) {
                    unDealTask.setTaskVmStatus(TTaskVm.STATUS_CLOSED);
                    context.commitChanges();
                }
            }
            taskVmService.createDeliveryTaskByIds(context, currentUser, new String[]{vmId + ""}, currentUser.getId(), 0, null);
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            TTaskVm unDealTask = mpTaskService.getUnDealTaskToday(context, mtVm, TTaskVm.TYPE_DELIVERY, StringUtil.dateToStr(new Date(), StringUtil.YMDPattern));
            if (null == unDealTask) {
                map.put("type", "error");
                map.put("message", "货满无需补货");
            } else {
                map.put("type", "success");
                map.put("taskId", unDealTask.getTaskVmId());
            }
            return map;

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private String createHtml(List list) {
        StringBuilder builder = new StringBuilder();
        for (Object mtVm : list) {
            builder.append("<div class=\"weui_cells weui_cells_access mt15\">\n").append("<div class=\"weui_cell\">\n").append("<a class=\"weui_cell_bd weui_cell_primary\" vmId=\"").append(((TMtVm) mtVm).getId()).append("\" href=\"javascript:;\">\n").append("<p class=\"f14 cf3\">").append(((TMtVm) mtVm).getInnerCode()).append("</p>\n").append("<p class=\"f12 cf9\">").append(((TMtVm) mtVm).getNodeName()).append("</p>\n").append("</a>\n").append("<div class=\"weui_cell_ft2\"></div>\n").append("</div>\n").append("</div>");
        }
        return builder.toString();
    }

}
