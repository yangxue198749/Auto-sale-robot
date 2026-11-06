package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.ReturnCodeUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.ISkuService;
import com.kdx.vms.service.IStSkuPriceService;
import com.kdx.vms.service.IStVmStrategyService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.SkuUtils;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/11/30.
 */
@Controller
@RequestMapping(value = "stSkuPrice")
public class StSkuPriceController {
    static String PATH="stSkuPrice";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    static int strategyType=TStVmStrategy.TYPE_ST_VM_PRICE;
    @Autowired
    private IStSkuPriceService stSkuPriceService;
    @Autowired
    private IStVmStrategyService stVmStrategyService;
    @Autowired
    private ISkuService skuService;
    @RequestMapping("list")
    public String list(HttpServletRequest request){
        return "stSkuPrice/sku_price_list";
    }
    @RequestMapping("table")
    public String table(HttpServletRequest request, Integer pageSize, Integer pageNo,String searchValue,
                        @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        List totalList=stSkuPriceService.findStrategyList(currentUser,context,searchValue,pageSize);
        String url = PATH+"/table";
        Integer totalSize = totalList.size();
        List subList= PageUtil.formatList(pageNo,pageSize,totalList);
        try {
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "stSkuPrice/table/sku_price_table";
    }
    @RequestMapping("save")
    @ResponseBody
    public Map<String,Object> save(String  strategyName,String strategyDesc,Integer[] skuIds,Integer[] realPrices, HttpServletRequest request){
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        String strategyId=request.getParameter("strategyId");
        if(StringUtil.isEmpty(strategyId)){
            return stSkuPriceService.saveStrategy(currentUser,context,strategyName,strategyDesc,skuIds,realPrices);
        }
        else{
            return stSkuPriceService.saveStrategy(currentUser,context,strategyName,strategyDesc,skuIds,realPrices,Integer.parseInt(strategyId));
        }
    }
    @RequestMapping("delete")
    @ResponseBody
    public Map<String,Object> delete(Integer[] ids,HttpServletRequest request){
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return stVmStrategyService.deleteStrategy(currentUser,context,ids,strategyType);
    }
    @RequestMapping("detail")
    public String detail(HttpServletRequest request,Integer strategyId){
        SSOToken token=TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        List totalList=stVmStrategy.getStPrice();
            request.setAttribute("subList", totalList);
        return "stSkuPrice/table/sku_price_table_detail";
    }

    @RequestMapping("add")
    public String add(HttpServletRequest request){
        return "stSkuPrice/table/sku_price_table_update";
    }

    @RequestMapping("update")
    public String update(HttpServletRequest request,Integer ids){
        SSOToken token=TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, ids);
        try {
            request.setAttribute("stVmStrategy", stVmStrategy);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "stSkuPrice/table/sku_price_table_update";
    }
    @RequestMapping("sku")
    public String sku(HttpServletRequest request,Integer[] skuIds,Integer skuGroupTypeId,Integer skuTypeId,String skuName, String skuCode){
//        if(!StringUtil.isEmpty(skuName)){
//            try {
//                skuName= URLDecoder.decode(skuName, "UTF-8");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        List totalList= SkuUtils.getSkuList(skuTypeId,skuGroupTypeId,skuIds,skuName);
//        try {
//            request.setAttribute("subList", totalList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getLocalizedMessage());
//        }
        request.setAttribute("skuIds",StringUtil.join(",",skuIds));
        request.setAttribute("skuName",skuName);
        request.setAttribute("skuCode",skuCode);
        request.setAttribute("skuGroupTypeId",skuGroupTypeId);
        request.setAttribute("skuTypeId",skuTypeId);
        return "stSkuPrice/table/sku_table";
    }
}
