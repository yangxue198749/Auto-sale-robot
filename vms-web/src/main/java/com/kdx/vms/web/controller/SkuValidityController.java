package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmChannel;
import com.kdx.vms.orm.dao.MtVmChannelDao;
import com.kdx.vms.service.IMtVmChannelService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 商品效期监控
 *
 * Created by Zhouxing
 */
@Controller
@RequestMapping(value = "skuValidity")
public class SkuValidityController {

    static String PATH = "skuValidity";
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private MtVmChannelDao mtVmChannelDao;
    
    @RequestMapping(value ="home")
    public String home() {
        return "skuValidity/skuValidity_list";
    }

    @RequestMapping("table")
    public String table(HttpServletRequest request, Integer pageSize, Integer pageNo, Integer searchType, String searchValue,
                        @CookieValue(value = "pageSize", required = false) Integer cookiePageSize,String skuCode) {
        SSOToken token = TokenUtil.getToken();
        if (pageSize == null && cookiePageSize != null) pageSize = cookiePageSize;
        if (pageNo == null) pageNo = 1;
        if (pageSize == null) pageSize = 10;
        List totalList = mtVmChannelDao.queryValidityByEnd(token.getSysUser(),searchType,searchValue,pageSize,skuCode);

        String url = PATH + "/table";
        Integer totalSize = totalList.size();
        List<TMtVmChannel> tMtVmChannels = PageUtil.formatList(pageNo, pageSize, totalList);
        try {
            request.setAttribute("url", url);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("tMtVmChannels", tMtVmChannels);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "skuValidity/table/table";
    }





}