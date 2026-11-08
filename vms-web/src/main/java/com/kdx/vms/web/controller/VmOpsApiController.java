package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmChannel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TVmStatus;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.service.IVmStatusHisService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 售货机库存 / 状态 / 历史状态 REST API
 */
@Controller
public class VmOpsApiController {

    private static final Logger logger = LoggerFactory.getLogger(VmOpsApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IMtVmService mtVmService;

    @Resource
    private IVmStatusHisService vmStatusHisService;

    private TSysUser currentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) return null;
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) return null;
        return ((SSOToken) principal).getSysUser();
    }

    private static void requireAuth() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new UnauthorizedException("未登录");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 401);
        err.put("message", ex.getMessage());
        return err;
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception ex) {
        logger.error("Internal error", ex);
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 500);
        err.put("message", "服务器内部错误");
        return err;
    }

    // ==================== 库存 ====================

    /**
     * 售货机库存列表（设备维度）
     */
    @RequestMapping(value = "/vms/api/vmStock/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String stockList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "selectType", required = false) String selectType,
            @RequestParam(value = "selectValue", required = false) String selectValue) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null || user.getCompany() == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String innerCode = null;
        String nodeName = null;
        if ("1".equals(selectType) && !StringUtil.isEmpty(selectValue)) innerCode = selectValue;
        else if ("2".equals(selectType) && !StringUtil.isEmpty(selectValue)) nodeName = selectValue;

        List<TMtVm> list = mtVmService.getMtVmForStocks(user.getCompany(), innerCode, nodeName, MAX_QUERY);
        int total = list == null ? 0 : list.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (list != null) {
            for (int i = from; i < to; i++) {
                rows.add(vmToRow(list.get(i)));
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 设备货道库存明细（按临期排序，与旧页面一致）
     */
    @RequestMapping(value = "/vms/api/vmStock/{vmId}/channels", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String stockChannels(@PathVariable("vmId") Integer vmId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        TMtVm vm = mtVmService.getMtVm(context, vmId);
        if (vm == null) return notFound("设备不存在");

        List<TMtVmChannel> channels = new ArrayList<>();
        if (vm.getChannelInfo() != null) {
            for (TMtVmChannel ch : vm.getChannelInfo()) {
                if (ch.getSku() != null) channels.add(ch);
            }
        }
        channels.sort((o1, o2) -> {
            String s1 = o1.getManufactureEndDate() == null ? "" : String.valueOf(o1.getManufactureEndDate());
            String s2 = o2.getManufactureEndDate() == null ? "" : String.valueOf(o2.getManufactureEndDate());
            if (s1.equals(s2)) return o1.compareTo(o2);
            if (s1.isEmpty() || s2.isEmpty()) return s2.compareTo(s1);
            return s1.compareTo(s2);
        });

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtVmChannel ch : channels) {
            TMtSku sku = ch.getSku();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("name", sku.getSkuName() + (sku.getSkuUnit() == null ? "" : sku.getSkuUnit()));
            row.put("channel", ch.getChannelCode());
            row.put("currCount", ch.getCurrNum());
            row.put("maxCount", ch.getTempMaxCapacity());
            row.put("hasStock", ch.getHasStock());
            row.put("dayCount", ch.getDay() == null ? 0 : ch.getDay());
            row.put("manufactureEndDate", ch.getManufactureEndDate() != null
                    ? ch.getManufactureEndDate().toInstant().toString() : null);
            rows.add(row);
        }
        return JSON.toJSONString(rows);
    }

    // ==================== 实时状态 ====================

    /**
     * 售货机状态列表
     */
    @RequestMapping(value = "/vms/api/vmStatus/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String statusList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "selectType", required = false) String selectType,
            @RequestParam(value = "selectValue", required = false) String selectValue,
            @RequestParam(value = "netStatus", required = false) Integer netStatus,
            @RequestParam(value = "vmcStatus", required = false) Integer vmcStatus,
            @RequestParam(value = "taskStatus", required = false) Integer taskStatus,
            @RequestParam(value = "tempStatus", required = false) Integer tempStatus) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String innerCode = null;
        String nodeName = null;
        if ("1".equals(selectType) && !StringUtil.isEmpty(selectValue)) innerCode = selectValue;
        else if ("2".equals(selectType) && !StringUtil.isEmpty(selectValue)) nodeName = selectValue;

        List list = mtVmService.getMtVmForStatus(user, innerCode, nodeName,
                netStatus, vmcStatus, taskStatus, tempStatus, MAX_QUERY);
        int total = list == null ? 0 : list.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (list != null) {
            for (int i = from; i < to; i++) {
                Object obj = list.get(i);
                if (obj instanceof TMtVm) {
                    Map<String, Object> row = vmToRow((TMtVm) obj);
                    TVmStatus st = ((TMtVm) obj).getVmStatusLog();
                    if (st != null) {
                        row.put("netStatus", st.getOnlineStatus());
                        row.put("vmcStatus", st.getVmStatus());
                        row.put("taskStatus", st.getTaskStatus());
                        row.put("tempStatus", st.getTempStatus());
                        row.put("tempValue", st.getTempValue());
                        row.put("doorStatus", st.getVmDoorStatus());
                        row.put("vmStatusDetail", st.getVmStatusDatail());
                        row.put("vmTime", st.getVmTime() != null ? st.getVmTime().toInstant().toString() : null);
                    }
                    rows.add(row);
                }
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 派单 / 取消派单
     */
    @RequestMapping(value = "/vms/api/vmStatus/dispatch", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String dispatch(
            @RequestParam(value = "ids", required = false) String idsCsv,
            @RequestParam(value = "cancel", required = false, defaultValue = "false") Boolean cancel) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择设备");

        ObjectContext context = user.getObjectContext();
        try {
            List<TVmStatus> statusList = new ArrayList<>();
            for (String part : idsCsv.split(",")) {
                TMtVm vm = Cayenne.objectForPK(context, TMtVm.class, Integer.parseInt(part.trim()));
                if (vm == null) continue;
                TVmStatus status = vm.getVmStatusLog();
                if (status == null) return errorJson("所选机器未投放，无法派单");
                statusList.add(status);
            }
            int target = cancel ? TVmStatus.TASK_STATUS_NORMAL : TVmStatus.TASK_STATUS_DISPATCH;
            for (TVmStatus status : statusList) {
                status.setTaskStatus(target);
            }
            context.commitChanges();
            Map<String, Object> ok = new LinkedHashMap<>();
            ok.put("type", "success");
            ok.put("message", cancel ? "已取消派单" : "派单成功");
            return JSON.toJSONString(ok);
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error("Dispatch failed", e);
            return errorJson("操作失败：" + e.getMessage());
        }
    }

    // ==================== 历史状态 ====================

    /**
     * 售货机历史状态（默认最近 1 小时）
     */
    @RequestMapping(value = "/vms/api/vmStatusHis/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String statusHisList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "selectType", required = false) String selectType,
            @RequestParam(value = "selectValue", required = false) String selectValue,
            @RequestParam(value = "vmStatus", required = false) Integer vmStatus,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        String innerCode = null;
        String nodeName = null;
        if ("1".equals(selectType) && !StringUtil.isEmpty(selectValue)) innerCode = selectValue;
        else if ("2".equals(selectType) && !StringUtil.isEmpty(selectValue)) nodeName = selectValue;

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
        if (StringUtil.isEmpty(startTime)) startTime = df.format(calendar.getTime()) + ":00";
        else startTime = startTime + ":00";
        if (StringUtil.isEmpty(endTime)) endTime = df.format(new Date()) + ":59";
        else endTime = endTime + ":59";

        List list = vmStatusHisService.findListPage(user.getObjectContext(), user, vmStatus,
                innerCode, nodeName, startTime, endTime, MAX_QUERY);
        int total = list == null ? 0 : list.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (list != null) {
            for (int i = from; i < to; i++) {
                Object obj = list.get(i);
                if (obj instanceof com.kdx.vms.model.bean.TVmStatusHis) {
                    com.kdx.vms.model.bean.TVmStatusHis h = (com.kdx.vms.model.bean.TVmStatusHis) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("vmId", h.getVmId());
                    TMtVm vm = h.getVmId() == null ? null
                            : Cayenne.objectForPK(user.getObjectContext(), TMtVm.class, h.getVmId());
                    if (vm != null) {
                        row.put("innerCode", vm.getInnerCode());
                        if (vm.getNode() != null) row.put("nodeName", vm.getNode().getNodeName());
                    }
                    row.put("vmStatus", h.getVmStatus());
                    row.put("doorStatus", h.getDoorStatus());
                    row.put("tempValue", h.getTempValue());
                    row.put("vmStatusDetail", h.getVmStatusDatail());
                    row.put("vmTime", h.getVmTime() != null ? h.getVmTime().toInstant().toString() : null);
                    rows.add(row);
                }
            }
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    private static Map<String, Object> vmToRow(TMtVm vm) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("vmId", Cayenne.intPKForObject(vm));
        row.put("innerCode", vm.getInnerCode());
        row.put("vmDesc", vm.getVmDesc());
        if (vm.getNode() != null) {
            row.put("nodeName", vm.getNode().getNodeName());
        }
        row.put("stockStatus", vm.getStockStatus());
        return row;
    }

    private static String notFound(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 404);
        err.put("message", message);
        return JSON.toJSONString(err);
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
