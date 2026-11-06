package com.kdx.vms.web.externalInterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kdx.vms.model.bean.TMtArea;
import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.model.bean.TMtNode;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtNodeDao;
import com.kdx.vms.service.IMtAreaService;
import com.kdx.vms.service.IMtBusinessDistrictService;

/**
 * 对外接口
 * @date 2016年12月20日 下午2:52:32 
 * @author chensifan
 * @version 1.0
 */
@Controller
@RequestMapping(value = "adms")
public class AdmsExternalController {
	
	private static ObjectContext objectContext = ObjectContextFactory.getInstance().newObjectContext();
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Resource(name="mtAreaService")
	private IMtAreaService iMtAreaService;
	
	@Resource
	private IMtBusinessDistrictService iMtBusinessDistrictService;
	
	@Resource
	private MtNodeDao mtNodeDao;
	
//	public Object getResult(){
//		
//	}
	@RequestMapping("toGet")
	@ResponseBody
	public MappingJacksonValue directives(String jsoncallback ,String type ,String cityIds ,String searchKey ,String businessIds ,String nodeIds) {
		MappingJacksonValue mappingJacksonValue = null;
		switch (type) {
		case "getProvinceAndCity":
			mappingJacksonValue = new MappingJacksonValue(getProvinceAndCity());
			break;
		case "getBusinessDistrict":
			mappingJacksonValue = new MappingJacksonValue(getBusinessDistrict());
			break;
		case "getNodeByName":
			mappingJacksonValue = new MappingJacksonValue(getNodeByName(cityIds ,searchKey));
			break;
		case "getVm":
			mappingJacksonValue = new MappingJacksonValue(getVm(cityIds ,businessIds ,nodeIds));
			break;
		default:
			mappingJacksonValue = new MappingJacksonValue("格式不对");
			break;
		}
		mappingJacksonValue.setJsonpFunction(jsoncallback);
		return mappingJacksonValue;
	}

	public List<Map<String,Object>> getProvinceAndCity(){
		try {
			List<TMtArea> areaList = iMtAreaService.findMtAreaByLevel(objectContext, 2);
			if(areaList == null) return null;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for (TMtArea tMtArea : areaList) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("areaName", tMtArea.getAreaName());
				map.put("areaId", tMtArea.getId());
				List<TMtArea> childArea = tMtArea.getChildArea();
				List<Map<String,Object>> childList = new ArrayList<Map<String,Object>>();
				map.put("citys", childList);
				for (TMtArea tMtArea2 : childArea) {
					Map<String,Object> childMap = new HashMap<String, Object>();
					childMap.put("cityName", tMtArea2.getAreaName());
					childMap.put("cityId", tMtArea2.getId());
					childList.add(childMap);
				}
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage() ,e);
		}
		return null;
	}
	
	public List<Map<String,Object>> getBusinessDistrict(){
		try {
			List<TMtBusinessDistrict> allList = iMtBusinessDistrictService.allList(objectContext);
			if(allList == null) return null;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for (TMtBusinessDistrict tMtBusinessDistrict : allList) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("businessName", tMtBusinessDistrict.getBusinessDistrictName());
				map.put("businessId", tMtBusinessDistrict.getId());
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage() ,e);
		}
		return null;
	}
	
	public List<Map<String,Object>> getNodeByName(String cityIds ,String searchKey){
		try {
			List<String> cityIdList = Arrays.asList(cityIds.split(","));
			List<TMtNode> mtNodeList = mtNodeDao.getMtNodeList(objectContext, cityIdList, searchKey);
			if(mtNodeList == null) return null;
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			for (TMtNode tMtNode : mtNodeList) {
				Map<String,Object> map = new HashMap<String, Object>();
				map.put("nodeId", tMtNode.getId());
				map.put("nodeName", tMtNode.getNodeName());
				list.add(map);
			}
			return list;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage() ,e);
		}
		return null;
	}
	
	public Map<String ,String> getVm(String cityIds ,String businessIds ,String nodeIds){
		try {
			if(StringUtils.isNotEmpty(cityIds)){
				Map<String ,String> map = new HashMap<String, String>();
				StringBuilder sb = new StringBuilder();
				if(StringUtils.isNotEmpty(nodeIds)){
					for (String nodeId : nodeIds.split(",")) {
						TMtNode tMtNode = Cayenne.objectForPK(objectContext, TMtNode.class, nodeId);
						for (TMtVm tMtVm : tMtNode.getVm()) {
							if(tMtVm.getVmStatus() == TMtVm.STATUS_SALE){
								sb.append(tMtVm.getInnerCode()).append(",");
							}
						}
					}
				}else{
					List<TMtNode> mtNodeList;
					if(StringUtils.isNotEmpty(businessIds)){
						mtNodeList = mtNodeDao.getMtNodeList(objectContext, Arrays.asList(cityIds.split(",")), Arrays.asList(businessIds.split(",")));
					}else{
						mtNodeList = mtNodeDao.getMtNodeList(objectContext, Arrays.asList(cityIds.split(",")), new ArrayList<String>());
					}
					for (TMtNode tMtNode : mtNodeList) {
						for (TMtVm tMtVm : tMtNode.getVm()) {
							if(tMtVm.getVmStatus() == TMtVm.STATUS_SALE){
								sb.append(tMtVm.getInnerCode()).append(",");
							}
						}
					}
				}
				
				map.put("innerCodes", sb.length()>0?sb.substring(0, sb.length()-1):"");
				return map;
			}
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage() ,e);
		}
		return null;
	}
}
