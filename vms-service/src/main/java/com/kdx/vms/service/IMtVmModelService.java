package com.kdx.vms.service;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TSysUser;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by liyang on 16-11-30.
 */
public interface IMtVmModelService {

    /**
     * 获取全部TMtModel
     * @param context
     * @return List<TMtModel>
     */
    public List findMtVmModel(ObjectContext context, Boolean showShortcuts);

    public List findMtVmModelByCombinatorialPatterns(TSysUser user, Object... combinatorialPatterns);
    
    /**
     * 创建售货机型号
     * @param tMtVmModel
     * @param user
     * @param context
     * @return Integer
     * @date 2016年12月5日 下午3:25:15
     * @author chenifan
     */
    public Integer createVmModel(TMtVmModel tMtVmModel, TSysUser user, ObjectContext context);

    /**
     * 更新售货机
     * @param tMtVmModel
     * @param user
     * @param context
     * @return Boolean
     * @date 2016年12月5日 下午4:12:22
     * @author chenifan
     */
    public Integer updateVmModel(TMtVmModel tMtVmModel, TSysUser user, ObjectContext context);
    
    /**
     * 删除售货机型号表，根据id
     * @param id
     * @param context
     * @return Boolean
     * @date 2016年12月5日 下午5:08:15
     * @author chenifan
     */
    public Boolean deleteVmModel(String id, ObjectContext context);
    
    /**
     * 分页查询售货机列表
     * @param name
     * @param pageSize
     * @param pageNo
     * @param context
     * @return Map<String,Object>
     * @date 2016年12月5日 下午5:29:01
     * @author chenifan
     * @QQ 995998760
     */
    public Map<String, Object> list(String name ,Integer pageSize ,Integer pageNo ,ObjectContext context);
    
    /**
     * 根据id查询询售货机
     * @param id
     * @return TMtVmModel
     * @date 2016年12月6日 上午11:14:48
     * @author chenifan
     */
    public TMtVmModel getTMtVmModel(String id ,ObjectContext context);

    /**
     * 售货机型号禁用与启用
     * @param id
     * @param context
     * @return
     */
    Map<String,Object> lockedOrUnlocked(String id,ObjectContext context);

}
