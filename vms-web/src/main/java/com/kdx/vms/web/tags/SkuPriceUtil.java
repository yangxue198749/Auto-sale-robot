package com.kdx.vms.web.tags;

import java.util.List;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmPrice;
import com.kdx.vms.model.context.ObjectContextFactory;

public class SkuPriceUtil {
	private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
	public static String getSkuPirceForMtVm(Integer skuId,Integer mtVmId) {
        if (null == skuId || skuId <= 0 || mtVmId ==null || mtVmId <=0) return "";
        TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, mtVmId);
        if(null != mtVm){
        	List<TMtVmPrice> priveList = mtVm.getPrice();
        	if(priveList != null && priveList.size()>0){
        		for (TMtVmPrice tMtVmPrice : priveList) {
					if(tMtVmPrice.getSkuId().equals(skuId)){
						return tMtVmPrice.getRealPrice()+"";
					}
				}
        	}
        }
        return "";
    }
}
