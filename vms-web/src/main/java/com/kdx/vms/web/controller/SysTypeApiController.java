package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.ISysTypeInfoService;
import org.apache.cayenne.ObjectContext;
import org.apache.shiro.SecurityUtils;
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
 * 字典类型管理 REST API
 */
@Controller
@RequestMapping("/vms/api/sysType")
public class SysTypeApiController {

    private static final Logger logger = LoggerFactory.getLogger(SysTypeApiController.class);

    @Resource
    private ISysTypeInfoService sysTypeInfoService;

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
     * 字典类型列表
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
        Map<String, Object> data = sysTypeInfoService.list(name, pageSize, page, context);

        List<Map<String, Object>> rows = new ArrayList<>();
        Object nodeList = data.get("nodeList");
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TSysTypeInfo) {
                    TSysTypeInfo t = (TSysTypeInfo) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", getId(t));
                    row.put("typeCode", t.getTypeCode());
                    row.put("typeName", t.getTypeName());
                    row.put("typeDesc", t.getTypeDesc());
                    rows.add(row);
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
     * 创建字典类型
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "typeCode", required = false) String typeCode,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "typeDesc", required = false) String typeDesc) {

        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysTypeInfo entity = context.newObject(TSysTypeInfo.class);
        entity.setTypeCode(typeCode);
        entity.setTypeName(typeName);
        entity.setTypeDesc(typeDesc);

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer id = sysTypeInfoService.addSysTypeinfo(entity, context);
            response.put("success", true);
            response.put("message", "创建成功");
            response.put("id", id);
        } catch (Exception e) {
            logger.error("Create sysType failed", e);
            response.put("success", false);
            response.put("message", "创建失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 更新字典类型
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "typeCode", required = false) String typeCode,
            @RequestParam(value = "typeName", required = false) String typeName,
            @RequestParam(value = "typeDesc", required = false) String typeDesc) {

        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysTypeInfo existing = sysTypeInfoService.getTSysTypeInfoById(id, context);
        if (existing == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "字典类型不存在");
            return JSON.toJSONString(err);
        }
        if (typeCode != null) existing.setTypeCode(typeCode);
        if (typeName != null) existing.setTypeName(typeName);
        if (typeDesc != null) existing.setTypeDesc(typeDesc);

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            sysTypeInfoService.updateSysTypeinfo(existing, context);
            response.put("success", true);
            response.put("message", "更新成功");
        } catch (Exception e) {
            logger.error("Update sysType failed", e);
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 删除字典类型
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Boolean ok = sysTypeInfoService.deleteSysTypeinfo(new String[]{id.toString()}, context);
            response.put("success", ok != null && ok);
            response.put("message", ok != null && ok ? "删除成功" : "删除失败（可能被引用）");
        } catch (Exception e) {
            logger.error("Delete sysType failed", e);
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    private Integer getId(TSysTypeInfo t) {
        try {
            Object id = org.apache.cayenne.Cayenne.pkForObject(t);
            return id != null ? Integer.valueOf(id.toString()) : null;
        } catch (Exception e) {
            return null;
        }
    }
}
