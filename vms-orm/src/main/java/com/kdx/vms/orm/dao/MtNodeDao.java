package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtNode;

import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by yangshuxi on 2016/11/28.
 */
public interface MtNodeDao {
    List getMtNodeList(ObjectContext context, String deliveryName, String repairName, String partnerName, String nodeName, String propertyName, TMtCompany company, Integer province,Integer city,Integer area, Integer businessDistrictId, Integer pageSize);
    List getMtNodeList(ObjectContext context,TMtCompany company,String startTime,String endTime);
    
    public List<TMtNode> getMtNodeList(ObjectContext context ,List<?> cityIdList ,String addressName);
    
    public List<TMtNode> getMtNodeList(ObjectContext context ,List<?> cityIdList ,List<?> businessIds );

    List getAllNode(ObjectContext context, boolean prefetch);


}
