package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.ReturnCodeUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IMtVmTextService;
import com.kdx.vms.service.IStVmTextService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.DateUtils;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/12.
 */
@Controller
@RequestMapping(value = "mtVmText")
public class MtVmTextController {
    static String PATH="mtVmText";
    @Autowired
    private IMtVmTextService mtVmTextService;
    @Autowired
    private IStVmTextService stVmTextService;
    @RequestMapping("save")
    @ResponseBody
    public Map<String,Object> save(String mtText, Integer[] ids, String beginDate,String endDate, HttpServletRequest request){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmTextService.saveText(currentUser,context,mtText,ids, DateUtils.parseDate(beginDate+" 00:00:00"),DateUtils.parseDate(endDate+" 23:59:59"));
    }
    @RequestMapping("save/st")
    @ResponseBody
    public Map<String,Object> saveBySt(Integer strategyId, Integer[] ids, String beginDate,String endDate, HttpServletRequest request){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        return mtVmTextService.saveText(currentUser,context,strategyId,ids, DateUtils.parseDate(beginDate+" 00:00:00"),DateUtils.parseDate(endDate+" 23:59:59"));
    }
    @RequestMapping("list")
    public String list(HttpServletRequest request,Integer pageNo,Integer[] ids){
        SSOToken token= TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TMtVm firstMtVm= Cayenne.objectForPK(context,TMtVm.class,ids[0]);
        try {
            request.setAttribute("mtVm", firstMtVm);
            request.setAttribute("ids", ids);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/text/mt_vm_text";
    }

    @RequestMapping("page")
    public String page(HttpServletRequest request,String strategyName,Integer pageNo){
        if(pageNo == null) pageNo = 1;
        SSOToken token= TokenUtil.getToken();
        String url = PATH+"/page";
        List totalList = stVmTextService.findStVmTextPage(token.getObjectContext(), token.getSysUser(), strategyName, null, 5);
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
        return "mtVm/dialog/text/mt_vm_text_table";
    }

    @RequestMapping("detail")
    public String detail(HttpServletRequest request,Integer strategyId){
        SSOToken token= TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TStVmStrategy stVmStrategy=Cayenne.objectForPK(context,TStVmStrategy.class,strategyId);
        String textContents=stVmStrategy.getStText().getTextContents();
        try {
            request.setAttribute("textContents",textContents);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/text/mt_vm_text_detail";
    }

}
