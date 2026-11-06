package com.kdx.vms.service;

import java.util.List;
import java.util.Map;

import com.kdx.vms.model.bean.TMtSkuImg;
import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtSku;

public interface ISkuService {
	
	/**
	 * 查询商品
	 * @param name
	 * @param skuCode
	 * @param skuTypePid
	 * @param skuTypeId
	 * @param pageSize
	 * @param pageNo
	 * @return Map<String,Object>
	 */
	public Map<String, Object> list(ObjectContext context,String name, String skuCode, Integer skuTypePid ,Integer skuTypeId ,Integer pageSize ,Integer pageNo);
	
	/**
	 * 新建商品
	 * @param tMtSku
	 * @param skuTypeId
	 * @param userId
	 * @return Boolean
	 */
	public Integer createSku(ObjectContext context,TMtSku tMtSku ,Integer skuTypeId ,Integer userId);

	/**
	 * 更新商品
	 * @param tMtSku
	 * @param skuTypeId
	 * @param userId
	 */
	public Integer updateSku(ObjectContext context,TMtSku tMtSku ,Integer skuTypeId ,Integer userId);
	
	/**
	 * 根据id查看商品
	 * @param context
	 * @param skuId
	 */
	public TMtSku getTMtSku(ObjectContext context,Integer skuId);

	/**
	 * 编辑商品
	 * @param id
	 * @param imgUrl
	 */
	Boolean changeSkuNew(ObjectContext context,Integer id ,String imgUrl ,Integer index ,Integer source, Integer detail);

	/**
	 * 根据skuId查询商品图片
	 * @param context
	 * @param id
	 * @param index
	 * @return
	 */
	TMtSkuImg findBySkuId(ObjectContext context,Integer id ,Integer index, Integer source);

	/**
	 * 添加商品描述
	 * @param context
	 * @param id
	 * @param describe
	 * @return
	 */
	Integer updateSkuDescribe(ObjectContext context,Integer id ,String describe ,Integer userId);

	List<TMtSkuImg> findSkuImgsBySkuId(ObjectContext context, Integer skuId);
}
