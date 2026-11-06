package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtCustomer;

/**
 * Created by liyang on 2016/11/28.
 */
public interface MtCustomerDao {
	
	public List<Object> findCustomerList(String customerName ,String manageName ,String customerMode ,String customerType ,Integer pageSize ,ObjectContext context);
	
	public List<Object> findCustomerList(List<Integer> ids ,String customerName ,String manageName ,String customerMode ,String customerType ,Integer pageSize ,ObjectContext context);

	public List<TMtCustomer> findUnusedCustomer(ObjectContext context);

    public List findCustomerB(ObjectContext context);

    TMtCustomer findCustomerByPhone(ObjectContext context, String phone);

}
