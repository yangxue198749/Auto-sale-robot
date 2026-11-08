package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TMtVmModelChannel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtVmModelChannelService;
import com.kdx.vms.service.IMtVmModelService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.Ordering;
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
 * 售货机型号管理 REST API
 */
@Controller
@RequestMapping("/vms/api/vmModel")
public class MtVmModelApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmModelApiController.class);

    @Resource
    private IMtVmModelService mtVmModelService;

    @Resource
    private IMtVmModelChannelService mtVmModelChannelService;

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
     * 型号列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", required = false) String name) {

        requireAuth();
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> data = mtVmModelService.list(name, pageSize, page, context);

        List<Map<String, Object>> rows = new ArrayList<>();
        Object nodeList = data.get("nodeList");
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TMtVmModel) {
                    rows.add(convertModelToMap((TMtVmModel) obj));
                }
            }
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("total", data.get("totalSize"));
        response.put("page", page);
        response.put("pageSize", pageSize);
        response.put("data", rows);
        return JSON.toJSONString(response);
    }

    /**
     * 型号详情
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVmModel model = mtVmModelService.getTMtVmModel(String.valueOf(id), context);
        if (model == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "型号不存在");
            return JSON.toJSONString(err);
        }
        return JSON.toJSONString(convertModelToMap(model));
    }

    /**
     * 创建型号
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam("modelName") String modelName,
            @RequestParam(value = "modelDesc", required = false) String modelDesc,
            @RequestParam(value = "modelType", required = false) Integer modelType,
            @RequestParam(value = "channelType", required = false) Integer channelType,
            @RequestParam(value = "screenType", required = false) Integer screenType) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVmModel model = context.newObject(TMtVmModel.class);
        model.setModelName(modelName);
        model.setModelDesc(modelDesc);
        model.setModelType(modelType);
        model.setChannelType(channelType);
        model.setScreenType(screenType);
        model.setDeleteMark(0);

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer id = mtVmModelService.createVmModel(model, user, context);
            response.put("success", id != null && id > 0);
            response.put("message", id != null && id > 0 ? "创建成功" : "创建失败");
            response.put("id", id);
        } catch (Exception e) {
            logger.error("Create vmModel failed", e);
            response.put("success", false);
            response.put("message", "创建失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 更新型号
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "modelName", required = false) String modelName,
            @RequestParam(value = "modelDesc", required = false) String modelDesc,
            @RequestParam(value = "modelType", required = false) Integer modelType,
            @RequestParam(value = "channelType", required = false) Integer channelType,
            @RequestParam(value = "screenType", required = false) Integer screenType) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVmModel existing = mtVmModelService.getTMtVmModel(String.valueOf(id), context);
        if (existing == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "型号不存在");
            return JSON.toJSONString(err);
        }
        if (modelName != null) existing.setModelName(modelName);
        if (modelDesc != null) existing.setModelDesc(modelDesc);
        if (modelType != null) existing.setModelType(modelType);
        if (channelType != null) existing.setChannelType(channelType);
        if (screenType != null) existing.setScreenType(screenType);

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer result = mtVmModelService.updateVmModel(existing, user, context);
            response.put("success", result != null && result > 0);
            response.put("message", result != null && result > 0 ? "更新成功" : "更新失败");
        } catch (Exception e) {
            logger.error("Update vmModel failed", e);
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 删除型号
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Boolean ok = mtVmModelService.deleteVmModel(String.valueOf(id), context);
            response.put("success", ok != null && ok);
            response.put("message", ok != null && ok ? "删除成功" : "删除失败（可能被设备引用）");
        } catch (Exception e) {
            logger.error("Delete vmModel failed", e);
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 型号货道列表
     */
    @RequestMapping(value = "/{id}/channels", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listChannels(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVmModel model = mtVmModelService.getTMtVmModel(String.valueOf(id), context);
        if (model == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "型号不存在");
            return JSON.toJSONString(err);
        }

        SelectQuery query = new SelectQuery(TMtVmModelChannel.class);
        query.setQualifier(ExpressionFactory.matchExp("model", model));
        query.addOrdering("channelCode", SortOrder.ASCENDING);
        List<TMtVmModelChannel> channels = context.performQuery(query);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtVmModelChannel ch : channels) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", Cayenne.intPKForObject(ch));
            row.put("channelCode", ch.getChannelCode());
            row.put("channelWidth", ch.getChannelWidth());
            row.put("channelHeight", ch.getChannelHeight());
            row.put("channelLength", ch.getChannelLength());
            row.put("channelType", ch.getChannelType());
            row.put("acceptWidthMax", ch.getAcceptWidthMax());
            row.put("isFixedCapacity", ch.getIsFixedCapacity());
            row.put("fixedCapacity", ch.getFixedCapacity());
            row.put("displayMode", ch.getDisplayMode());
            rows.add(row);
        }
        return JSON.toJSONString(rows);
    }

    private Map<String, Object> convertModelToMap(TMtVmModel m) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", Cayenne.intPKForObject(m));
        row.put("modelName", m.getModelName());
        row.put("modelDesc", m.getModelDesc());
        row.put("modelType", m.getModelType());
        row.put("channelType", m.getChannelType());
        row.put("screenType", m.getScreenType());
        row.put("deleteMark", m.getDeleteMark());
        return row;
    }
}
