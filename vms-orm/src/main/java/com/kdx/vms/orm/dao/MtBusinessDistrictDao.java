package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtBusinessDistrict;


/**
 * Created by liyang on 2016/11/28.
 */
public interface MtBusinessDistrictDao {
	
	public List<Object> findBusinessDistrict(Integer pageSize , ObjectContext context);
	
	public List<TMtBusinessDistrict> findAllBusinessDistrict(ObjectContext context);

    List getAllBusinessDistrict(ObjectContext context);
}
