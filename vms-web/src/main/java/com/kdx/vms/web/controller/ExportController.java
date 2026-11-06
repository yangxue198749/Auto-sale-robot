package com.kdx.vms.web.controller;

import com.kdx.vms.model.bean.TMtExport;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtExportDao;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/12/6.
 */
@Controller
@RequestMapping("export")
public class ExportController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MtExportDao mtExportDao;

    @RequestMapping("report/list")
    public String reportList() {
        return "export/report/report_list";
    }

    @RequestMapping("report/listPage")
    public String reportListPage(HttpServletRequest request) {
        SSOToken token = TokenUtil.getToken();
        TSysUser user = token.getSysUser();
        List<TMtExport> exports = mtExportDao.findExportData(user).stream().map(TMtExport.class::cast).collect(Collectors.toList());
        request.setAttribute("list", exports);
        return "export/report/table/report_table";
    }
}
