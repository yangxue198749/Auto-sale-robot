package com.kdx.vms.orm.dao;

import java.util.List;

import com.kdx.vms.model.bean.TMtWarehouse;
import org.apache.cayenne.ObjectContext;

/**
 * Created by liyang on 2016/11/28.
 */
public interface MtWarehouseDao {
	
	/**
	 * 
	 * @param idCode 仓库编号id
	 * @param name 仓库名字
	 * @param pageSize 页数
	 * @param context
	 * @return List<TMtWarehouse>
	 * @date 2016年11月29日 下午5:41:31
	 * @author chenifan
	 */
	public List<Object> findWarehouseList(String idCode ,String name ,int pageSize ,ObjectContext context);

	public TMtWarehouse findByCode(ObjectContext context,String warehouseCode);
	
}
