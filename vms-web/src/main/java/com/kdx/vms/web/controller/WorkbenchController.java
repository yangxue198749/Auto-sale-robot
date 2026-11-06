package com.kdx.vms.web.controller;

import com.kdx.vms.service.IWorkbenchService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/16.
 */
@Controller
@RequestMapping("workbench")
public class WorkbenchController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IWorkbenchService workbenchService;
    @RequestMapping("list")
    public String list(){
        return "workbench/workbench_list";
    }
    @RequestMapping("deliveryCount")
    public String deliveryTaskCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Map<Long,Integer> map=workbenchService.findDeliveryTaskList(token.getObjectContext(), token.getSysUser());
        request.setAttribute("counts",map);
        request.setAttribute("url","deliveryTask/list");
        return "workbench/table/delivery_count";
    }
    @RequestMapping("repairCount")
    public String repairTaskCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Map<Long,Integer> map=workbenchService.findRepairTaskList(token.getObjectContext(), token.getSysUser());
        request.setAttribute("counts",map);
        request.setAttribute("url","repairTask/list");
        return "workbench/table/repair_count";
    }
    @RequestMapping("deployCount")
    public String deployTaskCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Map<Long,Integer> map=workbenchService.findDeployTaskList(token.getObjectContext(), token.getSysUser());
        request.setAttribute("counts",map);
        request.setAttribute("url","deployTask/list");
        return "workbench/table/deploy_count";
    }
    @RequestMapping("withdrawCount")
    public String withdrawTaskCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Map<Long,Integer> map=workbenchService.findWithdrawTaskList(token.getObjectContext(), token.getSysUser());
        request.setAttribute("counts",map);
        request.setAttribute("url","withdrawTask/list");
        return "workbench/table/withdraw_count";
    }
    @RequestMapping("newNodeCount")
    public String newNodeCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Integer count=workbenchService.findNewNode(token.getObjectContext(), token.getSysUser());
        request.setAttribute("count",count);
        request.setAttribute("url","mtNode/list");
        return "workbench/table/new_node_count";
    }
    @RequestMapping("newVmCount")
    public String newVmCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Integer count=workbenchService.findNewVm(token.getObjectContext(), token.getSysUser());
        request.setAttribute("count",count);
        request.setAttribute("url","mtVm/list");
        return "workbench/table/new_vm_count";
    }
    @RequestMapping("eventVmCount")
    public String eventVmCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Integer count=workbenchService.findEventVm(token.getObjectContext(), token.getSysUser());
        request.setAttribute("count",count);
        request.setAttribute("url","mtVmEvent/list");
        return "workbench/table/event_vm_count";
    }
    @RequestMapping("oosVmCount")
    public String oosVmCount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Integer count=workbenchService.findOutOfStockVm(token.getObjectContext(), token.getSysUser());
        request.setAttribute("count",count);
        request.setAttribute("url","vmStock/list");
        return "workbench/table/oos_vm_count";
    }
    @RequestMapping("salesVolume")
    public String salesVolume(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        Integer count=workbenchService.findSalesVolume(token.getObjectContext(), token.getSysUser());
        request.setAttribute("count",count);
        request.setAttribute("url","vendout/home");
        return "workbench/table/sales_volume";
    }
    @RequestMapping("salesAmount")
    public String salesAmount(HttpServletRequest request){
        SSOToken token = TokenUtil.getToken();
        String count=workbenchService.findSalesAmount(token.getObjectContext(), token.getSysUser());
        request.setAttribute("count",count);
        request.setAttribute("url","vendout/home");
        return "workbench/table/sales_amount";
    }
}
