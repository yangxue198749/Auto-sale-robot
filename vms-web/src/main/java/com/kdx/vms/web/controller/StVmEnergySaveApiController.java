package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TStVmEnergySave;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.IStVmEnergySaveService;
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
 * 节能策略 REST API
 */
@Controller
@RequestMapping("/vms/api/stVmEnergySave")
public class StVmEnergySaveApiController {

    private static final Logger logger = LoggerFactory.getLogger(StVmEnergySaveApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IStVmEnergySaveService stVmEnergySaveService;

    @Resource
    private DataScopeService dataScopeService;

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

    @ResponseStatus(HttpStatus.FORBIDDEN)
    public static class ForbiddenException extends RuntimeException {
        public ForbiddenException(String msg) { super(msg); }
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

    @org.springframework.web.bind.annotation.ExceptionHandler(ForbiddenException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, Object> handleForbidden(ForbiddenException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 403);
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
     * 节能策略列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchStVmName", required = false) String searchStVmName,
            @RequestParam(value = "searchVmModelId", required = false) Integer searchVmModelId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = user.getObjectContext();
        List<TStVmStrategy> full = stVmEnergySaveService.findPage(context, user, searchStVmName, searchVmModelId, MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            TStVmStrategy s = full.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("strategyId", s.getStrategyId());
            row.put("strategyName", s.getStrategyName());
            row.put("strategyDesc", s.getStrategyDesc());
            row.put("vmModelId", s.getVmModel() != null ? Cayenne.intPKForObject(s.getVmModel()) : null);
            row.put("vmModelName", s.getVmModel() != null ? s.getVmModel().getModelName() : null);
            row.put("detailCount", s.getStEnergySave() != null ? s.getStEnergySave().size() : 0);
            row.put("ctorName", userName(context, s.getCtor()));
            row.put("ctime", s.getCtime() != null ? s.getCtime().toInstant().toString() : null);
            row.put("utorName", userName(context, s.getUtor()));
            row.put("utime", s.getUtime() != null ? s.getUtime().toInstant().toString() : null);
            rows.add(row);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", total);
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 策略详情：型号温控能力 + 节能明细
     */
    @RequestMapping(value = "/{strategyId}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("strategyId") Integer strategyId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        TStVmStrategy strategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        if (strategy == null || strategy.getStrategyType() == null
                || strategy.getStrategyType() != TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE) {
            return notFound("策略不存在");
        }
        if (!dataScopeService.canAccessCompany(user, strategy.getCompany())) {
            throw new ForbiddenException("无权访问该策略");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("strategyId", strategy.getStrategyId());
        result.put("strategyName", strategy.getStrategyName());
        result.put("strategyDesc", strategy.getStrategyDesc());

        TMtVmModel model = strategy.getVmModel();
        if (model != null) {
            result.put("vmModelId", Cayenne.intPKForObject(model));
            result.put("vmModelName", model.getModelName());
            result.put("temperatureHandlerMode", model.getTemperatureHandlerMode());
            result.put("screenHave", model.getScreenHave());
            result.put("tempBoxCount", model.getTempBoxCount());
            result.put("tempCtrlDesc", model.getTempCtrlDesc());
        }

        List<Map<String, Object>> details = new ArrayList<>();
        List<TStVmEnergySave> list = stVmEnergySaveService.findByStrategyId(context, strategyId);
        if (list != null) {
            for (TStVmEnergySave e : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("energySaveId", Cayenne.intPKForObject(e));
                row.put("energySaveType", e.getEnergySaveType());
                row.put("targetValue", e.getTargetValue());
                row.put("tempBoxNo", e.getTempBoxNo());
                row.put("startTime", e.getStartTime());
                row.put("endTime", e.getEndTime());
                row.put("remark", e.getRemark());
                details.add(row);
            }
        }
        result.put("details", details);
        return JSON.toJSONString(result);
    }

    /**
     * 新建节能策略（仅策略头，明细另行添加）
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "strategyName", required = false) String strategyName,
            @RequestParam(value = "strategyDesc", required = false) String strategyDesc,
            @RequestParam(value = "vmModelId", required = false) Integer vmModelId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(strategyName) || vmModelId == null) {
            return errorJson("请选择型号并填写策略名称");
        }

        try {
            return JSON.toJSONString(stVmEnergySaveService.addStVmStrategy(
                    user.getObjectContext(), strategyName, strategyDesc, vmModelId, user));
        } catch (Exception e) {
            logger.error("Create energy strategy failed", e);
            return errorJson("创建失败：" + e.getMessage());
        }
    }

    /**
     * 删除节能策略（含明细）
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的策略");

        try {
            String errMsg = stVmEnergySaveService.deleteStVmEnergySaveByStrategyId(
                    user.getObjectContext(), parseIds(idsCsv), user);
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Delete energy strategy failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    /**
     * 新增节能明细
     */
    @RequestMapping(value = "/detail/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String createDetail(
            @RequestParam(value = "strategyId", required = false) Integer strategyId,
            @RequestParam(value = "targetValue", required = false) Integer targetValue,
            @RequestParam(value = "energySaveType", required = false) Integer energySaveType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "tempBoxNo", required = false) Integer tempBoxNo) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (strategyId == null || energySaveType == null) return errorJson("参数不完整");

        try {
            return JSON.toJSONString(stVmEnergySaveService.addStVmEnergySave(user.getObjectContext(), user,
                    strategyId, targetValue, energySaveType, startTime, endTime, remark, tempBoxNo));
        } catch (Exception e) {
            logger.error("Create energy detail failed", e);
            return errorJson("新增失败：" + e.getMessage());
        }
    }

    /**
     * 编辑节能明细
     */
    @RequestMapping(value = "/detail/edit", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String editDetail(
            @RequestParam(value = "energySaveId", required = false) Integer energySaveId,
            @RequestParam(value = "targetValue", required = false) Integer targetValue,
            @RequestParam(value = "energySaveType", required = false) Integer energySaveType,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "remark", required = false) String remark,
            @RequestParam(value = "tempBoxNo", required = false) Integer tempBoxNo) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (energySaveId == null) return errorJson("参数不完整");

        try {
            return JSON.toJSONString(stVmEnergySaveService.editEnergySave(user.getObjectContext(), user,
                    energySaveId, targetValue, energySaveType, startTime, endTime, remark, tempBoxNo));
        } catch (Exception e) {
            logger.error("Edit energy detail failed", e);
            return errorJson("保存失败：" + e.getMessage());
        }
    }

    /**
     * 删除节能明细
     */
    @RequestMapping(value = "/detail/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String deleteDetail(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的明细");

        try {
            String errMsg = stVmEnergySaveService.deleteEnergySave(user.getObjectContext(), parseIds(idsCsv), user);
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Delete energy detail failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    private static String userName(ObjectContext context, Integer userId) {
        if (userId == null) return null;
        try {
            TSysUser u = Cayenne.objectForPK(context, TSysUser.class, userId);
            return u != null ? u.getUserName() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static Integer[] parseIds(String csv) {
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }
        return ids;
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
