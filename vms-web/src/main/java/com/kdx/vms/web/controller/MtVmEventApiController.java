package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmEvent;
import com.kdx.vms.model.bean.TMtVmEventMaster;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TStVmEvent;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtVmEventService;
import com.kdx.vms.service.IStVmEventService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.DateUtils;
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
 * 应用促销策略 REST API（已应用到售货机的促销管理）
 */
@Controller
@RequestMapping("/vms/api/mtVmEvent")
public class MtVmEventApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmEventApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IMtVmEventService mtVmEventService;

    @Resource
    private IStVmEventService stVmEventService;

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
     * 已应用促销列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "searchInnerCode", required = false) String searchInnerCode,
            @RequestParam(value = "searchNodeName", required = false) String searchNodeName,
            @RequestParam(value = "businessDistrictId", required = false) Integer businessDistrictId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = user.getObjectContext();
        List<TMtVmEventMaster> full = mtVmEventService.findMtEventMaster(user, context, searchNodeName,
                searchInnerCode, null, null, null, businessDistrictId, MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String nowTime = new SimpleDateFormat("HH:mm").format(new Date());
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            TMtVmEventMaster m = full.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("eventId", Cayenne.intPKForObject(m));
            row.put("strategyEventId", m.getEventId());
            row.put("eventName", m.getEventName());
            row.put("eventDesc", m.getEventDesc());
            row.put("eventType", m.getEventType());
            row.put("beginDate", formatDate(m.getBeginDate()));
            row.put("endDate", formatDate(m.getEndDate()));
            row.put("startTime", m.getStartTime());
            row.put("endTime", m.getEndTime());
            row.put("status", statusOf(m, today, nowTime));
            if (m.getMtVm() != null) {
                row.put("vmId", Cayenne.intPKForObject(m.getMtVm()));
                row.put("innerCode", m.getMtVm().getInnerCode());
                row.put("vmDesc", m.getMtVm().getVmDesc());
                if (m.getMtVm().getNode() != null) {
                    row.put("nodeName", m.getMtVm().getNode().getNodeName());
                }
            }
            row.put("ctorName", userName(context, m.getCtor()));
            row.put("ctime", m.getCtime() != null ? m.getCtime().toInstant().toString() : null);
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
     * 已应用促销详情
     */
    @RequestMapping(value = "/{eventId}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("eventId") Integer eventId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        TMtVmEventMaster m = Cayenne.objectForPK(context, TMtVmEventMaster.class, eventId);
        if (m == null) return notFound("促销记录不存在");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("eventId", Cayenne.intPKForObject(m));
        result.put("eventName", m.getEventName());
        result.put("eventDesc", m.getEventDesc());
        result.put("eventType", m.getEventType());
        result.put("beginDate", formatDate(m.getBeginDate()));
        result.put("endDate", formatDate(m.getEndDate()));
        result.put("startTime", m.getStartTime());
        result.put("endTime", m.getEndTime());
        if (m.getMtVm() != null) {
            result.put("vmId", Cayenne.intPKForObject(m.getMtVm()));
            result.put("innerCode", m.getMtVm().getInnerCode());
            result.put("vmDesc", m.getMtVm().getVmDesc());
        }

        List<Map<String, Object>> events = new ArrayList<>();
        List<TMtVmEvent> list = m.getEvents();
        if (list != null) {
            for (TMtVmEvent e : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("discountType", e.getDiscountType());
                row.put("discountValue", e.getDiscountValue());
                TMtSku sku = e.getSku();
                if (sku != null) {
                    row.put("skuId", Cayenne.intPKForObject(sku));
                    row.put("skuName", sku.getSkuName());
                    row.put("skuCode", sku.getSkuCode());
                    row.put("offerPrice", sku.getOfferPrice());
                    row.put("skuImgUrl", sku.getImgUrl());
                }
                events.add(row);
            }
        }
        result.put("events", events);
        return JSON.toJSONString(result);
    }

    /**
     * 可用的商品促销策略
     */
    @RequestMapping(value = "/strategies/sku", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String skuStrategies(@RequestParam(value = "searchValue", required = false) String searchValue) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        List full = stVmEventService.findSkuEventList(user, context, searchValue, MAX_QUERY);
        List<Map<String, Object>> rows = new ArrayList<>();
        if (full != null) {
            for (Object obj : full) {
                if (obj instanceof TStVmStrategy) {
                    TStVmStrategy s = (TStVmStrategy) obj;
                    rows.add(strategyToMap(s));
                }
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 可用的整机促销策略
     */
    @RequestMapping(value = "/strategies/vm", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String vmStrategies(@RequestParam(value = "searchValue", required = false) String searchValue) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        List full = stVmEventService.findVmEventList(user, context, null, searchValue, MAX_QUERY);
        List<Map<String, Object>> rows = new ArrayList<>();
        if (full != null) {
            for (Object obj : full) {
                if (obj instanceof TStVmEvent) {
                    TStVmEvent e = (TStVmEvent) obj;
                    if (e.getStrategy() == null) continue;
                    Map<String, Object> row = strategyToMap(e.getStrategy());
                    row.put("discountType", e.getDiscountType());
                    row.put("discountValue", e.getDiscountValue());
                    rows.add(row);
                }
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 可应用促销的售货机（type: sku / vm）
     */
    @RequestMapping(value = "/candidates/{type}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String candidates(
            @PathVariable("type") String type,
            @RequestParam(value = "searchInnerCode", required = false) String searchInnerCode,
            @RequestParam(value = "searchNodeName", required = false) String searchNodeName,
            @RequestParam(value = "businessDistrictId", required = false) Integer businessDistrictId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        List<TMtVm> list;
        if ("vm".equalsIgnoreCase(type)) {
            list = mtVmEventService.findVmEventVmList(user, context, searchNodeName, searchInnerCode,
                    null, null, null, businessDistrictId, null);
        } else {
            list = mtVmEventService.findSkuEventVmList(user, context, searchNodeName, searchInnerCode,
                    null, null, null, businessDistrictId, null);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        if (list != null) {
            for (TMtVm vm : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("vmId", Cayenne.intPKForObject(vm));
                row.put("innerCode", vm.getInnerCode());
                row.put("vmDesc", vm.getVmDesc());
                row.put("vmType", vm.getVmType());
                if (vm.getNode() != null) {
                    row.put("nodeName", vm.getNode().getNodeName());
                }
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 应用促销（eventType: 1=整机，2=商品）
     */
    @RequestMapping(value = "/apply", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String apply(
            @RequestParam(value = "eventType", required = false) Integer eventType,
            @RequestParam(value = "strategyId", required = false) Integer strategyId,
            @RequestParam(value = "vmIds", required = false) String vmIdsCsv,
            @RequestParam(value = "beginDate", required = false) String beginDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (eventType == null || strategyId == null || StringUtil.isEmpty(vmIdsCsv)
                || StringUtil.isEmpty(beginDate) || StringUtil.isEmpty(endDate)
                || StringUtil.isEmpty(startTime) || StringUtil.isEmpty(endTime)) {
            return errorJson("参数不完整");
        }

        try {
            Integer[] vmIds = parseIds(vmIdsCsv);
            // 副机不允许设置促销
            ObjectContext context = user.getObjectContext();
            for (Integer vmId : vmIds) {
                TMtVm vm = Cayenne.objectForPK(context, TMtVm.class, vmId);
                if (vm == null) continue;
                if (vm.getVmType() != null && vm.getVmType().equals(TMtVmModel.COMBINATORIAL_EXTENTION)) {
                    return errorJson("副机无法设置促销策略，请通过主机设置");
                }
            }
            Map<String, Object> result = mtVmEventService.saveMtVmEvent(user, context, vmIds, strategyId,
                    DateUtils.parseDate(beginDate + " 00:00:00"),
                    DateUtils.parseDate(endDate + " 23:59:59"),
                    startTime, endTime, eventType);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Apply promotion failed", e);
            return errorJson("应用失败：" + e.getMessage());
        }
    }

    /**
     * 取消应用促销
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要取消的促销");

        try {
            return JSON.toJSONString(mtVmEventService.deleteEvent(user, user.getObjectContext(), parseIds(idsCsv)));
        } catch (Exception e) {
            logger.error("Delete applied promotion failed", e);
            return errorJson("取消失败：" + e.getMessage());
        }
    }

    private static Map<String, Object> strategyToMap(TStVmStrategy s) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("strategyId", s.getStrategyId());
        row.put("strategyName", s.getStrategyName());
        row.put("strategyDesc", s.getStrategyDesc());
        return row;
    }

    private static String statusOf(TMtVmEventMaster m, String today, String nowTime) {
        String begin = formatDate(m.getBeginDate());
        String end = formatDate(m.getEndDate());
        if (begin != null && today.compareTo(begin) < 0) return "未开始";
        if (end != null && today.compareTo(end) > 0) return "已结束";
        if (begin != null && today.equals(begin) && m.getStartTime() != null && nowTime.compareTo(m.getStartTime()) < 0) {
            return "未开始";
        }
        if (end != null && today.equals(end) && m.getEndTime() != null && nowTime.compareTo(m.getEndTime()) > 0) {
            return "已结束";
        }
        return "进行中";
    }

    private static String formatDate(Date date) {
        if (date == null) return null;
        return new SimpleDateFormat("yyyy-MM-dd").format(date);
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
