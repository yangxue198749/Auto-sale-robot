package com.kdx.vms.service;

import java.util.List;

import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TVmStatusHis;

/**
 * 售货机上报的历史状态接口
 * @author zhangguodong
 *
 */
public interface IVmStatusHisService {

	/**
	 * 获取售货机状态历史记录
	 * @param objectContext
	 * @param sysUser 登录用户
	 * @param vmStatus 售货机整体状态
	 * @param innerCode 售货机编号
	 * @param nodeName 点位名称
	 * @param startTime 上报开始时间
	 * @param endTime 上报结束时间
	 * @param pageSize 每页数量
	 * @return
	 */
	List<TVmStatusHis> findListPage(ObjectContext objectContext, TSysUser sysUser,
			Integer vmStatus, String innerCode, String nodeName,
			String startTime, String endTime, Integer pageSize);

}
