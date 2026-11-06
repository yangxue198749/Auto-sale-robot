package com.kdx.vms.orm.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TVmStatusHis;
import com.kdx.vms.orm.dao.MtVmDao;
import com.kdx.vms.orm.dao.VmStatusHisDao;

/**
 * 售货机历史状态接口实现类
 * @author zhangguodong
 *
 */
@Service("vmStatusHisDao")
public class VmStatusHisDaoImpl implements VmStatusHisDao {
	
	Logger logger = Logger.getLogger(getClass());

	@Resource
	private MtVmDao mtVmDao;
	@Override
	public List<TVmStatusHis> findListPage(ObjectContext context,
			TSysUser user, String innerCode, String nodeName, Integer vmStatus,
			String startTime, String endTime, Integer pageSize) {
		try {
			//先获取当前用户所能看到的所有售货机的标识
			List<TMtVm> vmList = mtVmDao.findByInnerCodeAndNodeName(context, innerCode, user, nodeName);
			if(null == vmList || vmList.size()==0){
				return null;
			}
			List<Integer> vmIdList = new ArrayList<Integer>();
			for (TMtVm mtVm : vmList) {
				vmIdList.add(mtVm.getId());
			}
			Expression expression = ExpressionFactory.inExp(TVmStatusHis.VM_ID_PROPERTY, vmIdList);
			if(null != vmStatus){
				expression = expression.andExp(ExpressionFactory.matchExp(TVmStatusHis.VM_STATUS_PROPERTY, vmStatus));
			}
			if(!StringUtil.isEmpty(startTime)){
				expression = expression.andExp(ExpressionFactory.greaterOrEqualExp(TVmStatusHis.VM_TIME_PROPERTY, StringUtil.strToDate(startTime , StringUtil.Pattern)));
			}
			if(!StringUtil.isEmpty(endTime)){
				expression = expression.andExp(ExpressionFactory.lessOrEqualExp(TVmStatusHis.VM_TIME_PROPERTY, StringUtil.strToDate(endTime, StringUtil.Pattern)));
			}
			SelectQuery selectQuery = new SelectQuery(TVmStatusHis.class, expression);
			selectQuery.addOrdering(TVmStatusHis.VM_TIME_PROPERTY, SortOrder.DESCENDING);
			if(null != pageSize && pageSize>0){
				selectQuery.setPageSize(pageSize);
			}
	        return context.performQuery(selectQuery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
