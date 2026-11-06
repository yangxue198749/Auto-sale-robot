package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.ReturnCodeUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMtVmEventService;
import com.kdx.vms.service.IStVmEventService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.BusinessDistrictUtils;
import com.kdx.vms.web.tags.DateUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/6.
 */
@Controller
@RequestMapping(value = "mtVmEvent")
public class MtVmEventController {
    static String PATH="mtVmEvent";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IStVmEventService stVmEventService;
    @Autowired
    private IMtVmEventService mtVmEventService;
    @RequestMapping("list")
    public String list(HttpServletRequest request){
        List<TMtBusinessDistrict> list= BusinessDistrictUtils.getBusinessDistrictList();
        request.setAttribute("bdlist", list);
        return "mtVmEvent/mt_event_list";
    }
    @RequestMapping("table")
    public String table(HttpServletRequest request, Integer pageSize, Integer pageNo,String searchInnerCode,String searchNodeName,Integer province,Integer city,Integer area,Integer businessDistrictId,
                        @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        List totalList=mtVmEventService.findMtEventMaster(currentUser,context,searchNodeName,searchInnerCode,province,city,area,businessDistrictId,pageSize);
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
        return "mtVmEvent/table/mt_event_table";
    }
    /**
     * 待应用商品策略详情
     */
    @RequestMapping("detail/st")
    public String stSkuDetail(HttpServletRequest request,Integer strategyId){
        SSOToken token=TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
        request.setAttribute("stVmStrategy", stVmStrategy);
        return "mtVmEvent/table/st_sku_detail";
    }
    /**
     * 已应用的策略详情
     */
    @RequestMapping("detail/mt")
    public String mtDetail(HttpServletRequest request,Integer mtVmEventId){
        SSOToken token=TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TMtVmEventMaster mtVmEventMaster = Cayenne.objectForPK(context, TMtVmEventMaster.class, mtVmEventId);
        List list=mtVmEventMaster.getEvents();
        if(null!=list&&!list.isEmpty()){
            try {
                if(mtVmEventMaster.getEventType().equals(TStVmEvent.EVENT_TYPE_SKU)) {
                    request.setAttribute("mtEvent", mtVmEventMaster);
                    return "mtVmEvent/table/mt_sku_detail";
                }
                else {
                    request.setAttribute("mtEvent", list.get(0));
                    return "mtVmEvent/table/mt_vm_detail";
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        }
       return null;
    }
    /**
     * 待应用整机策略的售货机
     */
    @RequestMapping("table/vm")
    public String vmTable(HttpServletRequest request, Integer pageSize, Integer pageNo,String searchInnerCode,String searchNodeName,Integer province,Integer city,Integer area,Integer businessDistrictId,Integer[] ids,
                        @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        List totalList=mtVmEventService.findVmEventVmList(currentUser,context,searchNodeName,searchInnerCode,province,city,area,businessDistrictId,ids);
        String url = PATH+"/table/vm";
        Integer totalSize = totalList.size();
        List subList= PageUtil.formatList(pageNo,pageSize,totalList);
        List<TMtBusinessDistrict> list= BusinessDistrictUtils.getBusinessDistrictList();
        request.setAttribute("bdlist", list);
        try {
            request.setAttribute("url", url);
            request.setAttribute("ids", ids);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);

            request.setAttribute("searchInnerCode", searchInnerCode);
            request.setAttribute("searchNodeName", searchNodeName);
            request.setAttribute("province", province);
            request.setAttribute("city", city);
            request.setAttribute("area", area);
            request.setAttribute("businessDistrictId", businessDistrictId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVmEvent/table/mt_event_vm_table";
    }
    /**
     * 待应用商品策略的售货机
     */
    @RequestMapping("table/sku")
    public String skuTable(HttpServletRequest request, Integer pageSize, Integer pageNo,String searchInnerCode,String searchNodeName,Integer province,Integer city,Integer area,Integer businessDistrictId,Integer[] ids,
                          @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        List totalList=mtVmEventService.findSkuEventVmList(currentUser,context,searchNodeName,searchInnerCode,province,city,area,businessDistrictId,ids);
        String url = PATH+"/table/sku";
        Integer totalSize = totalList.size();
        List subList= PageUtil.formatList(pageNo,pageSize,totalList);
        List<TMtBusinessDistrict> list= BusinessDistrictUtils.getBusinessDistrictList();
        request.setAttribute("bdlist", list);
        try {
            request.setAttribute("url", url);
            request.setAttribute("ids", ids);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);

            request.setAttribute("searchInnerCode", searchInnerCode);
            request.setAttribute("searchNodeName", searchNodeName);
            request.setAttribute("province", province);
            request.setAttribute("city", city);
            request.setAttribute("area", area);
            request.setAttribute("businessDistrictId", businessDistrictId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVmEvent/table/mt_event_sku_table";
    }
    @RequestMapping("save/sku")
    @ResponseBody
    public Map<String,Object> saveSku(Integer[] vmIds, Integer strategyId,String startTime,String endTime,String beginDate,String endDate ,HttpServletRequest request){
        if(null==vmIds||vmIds.length==0||strategyId==null|| StringUtil.isEmpty(startTime)|| StringUtil.isEmpty(endTime)|| StringUtil.isEmpty(beginDate)|| StringUtil.isEmpty(endDate)){
            return ReturnCode.PARAMETER_ERROR.toMap();
        }
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return  mtVmEventService.saveMtVmEvent(currentUser,context,vmIds,strategyId, DateUtils.parseDate(beginDate+" 00:00:00"),DateUtils.parseDate(endDate+" 23:59:59"),startTime,endTime, TStVmEvent.EVENT_TYPE_SKU);
    }

    @RequestMapping("save/vm")
    @ResponseBody
    public Map<String,Object> saveVm(Integer[] vmIds, Integer strategyId,String startTime,String endTime,String beginDate,String endDate ,HttpServletRequest request){
        if(null==vmIds||vmIds.length==0||strategyId==null|| StringUtil.isEmpty(startTime)|| StringUtil.isEmpty(endTime)|| StringUtil.isEmpty(beginDate)|| StringUtil.isEmpty(endDate)){
            return ReturnCode.PARAMETER_ERROR.toMap();
        }
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmEventService.saveMtVmEvent(currentUser,context,vmIds,strategyId, DateUtils.parseDate(beginDate+" 00:00:00"),DateUtils.parseDate(endDate+" 23:59:59"),startTime,endTime, TStVmEvent.EVENT_TYPE_VM);
    }

    @RequestMapping("delete")
    @ResponseBody
    public Map<String,Object> delete(Integer[] ids,HttpServletRequest request){
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmEventService.deleteEvent(currentUser,context,ids);
    }

    /**
     * 可用商品促销策略
     */
    @RequestMapping("/table/st/sku")
    public String stSkuList(HttpServletRequest request, Integer pageSize, Integer pageNo,String searchValue,
                        @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        List totalList=stVmEventService.findSkuEventList(currentUser,context,searchValue,pageSize);
        String url = PATH+"/table/st/sku";
        Integer totalSize = totalList.size();
        List subList= PageUtil.formatList(pageNo,pageSize,totalList);
        try {
            request.setAttribute("url", url);
            request.setAttribute("searchValue", searchValue);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVmEvent/table/st_sku_event_table";
    }
    /**
     * 可用整机促销策略
     */
    @RequestMapping("/table/st/vm")
    public String stVmList(HttpServletRequest request, Integer pageSize, Integer pageNo,String searchValue,
                            @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        List totalList=stVmEventService.findVmEventList(currentUser,context,null,searchValue,pageSize);
        String url = PATH+"/table/st/vm";
        Integer totalSize = totalList.size();
        List subList= PageUtil.formatList(pageNo,pageSize,totalList);
        try {
            request.setAttribute("url", url);
            request.setAttribute("searchValue", searchValue);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", totalSize);
            request.setAttribute("subList", subList);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVmEvent/table/st_vm_event_table";
    }
    /**
     * 应用整机促销窗口
     */
    @RequestMapping("/add/vm")
    public String setVmEvent(HttpServletRequest request){
        return "mtVmEvent/table/mt_vm_add";
    }

    /**
     * 应用商品促销窗口
     */
    @RequestMapping("/add/sku")
    public String setSkuEvent(HttpServletRequest request){
        return "mtVmEvent/table/mt_sku_add";
    }


    @RequestMapping("validateStrategyPromotion")
    @ResponseBody
    public Map<String, Object> validateStrategyPromotion(Integer[] ids) {
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
                    resultmMap.put("message", "副机无法设置促销策略，请通过主机设置!");
                    return resultmMap;
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultmMap.put("type", "error");
            resultmMap.put("message", "副机无法设置促销策略，请通过主机设置!");
            return resultmMap;
        }
        return resultmMap;
    }

}
