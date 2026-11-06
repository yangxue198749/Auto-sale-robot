package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtVmResource;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;

/**
 * Created by zhangguodong on 2017/5/23.
 */
public interface MtVmResourceDao {
    /**
     *
     * @param user
     * @param name
     * @param pageSize
     * @param type
     * @return
     */
    List<TMtVmResource> findPage(TSysUser user,String name,Integer pageSize,Integer type);

    /**
     *
     * @param user
     * @param id
     * @return
     */
    TMtVmResource findById(TSysUser user,Integer id);
}
