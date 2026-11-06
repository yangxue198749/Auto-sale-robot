package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.service.IMtVmEnergySaveService;
import com.kdx.vms.service.IStVmEnergySaveService;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2016/12/12.
 */
@Controller
@RequestMapping(value = "mtVmEnergySave")
public class MtVmEnergySaveController {
    static String PATH="mtVmEnergySave";
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    IMtVmEnergySaveService mtVmEnergySaveService;
    @Resource
    private IStVmEnergySaveService stVmEnergySaveService;
    @RequestMapping("list")
    public String list(HttpServletRequest request,String ids){

        SSOToken token= TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        TMtVm firstMtVm= Cayenne.objectForPK(context,TMtVm.class,ids.split(",")[0]);
        try {
            request.setAttribute("mtVm", firstMtVm);
            request.setAttribute("ids", ids);
            request.setAttribute("idsArry", ids.split(","));
            request.setAttribute("html", mtVmEnergySaveService.makeTableString(context,firstMtVm.getId()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "mtVm/dialog/mt_vm_energy_dialog";
    }

    @RequestMapping("listPage")
    public String listPage(HttpServletRequest request, Integer pageSize, Integer pageNo, 
			@CookieValue(value = "pageSize",required  = false) Integer cookiePageSize,Integer vmModelId,String stName){
    	if(pageSize == null && cookiePageSize!=null) pageSize = cookiePageSize;
    	if(pageNo == null) pageNo = 1;
    	if(pageSize == null) pageSize = 10;
		String url = "/mtVmEnergySave/listPage";
		try {
			SSOToken token = TokenUtil.getToken();
			List list = stVmEnergySaveService.findPage(token.getObjectContext(), token.getSysUser(), stName, vmModelId, pageSize);
			request.setAttribute("url", url);
			request.setAttribute("pageSize",pageSize);
			request.setAttribute("pageNo", pageNo);
			request.setAttribute("totalSize", null != list?list.size():0);
			request.setAttribute("stVmStrategyList", PageUtil.formatList(pageNo, pageSize, list));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage());
		}
    	return "mtVm/page/mt_vm_energy_save_page";
    }
    @RequestMapping("add")
    public String add(HttpServletRequest request,String vmId){
    	request.setAttribute("vmId", vmId);
        TMtVm mtVm = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(),TMtVm.class,vmId);
        if(null!=mtVm){
            TMtVmModel mtVmModel = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(),TMtVmModel.class,mtVm.getVmModelId());
            request.setAttribute("mtVmModel", mtVmModel);
        }
        return "mtVm/dialog/add_mt_vm_energy_save_dialog";
    }
    @RequestMapping("update")
    public String update(HttpServletRequest request,Integer ids,String vmId) {
        SSOToken token = TokenUtil.getToken();
        ObjectContext context = token.getObjectContext();
        TMtVmEnergySave mtVmEnergySave = Cayenne.objectForPK(context, TMtVmEnergySave.class, ids);
        TMtVm mtVm = mtVmEnergySave.getMtVm();
        if(null!=mtVm){
            TMtVmModel mtVmModel = Cayenne.objectForPK(TokenUtil.getToken().getObjectContext(),TMtVmModel.class,mtVm.getVmModelId());
            request.setAttribute("mtVmModel", mtVmModel);
        }
        try {
            request.setAttribute("mtVmEnergySave", mtVmEnergySave);
            request.setAttribute("vmId", vmId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage());
        }
        return "mtVm/dialog/edit_mt_vm_energy_save_dialog";
    }

    @RequestMapping("save")
    @ResponseBody
    public Map<String,Object> save(Integer targetValue,Integer vmId,Integer energySaveType, String startTime,String endTime, String remark,Integer energySaveId, HttpServletRequest request,Integer tempBoxNo){
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        Map<String, Object> respMap;
        ObjectContext context=token.getObjectContext();
        if(energySaveId == null){
            respMap = mtVmEnergySaveService.saveEnergySave(currentUser,context,targetValue,vmId,energySaveType,startTime,endTime,remark,tempBoxNo);
        }
        else{
            respMap = mtVmEnergySaveService.saveEnergySave(currentUser,context,energySaveId,targetValue,vmId,energySaveType,startTime,endTime,remark,tempBoxNo);
        }
        if(respMap.get("type").equals("success")){
        	respMap.put("html", mtVmEnergySaveService.makeTableString(context, vmId));
        }
        return respMap;
    }
    @RequestMapping("delete")
    @ResponseBody
    public Map<String,Object> delete(Integer[] ids,HttpServletRequest request){
        SSOToken token=TokenUtil.getToken();
        TSysUser currentUser = token.getSysUser();
        Map<String, Object> respMap;
        ObjectContext context=token.getObjectContext();
        respMap = mtVmEnergySaveService.delete(currentUser,context,ids);
        return respMap;
    }
    @RequestMapping("selectSt")
    @ResponseBody
    public Map<String,Object> selectSt(Integer[] mtVmIds,Integer strategyId){
    	Map<String,Object> resultMap = new HashMap<String, Object>();
    	try {
			SSOToken token = TokenUtil.getToken();
			String errMsg = mtVmEnergySaveService.selectSt(token.getObjectContext(),mtVmIds,strategyId,token.getSysUser());
			if(StringUtil.isEmpty(errMsg)){
				resultMap.put("type", "success");
				resultMap.put("message", "应用节能策略成功");
			}else{
				resultMap.put("type", "error");
				resultMap.put("message", errMsg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
    	return resultMap;
    }
    @RequestMapping("pushToMCS")
    @ResponseBody
    public Map<String,Object> pushToMCS(Integer vmId){
    	Map<String,Object> resultMap = new HashMap<String,Object>();
    	try {
    		SSOToken token = TokenUtil.getToken();
    		String errMsg = mtVmEnergySaveService.pushToMCS(token.getObjectContext(), new Integer[]{vmId});
    		String NewErrMsg = mtVmEnergySaveService.pushToMCSNew(token.getObjectContext(), new Integer[]{vmId});
    		if(StringUtil.isEmpty(errMsg) && StringUtil.isEmpty(NewErrMsg)){
    			resultMap.put("type", "success");
				resultMap.put("message", "应用成功");

                TMtVm mtVm = Cayenne.objectForPK(token.getObjectContext(), TMtVm.class, vmId);
                List<TMtVmStrategy> stList=mtVm.getStrategy();
                boolean flag=false;
                for(TMtVmStrategy mtVmStrategy:stList){
                    if(mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE)){
                        mtVmStrategy.clear(token.getSysUser().getId());
                        flag=true;
                        break;
                    }
                }
                if(!flag){
                    TMtVmStrategy mtVmStrategy=token.getObjectContext().newObject(TMtVmStrategy.class);
                    mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE);
                    mtVmStrategy.setMtVm(mtVm);
                    mtVmStrategy.clear(token.getSysUser().getId());
                }
                token.getObjectContext().commitChanges();
    		}else{
    			resultMap.put("type", "error");
				resultMap.put("message", "保存成功。");
    		}
		} catch (Exception e) {
			e.printStackTrace();TokenUtil.getToken().getObjectContext().rollbackChanges();
			logger.error(e.getLocalizedMessage(),e);
		}
    	return resultMap;
    }
}
