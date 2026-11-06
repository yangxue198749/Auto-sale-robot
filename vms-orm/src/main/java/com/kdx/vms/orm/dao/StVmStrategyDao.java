package com.kdx.vms.orm.dao;

import java.util.List;

import com.kdx.vms.model.bean.TMtCompany;
import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;

/**
 *
 * Created by liyang on 16-11-29.
 */
public interface StVmStrategyDao {

    public TStVmStrategy findTStVmStrategyPk(ObjectContext context, Integer pk);

    /**
     * 获取闪购建策略
     * @param context
     * @param company
     * @param strategyName
     * @param modelId
     * @param pageSize
     * @return
     */
    List findShortcutsStrategy(ObjectContext context, TMtCompany company, String strategyName, Integer modelId, Integer pageSize);

    List findStVmStrategy(ObjectContext context, TMtCompany company, Integer strategyType, String strategyName, Integer pageSize);
	/**
	 * 获取走马灯策略
	 * @param context
	 * @param user 登录用户
	 * @param stVmName 策略名称
	 * @param text 走马灯内容
	 * @param pageSize 每页数量
	 * @return
	 */
    List<TStVmStrategy> findStVmTextPage(ObjectContext context, TSysUser user,
			String stVmName, String text, Integer pageSize);
    
    /**
     * 获取节能策略
     * @param context
     * @param user 登录用户
     * @param stVmName 策略名称
     * @param vmModelId 售货机型号标识
     * @param pageSize 每页数量
     * @return
     */
    List<TStVmStrategy> findStVmEnergyPage(ObjectContext context, TSysUser user,
			String stVmName, Integer vmModelId, Integer pageSize);

	public List findStVmChannelPage(ObjectContext objectContext,
			TSysUser sysUser, String searchStVmName, Integer searchVmModelId,
			Integer pageSize);

}
