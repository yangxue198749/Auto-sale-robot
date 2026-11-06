package com.kdx.vms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TVmStatusHis;
import com.kdx.vms.orm.dao.VmStatusHisDao;
import com.kdx.vms.service.IVmStatusHisService;

/**
 * 售货机上报的历史状态接口实现类
 * @author zhangguodong
 *
 */
@Service("vmStatusHisService")
public class VmStatusHisServiceImpl implements IVmStatusHisService {
	
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Resource
	private VmStatusHisDao vmStatusHisDao;
	
	@Override
	public List<TVmStatusHis> findListPage(ObjectContext context,
			TSysUser sysUser, Integer vmStatus, String innerCode,
			String nodeName, String startTime, String endTime, Integer pageSize) {
		try {
			return vmStatusHisDao.findListPage(context, sysUser, innerCode, nodeName, vmStatus, startTime, endTime, pageSize);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getLocalizedMessage(),e);
		}
		return null;
	}

}
