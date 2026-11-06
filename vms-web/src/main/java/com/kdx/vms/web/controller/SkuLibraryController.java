package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.orm.dao.MtSkuDao;
import com.kdx.vms.service.IMtSkuCompanyService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品库
 * Created by zhangguodong on 2017/4/25.
 */
@RequestMapping("skuLibrary")
@Controller
public class SkuLibraryController {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private IMtSkuCompanyService mtSkuCompanyService;
    @Resource
    private MtSkuDao mtSkuDao;
    /**
     * 进入商品库
     * @return
     */
    @RequestMapping("list")
    public String list(){


        return "skuLibrary/sku_library_list";
    }

    /**
     * 商品库列表
     * @param request
     * @param pageSize
     * @param pageNo
     * @param cookiePageSize
     * @param skuTypePid
     * @param skuName
     * @param skuTypeId
     * @return
     */
    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                           @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,
                           Integer skuTypePid, String skuName, Integer skuTypeId, String skuCode){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        String url = "/skuLibrary/listPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtSkuCompanyService.getList(token.getSysUser(),skuTypePid,skuTypeId,skuName,pageSize, skuCode);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("skuList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "skuLibrary/page/sku_library_page";
    }

    /**
     * 商品库详情
     * @param request
     * @param id
     * @return
     */
    @RequestMapping("showSkuLibraryDetailDialog")
    public  String showSkuLibraryDetailDialog(HttpServletRequest request,Integer id){
        try {
            request.setAttribute("skuLibrary",mtSkuCompanyService.getByPk(TokenUtil.getToken().getSysUser(),id));
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return "skuLibrary/dialog/show_sku_library_detail_dialog";
    }


    /**
     * 添加到商品库页面
     * @return
     */
    @RequestMapping("addSkuLibraryDialog")
    public String addSkuLibraryDialog(){

        return "skuLibrary/dialog/add_sku_library_dialog";
    }

    /**
     * 添加到商品库页面分页
     * @param request
     * @param pageSize
     * @param pageNo
     * @param cookiePageSize
     * @param skuTypePid
     * @param skuName
     * @param skuTypeId
     * @return
     */
    @RequestMapping("skuListPage")
    public String skuListPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                              @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,
                              Integer skuTypePid, String skuName, String skuCode, Integer skuTypeId){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        String url = "/skuLibrary/skuListPage";
        try {
            SSOToken token = TokenUtil.getToken();
            List list = mtSkuDao.findSkuList( skuName ,skuCode, skuTypePid , skuTypeId , pageSize ,token.getObjectContext());
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("skuList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "skuLibrary/page/sku_list_page";
    }

    /**
     * 添加到商品库事件
     * @param ids
     * @return
     */
    @RequestMapping("addToSkuLibrary")
    @ResponseBody
    public Map<String,Object> addToSkuLibrary(Integer[] ids){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try {
           String errMsg = mtSkuCompanyService.addToMtSkuCompany(TokenUtil.getToken().getSysUser(),ids);
            resultMap.put("type", StringUtil.isEmpty(errMsg)?"success":"error");
            resultMap.put("message", StringUtil.isEmpty(errMsg)?"添加成功":errMsg);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return resultMap;

    }
    @RequestMapping("delete")
    @ResponseBody
    public Map<String,Object> delete(Integer [] ids){
        Map<String,Object> resultMap = new HashMap<String,Object>();
        try {
            String errMsg = mtSkuCompanyService.delete(TokenUtil.getToken().getSysUser(),ids);
            resultMap.put("type", StringUtil.isEmpty(errMsg)?"success":"error");
            resultMap.put("message", StringUtil.isEmpty(errMsg)?"删除成功":errMsg);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return resultMap;


    }
}
