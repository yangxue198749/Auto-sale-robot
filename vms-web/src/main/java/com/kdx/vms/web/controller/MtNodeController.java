package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMtNodeService;
import com.kdx.vms.service.ISysUserService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.BusinessDistrictUtils;
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
 * Created by yangshuxi on 2016/11/29.
 */
@Controller
@RequestMapping(value = "mtNode")
public class MtNodeController {
    static String PATH = "mtNode";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMtNodeService mtNodeService;
    @Autowired
    private ISysUserService sysUserService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        List<TMtBusinessDistrict> list = BusinessDistrictUtils.getBusinessDistrictList();
        request.setAttribute("bdlist", list);
        return "mtNode/mt_node_list";
    }

    @RequestMapping("table")
    public String table(HttpServletRequest request, Integer pageSize, Integer pageNo, Integer province, Integer city, Integer area, Integer businessDistrictId, String searchType, String searchValue,
                        @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        List totalList = mtNodeService.findNodeList(currentUser, context, searchType, searchValue, province, city, area, businessDistrictId, pageSize);
        String url = PATH + "/table";
        Integer totalSize = totalList.size();
        List subList = PageUtil.formatList(pageNo, pageSize, totalList);
        try {
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtNode/table/mt_node_table";
    }

    @RequestMapping("save")
    @ResponseBody
    public Map<String, Object> save(TMtNode mtNode, HttpServletRequest request, Integer partnerId,Integer deliveryId,Integer repairId) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        String nodeId = request.getParameter("nodeId");
        if (StringUtil.isEmpty(nodeId)) {
            return mtNodeService.saveNode(currentUser, context, mtNode, partnerId,deliveryId,repairId);
        } else {
            return mtNodeService.saveNode(currentUser, context, mtNode, Integer.parseInt(nodeId), partnerId,deliveryId,repairId);
        }
    }

    @RequestMapping("detail")
    public String detail(Integer nodeId, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        ObjectContext context = token.getObjectContext();
        TMtNode mtNode = mtNodeService.findNode(context, nodeId);
        request.setAttribute("mtNode", mtNode);
        return "mtNode/table/mt_node_table_detail";
    }

    @RequestMapping("delete")
    @ResponseBody
    public Map<String, Object> delete(Integer[] ids, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        return mtNodeService.deleteNode(currentUser, context, ids);
    }

    @RequestMapping("delivery")
    @ResponseBody
    public Map<String, Object> setDelivery(Integer deliveryId, Integer[] ids, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        return mtNodeService.setDelivery(currentUser, context, Cayenne.objectForPK(context, TSysUser.class, deliveryId), ids);
    }

    @RequestMapping("repair")
    @ResponseBody
    public Map<String, Object> setRepair(Integer repairId, Integer[] ids, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        return mtNodeService.setRepair(currentUser, context, Cayenne.objectForPK(context, TSysUser.class, repairId), ids);
    }

    @RequestMapping("add")
    public String add(HttpServletRequest request) {
        try {
            List<TMtBusinessDistrict> list = BusinessDistrictUtils.getBusinessDistrictList();
            request.setAttribute("bdlist", list);
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TSysUser currentUser = token.getSysUser();
            TMtCompany company = currentUser.getCompany();
            List<TSysUser> deliveryList = sysUserService.findSysUsersByRoleLevelForCompany(context, "", TSysRole.ROLE_LEVEL_DELIVERY, company.getId());
            List<TSysUser> repairList = sysUserService.findSysUsersByRoleLevelForCompany(context, "", TSysRole.ROLE_LEVEL_REPAIR, company.getId());
            request.setAttribute("deliveryList", deliveryList);
            request.setAttribute("repairList", repairList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtNode/table/mt_node_table_update";
    }

    @RequestMapping("update")
    public String update(HttpServletRequest request, Integer ids) {
        try {
            List<TMtBusinessDistrict> list = BusinessDistrictUtils.getBusinessDistrictList();
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            TMtNode mtNode = Cayenne.objectForPK(context, TMtNode.class, ids);

            request.setAttribute("mtNode", mtNode);
            request.setAttribute("bdlist", list);
            TSysUser currentUser = token.getSysUser();
            TMtCompany company = currentUser.getCompany();
            List<TSysUser> deliveryList = sysUserService.findSysUsersByRoleLevelForCompany(context, "", TSysRole.ROLE_LEVEL_DELIVERY, company.getId());
            List<TSysUser> repairList = sysUserService.findSysUsersByRoleLevelForCompany(context, "", TSysRole.ROLE_LEVEL_REPAIR, company.getId());
            request.setAttribute("deliveryList", deliveryList);
            request.setAttribute("repairList", repairList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return "mtNode/table/mt_node_table_update";
    }

    @RequestMapping("deliveryList")
    public String deliveryList(HttpServletRequest request, String userName, Integer[] ids) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        TMtCompany company = currentUser.getCompany();
        List<TSysUser> list = sysUserService.findSysUsersByRoleLevelForCompany(context, userName, TSysRole.ROLE_LEVEL_DELIVERY, company.getId());
        try {
            request.setAttribute("list", list);
            request.setAttribute("ids", ids);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtNode/table/mt_node_table_delivery";
    }

    @RequestMapping("repairList")
    public String repairList(HttpServletRequest request, String userName, Integer[] ids) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        TMtCompany company = currentUser.getCompany();
        List<TSysUser> list = sysUserService.findSysUsersByRoleLevelForCompany(context, userName, TSysRole.ROLE_LEVEL_REPAIR, company.getId());
        try {
            request.setAttribute("list", list);
//            request.getSession().setAttribute("ids", ids);
            request.setAttribute("ids", ids);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtNode/table/mt_node_table_repair";
    }
}