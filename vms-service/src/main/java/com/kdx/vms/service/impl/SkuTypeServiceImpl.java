package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.PageUtil;
import com.kdx.vms.model.bean.TMtSkuType;
import com.kdx.vms.orm.dao.MtSkuTypeDao;
import com.kdx.vms.service.ISkuTypeService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SkuTypeServiceImpl implements ISkuTypeService {
	
	private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
	
	@Resource
	private MtSkuTypeDao std;

	@Override
	public Map<String, Object> list(Integer skuTypePid ,Integer pageSize, Integer pageNo, ObjectContext context) {
		Map<String, Object> map = new HashMap<String, Object>(5);
		if(pageSize == null) pageSize = 10;
		if(pageNo == null) pageNo = 1;
		map.put("pageSize",pageSize);
		map.put("pageNo", pageNo);
		map.put("url","skuType/list");
		try {
			List<Object> list = std.findSkuTypeList(skuTypePid ,pageNo, context);
			map.put("totalSize", list.size());
			map.put("nodeList", PageUtil.formatList(pageNo, pageSize, list));
			return map;
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(),e);
		}
		map.put("totalSize", 0);
		map.put("nodeList",null);
		return map;
	}
	
	public Integer updateSkuType(Integer id ,String name ,Integer userId ,ObjectContext context){
		try {
			TMtSkuType tMtSkuType = Cayenne.objectForPK(context, TMtSkuType.class ,id);
			tMtSkuType.setUtime(new Date());
			tMtSkuType.setTypeName(name);
			tMtSkuType.setUtor(userId);
			context.commitChanges();
			return tMtSkuType.getId();
		} catch (Exception e) {
			context.rollbackChanges();
			 logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Integer newSkuType(Integer pid ,String name, Integer userId, ObjectContext context) {
		try {
			TMtSkuType tMtSkuType = context.newObject(TMtSkuType.class);
			tMtSkuType.setCtime(new Date());
			tMtSkuType.setCtor(userId);
			tMtSkuType.setTypeName(name);
			if(pid != null){
				TMtSkuType pSkuType = Cayenne.objectForPK(context ,TMtSkuType.class ,pid);
				pSkuType.addToChilds(tMtSkuType);
			}
			context.commitChanges();
			return tMtSkuType.getId();
		} catch (Exception e) {
			context.rollbackChanges();
			 logger.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	@Override
	public Boolean deleteSkuType(String[] skuTypeIds, ObjectContext context) {
		try {
			if(skuTypeIds == null || skuTypeIds.length == 0){
				return false;
			}
			for (String skuTypeId : skuTypeIds) {
				TMtSkuType tMtSkuType = Cayenne.objectForPK(context ,TMtSkuType.class ,skuTypeId);
				List<TMtSkuType> childs = tMtSkuType.getChilds();
				if(childs != null) context.deleteObjects(childs);
				context.deleteObject(tMtSkuType);
			}
			context.commitChanges();
			return true;
		} catch (Exception e) {
			context.rollbackChanges();
			 logger.error(e.getLocalizedMessage(), e);
		}
		return false;
	}

}
