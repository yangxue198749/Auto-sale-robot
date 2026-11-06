package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
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
 * Created by yangshuxi on 2017/4/25.
 */
@Controller
@RequestMapping("uniqueCode")
public class UniqueCodeController {
    static String PATH = "uniqueCode";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMtVmService mtVmService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        if (currentUser.getCompany().getCompanyLevel() != TMtCompany.LEVEL_ONE) {
            return "home/unauthorized";
        }
        return "uniqueCode/list";
    }

    @RequestMapping("table")
    public String table(HttpServletRequest request, Integer pageSize, Integer pageNo, Integer searchType, String searchValue,
                        @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        SSOToken token = TokenUtil.getToken();
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        List totalList = mtVmService.findMtVmList(token.getObjectContext(), searchType, searchValue, null, null, null, null, null, null, token.getSysUser(), pageSize, true);
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
        return "uniqueCode/table/table";
    }

    @RequestMapping("unbind")
    @ResponseBody
    public Map<String, Object> unbind(Integer[] ids, HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context = token.getObjectContext();
        return mtVmService.unbind(currentUser, context, ids);
    }

}
