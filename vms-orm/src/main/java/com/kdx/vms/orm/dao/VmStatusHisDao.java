package com.kdx.vms.orm.dao;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TVmStatusHis;

/**
 * 售货机状态历史记录接口
 * @author zhangguodong
 *
 */
public interface VmStatusHisDao {

	
	/**
	 * 获取历史记录列表
	 * @param context
	 * @param user 登录用户
	 * @param innerCode 售货机编号
	 * @param nodeName 点位名称
	 * @param vmStatus 上报的售货机状态
	 * @param startTime 上报开始时间
	 * @param endTime 上报结束时间
	 * @param pageSize 每页数量
	 * @return
	 */
	public List<TVmStatusHis> findListPage(ObjectContext context, TSysUser user,String innerCode,String nodeName,Integer vmStatus,String startTime,String endTime,Integer pageSize);
}
