package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.commons.util.SyslogEnum;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TSysLog;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtUpgradePackageService;
import com.kdx.vms.web.log.AddLog;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Created by liyang on 2017/6/22.
 */
@Controller
@RequestMapping(value = "upgradePackage")
public class UpgradePackageController {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Resource IMtUpgradePackageService mtUpgradePackageService;

    @RequestMapping(value = "list")
    public String list() {
        return "upgradePackage/upgradePackage_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                           Integer type,
                           String version,
                           Integer pageSize,
                           Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        LOGGER.info("[listPage] type:" + type + ", version:" + version);
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        try {
            // 获取session中当前登录用户
            SSOToken token = TokenUtil.getToken();
            TSysUser sysUser = token.getSysUser();

            List list = mtUpgradePackageService.getUpgradePackage(sysUser, type, version, pageSize);
            LOGGER.info("find data:" + list.size());
            // 分页返回
            Integer start = (pageNo - 1) * pageSize;
            Integer end = pageNo * pageSize;
            start = start > list.size() ? list.size() : start;
            end = end > list.size() ? list.size() : end;
            request.setAttribute("upgradePackageList", list.subList(start, end));
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", list.size());

        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return "upgradePackage/table/upgradePackage_table";
    }

    @RequestMapping("addPage")
    public String addPage() {
        LOGGER.info("[addPage]");
        return "upgradePackage/dialog/upgradePackage_add_dialog";
    }

    @ResponseBody
    @RequestMapping("addUpgradePackage")
    @AddLog(logDesc = "", logOperation = TSysLog.INSERT, syslogEnum = SyslogEnum.MT_UPGRADE_PACKAGE)
    public Map<String, Object> addUpgradePackage(Integer programType, String version, HttpServletRequest request) {
        try {
            SSOToken token       = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();

            MultipartFile upgradePackageZip = ((MultipartHttpServletRequest) request).getFile("upgradePackageZip");

            String fileName = "upgradePackage/" + System.currentTimeMillis() + "_" + RandomUtils.nextLong(1000, 9999L);
            String zipFileName= fileName + ".zip";
            if (AliyunOssUtils.upload(zipFileName, upgradePackageZip.getInputStream())) {
                LOGGER.info("zip上传成功。");
                SystemCode code = mtUpgradePackageService.addUpgradePackage(currentUser, programType, version, zipFileName, "", Md5Util.md5ByInputStream(upgradePackageZip.getInputStream()), upgradePackageZip.getSize());
                if (!code.equals(SystemCode.SUCCESS)) {
                    LOGGER.info("保存数据库失败，回滚文件(从OSS删除)");
                    AliyunOssUtils.delete(zipFileName);
                } else {
                    return SystemCode.SUCCESS.toMap();
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @ResponseBody
    @RequestMapping("disable")
    public Map<String, Object> disable(Integer packageId) {
        try {
            LOGGER.info("packageId:" + packageId);
            SSOToken token = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            return mtUpgradePackageService
                    .editUpgradePackage(currentUser, packageId)
                    .toMap();
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

}
