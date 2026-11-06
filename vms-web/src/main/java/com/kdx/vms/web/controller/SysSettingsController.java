package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtNode;
import com.kdx.vms.model.bean.TSysSettings;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysSettingsService;
import com.kdx.vms.utils.SysSettingsCache;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/5/22.
 */
@Controller
@RequestMapping(value = "settings")
public class SysSettingsController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    static String PATH = "settings";
    @Autowired
    ISysSettingsService sysSettingsService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        try {
            List typeList = SysSettingsCache.getRoot().getChild();
            request.setAttribute("typeList", typeList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "sysSettings/setting_list";
    }

    @RequestMapping("type/table")
    public String typeTable(HttpServletRequest request, Integer pageSize, Integer pageNo, Integer pid, String searchValue,
                        @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        try {
            String url = PATH + "/type/table";
            SSOToken token = TokenUtil.getToken();
            if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
            if (pageNo == null) pageNo = 1;
            if (pageSize == null) pageSize = 10;
            List totalList = sysSettingsService.findAllSettings(token.getObjectContext(), pageSize, searchValue);
            Integer totalSize = totalList.size();
            List subList = PageUtil.formatList(pageNo, pageSize, totalList);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "sysSettings/table/type_table";
    }

    @RequestMapping("setting/table")
    public String settingTable(HttpServletRequest request, Integer pageSize, Integer pageNo, Integer pid,
                        @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        try {
            String url = PATH + "/setting/table";
            SSOToken token = TokenUtil.getToken();
            if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
            if (pageNo == null) pageNo = 1;
            if (pageSize == null) pageSize = 10;
            if (pid == null){
                return "sysSettings/table/settings_table";
            }
                TSysSettings parent = Cayenne.objectForPK(token.getObjectContext(),TSysSettings.class,pid);
            List totalList = parent.getChild();
            Integer totalSize = totalList.size();
            List subList = PageUtil.formatList(pageNo, pageSize, totalList);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "sysSettings/table/settings_table";
    }

    @RequestMapping("type/add")
    public String typeAdd(HttpServletRequest request) {
        return "sysSettings/dialog/type_update";
    }

    @RequestMapping("setting/add")
    public String settingAdd(HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysSettings root = Cayenne.objectForPK(token.getObjectContext(),TSysSettings.class,TSysSettings.ROOT_ID);
        List typeList = root.getChild();
        request.setAttribute("typeList", typeList);
        return "sysSettings/dialog/setting_update";
    }

    @RequestMapping("setting/update")
    public String settingUpdate(HttpServletRequest request, Integer ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TSysSettings sysSetting = Cayenne.objectForPK(context, TSysSettings.class, ids);
            TSysSettings root = Cayenne.objectForPK(token.getObjectContext(),TSysSettings.class,TSysSettings.ROOT_ID);
            List typeList = root.getChild();
            request.setAttribute("typeList", typeList);
            request.setAttribute("pid", sysSetting.getParent().getId());
            request.setAttribute("sysSetting", sysSetting);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "sysSettings/dialog/setting_update";
    }

    @RequestMapping("setting/delete")
    @ResponseBody
    public Map<String, Object> settingDelete(Integer[] ids, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        return sysSettingsService.deleteSetting(currentUser, ids);
    }

    @RequestMapping("save")
    @ResponseBody
    public Map<String, Object> save(HttpServletRequest request, Integer pid, String name, String key, String value) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        String sysSettingId = request.getParameter("sysSettingId");
        if (StringUtil.isEmpty(sysSettingId)) {
            return sysSettingsService.addSetting(currentUser, key, value, pid, name);
        } else {
            return sysSettingsService.updateSetting(currentUser, key, value, pid, name, Integer.parseInt(sysSettingId));
        }
    }


}
