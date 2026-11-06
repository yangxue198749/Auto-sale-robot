package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtSku;

/**
 * Created by liyang on 2016/11/28.
 */
public interface MtSkuDao {
	
	public List<Object> findSkuList(String name, String skuCode,Integer skuTypePid ,Integer skuTypeId ,Integer pageSize ,ObjectContext context);

	/**
	 * 根据主键获取商品信息
	 * @param context
	 * @param skuId 商品主键
	 * @return
	 */
	public TMtSku findByPk(ObjectContext context, Integer skuId);

	public TMtSku findByCode(ObjectContext context,String skuCode);
}
