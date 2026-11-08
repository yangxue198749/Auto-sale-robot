package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.ImageUtil;
import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtTheme;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtThemeService;
import com.kdx.vms.utils.SysSettingsCache;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 皮肤策略（主题包管理）REST API
 */
@Controller
@RequestMapping("/vms/api/theme")
public class ThemeApiController {

    private static final Logger logger = LoggerFactory.getLogger(ThemeApiController.class);
    private static final int MAX_QUERY = 10000;

    @Resource
    private IMtThemeService mtThemeService;

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
     * 皮肤策略列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String list(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "themeName", required = false) String themeName) {

        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (page < 1) page = 1;
        if (pageSize < 1 || pageSize > 100) pageSize = 10;

        List rawList = mtThemeService.getThemesByName(user, themeName, MAX_QUERY);
        int total = rawList == null ? 0 : rawList.size();
        int from = Math.min((page - 1) * pageSize, total);
        int to = Math.min(from + pageSize, total);

        String prefix = (String) PropertiesLoader.map.get("imgUrlPrefix");
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = from; i < to; i++) {
            Object obj = rawList.get(i);
            if (obj instanceof TMtTheme) {
                TMtTheme t = (TMtTheme) obj;
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("id", Cayenne.intPKForObject(t));
                row.put("themeName", t.getThemeName());
                row.put("themeDesc", t.getThemeDesc());
                row.put("themeUrl", t.getThemeUrl());
                row.put("themeSize", t.getThemeSize());
                row.put("previewUrls", t.getPreviewUrls());
                row.put("previewFullUrl", StringUtil.isEmpty(t.getPreviewUrls())
                        ? prefix + "default.png" : prefix + t.getPreviewUrls());
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
     * 上传皮肤包与预览图，新建主题
     */
    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String create(HttpServletRequest request) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (!(request instanceof MultipartHttpServletRequest)) {
            return errorJson("请使用表单上传皮肤包");
        }

        String themeName = request.getParameter("themeName");
        String themeDesc = request.getParameter("themeDesc");
        if (StringUtil.isEmpty(themeName)) return errorJson("请填写皮肤名称");

        try {
            MultipartHttpServletRequest multipart = (MultipartHttpServletRequest) request;
            MultipartFile themeZip = multipart.getFile("themeZip");
            MultipartFile themePreview = multipart.getFile("themePreview");

            String maxZipSize = SysSettingsCache.getSetting(TSysSettings.THEME_MAX_ZIP_SIZE, TSysSettings.UPLOAD_TYPE, "10");
            String maxPreviewSize = SysSettingsCache.getSetting(TSysSettings.THEME_MAX_PREVIEW_SIZE, TSysSettings.UPLOAD_TYPE, "3");
            if (!validation(themeZip, ".zip", StringUtil.getDoubleValue(maxZipSize, 10d))) {
                Map<String, Object> map = ReturnCode.ZIP_DENY.toMap();
                map.put("message", "上传失败，皮肤包大小不能超过" + maxZipSize + "MB");
                return JSON.toJSONString(map);
            }
            if (!validation(themePreview, ".png", StringUtil.getDoubleValue(maxPreviewSize, 3d))) {
                Map<String, Object> map = ReturnCode.VIEW_DENY.toMap();
                map.put("message", "上传失败，预览图大小不能超过" + maxPreviewSize + "MB");
                return JSON.toJSONString(map);
            }

            String fileName = "theme/" + System.currentTimeMillis() + "_" + RandomUtils.nextLong(1000, 9999L);
            String zipFileName = fileName + ".zip";
            String pngFileName = fileName + ".png";

            if (!AliyunOssUtils.upload(zipFileName, themeZip.getInputStream())) {
                return errorJson("皮肤包上传失败");
            }
            if (!AliyunOssUtils.upload(pngFileName, themePreview.getInputStream())) {
                AliyunOssUtils.delete(zipFileName);
                return errorJson("预览图上传失败");
            }
            SystemCode code = mtThemeService.createTheme(user, themeName, themeDesc, zipFileName, pngFileName,
                    themeZip.getSize(), Md5Util.md5ByInputStream(themeZip.getInputStream()));
            if (!code.equals(SystemCode.SUCCESS)) {
                AliyunOssUtils.delete(zipFileName);
                AliyunOssUtils.delete(pngFileName);
                return errorJson("保存皮肤信息失败");
            }
            return JSON.toJSONString(SystemCode.SUCCESS.toMap());
        } catch (Exception e) {
            logger.error("Create theme failed", e);
            return errorJson("新建失败：" + e.getMessage());
        }
    }

    /**
     * 删除皮肤策略
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String delete(@RequestParam(value = "ids", required = false) String idsCsv) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");
        if (StringUtil.isEmpty(idsCsv)) return errorJson("请选择要删除的皮肤");

        try {
            return JSON.toJSONString(mtThemeService.deleteThemes(user, Arrays.asList(parseIds(idsCsv))).toMap());
        } catch (Exception e) {
            logger.error("Delete theme failed", e);
            return errorJson("删除失败：" + e.getMessage());
        }
    }

    /**
     * 皮肤预览图
     */
    @RequestMapping(value = "/{themeId}/preview", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String preview(@PathVariable("themeId") Integer themeId) {
        requireAuth();
        TSysUser user = currentUser();
        if (user == null) throw new UnauthorizedException("用户信息获取失败");

        TMtTheme theme = Cayenne.objectForPK(user.getObjectContext(), TMtTheme.class, themeId);
        if (theme == null) return errorJson("皮肤不存在");

        String prefix = (String) PropertiesLoader.map.get("imgUrlPrefix");
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", Cayenne.intPKForObject(theme));
        result.put("themeName", theme.getThemeName());
        result.put("themeDesc", theme.getThemeDesc());
        result.put("previewUrls", theme.getPreviewUrls());
        result.put("previewFullUrl", StringUtil.isEmpty(theme.getPreviewUrls())
                ? prefix + "default.png" : prefix + theme.getPreviewUrls());
        return JSON.toJSONString(result);
    }

    private boolean validation(MultipartFile multipartFile, String suffix, Double size) {
        try {
            if (null == multipartFile) throw new NullPointerException("multipartFile is null.");
            if (StringUtil.isEmpty(suffix)) throw new IllegalArgumentException("suffix illegal.");
            if (null == size) throw new IllegalArgumentException("size illegal.");
            String name = multipartFile.getOriginalFilename() == null ? "" : multipartFile.getOriginalFilename().toLowerCase();
            if (!name.endsWith(suffix)) return false;
            if (".png".equalsIgnoreCase(suffix) && !ImageUtil.isImage(multipartFile.getInputStream())) {
                return false;
            }
            return multipartFile.getSize() <= size * 1024 * 1024;
        } catch (Exception e) {
            logger.error("Validation failed", e);
        }
        return false;
    }

    private static Integer[] parseIds(String csv) {
        String[] parts = csv.split(",");
        Integer[] ids = new Integer[parts.length];
        for (int i = 0; i < parts.length; i++) {
            ids[i] = Integer.parseInt(parts[i].trim());
        }
        return ids;
    }

    private static String errorJson(String message) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("success", false);
        err.put("type", "error");
        err.put("message", message);
        return JSON.toJSONString(err);
    }
}
