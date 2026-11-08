package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TStVmChannel;
import com.kdx.vms.model.bean.TStVmSkuSort;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.DataScopeService;
import com.kdx.vms.service.IStVmChannelService;
import com.kdx.vms.service.IStVmStrategyService;
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
 * 货道策略 REST API
 */
@Controller
@RequestMapping("/vms/api/stVmChannel")
public class StVmChannelApiController {

    private static final Logger logger = LoggerFactory.getLogger(StVmChannelApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IStVmChannelService stVmChannelService;

    @Resource
    private IStVmStrategyService stVmStrategyService;

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
     * 策略列表（按公司数据范围过滤）
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
        List full = stVmChannelService.getPage(context, user, searchStVmName, searchVmModelId, MAX_QUERY);
        int total = full == null ? 0 : full.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            Object obj = full.get(i);
            if (obj instanceof TStVmStrategy) {
                TStVmStrategy s = (TStVmStrategy) obj;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("strategyId", s.getStrategyId());
                row.put("strategyName", s.getStrategyName());
                row.put("strategyDesc", s.getStrategyDesc());
                row.put("vmModelId", s.getVmModel() != null ? Cayenne.intPKForObject(s.getVmModel()) : null);
                row.put("vmModelName", s.getVmModel() != null ? s.getVmModel().getModelName() : null);
                row.put("ctor", s.getCtor());
                row.put("ctorName", userName(context, s.getCtor()));
                row.put("ctime", s.getCtime() != null ? s.getCtime().toInstant().toString() : null);
                row.put("utor", s.getUtor());
                row.put("utorName", userName(context, s.getUtor()));
                row.put("utime", s.getUtime() != null ? s.getUtime().toInstant().toString() : null);
                rows.add(row);
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
     * 策略详情：型号版式 + 货道 + 列表页排序
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
                || strategy.getStrategyType() != TStVmStrategy.TYPE_ST_VM_CHANNEL) {
            return notFound("策略不存在");
        }
        if (!dataScopeService.canAccessCompany(user, strategy.getCompany())) {
            throw new ForbiddenException("无权访问该策略");
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("strategyId", strategy.getStrategyId());
        result.put("strategyName", strategy.getStrategyName());
        result.put("strategyDesc", strategy.getStrategyDesc());

        TMtVmModel vmModel = strategy.getVmModel();
        if (vmModel != null) {
            result.put("vmModelId", Cayenne.intPKForObject(vmModel));
            result.put("vmModelName", vmModel.getModelName());
            result.put("channelLayerQty", vmModel.getChannelLayerQty());
            result.put("channelPerLayer", vmModel.getChannelPerLayer());
            result.put("allowMerge", vmModel.getAllowMerge());
        }

        List<Map<String, Object>> channels = new ArrayList<>();
        List<TStVmChannel> channelList = strategy.getStChannel();
        if (channelList != null) {
            for (TStVmChannel c : channelList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("channelId", c.getChannelId());
                row.put("channelCode", c.getChannelCode());
                row.put("skuId", c.getSkuId());
                row.put("mergeQty", c.getMergeQty());
                row.put("mergePid", c.getMergePid());
                row.put("maxCapacity", c.getMaxCapacity());
                row.put("adjustMaxCapacity", c.getAdjustMaxCapacity());
                row.put("isFixedCapacity", c.getIsFixedCapacity());
                row.put("fixedCapacity", c.getFixedCapacity());
                row.put("displayMode", c.getDisplayMode());
                row.put("channelWidth", c.getChannelWidth());
                row.put("channelHeight", c.getChannelHeight());
                row.put("channelLong", c.getChannelLong());
                row.put("acceptWidthMax", c.getAcceptWidthMax());
                if (c.getSkuId() != null && c.getSkuId() != 0) {
                    TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, c.getSkuId());
                    if (sku != null) {
                        row.put("skuName", sku.getSkuName());
                        row.put("skuUnit", sku.getSkuUnit());
                        row.put("skuImgUrl", sku.getImgUrl());
                    }
                }
                channels.add(row);
            }
        }
        result.put("channels", channels);

        List<Map<String, Object>> sorts = new ArrayList<>();
        List<TStVmSkuSort> sortList = strategy.getStSkuSort();
        if (sortList != null) {
            Collections.sort(sortList, new Comparator<TStVmSkuSort>() {
                @Override
                public int compare(TStVmSkuSort a, TStVmSkuSort b) {
                    int sa = a.getSort() == null ? 0 : a.getSort();
                    int sb = b.getSort() == null ? 0 : b.getSort();
                    return Integer.compare(sa, sb);
                }
            });
            for (TStVmSkuSort s : sortList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("sortId", s.getSortId());
                row.put("sort", s.getSort());
                row.put("skuId", s.getSkuId());
                if (s.getSkuId() != null) {
                    TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, s.getSkuId());
                    if (sku != null) {
                        row.put("skuName", sku.getSkuName());
                        row.put("skuImgUrl", sku.getImgUrl());
                    }
                }
                sorts.add(row);
            }
        }
        result.put("sorts", sorts);
        return JSON.toJSONString(result);
    }

    /**
     * 新建策略（按型号货道模板复制）
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam(value = "vmModelId", required = false) Integer vmModelId,
            @RequestParam(value = "strategyName", required = false) String strategyName,
            @RequestParam(value = "strategyDesc", required = false) String strategyDesc) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (vmModelId == null || StringUtil.isEmpty(strategyName)) {
            return errorJson("请选择型号并填写策略名称");
        }

        try {
            String result = stVmChannelService.addStVmChannel(user.getObjectContext(), user,
                    vmModelId, strategyName, strategyDesc);
            if (StringUtil.isEmpty(result)) {
                return errorJson("创建失败，系统内部异常");
            }
            if (result.startsWith("<input")) {
                return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            }
            return errorJson(result);
        } catch (Exception e) {
            logger.error("Create channel strategy failed", e);
            return errorJson("创建失败：" + e.getMessage());
        }
    }

    /**
     * 删除策略
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的策略");

        try {
            Integer[] ids = parseInts(idsCsv);
            Map<String, Object> map = stVmStrategyService.deleteStrategy(user, user.getObjectContext(),
                    ids, TStVmStrategy.TYPE_ST_VM_CHANNEL);
            if (Integer.valueOf(0).equals(map.get("code"))) {
                return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            }
            return JSON.toJSONString(map);
        } catch (Exception e) {
            logger.error("Delete channel strategy failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    /**
     * 关联商品到货道
     */
    @RequestMapping(value = "/relateSku", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String relateSku(
            @RequestParam(value = "channelIds", required = false) String channelIdsCsv,
            @RequestParam(value = "skuId", required = false) Integer skuId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(channelIdsCsv) || skuId == null) return errorJson("请选择货道和商品");

        try {
            return JSON.toJSONString(stVmChannelService.relateSku(user.getObjectContext(), user,
                    parseInts(channelIdsCsv), skuId));
        } catch (Exception e) {
            logger.error("Relate sku failed", e);
            return errorJson("关联商品失败：" + e.getMessage());
        }
    }

    /**
     * 取消货道商品关联
     */
    @RequestMapping(value = "/cancelRelate", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String cancelRelate(@RequestParam(value = "channelIds", required = false) String channelIdsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(channelIdsCsv)) return errorJson("请选择货道");

        try {
            String errMsg = stVmChannelService.cancelRelateSku(user.getObjectContext(), user,
                    Arrays.asList(parseInts(channelIdsCsv)), true);
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Cancel relate failed", e);
            return errorJson("取消失败：" + e.getMessage());
        }
    }

    /**
     * 合并货道
     */
    @RequestMapping(value = "/mergeChannel", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String mergeChannel(
            @RequestParam(value = "firstChannelId", required = false) Integer firstChannelId,
            @RequestParam(value = "secondChannelId", required = false) Integer secondChannelId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        try {
            String errMsg = stVmChannelService.mergeChannel(user.getObjectContext(), firstChannelId, secondChannelId, user);
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Merge channel failed", e);
            return errorJson("合并失败：" + e.getMessage());
        }
    }

    /**
     * 拆分已合并货道
     */
    @RequestMapping(value = "/cancelMerge", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String cancelMerge(@RequestParam(value = "channelId", required = false) Integer channelId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        try {
            String errMsg = stVmChannelService.cancelMergeChannel(user.getObjectContext(), channelId, user);
            if (StringUtil.isEmpty(errMsg)) return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            return errorJson(errMsg);
        } catch (Exception e) {
            logger.error("Cancel merge failed", e);
            return errorJson("拆分失败：" + e.getMessage());
        }
    }

    /**
     * 设置货道满量
     */
    @RequestMapping(value = "/setFull", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String setFull(
            @RequestParam(value = "channelId", required = false) Integer channelId,
            @RequestParam(value = "fullval", required = false) Integer fullval) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (channelId == null || fullval == null) return errorJson("货道或满量不能为空");

        try {
            return JSON.toJSONString(stVmChannelService.setFullSku(user.getObjectContext(), channelId, fullval));
        } catch (Exception e) {
            logger.error("Set full sku failed", e);
            return errorJson("设置满量失败：" + e.getMessage());
        }
    }

    /**
     * 列表页位置互换
     */
    @RequestMapping(value = "/changeSort", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String changeSort(
            @RequestParam(value = "firstSortId", required = false) Integer firstSortId,
            @RequestParam(value = "secondSortId", required = false) Integer secondSortId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        try {
            return JSON.toJSONString(stVmChannelService.changeSort(user.getObjectContext(), user,
                    firstSortId, secondSortId));
        } catch (Exception e) {
            logger.error("Change sort failed", e);
            return errorJson("交换失败：" + e.getMessage());
        }
    }

    /**
     * 复制列表页位置
     */
    @RequestMapping(value = "/copySort", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String copySort(@RequestParam(value = "sortId", required = false) Integer sortId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        try {
            return JSON.toJSONString(stVmChannelService.copySort(user.getObjectContext(), user, sortId));
        } catch (Exception e) {
            logger.error("Copy sort failed", e);
            return errorJson("复制失败：" + e.getMessage());
        }
    }

    /**
     * 删除列表页位置
     */
    @RequestMapping(value = "/deleteSort", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String deleteSort(@RequestParam(value = "sortId", required = false) Integer sortId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        try {
            return JSON.toJSONString(stVmChannelService.deleteSort(user.getObjectContext(), user, sortId));
        } catch (Exception e) {
            logger.error("Delete sort failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    /**
     * 商品能否放入货道预检查
     */
    @RequestMapping(value = "/checkSku", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String checkSku(
            @RequestParam(value = "channelIds", required = false) String channelIdsCsv,
            @RequestParam(value = "skuId", required = false) Integer skuId) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        try {
            return JSON.toJSONString(stVmChannelService.checkSku(user.getObjectContext(),
                    StringUtil.isEmpty(channelIdsCsv) ? null : parseInts(channelIdsCsv), skuId));
        } catch (Exception e) {
            logger.error("Check sku failed", e);
            return errorJson("检测失败：" + e.getMessage());
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

    private static Integer[] parseInts(String csv) {
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
