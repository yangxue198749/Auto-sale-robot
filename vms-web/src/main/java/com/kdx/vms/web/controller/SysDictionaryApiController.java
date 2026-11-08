package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.ISysDictionaryService;
import com.kdx.vms.service.ISysTypeInfoService;
import org.apache.cayenne.Cayenne;
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
 * 字典项管理 REST API
 */
@Controller
@RequestMapping("/vms/api/dictionary")
public class SysDictionaryApiController {

    private static final Logger logger = LoggerFactory.getLogger(SysDictionaryApiController.class);

    @Resource
    private ISysDictionaryService sysDictionaryService;

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
     * 字典项列表（按字典类型）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "typeInfoId", required = false) Integer typeInfoId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", required = false) String name) {

        requireAuth();
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> data = sysDictionaryService.list(typeInfoId, name, pageSize, page, context);

        List<Map<String, Object>> rows = new ArrayList<>();
        Object nodeList = data.get("nodeList");
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TSysDictionary) {
                    TSysDictionary d = (TSysDictionary) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(d));
                    row.put("dictionaryCode", d.getDictionaryCode());
                    row.put("dictionaryName", d.getDictionaryName());
                    row.put("dictionaryDesc", d.getDictionaryDesc());
                    row.put("dictionaryState", d.getDictionaryState());
                    if (d.getTypeInfo() != null) {
                        row.put("typeInfoId", Cayenne.intPKForObject(d.getTypeInfo()));
                        row.put("typeInfoName", d.getTypeInfo().getTypeName());
                    }
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
     * 创建字典项
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam("typeInfoId") Integer typeInfoId,
            @RequestParam(value = "dictionaryCode", required = false) String dictionaryCode,
            @RequestParam(value = "dictionaryName", required = false) String dictionaryName,
            @RequestParam(value = "dictionaryDesc", required = false) String dictionaryDesc,
            @RequestParam(value = "dictionaryState", required = false) Integer dictionaryState) {

        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysDictionary entity = context.newObject(TSysDictionary.class);
        entity.setDictionaryCode(dictionaryCode);
        entity.setDictionaryName(dictionaryName);
        entity.setDictionaryDesc(dictionaryDesc);
        entity.setDictionaryState(dictionaryState != null ? dictionaryState : 0);

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer id = sysDictionaryService.addSysTypeinfo(typeInfoId, entity, context);
            response.put("success", true);
            response.put("message", "创建成功");
            response.put("id", id);
        } catch (Exception e) {
            logger.error("Create dictionary failed", e);
            response.put("success", false);
            response.put("message", "创建失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 更新字典项
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "dictionaryCode", required = false) String dictionaryCode,
            @RequestParam(value = "dictionaryName", required = false) String dictionaryName,
            @RequestParam(value = "dictionaryDesc", required = false) String dictionaryDesc,
            @RequestParam(value = "dictionaryState", required = false) Integer dictionaryState) {

        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysDictionary existing = sysDictionaryService.getTSysDictionaryById(id, context);
        if (existing == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "字典项不存在");
            return JSON.toJSONString(err);
        }
        if (dictionaryCode != null) existing.setDictionaryCode(dictionaryCode);
        if (dictionaryName != null) existing.setDictionaryName(dictionaryName);
        if (dictionaryDesc != null) existing.setDictionaryDesc(dictionaryDesc);
        if (dictionaryState != null) existing.setDictionaryState(dictionaryState);

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            sysDictionaryService.updateSysTypeinfo(existing, context);
            response.put("success", true);
            response.put("message", "更新成功");
        } catch (Exception e) {
            logger.error("Update dictionary failed", e);
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 删除字典项
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Boolean ok = sysDictionaryService.deleteSysTypeinfo(new String[]{id.toString()}, context);
            response.put("success", ok != null && ok);
            response.put("message", ok != null && ok ? "删除成功" : "删除失败（可能被引用）");
        } catch (Exception e) {
            logger.error("Delete dictionary failed", e);
            response.put("success", false);
            response.put("message", "删除失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }
}
