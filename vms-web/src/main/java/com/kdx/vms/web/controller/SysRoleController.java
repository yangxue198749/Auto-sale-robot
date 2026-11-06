package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISysRoleService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by liyang on 2016/12/14.
 */
@Controller
@RequestMapping(value = "role")
public class SysRoleController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private ISysRoleService sysRoleService;

    @RequestMapping("list")
    public String list() {
        return "role/role_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request,
                           Integer pageSize,
                           Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        Integer totalSize = 1000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            TSysUser currUser = token.getSysUser();

            String selectType = request.getParameter("selectType");
            logger.info("selectType:" + selectType);

            String roleName = null;
            if (null != selectType) {
                switch (selectType) {
                    case "1":
                        roleName = request.getParameter("selectValue");
                }
            }
            logger.info("roleName:" + roleName);
            List list = sysRoleService.getSysRoleCompany(currUser.getCompany(), roleName, pageSize);

            if (null != list) {
                totalSize = list.size();
                Integer start = (pageNo - 1) * pageSize;
                Integer end = pageNo * pageSize;
                start = start > list.size() ? list.size() : start;
                end = end > list.size() ? list.size() : end;
                request.setAttribute("roleList", list.subList(start, end));
            }
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "role/table/role_table";
    }

    @RequestMapping("addPage")
    public String addPage() {
        return "role/dialog/role_add_dialog";
    }

    @ResponseBody
    @RequestMapping("addRole")
    public Map<String, Object> addRole(String roleName, String roleDesc) {
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser currUser = token.getSysUser();
            SystemCode systemCode = sysRoleService.addRole(roleName, roleDesc, currUser);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

    @RequestMapping("editPage")
    public String editPage(HttpServletRequest request, Integer ids) {
        try {
            if (IntegerUtils.isPositiveInteger(ids)) {
                SSOToken token = TokenUtil.getToken();
                ObjectContext context = token.getObjectContext();
                TSysRole sysRole = Cayenne.objectForPK(context, TSysRole.class, ids);
                request.setAttribute("role", sysRole);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "role/dialog/role_edit_dialog";
    }

    @ResponseBody
    @RequestMapping("editRole")
    public Map<String, Object> editRole(Integer roleId, String roleName, String roleDesc) {
        logger.info("roleId:" + roleId + ", roleName:" + roleName + ", roleDesc");
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser currUser = token.getSysUser();
            SystemCode systemCode = sysRoleService.editRole(roleId, roleName, roleDesc, currUser);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

    @RequestMapping("editElementPage")
    public String editElementPage(HttpServletRequest request, Integer ids) {
        logger.info("ids:" + ids);
        try {
            if (IntegerUtils.isPositiveInteger(ids)) {
                SSOToken token = TokenUtil.getToken();
                ObjectContext context = token.getObjectContext();
                TSysRole sysRole = Cayenne.objectForPK(context, TSysRole.class, ids);
                if (null != sysRole) {
                    request.setAttribute("roleId", sysRole.getId());
                    request.setAttribute("edit", TSysRole.ROLE_TYPE_COMPANY == sysRole.getRoleType() || token.getSysUser().getId() == 1 ? 1 : 2);
                }
//                List elementList = sysRoleService.getElementAll(sysRole);
                List elementList = sysRoleService.getElementAllBySystemAdmin(sysRole);
                request.setAttribute("elementList", elementList);
                request.setAttribute("elementIds", sysRoleService.getElementIdsForRole(sysRole));
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "role/dialog/role_edit_element_dialog";
    }

    @ResponseBody
    @RequestMapping("editElement")
    public Map<String, Object> editElement(HttpServletRequest request, Integer roleId) {
        logger.info("roleId:" + roleId);
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();

            String idStr = request.getParameter("elementIds");
            logger.info(idStr);
            List<Integer> elementIds = new ArrayList<>();
            if (!StringUtil.isEmpty(idStr)) {
                elementIds = Arrays.stream(idStr.split(",")).map(Integer::parseInt).collect(Collectors.toList());
            }
            if (sysRoleService.bindElement(Cayenne.objectForPK(context, TSysRole.class, roleId), elementIds, token.getSysUser())) {
                return SystemCode.SUCCESS.toMap();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @ResponseBody
    @RequestMapping("deleteRole")
    public Map<String, Object> deleteRole(Integer[] ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            List<Integer> list = Arrays.asList(ids);
            SystemCode systemCode = sysRoleService.deleteRoles(context, list);
            return systemCode.toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }

}
