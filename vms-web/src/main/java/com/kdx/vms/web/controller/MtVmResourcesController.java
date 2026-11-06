package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.*;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtVmResourceService;
import com.kdx.vms.utils.SysSettingsCache;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangguodong on 2017/5/23.
 */
@Controller
@RequestMapping("mtVmResources")
public class MtVmResourcesController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IMtVmResourceService mtVmResourceService;

    /**
     * 主页面
     * @return
     */
    @RequestMapping("list")
    public String list(){

        return "mtVmResources/mtVmResource_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,String name){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        String url = "/mtVmResources/listPage";
        try {
            SSOToken token = TokenUtil.getToken();
            logger.info("-----token-----" + token);
            List<TMtVmResource> list = mtVmResourceService.findPage(token.getSysUser(),name,pageSize,TMtVmResource.MT_VM_SHORT_CURTS_RESOURCE_TYPE);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("mtVmResourceList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVmResources/page/mtVmResource_page";
    }
    @ResponseBody
    @RequestMapping("addMtVmResource")
    public Map<String, Object> addTheme(String mtVmResourceName, String mtVmResourceDesc, HttpServletRequest request) {
        try {
            SSOToken token       = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();

            MultipartFile mtVmResourcesZip     = ((MultipartHttpServletRequest) request).getFile("mtVmResourcesZip");
            String maxZipSize = SysSettingsCache.getSetting(TSysSettings.THEME_MAX_ZIP_SIZE,TSysSettings.UPLOAD_TYPE,"10");
            if (!validation(mtVmResourcesZip, ".zip", StringUtil.getIntValue(maxZipSize,10))) {
                Map<String,Object> map = ReturnCode.ZIP_DENY.toMap();
                map.put("message","上传失败，包大小不能超过"+maxZipSize+"MB");
                return map;
            }

            String fileName = "mtVmResources/" + System.currentTimeMillis();
            String zipFileName= fileName + ".zip";
            if (AliyunOssUtils.upload(zipFileName, mtVmResourcesZip.getInputStream())) {
                logger.info("zip上传成功。");
                SystemCode code = mtVmResourceService.createMtVmResource(currentUser,mtVmResourceName,mtVmResourceDesc,Md5Util.md5ByInputStream(mtVmResourcesZip.getInputStream()),mtVmResourcesZip.getSize(),zipFileName);
                if (!code.equals(SystemCode.SUCCESS)) {
                    logger.error("保存数据库失败，回滚文件");
                    AliyunOssUtils.delete(zipFileName);
                } else {
                    return SystemCode.SUCCESS.toMap();
                }

            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @ResponseBody
    @RequestMapping("deleteMtVmResources")
    public Map<String, Object> deleteTheme(Integer[] ids) {
        try {
            logger.info("ids:" + Arrays.toString(ids));
            SSOToken token = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            SystemCode systemCode = mtVmResourceService.delMtVmResources(currentUser, ids);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }
    private boolean validation(MultipartFile multipartFile, String suffix, Integer size) {
        try {
            if (null == multipartFile) throw new NullPointerException("multipartFile is null.");
            if (StringUtil.isEmpty(suffix)) throw new IllegalArgumentException("suffix illegal.");
            if (!IntegerUtils.isPositiveInteger(size)) throw new IllegalArgumentException("size illegal.");
            return multipartFile.getOriginalFilename().toLowerCase().endsWith(suffix)
                    && !(".png".equalsIgnoreCase(suffix) && !ImageUtil.isImage(multipartFile.getInputStream()))
                    && multipartFile.getSize() <= size * 1024 * 1024;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage());
        }
        return false;
    }

    @RequestMapping("addPage")
    public String addPage() {
        logger.info("[addPage]");
        return "mtVmResources/dialog/add_mt_vm_resources_dialog";
    }

}
