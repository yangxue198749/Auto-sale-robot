package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserRole;
import com.kdx.vms.service.ISysRoleService;
import com.kdx.vms.service.ISysUserService;
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

import java.util.*;

/**
 *
 * Created by liyang on 16-12-7.
 */
@Controller
@RequestMapping(value = "user")
public class SysUserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource() private ISysUserService sysUserService;
    @Resource() private ISysRoleService sysRoleService;

    @RequestMapping("list")
    public String list() {
        return "user/user_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, @CookieValue(value = "pageSize", required  = false) Integer cookiePageSize){
        logger.info("[listPage]");
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null)   pageNo = 1;
        if (pageSize == null) pageSize = 10;
        String url = "listPage";
        Integer totalSize = 1000;
        try {
            // 从缓存中得到当前用户TSysUser
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            String selectType      = request.getParameter("selectType");
            logger.info("selectType:" + selectType);
            String selectText      = request.getParameter("selectText");
            logger.info("selectText:" + selectText);

            List list = sysUserService.getSysUserForCompany(context, selectType, selectText, token.getSysUser().getCompany(), pageSize);

            logger.info("list:" + list);
            if (null != list) {
                totalSize = list.size();
                Integer start = (pageNo - 1) * pageSize;
                Integer end = pageNo * pageSize;
                start = start > list.size() ? list.size() : start;
                end = end > list.size() ? list.size() : end;
                logger.info("start:" + start + ", end:" + end);
                request.setAttribute("userList", list.subList(start, end));
            }
            request.setAttribute("url"      , url);
            request.setAttribute("pageSize" , pageSize);
            request.setAttribute("pageNo"   , pageNo);
            request.setAttribute("totalSize", totalSize);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "user/table/user_table";
    }

    @RequestMapping("resetPw")
    public String resetPw(HttpServletRequest request, Integer ids) {
        try {
            SSOToken token       = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            String message;
            if (sysUserService.resetPassword(ids, currentUser)){
                message = "重置密码成功";
            } else {
                message = "重置密码失败";
            }
            request.setAttribute("message", message);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "user/dialog/prompt";
    }

    @RequestMapping("editPage")
    public String editPage(HttpServletRequest request, Integer ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser currUser = token.getSysUser();
            if (IntegerUtils.isPositiveInteger(ids)) {
                TSysUser sysUser = Cayenne.objectForPK(currUser.getObjectContext(), TSysUser.class, ids);
                logger.info("sysUser:" + sysUser);
                request.setAttribute("userObj", sysUser);
                request.setAttribute("companyObj", sysUser.getCompany());
                StringBuilder builder = new StringBuilder();
                List<TSysUserRole> sysUserRoleList = sysUser.getUserRole();
                if (null != sysUserRoleList) {
                    for (TSysUserRole sysUserRole : sysUserRoleList) {
                        TSysRole sysRole = sysUserRole.getRole();
                        if (null != sysRole) {
                            if (builder.length() != 0) builder.append(",");
                            builder.append(sysRole.getId());
                        }
                    }
                }
                request.setAttribute("roleIdStr" , builder.toString());
            }else{
            	 request.setAttribute("companyObj", currUser.getCompany());
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "user/dialog/user_edit_dialog";
    }

    @RequestMapping("editRolePage")
    public String editRolePage(HttpServletRequest request, Integer userId) {
        try {
            SSOToken token = TokenUtil.getToken();
            TSysUser currUser = token.getSysUser();

            if (IntegerUtils.isPositiveInteger(userId)) {
                TSysUser sysUser = Cayenne.objectForPK(currUser.getObjectContext(), TSysUser.class, userId);
                List<TSysUserRole> sysUserRoleList = sysUser.getUserRole();
                List<TSysRole> sysRoleList = new ArrayList<>();
                if (null != sysUserRoleList) {
                    for (TSysUserRole sysUserRole : sysUserRoleList) {
                        TSysRole sysRole = sysUserRole.getRole();
                        if (null != sysRole) sysRoleList.add(sysRole);
                    }
                }
                request.setAttribute("roleList" , sysRoleList);
                List roleList = sysRoleService.getSysRolByCompany(sysUser.getCompany());
                request.setAttribute("roleAllList" , roleList);
            }else{
            	  List roleList = sysRoleService.getSysRolByCompany(token.getSysUser().getCompany());
                  request.setAttribute("roleAllList" , roleList);
            }


        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "user/dialog/user_edit_role_dialog";
    }

    @ResponseBody
    @RequestMapping("editUser")
    public Map<String, Object> editUser(Integer userId,String userAccount, String userName, String userPhone, Integer companyId, String roleIdStr) {
        try {
            SSOToken token       = TokenUtil.getToken();
            TSysUser currentUser = token.getSysUser();
            List<Integer> roleIdList = new ArrayList<>();
            if (!StringUtil.isEmpty(roleIdStr)) {
                String[] roleIdStrArray = roleIdStr.split(",");
                for (String roleId : roleIdStrArray)
                    roleIdList.add(Integer.parseInt(roleId));
            }

            if (IntegerUtils.isPositiveInteger(userId)) { // 新建
                return sysUserService.updateUser(
                        userId,
                        userName,
                        userPhone,
                        companyId,
                        roleIdList,
                        currentUser).toMap();
            } else { // 更新
                return sysUserService.createUser(
                        userAccount,
                        userName,
                        userPhone,
                        companyId,
                        roleIdList,
                        currentUser).toMap();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return SystemCode.FAIL.toMap();
    }

    @ResponseBody
    @RequestMapping("deleteUser")
    public Map<String, Object> deleteUser(HttpServletRequest request, Integer[] ids) {
        try {
            SSOToken token = TokenUtil.getToken();
            return sysUserService.deleteUser(Arrays.asList(ids), token.getSysUser()).toMap();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return SystemCode.FAIL.toMap();
        }
    }
    @ResponseBody
    @RequestMapping("getUserByLevel")
    public Map<String, Object> getUserByLevel(Integer level,Integer defaultUserId) {
    	Map<String, Object> map = new HashMap<>();
    	try {
    		SSOToken token = TokenUtil.getToken();
    		String html = sysUserService.findSysUsersByRoleLevelForCompany(token.getObjectContext(), level, token.getSysUser().getCompany().getId(), defaultUserId);
    		map.put("html", html);
    	} catch (Exception e) {
    		logger.error(e.getLocalizedMessage(), e);
    	}
    	return map;
    }
    @ResponseBody
    @RequestMapping("getUserByCompany")
    public Map<String, Object> getUserByCompany() {
    	Map<String, Object> map = new HashMap<>();
    	try {
    		SSOToken token = TokenUtil.getToken();
    		String html = sysUserService.findSysUsersByCompany(token.getSysUser().getCompany());
    		map.put("html", html);
    	} catch (Exception e) {
    		logger.error(e.getLocalizedMessage(), e);
    	}
    	return map;
    }

}
