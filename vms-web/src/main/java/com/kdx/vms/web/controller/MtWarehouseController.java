package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kdx.vms.model.bean.TMtWarehouseArea;
import com.kdx.vms.service.IMtWarehouseAreaService;
import com.kdx.vms.service.IMtWarehouseService;
import com.kdx.vms.web.oauth.SSOToken;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value = "warehouse")
public class MtWarehouseController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource
	private IMtWarehouseService iMtWarehouseService;
	
	@Resource
	private IMtWarehouseAreaService iMtWarehouseAreaService;
	
	@RequestMapping(value = "home")
	public String home(){
		return "mtWarehouse/warehouse_list";
	}

	@RequestMapping(value = "list")
	@ResponseBody
	public ModelAndView list(Integer type ,String val ,Integer pageSize ,Integer pageNo ){
		if(type == null) type = 0;
		SSOToken token=TokenUtil.getToken();
        ObjectContext context=token.getObjectContext();
        return new ModelAndView("mtWarehouse/page/warehouse_page",iMtWarehouseService.list(type==1?val:null, type==2?val:null, pageSize, pageNo, context));
	}
	
	@RequestMapping(value = "uniteArea")
	@ResponseBody 
	public Integer updateWarehouse(String areaArray, Integer warehouseId ,Integer cityId){
		SSOToken token=TokenUtil.getToken();
		ObjectContext context=token.getObjectContext();
		return iMtWarehouseService.uniteArea(areaArray.split(","), warehouseId, cityId, TokenUtil.getToken().getSysUser().getId(), context);
	}
	
	@RequestMapping(value = "uniteAreaPage")
	@ResponseBody
	public ModelAndView uniteAreaPage(String ids){
		ModelAndView modelAndView = new ModelAndView("mtWarehouse/dialog/warehouse_area","warehouseId",ids);
		List<TMtWarehouseArea> allWarehouseArea = iMtWarehouseAreaService.getAllWarehouseArea();
		JSONArray array=new JSONArray();
		for (TMtWarehouseArea tMtWarehouseArea : allWarehouseArea) {
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("areaId",tMtWarehouseArea.getArea().getId());
            jsonObject.put("wareHouseId",tMtWarehouseArea.getWareHouse().getId());
			array.add(jsonObject);
		}
		modelAndView.addObject("usedIdList", array.toString());
		return modelAndView;
	}
	
	@RequestMapping(value = "lookUniteArea")
	@ResponseBody
	public ModelAndView lookUniteArea(int warehouseId){
		try {
			SSOToken token=TokenUtil.getToken();
			ObjectContext context=token.getObjectContext();
			return new ModelAndView("mtWarehouse/dialog/warehouse_lookArea","list",iMtWarehouseService.lookUniteArea(warehouseId, context));
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}
}
