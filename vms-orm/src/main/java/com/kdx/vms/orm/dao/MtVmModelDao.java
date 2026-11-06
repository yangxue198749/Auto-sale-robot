package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by liyang on 16-11-30.
 */
public interface MtVmModelDao {

    List findMtVmModelAll(ObjectContext context, Boolean showShortcuts);

    /**
     * 根据收获机型号标识查询售货机型号信息
     *
     * @param context
     * @param vmModelId
     * @return
     */
    TMtVmModel findMtVmModelById(ObjectContext context, Integer vmModelId);

    public List<Object> findVmModel(String name, Integer pageSize, ObjectContext context);

    /**
     * 根据组合方式查询售货机，允许多值
     * @param user
     * @param combinatorialPatterns
     * @return
     */
    List<?> findMtVmModelByCombinatorialPatterns(TSysUser user, Object... combinatorialPatterns);

}
