package com.kdx.vms.orm.dao;

import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 2017/6/22.
 */
public interface MtUpgradePackageDao {

    /**
     * 获取升级包列表
     * @param context  当前上下文
     * @param type     程序类型
     * @param version  版本号(模糊查询)
     * @param pageSize 分页尺寸
     * @return 升级包列表
     */
    List queryUpgradePackage(ObjectContext context,
                             Integer       type,
                             String        version,
                             Integer       pageSize);
    /**
     * 获取升级包列表
     * @param context  当前上下文
     * @param type     程序类型
     * @param version  版本号(模糊查询)
     * @param pageSize 分页尺寸
     * @param disable  是否禁用   0:禁用;1:启用
     * @return 升级包列表
     */
    List queryUpgradePackage(ObjectContext context,
                             Integer       type,
                             String        version,
                             Integer       pageSize,Integer disable);

}
