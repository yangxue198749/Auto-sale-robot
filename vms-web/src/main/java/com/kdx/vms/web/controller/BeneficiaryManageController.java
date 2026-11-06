package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.service.IMtBeneficiaryService;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2018/2/28.
 */
@Controller
@RequestMapping("beneficiaryManage")
public class BeneficiaryManageController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMtBeneficiaryService mtBeneficiaryService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        return "beneficiaryManage/enabled_beneficiary_list";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize", required = false) Integer cookiePageSize) {
        try {
            if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
            if (pageNo == null) pageNo = 1;
            if (pageSize == null) pageSize = 10;
            String url = "/beneficiaryManage/listPage";
            ObjectContext context = TokenUtil.getToken().getObjectContext();
            List<Map<String, String>> list = mtBeneficiaryService.findEnabledCompany(context);
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list ? list.size() : 0);
            request.setAttribute("companyList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "beneficiaryManage/page/enabled_beneficiary_page";
    }
}
