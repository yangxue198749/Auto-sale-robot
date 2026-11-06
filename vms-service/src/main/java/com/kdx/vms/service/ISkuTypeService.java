package com.kdx.vms.service;

import java.util.Map;

import org.apache.cayenne.ObjectContext;

public interface ISkuTypeService {
	
	/**
	 * 
	 * @param pageSize
	 * @param pageNo
	 * @param context
	 * @return Map<String, Object>
	 * @date 2016年11月30日 下午6:30:56
	 * @author chenifan
	 */
	public Map<String, Object> list(Integer skuTypePid ,Integer pageSize ,Integer pageNo ,ObjectContext context);
	
	public Integer updateSkuType(Integer id ,String name ,Integer userId ,ObjectContext context);
	
	public Integer newSkuType(Integer pid ,String name ,Integer userId ,ObjectContext context);
	
	public Boolean deleteSkuType(String[] skuTypeIds,ObjectContext context);

}
