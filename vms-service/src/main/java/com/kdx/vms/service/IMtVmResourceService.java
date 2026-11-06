package com.kdx.vms.service;

import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;

/**
 * Created by zhangguodong on 2017/5/23.
 */
public interface IMtVmResourceService {

    /**
     *
     * @param user
     * @param name
     * @param pageSize
     * @param type
     * @return
     */
    public List<TMtVmResource> findPage(TSysUser user,String name ,Integer pageSize,Integer type);

    /**
     * 新建
     * @param user
     * @param name
     * @param resourceDesc
     * @param resourceMd5
     * @param resourceSize
     * @param resourceUrl
     * @return
     */
    public SystemCode createMtVmResource(TSysUser user,String name,String resourceDesc,String resourceMd5,Long resourceSize,String resourceUrl);

    /**
     * 删除
     * @param user
     * @param ids
     * @return
     */
    public SystemCode delMtVmResources(TSysUser user, Integer[] ids);
}
