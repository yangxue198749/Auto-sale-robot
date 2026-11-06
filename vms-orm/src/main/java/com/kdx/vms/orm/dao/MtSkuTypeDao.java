package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtSkuType;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by liyang on 2016/11/28.
 */
public interface MtSkuTypeDao {
	
	public List<Object> findSkuTypeList(Integer skuTypePid ,Integer pageSize ,ObjectContext context);
	
	public List<Object> findSkuTypeList(ObjectContext context);
	public TMtSkuType findByExternalId(ObjectContext context, String externalId);
}
