package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMtVmPriceService;
import com.kdx.vms.service.IStSkuPriceService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/13.
 */
@Controller
@RequestMapping(value = "mtVmPrice")
public class MtVmPriceController {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    static String PATH="mtVmPrice";
    @Autowired
    private IMtVmPriceService mtVmPriceService;
    @Autowired
    private IStSkuPriceService stSkuPriceService;
    @RequestMapping("list")
    public String list(HttpServletRequest request, Integer pageNo, Integer[] ids){
        SSOToken token= TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        try {
            request.setAttribute("ids", ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/price/mt_vm_price";
    }

    @RequestMapping("page")
    public String page(HttpServletRequest request,String strategyName,Integer pageNo){
        if(pageNo == null) pageNo = 1;
        SSOToken token= TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TSysUser currentUser = token.getSysUser();
        String url = PATH+"/page";
        List totalList = stSkuPriceService.findStrategyList(currentUser,context,strategyName,5);
        List subList= PageUtil.formatList(pageNo,5,totalList);
        try {
            request.setAttribute("strategyName",strategyName);
            request.setAttribute("subList",subList);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("pageSize", 5);
            request.setAttribute("url", url);
            request.setAttribute("totalSize", totalList.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/price/mt_vm_price_table";
    }

    @RequestMapping("detail")
    public String detail(HttpServletRequest request,Integer strategyId,Integer pageNo){
        SSOToken token= TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TStVmStrategy stVmStrategy=Cayenne.objectForPK(context,TStVmStrategy.class,strategyId);
        List<TStVmPrice> subList=stVmStrategy.getStPrice();
        try {
            request.setAttribute("subList",subList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/price/mt_vm_price_detail";
    }

    @RequestMapping("save")
    @ResponseBody
    public Map<String,Object> save(Integer strategyId, Integer[] ids, HttpServletRequest request){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmPriceService.savePrice(currentUser,context,strategyId,ids);
    }

    @RequestMapping("validateStrategyPrice")
    @ResponseBody
    public Map<String, Object> validateStrategyPrice(Integer[] ids) {
        Map<String, Object> resultmMap = new HashMap<>();
        resultmMap.put("type", "success");
        try {
            SSOToken token = TokenUtil.getToken();
            ObjectContext context = token.getObjectContext();
            for(Integer vmId:ids){
                TMtVm firstMtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
                if (null == firstMtVm.getVmType()){
                    resultmMap.put("type", "error");
                    resultmMap.put("message", "售货机型号未知，请先设置组合模式");
                    return resultmMap;
                }
                if (firstMtVm.getVmType().equals(TMtVmModel.COMBINATORIAL_EXTENTION)) {
                    resultmMap.put("type", "error");
                    resultmMap.put("message", "副机无法设置价格，请通过主机设置!");
                    return resultmMap;
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "副机无法设置价格，请通过主机设置!");
            return resultmMap;
        }
        return resultmMap;
    }

    @RequestMapping("originalPrice")
    @ResponseBody
    public Map<String,Object> originalPrice(Integer[] ids){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmPriceService.originalPrice(currentUser,context,ids);
    }

}
