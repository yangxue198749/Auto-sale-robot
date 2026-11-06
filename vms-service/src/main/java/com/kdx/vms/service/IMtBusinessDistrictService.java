package com.kdx.vms.service;

import java.util.List;
import java.util.Map;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.model.bean.TSysUser;

public interface IMtBusinessDistrictService {
	
	/**
	 * 创建场景
	 * @param name 名字
	 * @param user
	 * @param context
	 * @return Boolean
	 * @date 2016年12月5日 上午10:56:09
	 * @author chenifan
	 */
	public Integer createBusinessDistrict(String name, TSysUser user, ObjectContext context);
	
	/**
	 * 修改场景名字
	 * @param name 名字
	 * @param id id
	 * @param user 用户
	 * @param context 
	 * @return Boolean
	 * @date 2016年12月5日 上午10:56:34
	 * @author chenifan
	 */
	public Integer updateBusinessDistrict(String name, String id ,TSysUser user, ObjectContext context);
	
	/**
	 * 删除场景
	 * @param id
	 * @param user
	 * @param context
	 * @return Boolean
	 * @date 2016年12月5日 上午11:00:46
	 * @author chenifan
	 */
	public Boolean deleteBusinessDistrict(String[] ids ,TSysUser user, ObjectContext context);
	
	/**
	 * 分页 查询场景
	 * @param pageSize
	 * @param pageNo
	 * @param user
	 * @param context
	 * @return Map<String,Object>
	 * @date 2016年12月5日 上午11:04:39
	 * @author chenifan
	 */
	public Map<String, Object> list(Integer pageSize ,Integer pageNo ,TSysUser user, ObjectContext context);
	
	/**
	 * 查询全部的场景
	 * @param context
	 * @return
	 * @return List<TMtBusinessDistrict>
	 * @date 2016年12月20日 下午3:11:16
	 * @author chenifan
	 * @QQ 995998760
	 */
	public List<TMtBusinessDistrict> allList(ObjectContext context);

}
