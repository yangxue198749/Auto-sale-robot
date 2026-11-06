package com.kdx.vms.service;

import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;

/**
 *
 * Created by liyang on 2017/6/22.
 */
public interface IMtUpgradePackageService {

    /**
     * 获取升级包列表
     * @param currUser 当前用户
     * @param type     程序类型
     * @param version  版本号
     * @param pageSize 分页尺寸
     * @return 升级包列表
     */
    List getUpgradePackage(TSysUser currUser,
                           Integer  type,
                           String   version,
                           Integer  pageSize);

    /**
     * 获取升级包列表
     * @param currUser 当前用户
     * @param type     程序类型
     * @param version  版本号
     * @param pageSize 分页尺寸
     * @param disable 是否禁用   0:禁用;1:启用
     * @return 升级包列表
     */
    List getUpgradePackage(TSysUser currUser,
                           Integer  type,
                           String   version,
                           Integer  pageSize,Integer disable);

    SystemCode addUpgradePackage(TSysUser currUser,
                                 Integer  type,
                                 String   version,
                                 String   url,
                                 String   name,
                                 String   md5,
                                 Long     size);

    /**
     * 修改升级包禁用/启用
     * @param currUser  当前用户
     * @param packageId 升级包id
     * @return SystemCode
     */
    SystemCode editUpgradePackage(TSysUser currUser,
                                  Integer  packageId);

}
