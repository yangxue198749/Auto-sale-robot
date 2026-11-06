package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtSkuCompany;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;

/**
 * 商品库
 * Created by zhangguodong on 2017/4/25.
 */
public interface MtSkuCompanyDao {


    /**
     * 查询当前用户公司下的商品库
     * @param user
     * @param skuTypeGroup
     * @param skuType
     * @param skuName
     * @param pageSize
     * @return
     */
    public List<TMtSkuCompany> getList(TSysUser user,Integer skuTypeGroup,Integer skuType ,String skuName,Integer pageSize, String skuCode);

    /**
     * 获取商品库信息
     * @param user 当前用户
     * @param id 商品库主键
     * @return
     */
    public TMtSkuCompany getByPk(TSysUser user,Integer id);

    /**
     * 获取商品库信息
     * @param user 当前用户
     * @param skuId 商品标识
     * @return
     */
    public TMtSkuCompany getBySkuIdAndCompany(TSysUser user ,Integer skuId);
}
