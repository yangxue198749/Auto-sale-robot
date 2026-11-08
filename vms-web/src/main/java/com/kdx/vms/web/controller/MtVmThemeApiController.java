package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ImageUtil;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtTheme;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TMtVmThemeEntrance;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtThemeService;
import com.kdx.vms.service.IMtVmResourceService;
import com.kdx.vms.service.ISysLogService;
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
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 设备皮肤配置 REST API
 */
@Controller
@RequestMapping("/vms/api/vmTheme")
public class MtVmThemeApiController {

    private static final Logger logger = LoggerFactory.getLogger(MtVmThemeApiController.class);

    @Resource
    private IMtThemeService mtThemeService;

    @Resource
    private IMtVmResourceService mtVmResourceService;

    @Resource
    private IMcsService mcsService;

    @Resource
    private ISysLogService sysLogService;

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
     * 皮肤主题列表（供设备绑定选择）
     */
    @RequestMapping(value = "/themes", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listThemes(@RequestParam(value = "themeName", required = false) String themeName) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List rawList = mtThemeService.getThemesByName(user, themeName, 100);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (rawList != null) {
            for (Object obj : rawList) {
                if (obj instanceof TMtTheme) {
                    TMtTheme t = (TMtTheme) obj;
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", Cayenne.intPKForObject(t));
                    row.put("themeName", t.getThemeName());
                    row.put("themeDesc", t.getThemeDesc());
                    row.put("themeUrl", t.getThemeUrl());
                    row.put("previewUrls", t.getPreviewUrls());
                    rows.add(row);
                }
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 入口跳转页资源列表（皮肤入口配置用）
     */
    @RequestMapping(value = "/resources", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listResources(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "pageSize", required = false, defaultValue = "50") Integer pageSize) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        List<TMtVmResource> list = mtVmResourceService.findPage(user, name, pageSize,
                TMtVmResource.MT_VM_SHORT_CURTS_RESOURCE_TYPE);

        List<Map<String, Object>> rows = new ArrayList<>();
        if (list != null) {
            for (TMtVmResource r : list) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", Cayenne.intPKForObject(r));
                row.put("resourceName", r.getResourceName());
                row.put("resourceDesc", r.getResourceDesc());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 设备当前入口配置
     */
    @RequestMapping(value = "/{vmId}/entrances", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String listEntrances(@PathVariable("vmId") Integer vmId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        ObjectContext context = user.getObjectContext();
        TMtVm vm = Cayenne.objectForPK(context, TMtVm.class, vmId);
        if (vm == null) {
            Map<String, Object> err = new LinkedHashMap<>();
            err.put("code", 404);
            err.put("message", "设备不存在");
            return JSON.toJSONString(err);
        }

        List<Map<String, Object>> rows = new ArrayList<>();
        List<TMtVmThemeEntrance> entrances = vm.getEntrances();
        if (entrances != null) {
            for (TMtVmThemeEntrance e : entrances) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", Cayenne.intPKForObject(e));
                row.put("entranceIndex", e.getEntranceIndex());
                row.put("resourceId", e.getResouseId());
                row.put("resourceName", e.getResouseName());
                row.put("imgUrl", e.getImgUrl());
                row.put("imgFullUrl", e.getSkuImgUrl());
                rows.add(row);
            }
        }
        return JSON.toJSONString(rows);
    }

    /**
     * 绑定皮肤主题到设备
     */
    @RequestMapping(value = "/bind", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String bindTheme(
            @RequestParam(value = "themeId", required = false) Integer themeId,
            @RequestParam(value = "vmIds", required = false) String vmIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (themeId == null || StringUtil.isEmpty(vmIdsCsv)) return errorJson("请选择皮肤和设备");

        try {
            return JSON.toJSONString(mtThemeService.bindTheme(user, parseIds(vmIdsCsv), themeId).toMap());
        } catch (Exception e) {
            logger.error("Bind theme failed", e);
            return errorJson("绑定失败：" + e.getMessage());
        }
    }

    /**
     * 设置皮肤版式（2=两页版式，3=三页版式）
     */
    @RequestMapping(value = "/format", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String setFormat(
            @RequestParam(value = "formatCode", required = false) Integer formatCode,
            @RequestParam(value = "vmIds", required = false) String vmIdsCsv) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (formatCode == null || StringUtil.isEmpty(vmIdsCsv)) return errorJson("请选择版式和设备");

        try {
            return JSON.toJSONString(mtThemeService.setFormat(user, parseIds(vmIdsCsv), formatCode).toMap());
        } catch (Exception e) {
            logger.error("Set format failed", e);
            return errorJson("设置版式失败：" + e.getMessage());
        }
    }

    /**
     * 保存皮肤入口配置（可同时上传 3 个入口图片，覆盖或清空）
     */
    @RequestMapping(value = "/configuration", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String saveConfiguration(HttpServletRequest request) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        try {
            String idsCsv = request.getParameter("vmIds");
            if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择设备");
            Integer[] ids = parseIds(idsCsv);

            Integer firstResourceId = parseOptionalInt(request.getParameter("firstResourceId"));
            Integer secondResourceId = parseOptionalInt(request.getParameter("secondResourceId"));
            Integer thirdResourceId = parseOptionalInt(request.getParameter("thirdResourceId"));

            Map<String, MultipartFile> files = collectFiles(request);
            MultipartFile[] ordered = new MultipartFile[]{
                    files.get("firstFile"), files.get("secondFile"), files.get("thirdFile")
            };
            Integer[] resourceIds = new Integer[]{firstResourceId, secondResourceId, thirdResourceId};
            int[] portals = new int[]{TMtVmThemeEntrance.FIRST_PORTAL,
                    TMtVmThemeEntrance.SECOND_PORTAL, TMtVmThemeEntrance.THIRD_PORTAL};

            ObjectContext context = user.getObjectContext();
            for (Integer vmId : ids) {
                for (int i = 0; i < portals.length; i++) {
                    MultipartFile file = ordered[i];
                    if (file != null && file.getSize() > 0) {
                        Map<String, Object> invalid = validateImgFile(file);
                        if (invalid != null) return JSON.toJSONString(invalid);

                        String newFileName = new Date().getTime() + "_"
                                + RandomUtils.nextLong(1000, 9999L)
                                + file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
                        if (!AliyunOssUtils.upload(newFileName, file.getInputStream())) {
                            logger.info("上传入口图片失败。");
                            return JSON.toJSONString(ReturnCode.CONTEXT_COMMIT_FAIL.toMap());
                        }
                        TMtVmThemeEntrance old = mtThemeService.findByVmId(context, portals[i], vmId);
                        if (old != null && !StringUtil.isEmpty(old.getImgUrl())) {
                            AliyunOssUtils.delete(old.getImgUrl());
                        }
                        mtThemeService.setConfiguration(context, resourceIds[i], vmId, newFileName, portals[i]);
                    } else if (resourceIds[i] != null) {
                        TMtVmThemeEntrance old = mtThemeService.findByVmId(context, portals[i], vmId);
                        mtThemeService.setConfiguration(context, resourceIds[i], vmId,
                                old != null ? old.getImgUrl() : null, portals[i]);
                    } else {
                        mtThemeService.delectConfiguration(context, vmId, portals[i]);
                    }
                }
            }
            context.commitChanges();

            String mcsMsg = mcsService.sendEntrance(context, Arrays.asList(ids), null);
            if (StringUtil.isEmpty(mcsMsg)) {
                sysLogService.saveSysLog("配置入口信息到售货机" + StringUtil.join(",", ids),
                        TSysLog.SAVE, SyslogEnum.MT_VM_CODE, user.getId(), user.getObjectContext());
                return JSON.toJSONString(ReturnCode.SUCCESS.toMap());
            }
            return JSON.toJSONString(ReturnCode.MCS_FAIL.toMap());
        } catch (Exception e) {
            logger.error("Save entrance configuration failed", e);
            return errorJson("保存入口配置失败：" + e.getMessage());
        }
    }

    private Map<String, MultipartFile> collectFiles(HttpServletRequest request) {
        Map<String, MultipartFile> files = new HashMap<>();
        if (request instanceof org.springframework.web.multipart.MultipartHttpServletRequest) {
            org.springframework.web.multipart.MultipartHttpServletRequest multipart =
                    (org.springframework.web.multipart.MultipartHttpServletRequest) request;
            for (String field : new String[]{"firstFile", "secondFile", "thirdFile"}) {
                MultipartFile f = multipart.getFile(field);
                if (f != null) files.put(field, f);
            }
        }
        return files;
    }

    private Map<String, Object> validateImgFile(MultipartFile imgFile) {
        try {
            int maxImageSize = 100 * 1024;
            String name = imgFile.getOriginalFilename() == null ? "" : imgFile.getOriginalFilename().toLowerCase();
            if (!name.endsWith(".png") && !name.endsWith(".jpg") && !name.endsWith(".jpeg")) {
                return ReturnCode.IMAGE_SUF_NEW.toMap();
            }
            if (!ImageUtil.isImage(imgFile.getInputStream())) {
                return ReturnCode.IMAGE_DENY.toMap();
            }
            if (imgFile.getSize() > maxImageSize) {
                Map<String, Object> map = ReturnCode.IMAGE_SIZE.toMap();
                map.put("message", "上传失败，上传文件不能大于" + 100 + "KB");
                return map;
            }
        } catch (Exception e) {
            logger.error("Validate entrance image failed", e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
        return null;
    }

    private static Integer parseOptionalInt(String value) {
        if (StringUtil.isEmpty(value)) return null;
        return Integer.parseInt(value.trim());
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }

    private Integer[] parseIds(String csv) {
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }
        return ids;
    }
}
