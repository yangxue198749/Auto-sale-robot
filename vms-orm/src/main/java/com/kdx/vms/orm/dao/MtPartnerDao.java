package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysUser;

public interface MtPartnerDao {

	public List<Object> findPartnerList(String partnerName ,String manageName ,String partnerType ,Integer pageSize ,Integer pageNo ,TSysUser user ,ObjectContext context);
}
