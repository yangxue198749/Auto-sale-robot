package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtSkuImg;
import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.ISkuService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.RandomUtils;
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
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.*;

/**
 * 商品管理 REST API
 */
@Controller
@RequestMapping("/vms/api/sku")
public class SkuApiController {

    private static final Logger logger = LoggerFactory.getLogger(SkuApiController.class);

    @Resource
    private ISkuService skuService;

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
     * 商品列表（分页 + 筛选）
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "skuCode", required = false) String skuCode,
            @RequestParam(value = "skuTypeId", required = false) Integer skuTypeId,
            @RequestParam(value = "skuTypePid", required = false) Integer skuTypePid) {

        requireAuth();
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        Map<String, Object> data = skuService.list(context, name, skuCode, skuTypePid, skuTypeId, pageSize, page);

        List<Map<String, Object>> rows = new ArrayList<>();
        Object nodeList = data.get("nodeList");
        if (nodeList instanceof List) {
            for (Object obj : (List<?>) nodeList) {
                if (obj instanceof TMtSku) {
                    rows.add(convertSkuToMap((TMtSku) obj));
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
     * 商品详情
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String detail(@PathVariable("id") Integer id) {
        requireAuth();
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtSku sku = skuService.getTMtSku(context, id);
        if (sku == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "商品不存在");
            return JSON.toJSONString(err);
        }
        return JSON.toJSONString(convertSkuToMap(sku));
    }

    /**
     * 创建商品
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(
            @RequestParam("skuName") String skuName,
            @RequestParam("skuTypeId") Integer skuTypeId,
            @RequestParam(value = "skuCode", required = false) String skuCode,
            @RequestParam(value = "offerPrice", required = false) Integer offerPrice,
            @RequestParam(value = "skuUnit", required = false) String skuUnit,
            @RequestParam(value = "skuDesc", required = false) String skuDesc,
            @RequestParam(value = "skuProducer", required = false) String skuProducer,
            @RequestParam(value = "skuShelfLife", required = false) Integer skuShelfLife,
            @RequestParam(value = "skuMadeArea", required = false) String skuMadeArea,
            @RequestParam(value = "skuColor", required = false) String skuColor,
            @RequestParam(value = "packingSpecification", required = false) String packingSpecification,
            @RequestParam(value = "storageMethod", required = false) String storageMethod,
            @RequestParam(value = "reserve", required = false) Integer reserve) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtSku sku = context.newObject(TMtSku.class);
        sku.setSkuName(skuName);
        sku.setSkuCode(skuCode);
        sku.setOfferPrice(offerPrice);
        sku.setSkuUnit(skuUnit);
        sku.setSkuDesc(skuDesc);
        sku.setSkuProducer(skuProducer);
        sku.setSkuShelfLife(skuShelfLife);
        sku.setSkuMadeArea(skuMadeArea);
        sku.setSkuColor(skuColor);
        sku.setPackingSpecification(packingSpecification);
        sku.setStorageMethod(storageMethod);
        sku.setReserve(reserve != null ? reserve : 0);

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer id = skuService.createSku(context, sku, skuTypeId, user.getId());
            response.put("success", id != null && id > 0);
            response.put("message", id != null && id > 0 ? "创建成功" : "创建失败");
            response.put("id", id);
        } catch (Exception e) {
            logger.error("Create sku failed", e);
            response.put("success", false);
            response.put("message", "创建失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 更新商品
     */
    @RequestMapping(value = "/{id}/update", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String update(
            @PathVariable("id") Integer id,
            @RequestParam(value = "skuName", required = false) String skuName,
            @RequestParam(value = "skuTypeId", required = false) Integer skuTypeId,
            @RequestParam(value = "skuCode", required = false) String skuCode,
            @RequestParam(value = "offerPrice", required = false) Integer offerPrice,
            @RequestParam(value = "skuUnit", required = false) String skuUnit,
            @RequestParam(value = "skuDesc", required = false) String skuDesc,
            @RequestParam(value = "skuProducer", required = false) String skuProducer,
            @RequestParam(value = "skuShelfLife", required = false) Integer skuShelfLife,
            @RequestParam(value = "skuMadeArea", required = false) String skuMadeArea,
            @RequestParam(value = "skuColor", required = false) String skuColor,
            @RequestParam(value = "packingSpecification", required = false) String packingSpecification,
            @RequestParam(value = "storageMethod", required = false) String storageMethod,
            @RequestParam(value = "reserve", required = false) Integer reserve) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtSku existing = skuService.getTMtSku(context, id);
        if (existing == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("success", false);
            err.put("message", "商品不存在");
            return JSON.toJSONString(err);
        }
        if (skuName != null) existing.setSkuName(skuName);
        if (skuCode != null) existing.setSkuCode(skuCode);
        if (offerPrice != null) existing.setOfferPrice(offerPrice);
        if (skuUnit != null) existing.setSkuUnit(skuUnit);
        if (skuDesc != null) existing.setSkuDesc(skuDesc);
        if (skuProducer != null) existing.setSkuProducer(skuProducer);
        if (skuShelfLife != null) existing.setSkuShelfLife(skuShelfLife);
        if (skuMadeArea != null) existing.setSkuMadeArea(skuMadeArea);
        if (skuColor != null) existing.setSkuColor(skuColor);
        if (packingSpecification != null) existing.setPackingSpecification(packingSpecification);
        if (storageMethod != null) existing.setStorageMethod(storageMethod);
        if (reserve != null) existing.setReserve(reserve);

        // skuTypeId 未传时沿用原类型
        Integer typeId = skuTypeId;
        if (typeId == null && existing.getSkuType() != null) {
            typeId = Cayenne.intPKForObject(existing.getSkuType());
        }

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            Integer result = skuService.updateSku(context, existing, typeId, user.getId());
            response.put("success", result != null && result > 0);
            response.put("message", result != null && result > 0 ? "更新成功" : "更新失败");
        } catch (Exception e) {
            logger.error("Update sku failed", e);
            response.put("success", false);
            response.put("message", "更新失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    /**
     * 上传商品主图（PNG，大小限制由配置决定）
     */
    @RequestMapping(value = "/{id}/image", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String uploadImage(@PathVariable("id") Integer id,
                              @RequestParam("file") MultipartFile file) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        Map<String, Object> response = new LinkedHashMap<>();
        try {
            if (file == null || file.isEmpty()) {
                response.put("success", false);
                response.put("message", "请选择图片文件");
                return JSON.toJSONString(response);
            }
            String originalName = file.getOriginalFilename();
            if (originalName == null || !originalName.toLowerCase().endsWith(".png")) {
                response.put("success", false);
                response.put("message", "只支持 PNG 格式");
                return JSON.toJSONString(response);
            }
            // 文件名：时间戳_随机.png（与现有 JSP 上传逻辑一致）
            String newFileName = System.currentTimeMillis() + "_" + RandomUtils.nextLong(1000, 9999L) + ".png";

            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            TMtSku sku = skuService.getTMtSku(context, id);
            if (sku == null) {
                response.put("success", false);
                response.put("message", "商品不存在");
                return JSON.toJSONString(response);
            }

            // 查询旧主图并删除
            TMtSkuImg oldImg = skuService.findBySkuId(context, id, TMtSkuImg.IMG_FIRST_URL, TMtSkuImg.IMG_SOURCE_VMS);
            String oldFileName = null;
            if (oldImg != null && TMtSkuImg.IMG_FIRST_URL == oldImg.getImgIndex()) {
                oldFileName = oldImg.getImgUrl();
            }

            if (!AliyunOssUtils.upload(newFileName, file.getInputStream())) {
                response.put("success", false);
                response.put("message", "图片上传失败");
                return JSON.toJSONString(response);
            }
            if (!StringUtil.isEmpty(oldFileName)) {
                AliyunOssUtils.delete(oldFileName);
            }

            Boolean ok = skuService.changeSkuNew(context, id, newFileName,
                    TMtSkuImg.IMG_FIRST_URL, TMtSkuImg.IMG_SOURCE_VMS, TMtSkuImg.NO_IMG_MASTER_URL);
            response.put("success", ok != null && ok);
            response.put("message", ok != null && ok ? "上传成功" : "上传失败");
            response.put("imgUrl", newFileName);
        } catch (Exception e) {
            logger.error("Upload sku image failed", e);
            response.put("success", false);
            response.put("message", "上传失败：" + e.getMessage());
        }
        return JSON.toJSONString(response);
    }

    private Map<String, Object> convertSkuToMap(TMtSku sku) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", sku.getId());
        row.put("skuName", sku.getSkuName());
        row.put("skuCode", sku.getSkuCode());
        row.put("offerPrice", sku.getOfferPrice());
        row.put("skuUnit", sku.getSkuUnit());
        row.put("skuDesc", sku.getSkuDesc());
        row.put("skuProducer", sku.getSkuProducer());
        row.put("skuShelfLife", sku.getSkuShelfLife());
        row.put("skuMadeArea", sku.getSkuMadeArea());
        row.put("skuColor", sku.getSkuColor());
        row.put("packingSpecification", sku.getPackingSpecification());
        row.put("storageMethod", sku.getStorageMethod());
        row.put("reserve", sku.getReserve());
        // getImgUrl() 已是完整 URL（imgUrlPrefix + 相对路径）
        row.put("imgUrl", sku.getImgUrl());
        row.put("imgName", sku.getImgName());
        if (sku.getSkuType() != null) {
            row.put("skuTypeId", Cayenne.intPKForObject(sku.getSkuType()));
            row.put("skuTypeName", sku.getSkuType().getTypeName());
        }
        row.put("ctime", sku.getCtime() != null ? sku.getCtime().toInstant().toString() : null);
        return row;
    }
}
