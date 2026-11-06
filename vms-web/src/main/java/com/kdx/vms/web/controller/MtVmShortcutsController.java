package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMtVmResourceService;
import com.kdx.vms.service.IMtVmShortcutsService;
import com.kdx.vms.service.IShortcutsService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.kdx.vms.web.tags.TokenUtil.getToken;

/**
 * Created by yangshuxi on 2016/12/12.
 */
@Controller
@RequestMapping(value = "mtVmShortcuts")
public class MtVmShortcutsController {
    static String PATH="mtVmShortcuts";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMtVmShortcutsService mtVmShortcutsService;
    @Autowired
    private IShortcutsService shortcutsService;
    @Resource
    private IMtVmResourceService mtVmResourceService;
    @RequestMapping("save")
    @ResponseBody
    public Map<String,Object> save(Integer[] skuIds,Integer[] ids,String[] shortcutsCodes, HttpServletRequest request,Integer[] resourceIds,Integer[] bindTypes){
        SSOToken token= getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmShortcutsService.saveShortcuts(currentUser,context,ids,skuIds,shortcutsCodes,resourceIds,bindTypes);
    }

    @RequestMapping("save/st")
    @ResponseBody
    public Map<String,Object> saveBySt(Integer strategyId,Integer[] ids, HttpServletRequest request){
        SSOToken token= getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmShortcutsService.saveShortcuts(currentUser,context,ids,strategyId);
    }
    @RequestMapping("list")
    public String list(HttpServletRequest request,Integer pageNo,Integer[] ids){
        SSOToken token= getToken();
        ObjectContext context=token.getObjectContext();
        TMtVm firstMtVm= Cayenne.objectForPK(context,TMtVm.class,ids[0]);
        try {
            request.setAttribute("mtVm", firstMtVm);
            request.setAttribute("ids", ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/shortcuts/mt_vm_shortcuts";
    }
    @RequestMapping("page")
    public String page(HttpServletRequest request,Integer pageNo,String strategyName,Integer modelId){
        if(pageNo == null) pageNo = 1;
        SSOToken token= getToken();
        TSysUser currentUser = token.getSysUser();
        String url = PATH+"/page";
        List totalList = shortcutsService.getShortcutsStrategy(currentUser.getCompany(),strategyName,modelId,5);
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
        return "mtVm/dialog/shortcuts/mt_vm_shortcuts_table";
    }

    @RequestMapping("sku")
    public String sku(HttpServletRequest request,String shortcutsCode){
        try {
            request.setAttribute("shortcutsCode", shortcutsCode);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVm/dialog/shortcuts/sku_table";
    }

    @RequestMapping("sku/page")
    public String skuPage(HttpServletRequest request,Integer skuGroupTypeId,Integer skuTypeId,String skuName,String skuCode){
//        List totalList= SkuUtils.getSkuList(skuTypeId,skuGroupTypeId,null,skuName,false);
//        try {
//            request.setAttribute("subList", totalList);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error(e.getLocalizedMessage());
//        }
        request.setAttribute("skuIds","");
        request.setAttribute("skuName",skuName);
        request.setAttribute("skuCode",skuCode);
        request.setAttribute("skuGroupTypeId",skuGroupTypeId);
        request.setAttribute("skuTypeId",skuTypeId);
        return "mtVm/dialog/shortcuts/sku_table_page";
    }

    @RequestMapping("detail")
    public String detail(HttpServletRequest request,Integer strategyId,Integer pageNo){
        SSOToken token= getToken();
        ObjectContext context=token.getObjectContext();
        TStVmStrategy stVmStrategy=Cayenne.objectForPK(context,TStVmStrategy.class,strategyId);
        Map<String,TStVmShortcuts> map=stVmStrategy.getStShortcuts();
        List<TStVmShortcuts> list=new ArrayList<>();
        for (int i = 1; i <=map.size() ; i++) {
           list.add(map.get(i+""));
        }
        try {
            request.setAttribute("subList",list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/shortcuts/mt_vm_shortcuts_detail";
    }

    @RequestMapping("selectMtVmResourcesDialog")
    public String selectMtVmResourcesDialog(HttpServletRequest request,String shortcutsCode){
        request.setAttribute("shortcutsCode",shortcutsCode);
        return "mtVm/dialog/shortcuts/mt_vm_shortcuts_resources_dialog";
    }
    @RequestMapping("selectMtVmResourcesPage")
    public String selectMtVmResourcesPage(HttpServletRequest request, Integer pageSize, Integer pageNo,
                                          @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize, String name){
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        String url = "/mtVmShortcuts/selectMtVmResourcesPage";
        try {
            SSOToken token = getToken();
            List<TMtVmResource> list = mtVmResourceService.findPage(token.getSysUser(),name,pageSize,TMtVmResource.MT_VM_SHORT_CURTS_RESOURCE_TYPE);
            request.setAttribute("url", url);
            request.setAttribute("pageSize",pageSize);
            request.setAttribute("pageNo", pageNo);
            request.setAttribute("totalSize", null != list?list.size():0);
            request.setAttribute("mtVmResourceList", PageUtil.formatList(pageNo, pageSize, list));
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVm/dialog/shortcuts/mt_vm_shortcuts_resources_page";
    }

}
