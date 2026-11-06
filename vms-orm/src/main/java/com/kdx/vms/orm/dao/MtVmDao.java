package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtPayType;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 2016/11/28.
 */
public interface MtVmDao {
	
	/**
	 * 根据售货机标识获取售货机信息
	 * @param context
	 * @param vmId 售货机标识
	 * @return
	 */
	TMtVm findByVmId(ObjectContext context,Integer vmId);
	
	/**
	 * 查询售货机列表
	 * @param context
	 * @param filedType 搜索字段类型   1售货机编码，2点位，3硬件编码 4所属公司
	 * @param filedValue 搜索字段值
	 * @param modelId 售货机型号标识
	 * @param vmStatus 售货机状态
	 * @param province 点位所在省
	 * @param city 点位所在市
	 * @param country 点位所在区
	 * @param bussinessId 场景标识
	 * @param user 登录用户
	 * @param pageSize 每页条数
	 * @param showPattern 只展示主机或单机类型 true false
	 * @return
	 */
	List<TMtVm> findListByCondition(ObjectContext context,Integer filedType,String filedValue,Integer modelId,Integer vmStatus,Integer province,Integer city,Integer country,Integer bussinessId,TSysUser user,Integer pageSize, Boolean showPattern) ;

	/**
	 * 查询用户下所有售货机
	 * @param context
	 * @param user
	 * @return
	 */
	List<TMtVm> findAllMtVm(ObjectContext context, TSysUser user);

    /**
     * 根据售货机状态查询售货机
     * @param context
     * @return
     */
    List findAllMtVmByStatus(ObjectContext context, int vmStatus);
    
    public List<TMtVm> findAllMtVmByCompanyId(ObjectContext context, Integer companyId);
    
    public List<TMtVm> findAllMtVmByCompany(ObjectContext context, TMtCompany company);

    /**
     * 获取状态为销售中 and 有补货员信息的售货机列表
     * 自动生成补货工单时调用
     * @param context
     * @return
     */
    List findAllMtVmByDelivery(ObjectContext context);

    List getAllVm(ObjectContext context);

    /**
     * 获取售货机
     * @param context
     * @param user
     * @param searchNodeName 点位名称
     * @param searchInnerCode 售货机编码
     * @param pageSize
     * @param vmStatus 售货机状态
     * @return
     */
	List findPage(ObjectContext context, TSysUser user, String searchNodeName,
			String searchInnerCode, Integer pageSize,Integer vmStatus);

    /**
     * 获取售货机及状态
     * @param user   人
     * @param innerCode 售货机编号
     * @param nodeName  点位名称
     * @param netStatus 网络状态
     * @param vmcStatus vmc状态
     * @return list
     */
    List findVmForStatus(TSysUser user, String innerCode, String nodeName, Integer netStatus, Integer vmcStatus,Integer taskStatus, Integer tempStatus, Integer pageSize);

    List<TMtVm> findVmForStocks(TMtCompany company, String innerCode, String nodeName, Integer pageSize);

	List findVmByTime(ObjectContext context,TMtCompany company,String startTime,String endTime);

	List findOnSaleVm(ObjectContext context,TMtCompany company);

	List findOnSaleVm(TSysUser sysUser,String innerCode);
	List findAllVm(TSysUser sysUser,String innerCode);

	List findByUniqueCode(ObjectContext context,String uniqueCode);

	TMtVm findByInnerCode(ObjectContext context, String innerCode);
	
	/**
	 * 获取售货机信息
	 * @param context
	 * @param innerCode 售货机编号
	 * @param user 登录用户
	 * @param nodeName 点位名称
	 * @return
	 */
	List<TMtVm> findByInnerCodeAndNodeName(ObjectContext context, String innerCode,TSysUser user,String nodeName);

	/**
	 * 查询售货机列表，不区分公司。提供给升级计划
	 * @param objectContext
	 * @param vmModelId
	 * @param province
	 * @param city
	 * @param country
	 * @param pageSize
	 * @return
	 */
	List<TMtVm> findForUpGrade(ObjectContext objectContext, Integer vmModelId, Integer province, Integer city, Integer country,Integer pageSize);
	List<TMtVm> findUnInitStockStatusList(ObjectContext objectContext);


	List<TMtPayType> findPayTypeByVmId(ObjectContext context, Integer vmId);
	/**
	 * 查询机构下库存的明细
	 * @param context
	 * @param company
	 * @return
	 */
	public List findStockMessage(ObjectContext context, TMtCompany company);

}
