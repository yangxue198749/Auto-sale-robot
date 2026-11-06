package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtTheme;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtThemeService;
import com.kdx.vms.utils.SysSettingsCache;
import com.kdx.vms.web.log.AddLog;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by liyang on 2017/5/3.
 */
@Controller
@RequestMapping(value = "theme")
public class ThemeController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IMtThemeService mtThemeService;

    @RequestMapping(value = "list")
    public String list() {
        return "theme/theme_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                           Integer pageSize,
                           Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        LOGGER.info("[listPage]");
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        try {
            // 获取session中当前登录用户
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();
            // 获取页面参数
            String themeName = request.getParameter("themeName");
            LOGGER.debug("themeName:" + themeName);
            List list = mtThemeService.getThemesByName(sysUser, themeName, pageSize);
            LOGGER.info("list:" + list.size());
            // 分页返回
            Integer start = (pageNo - 1) * pageSize;
            Integer end = pageNo * pageSize;
            start = start > list.size() ? list.size() : start;
            end = end > list.size() ? list.size() : end;
            request.setAttribute("themeList", list.subList(start, end));
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", list.size());

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return "theme/table/theme_table";
    }

    @RequestMapping("addPage")
    public String addPage() {
        LOGGER.info("[addPage]");
        return "theme/dialog/theme_add_dialog";
    }

    @ResponseBody
    @RequestMapping("addTheme")
    @AddLog(logDesc = "", logOperation = TSysLog.INSERT, syslogEnum = SyslogEnum.MT_THEME)
    public Map<String, Object> addTheme(String themeName, String themeDesc, HttpServletRequest request) {
        try {
            SSOToken token       = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();

            MultipartFile themeZip     = ((MultipartHttpServletRequest) request).getFile("themeZip");
            MultipartFile themePreview = ((MultipartHttpServletRequest) request).getFile("themePreview");
            String maxZipSize = SysSettingsCache.getSetting(TSysSettings.THEME_MAX_ZIP_SIZE,TSysSettings.UPLOAD_TYPE,"10");
            String maxPreviewSize = SysSettingsCache.getSetting(TSysSettings.THEME_MAX_PREVIEW_SIZE,TSysSettings.UPLOAD_TYPE,"3");
            if (!validation(themeZip, ".zip", StringUtil.getDoubleValue(maxZipSize,10d))) {
                Map<String,Object> map = ReturnCode.ZIP_DENY.toMap();
                map.put("message","上传失败，皮肤包大小不能超过"+maxZipSize+"MB");
                return map;
            }
            if (!validation(themePreview, ".png", StringUtil.getDoubleValue(maxPreviewSize,3d))) {
                Map<String,Object> map = ReturnCode.VIEW_DENY.toMap();
                map.put("message","上传失败，预览图大小不能超过"+maxPreviewSize+"MB");
                return map;
            }

            String fileName = "theme/" + System.currentTimeMillis() + "_" + RandomUtils.nextLong(1000, 9999L);
            String zipFileName= fileName + ".zip";
            String pngFileName= fileName + ".png";
            if (AliyunOssUtils.upload(zipFileName, themeZip.getInputStream())) {
                LOGGER.info("zip上传成功。");
                if (AliyunOssUtils.upload(pngFileName, themePreview.getInputStream())) {
                    LOGGER.info("png上传成功。");
                    SystemCode code = mtThemeService.createTheme(currentUser, themeName, themeDesc, zipFileName, pngFileName, themeZip.getSize(), Md5Util.md5ByInputStream(themeZip.getInputStream()));
                    if (!code.equals(SystemCode.SUCCESS)) {
                        LOGGER.error("保存数据库失败，回滚文件");
                        AliyunOssUtils.delete(zipFileName);
                        AliyunOssUtils.delete(pngFileName);
                    } else {
                        return SystemCode.SUCCESS.toMap();
                    }
                } else {
                    LOGGER.error("png上传失败，回滚文件");
                    AliyunOssUtils.delete(zipFileName);
                }

            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @ResponseBody
    @RequestMapping("deleteTheme")
    public Map<String, Object> deleteTheme(Integer[] ids) {
        try {
            LOGGER.info("ids:" + Arrays.toString(ids));
            SSOToken token = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            SystemCode systemCode = mtThemeService.deleteThemes(currentUser, Arrays.asList(ids));
            return systemCode.toMap();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

    private static String imageUrl = (String) PropertiesLoader.map.get("imgUrlPrefix");
    private static String defaultImage = imageUrl + "default.png";

    @RequestMapping("preview")
    public String preview(HttpServletRequest request, Integer themeId) {
        try {
            SSOToken token       = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            TMtTheme theme = Cayenne.objectForPK(currentUser.getObjectContext(), TMtTheme.class, themeId);
            if(StringUtil.isEmpty(theme.getPreviewUrls())){
                request.setAttribute("imgUrl", defaultImage);
		    } else {
                request.setAttribute("imgUrl", imageUrl + theme.getPreviewUrls());
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return "theme/dialog/theme_preview_dialog";
    }


    private boolean validation(MultipartFile multipartFile, String suffix, Double size) {
        try {
            if (null == multipartFile) throw new NullPointerException("multipartFile is null.");
            if (StringUtil.isEmpty(suffix)) throw new IllegalArgumentException("suffix illegal.");
            if (null == size) throw new IllegalArgumentException("size illegal.");
            if (!multipartFile.getOriginalFilename().toLowerCase().endsWith(suffix)) {
                LOGGER.info("文件后缀错误.");
                return false;
            }
            if (".png".equalsIgnoreCase(suffix)) {
                LOGGER.info("png文件，通过文件流校验是否是图片文件");
                if (!ImageUtil.isImage(multipartFile.getInputStream())) {
                    LOGGER.info("不是图片文件");
                    return false;
                }
            }
            LOGGER.info("校验文件大小[" + multipartFile.getSize() + "] 超过限制大小 [" + size * 1024 * 1024 + "]");
            if (multipartFile.getSize() > size * 1024 * 1024) {
                return false;
            }
            return true;
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage());
        }
        return false;
    }

}
