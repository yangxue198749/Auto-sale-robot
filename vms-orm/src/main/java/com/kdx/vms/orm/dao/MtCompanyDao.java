package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 2016/11/28.
 */
public interface MtCompanyDao {
	
	/**
	 * 分页查询公司
	 * @param companyName 公司名字
	 * @param customerName 客户名字
	 * @param adminName 管理员名字
	 * @param unusedLevel 不使用的等级，为1,2,3  该值为去掉该等级的公司
	 * @param twoLevelCompanyId 二级公司 id
	 * @param pageSize
	 * @param context
	 * @return List<Object>
	 * @date 2016年12月7日 下午9:54:18
	 * @author chenifan
	 */
	public List<Object> findCompanyList(String companyName ,String customerName ,String adminName ,Integer unusedLevel  ,Integer twoLevelCompanyId ,Integer pageSize ,ObjectContext context);
	
    public TSysUser findAdminSysUserByCompany(ObjectContext context, Integer companyId ,Integer level);

	/**
	 * 接口管理公司查询中使用
	 * @param companyName
	 * @param company
	 * @param pageSize
	 * @param context
	 * @return
	 */
	List<TMtCompany> findCompanyPage(String companyName, TMtCompany company, Integer pageSize, ObjectContext context);
	TMtCompany findCompanyById(ObjectContext context, Integer companyId);
}
