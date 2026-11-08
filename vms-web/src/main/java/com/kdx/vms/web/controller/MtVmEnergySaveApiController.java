package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmEnergySave;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtVmEnergySaveService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
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
 * 设备节能配置 REST API
 */
@Controller
@RequestMapping("/vms/api/vmEnergy")
public class MtVmEnergySaveApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmEnergySaveApiController.class);

    @Resource
    private IMtVmEnergySaveService mtVmEnergySaveService;

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
     * 设备节能配置列表（直接查库，JSON）
     */
    @RequestMapping(value = "/{vmId}/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(@PathVariable("vmId") Integer vmId) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVm vm = Cayenne.objectForPK(context, TMtVm.class, vmId);
        if (vm == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "设备不存在");
            return JSON.toJSONString(err);
        }

        SelectQuery query = new SelectQuery(TMtVmEnergySave.class);
        query.setQualifier(ExpressionFactory.matchExp("mtVm", vm));
        query.addOrdering("startTime", SortOrder.ASCENDING);
        List<TMtVmEnergySave> list = context.performQuery(query);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtVmEnergySave e : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", Cayenne.intPKForObject(e));
            row.put("energySaveType", e.getEnergySaveType());
            row.put("targetValue", e.getTargetValue());
            row.put("startTime", e.getStartTime());
            row.put("endTime", e.getEndTime());
            row.put("remark", e.getRemark());
            row.put("tempBoxNo", e.getTempBoxNo());
            rows.add(row);
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 保存节能配置（新增或更新）
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String save(
            @RequestParam(value = "energySaveId", required = false) Integer energySaveId,
            @RequestParam("vmId") Integer vmId,
            @RequestParam(value = "targetValue", required = false) Integer targetValue,
            @RequestParam(value = "energySaveType", required = false) Integer energySaveType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "tempBoxNo", required = false) Integer tempBoxNo) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            Map<String, Object> result;
            if (energySaveId == null) {
                result = mtVmEnergySaveService.saveEnergySave(user, context, targetValue, vmId,
                        energySaveType, startTime, endTime, remark, tempBoxNo);
            } else {
                result = mtVmEnergySaveService.saveEnergySave(user, context, energySaveId, targetValue,
                        vmId, energySaveType, startTime, endTime, remark, tempBoxNo);
            }
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Save energy config failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "保存失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    /**
     * 删除节能配置
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            Map<String, Object> result = mtVmEnergySaveService.delete(user, context, new Integer[]{id});
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Delete energy config failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "删除失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    /**
     * 下发节能配置到设备（MCS）
     */
    @RequestMapping(value = "/{vmId}/push", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String pushToMcs(@PathVariable("vmId") Integer vmId) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> response = new LinkedHashMap<>();
        try {
            String errMsg = mtVmEnergySaveService.pushToMCS(context, new Integer[]{vmId});
            String newErrMsg = mtVmEnergySaveService.pushToMCSNew(context, new Integer[]{vmId});
            if (StringUtil.isEmpty(errMsg) && StringUtil.isEmpty(newErrMsg)) {
                response.put("success", true);
                response.put("type", "success");
                response.put("message", "下发成功");
            } else {
                response.put("success", false);
                response.put("type", "error");
                response.put("message", !StringUtil.isEmpty(errMsg) ? errMsg : newErrMsg);
            }
        } catch (Exception e) {
            logger.error("Push energy config failed", e);
            response.put("success", false);
            response.put("message", "下发失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }
}
