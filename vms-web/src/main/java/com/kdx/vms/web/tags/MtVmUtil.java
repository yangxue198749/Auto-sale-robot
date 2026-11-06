package com.kdx.vms.web.tags;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;

/**
 * 售货机工具类
 * @author zhangguodong
 *
 */
public class MtVmUtil {
	private final static ObjectContext CONTEXT = ObjectContextFactory.getInstance().newObjectContext();

	public static String getInnerCodeById(Integer vmId){
		try {
			if(null == vmId )return "";
			TMtVm  mtVm = Cayenne.objectForPK(CONTEXT,TMtVm.class, vmId);
			if(null != mtVm) return mtVm.getInnerCode();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String getNodeName(Integer vmId){
		try {
			if(null == vmId )return "";
			TMtVm  mtVm = Cayenne.objectForPK(CONTEXT,TMtVm.class, vmId);
			if(null != mtVm) return mtVm.getNodeName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	public static String getAreaNameByVmId(Integer vmId){
		try {
			if(null == vmId )return "";
			TMtVm  mtVm = Cayenne.objectForPK(CONTEXT,TMtVm.class, vmId);
			if(null != mtVm) return mtVm.getAreaName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
