package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.web.tags.DateUtils;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmText;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtVmTextService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
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
 * 设备走马灯配置 REST API
 */
@Controller
@RequestMapping("/vms/api/vmText")
public class MtVmTextApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmTextApiController.class);

    @Resource
    private IMtVmTextService mtVmTextService;

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
     * 设备走马灯列表
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

        SelectQuery query = new SelectQuery(TMtVmText.class);
        query.setQualifier(ExpressionFactory.matchExp("mtVm", vm));
        List<TMtVmText> list = context.performQuery(query);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtVmText t : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", Cayenne.intPKForObject(t));
            row.put("textContents", t.getTextContents());
            row.put("beginDate", t.getBeginDate() != null ? t.getBeginDate().toInstant().toString() : null);
            row.put("endDate", t.getEndDate() != null ? t.getEndDate().toInstant().toString() : null);
            rows.add(row);
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 保存走马灯（应用到设备）
     */
    @RequestMapping(value = "/{vmId}/save", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String save(
            @PathVariable("vmId") Integer vmId,
            @RequestParam("mtText") String mtText,
            @RequestParam(value = "beginDate", required = false) String beginDate,
            @RequestParam(value = "endDate", required = false) String endDate) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            Map<String, Object> result = mtVmTextService.saveText(user, context, mtText,
                    new Integer[]{vmId},
                    beginDate != null ? DateUtils.parseDate(beginDate + " 00:00:00") : null,
                    endDate != null ? DateUtils.parseDate(endDate + " 23:59:59") : null);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Save vmText failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "保存失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }
}
