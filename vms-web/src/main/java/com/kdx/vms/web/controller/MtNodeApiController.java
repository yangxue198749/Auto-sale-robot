package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.model.bean.TMtNode;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IMtNodeService;
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
 * 点位管理 REST API
 */
@Controller
@RequestMapping("/vms/api/mtNode")
public class MtNodeApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtNodeApiController.class);

    @Resource
    private IMtNodeService mtNodeService;

    private TSysUser getCurrentUser() {
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
     * 点位列表（分页 + 筛选）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "province", required = false) Integer province,
            @RequestParam(value = "city", required = false) Integer city,
            @RequestParam(value = "area", required = false) Integer area,
            @RequestParam(value = "businessDistrictId", required = false) Integer businessDistrictId,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchValue", required = false) String searchValue) {

        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        List rawList = mtNodeService.findNodeList(user, context, searchType, searchValue,
                province, city, area, businessDistrictId, 10000);

        int total = rawList != null ? rawList.size() : 0;
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (rawList != null && fromIndex < total) {
            List pageList = rawList.subList(fromIndex, toIndex);
            for (Object obj : pageList) {
                if (obj instanceof TMtNode) {
                    rows.add(convertNodeToMap((TMtNode) obj));
                }
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
     * 点位详情
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("id") Integer id) {
        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtNode node = mtNodeService.findNode(context, id);
        if (node == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "点位不存在");
            return JSON.toJSONString(err);
        }
        return JSON.toJSONString(convertNodeToMap(node));
    }

    /**
     * 新增点位
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam("nodeName") String nodeName,
            @RequestParam(value = "nodeAddress", required = false) String nodeAddress,
            @RequestParam(value = "provinceId", required = false) Integer provinceId,
            @RequestParam(value = "cityId", required = false) Integer cityId,
            @RequestParam(value = "areaId", required = false) Integer areaId,
            @RequestParam(value = "businessDistrictId", required = false) Integer businessDistrictId,
            @RequestParam(value = "partnerId", required = false) Integer partnerId,
            @RequestParam(value = "deliveryId", required = false) Integer deliveryId,
            @RequestParam(value = "repairId", required = false) Integer repairId,
            @RequestParam(value = "propertyName", required = false) String propertyName,
            @RequestParam(value = "propertyManager", required = false) String propertyManager,
            @RequestParam(value = "propertyPhone", required = false) String propertyPhone) {

        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtNode node = context.newObject(TMtNode.class);
        node.setNodeName(nodeName);
        node.setNodeAddress(nodeAddress);
        node.setProvinceId(provinceId);
        node.setCityId(cityId);
        node.setAreaId(areaId);
        node.setBusinessDistrictId(businessDistrictId);
        if (partnerId != null) node.setPartner(Cayenne.objectForPK(context, com.kdx.vms.model.bean.TMtPartner.class, partnerId));
        if (deliveryId != null) node.setDelivery(Cayenne.objectForPK(context, TSysUser.class, deliveryId));
        if (repairId != null) node.setRepair(Cayenne.objectForPK(context, TSysUser.class, repairId));
        node.setPropertyName(propertyName);
        node.setPropertyManager(propertyManager);
        node.setPropertyPhone(propertyPhone);
        node.setCtime(new Date());
        node.setCtor(user.getId());

        try {
            Map<String, Object> result = mtNodeService.saveNode(user, context, node, partnerId, deliveryId, repairId);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Create node failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "创建失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    /**
     * 更新点位
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "nodeName", required = false) String nodeName,
            @RequestParam(value = "nodeAddress", required = false) String nodeAddress,
            @RequestParam(value = "provinceId", required = false) Integer provinceId,
            @RequestParam(value = "cityId", required = false) Integer cityId,
            @RequestParam(value = "areaId", required = false) Integer areaId,
            @RequestParam(value = "businessDistrictId", required = false) Integer businessDistrictId,
            @RequestParam(value = "partnerId", required = false) Integer partnerId,
            @RequestParam(value = "deliveryId", required = false) Integer deliveryId,
            @RequestParam(value = "repairId", required = false) Integer repairId,
            @RequestParam(value = "propertyName", required = false) String propertyName,
            @RequestParam(value = "propertyManager", required = false) String propertyManager,
            @RequestParam(value = "propertyPhone", required = false) String propertyPhone) {

        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtNode node = mtNodeService.findNode(context, id);
        if (node == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "点位不存在");
            return JSON.toJSONString(err);
        }
        if (nodeName != null) node.setNodeName(nodeName);
        if (nodeAddress != null) node.setNodeAddress(nodeAddress);
        if (provinceId != null) node.setProvinceId(provinceId);
        if (cityId != null) node.setCityId(cityId);
        if (areaId != null) node.setAreaId(areaId);
        if (businessDistrictId != null) node.setBusinessDistrictId(businessDistrictId);
        if (partnerId != null) node.setPartner(Cayenne.objectForPK(context, com.kdx.vms.model.bean.TMtPartner.class, partnerId));
        if (deliveryId != null) node.setDelivery(Cayenne.objectForPK(context, TSysUser.class, deliveryId));
        if (repairId != null) node.setRepair(Cayenne.objectForPK(context, TSysUser.class, repairId));
        if (propertyName != null) node.setPropertyName(propertyName);
        if (propertyManager != null) node.setPropertyManager(propertyManager);
        if (propertyPhone != null) node.setPropertyPhone(propertyPhone);
        node.setUtime(new Date());
        node.setUtor(user.getId());

        try {
            Map<String, Object> result = mtNodeService.saveNode(user, context, node, id, partnerId, deliveryId, repairId);
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Update node failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "更新失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    /**
     * 删除点位
     */
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@PathVariable("id") Integer id) {
        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        try {
            Map<String, Object> result = mtNodeService.deleteNode(user, context, new Integer[]{id});
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Delete node failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "删除失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    /**
     * 配置配送员
     */
    @RequestMapping(value = "/{id}/delivery", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String setDelivery(@PathVariable("id") Integer id, @RequestParam("deliveryId") Integer deliveryId) {
        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysUser delivery = Cayenne.objectForPK(context, TSysUser.class, deliveryId);
        try {
            Map<String, Object> result = mtNodeService.setDelivery(user, context, delivery, new Integer[]{id});
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Set delivery failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "配置失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    /**
     * 配置维修员
     */
    @RequestMapping(value = "/{id}/repair", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String setRepair(@PathVariable("id") Integer id, @RequestParam("repairId") Integer repairId) {
        requireAuth();
        TSysUser user = getCurrentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TSysUser repair = Cayenne.objectForPK(context, TSysUser.class, repairId);
        try {
            Map<String, Object> result = mtNodeService.setRepair(user, context, repair, new Integer[]{id});
            return JSON.toJSONString(result);
        } catch (Exception e) {
            logger.error("Set repair failed", e);
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "配置失败：" + e.getMessage());
            return JSON.toJSONString(err);
        }
    }

    private Map<String, Object> convertNodeToMap(TMtNode node) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", Cayenne.intPKForObject(node));
        row.put("nodeName", node.getNodeName());
        row.put("nodeAddress", node.getNodeAddress());
        row.put("provinceId", node.getProvinceId());
        row.put("cityId", node.getCityId());
        row.put("areaId", node.getAreaId());
        row.put("businessDistrictId", node.getBusinessDistrictId());
        row.put("partnerId", node.getPartner() != null ? Cayenne.intPKForObject(node.getPartner()) : null);
        row.put("partnerName", node.getPartner() != null ? node.getPartner().getPartnerName() : null);
        row.put("deliveryId", node.getDelivery() != null ? Cayenne.intPKForObject(node.getDelivery()) : null);
        row.put("deliveryName", node.getDelivery() != null ? node.getDelivery().getUserName() : null);
        row.put("repairId", node.getRepair() != null ? Cayenne.intPKForObject(node.getRepair()) : null);
        row.put("repairName", node.getRepair() != null ? node.getRepair().getUserName() : null);
        row.put("propertyName", node.getPropertyName());
        row.put("propertyManager", node.getPropertyManager());
        row.put("propertyPhone", node.getPropertyPhone());
        row.put("longitude", node.getLongitude());
        row.put("latitude", node.getLatitude());
        row.put("ctime", node.getCtime() != null ? node.getCtime().toInstant().toString() : null);
        return row;
    }
}
