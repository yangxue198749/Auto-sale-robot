package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.ReturnCodeUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TStVmEvent;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.service.IStVmEventService;
import com.kdx.vms.service.IStVmStrategyService;
import com.kdx.vms.web.oauth.SSOToken;
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
 * Created by yangshuxi on 2016/12/1.
 */
@Controller
@RequestMapping(value = "stVmEvent")
public class StVmEventController {
    static String PATH="stVmEvent";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    static int strategyType = TStVmStrategy.TYPE_ST_VM_EVENT;
    static int eventType = TStVmEvent.EVENT_TYPE_VM;
    @Autowired
    private IStVmStrategyService stVmStrategyService;
    @Autowired
    private IStVmEventService stVmEventService;

    @RequestMapping("list")
    public String list(HttpServletRequest request){
        return "stVmEvent/vm_event_list";
    }
    @RequestMapping("table")
    public String table(HttpServletRequest request, Integer pageSize, Integer pageNo,String selectValue,String searchValue,
                        @CookieValue(value = "pageSize",required  = false) Integer cookiePageSize){
        SSOToken token= TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
        if(pageNo == null) pageNo = 1;
        if(pageSize == null) pageSize = 10;
        List totalList=stVmEventService.findVmEventList(currentUser,context,selectValue,searchValue,pageSize);
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
        return "stVmEvent/table/vm_event_table";
    }

    @RequestMapping("save")
    @ResponseBody
    public Map<String,Object> save(String  strategyName, String strategyDesc, Integer discountType, Integer discountValue, HttpServletRequest request){
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        ObjectContext context=token.getObjectContext();
        String strategyId=request.getParameter("strategyId");
        if(StringUtil.isEmpty(strategyId)){
            return stVmEventService.saveVmEvent(currentUser,context,strategyName,strategyDesc,discountType,discountValue);
        }
        else{
            return stVmEventService.saveVmEvent(currentUser,context,strategyName,strategyDesc,discountType,discountValue,Integer.parseInt(strategyId));
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

    @RequestMapping("update")
    public String update(HttpServletRequest request,Integer ids){
        SSOToken token=TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, ids);
        List subList=stVmStrategy.getStEvent();
        if(null!=subList&&!subList.isEmpty()){
            try {
                request.setAttribute("vmEvent", subList.get(0));
            } catch (Exception e) {
                e.printStackTrace();
                logger.error(e.getLocalizedMessage());
            }
        }
        return "stVmEvent/table/vm_event_update";
    }

    @RequestMapping("add")
    public String add(HttpServletRequest request){
        return "stVmEvent/table/vm_event_update";
    }
}
