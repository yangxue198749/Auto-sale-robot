package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtSkuCompany;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;

/**
 * 商品库
 * Created by zhangguodong on 2017/4/26.
 */
public interface IMtSkuCompanyService {

    /**
     *  查询当前用户公司下的商品库
     * @param user
     * @param skuTypeGroup
     * @param skuType
     * @param skuName
     * @param pageSize
     * @return
     */
    public List<TMtSkuCompany>  getList(TSysUser user, Integer skuTypeGroup, Integer skuType , String skuName, Integer pageSize, String skuCode);

    /**
     * 获取商品库信息
     * @param user 当前用户
     * @param id 商品库主键
     * @return
     */
    public TMtSkuCompany getByPk(TSysUser user,Integer id);

    /**
     * 加入商品库
     * @param user 当前用户
     * @param skuIds 商品标识数组
     * @return
     */
    public String addToMtSkuCompany(TSysUser user,Integer[] skuIds);

    /**
     * 删除商品库中的商品
     * @param user
     * @param skuIds
     * @return
     */
    public String delete(TSysUser user,Integer[] skuIds);
}
