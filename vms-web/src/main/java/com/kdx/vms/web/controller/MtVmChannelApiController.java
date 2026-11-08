package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmChannel;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.custom.ChannelConfigCache;
import com.kdx.vms.service.IMtVmChannelService;
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
 * 设备货道配置 REST API
 */
@Controller
@RequestMapping("/vms/api/vmChannel")
public class MtVmChannelApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmChannelApiController.class);

    @Resource
    private IMtVmChannelService mtVmChannelService;

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
     * 设备货道列表（直接查库，JSON）
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

        SelectQuery query = new SelectQuery(TMtVmChannel.class);
        query.setQualifier(ExpressionFactory.matchExp("mtVm", vm));
        query.addOrdering("channelCode", SortOrder.ASCENDING);
        List<TMtVmChannel> channels = context.performQuery(query);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (TMtVmChannel ch : channels) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", Cayenne.intPKForObject(ch));
            row.put("channelCode", ch.getChannelCode());
            row.put("channelStatus", ch.getChannelStatus());
            row.put("currNum", ch.getCurrNum());
            row.put("maxCapacity", ch.getMaxCapacity());
            row.put("fixedCapacity", ch.getFixedCapacity());
            row.put("isFixedCapacity", ch.getIsFixedCapacity());
            row.put("skuId", ch.getSku() != null ? Cayenne.intPKForObject(ch.getSku()) : null);
            row.put("skuName", ch.getSku() != null ? ch.getSku().getSkuName() : null);
            row.put("skuCode", ch.getSku() != null ? ch.getSku().getSkuCode() : null);
            row.put("displayMode", ch.getDisplayMode());
            row.put("remarks", ch.getRemarks());
            rows.add(row);
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 关联商品到货道（缓存编辑模式，需随后调用 /commit 提交）
     */
    @RequestMapping(value = "/{vmId}/relateSku", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String relateSku(@PathVariable("vmId") Integer vmId,
                            @RequestParam("channelIds") String channelIdsCsv,
                            @RequestParam("skuId") Integer skuId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVm vm = Cayenne.objectForPK(context, TMtVm.class, vmId);
        if (vm == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "设备不存在");
            return JSON.toJSONString(err);
        }
        Integer[] channelIds = parseChannels(channelIdsCsv);

        try {
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(user, vm);
            Map<String, Object> result = mtVmChannelService.relateSku(user, cache, channelIds, skuId);
            mtVmChannelService.updateChannelCache(cache);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Relate sku failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "关联失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    /**
     * 取消货道商品关联
     */
    @RequestMapping(value = "/{vmId}/cancelRelate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String cancelRelate(@PathVariable("vmId") Integer vmId,
                               @RequestParam("channelIds") String channelIdsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtVm vm = Cayenne.objectForPK(context, TMtVm.class, vmId);
        if (vm == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "设备不存在");
            return JSON.toJSONString(err);
        }
        Integer[] channelIds = parseChannels(channelIdsCsv);

        try {
            ChannelConfigCache cache = mtVmChannelService.getChannelCache(user, vm);
            String errorMsg = mtVmChannelService.cancelRelateSku(cache, Arrays.asList(channelIds));
            Map<String, Object> result = new LinkedHashMap<>();
            if (errorMsg != null && !errorMsg.isEmpty()) {
                result.put("success", false);
                result.put("message", errorMsg);
            } else {
                mtVmChannelService.updateChannelCache(cache);
                result.put("success", true);
                result.put("message", "取消关联成功");
            }
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Cancel relate failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "取消关联失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    private Integer[] parseChannels(String csv) {
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }
        return ids;
    }
}
