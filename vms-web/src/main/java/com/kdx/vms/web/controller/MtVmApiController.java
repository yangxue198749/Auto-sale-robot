package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtVmService;
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
import java.util.*;

/**
 * 售货机设备管理 REST API
 */
@Controller
@RequestMapping("/vms/api/mtVm")
public class MtVmApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmApiController.class);

    @Resource
    private IMtVmService mtVmService;

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

    /**
     * 设备列表（分页 + 多条件筛选，数据范围由 service 处理）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "filedType", required = false) Integer filedType,
            @RequestParam(value = "filedValue", required = false) String filedValue,
            @RequestParam(value = "modelId", required = false) Integer modelId,
            @RequestParam(value = "vmStatus", required = false) Integer vmStatus) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        List<TMtVm> allTasks = mtVmService.findMtVmList(context, filedType, filedValue, modelId, vmStatus,
                null, null, null, null, user, 10000, null);

        int total = allTasks != null ? allTasks.size() : 0;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<TMtVm> paged = new ArrayList<>();
        if (allTasks != null && fromIndex < total) {
            paged = allTasks.subList(fromIndex, toIndex);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtVm vm : paged) {
            rows.add(convertVmToMap(vm));
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 设备详情
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVm vm = mtVmService.getMtVm(context, id);
        if (vm == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "设备不存在");
            return JSON.toJSONString(err);
        }
        return JSON.toJSONString(convertVmToMap(vm));
    }

    /**
     * 新增设备
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam("nodeId") Integer nodeId,
            @RequestParam("vmModelId") Integer vmModelId,
            @RequestParam(value = "copyMtVmId", required = false) Integer copyMtVmId,
            @RequestParam(value = "vmDesc", required = false) String vmDesc,
            @RequestParam(value = "printer", required = false) Integer printer,
            @RequestParam(value = "reserve", required = false) Integer reserve) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            String errMsg = mtVmService.addMtVm(context, nodeId, vmModelId, copyMtVmId, vmDesc, user,
                    printer, reserve, null);
            if (StringUtil.isEmpty(errMsg)) {
                response.put("success", true);
                response.put("message", "新建成功");
            } else {
                response.put("success", false);
                response.put("message", errMsg);
            }
        } catch (Exception e) {
            logger.error("Create mtVm failed", e);
            response.put("success", false);
            response.put("message", "创建失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 编辑设备
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "vmDesc", required = false) String vmDesc,
            @RequestParam(value = "printer", required = false) Integer printer,
            @RequestParam(value = "reserve", required = false) Integer reserve) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            String errMsg = mtVmService.editMtVm(context, id, vmDesc, printer, reserve, null, user);
            if (StringUtil.isEmpty(errMsg)) {
                response.put("success", true);
                response.put("message", "更新成功");
            } else {
                response.put("success", false);
                response.put("message", errMsg);
            }
        } catch (Exception e) {
            logger.error("Update mtVm failed", e);
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 销售控制（切换在售/停售）
     */
    @RequestMapping(value = "/{id}/changeSale", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeSale(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            String errMsg = mtVmService.changeSale(context, id);
            if (StringUtil.isEmpty(errMsg)) {
                response.put("success", true);
                response.put("message", "切换成功");
            } else {
                response.put("success", false);
                response.put("message", errMsg);
            }
        } catch (Exception e) {
            logger.error("Change sale failed", e);
            response.put("success", false);
            response.put("message", "切换失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    private Map<String, Object> convertVmToMap(TMtVm vm) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", Cayenne.intPKForObject(vm));
        row.put("innerCode", vm.getInnerCode());
        row.put("hardwareCode", vm.getHardwareCode());
        row.put("vmDesc", vm.getVmDesc());
        row.put("vmStatus", vm.getVmStatus());
        row.put("vmType", vm.getVmType());
        row.put("stockStatus", vm.getStockStatus());
        row.put("deleteMark", vm.getDeleteMark());
        row.put("isReserve", vm.getIsReserve());
        row.put("vmModelId", vm.getVmModelId());
        if (vm.getNode() != null) {
            row.put("nodeId", Cayenne.intPKForObject(vm.getNode()));
            row.put("nodeName", vm.getNode().getNodeName());
            row.put("nodeAddress", vm.getNode().getNodeAddress());
        }
        if (vm.getCompany() != null) {
            row.put("companyId", Cayenne.intPKForObject(vm.getCompany()));
            row.put("companyName", vm.getCompany().getCompanyName());
        }
        row.put("ctime", vm.getCtime() != null ? vm.getCtime().toInstant().toString() : null);
        return row;
    }
}
