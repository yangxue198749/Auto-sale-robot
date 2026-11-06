package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmEventMaster;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by yangshuxi on 2016/12/5.
 */
public interface MtVmEventMasterDao {
    public List<TMtVmEventMaster> findMtEventMaster(ObjectContext context, TMtCompany company, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId, Integer pageSize);
    public List<TMtVm> findSkuEventVmList(ObjectContext context, TMtCompany company, String nodeName, String innerCode, Integer province,Integer city,Integer area, Integer businessDistrictId ,Integer[] removeIds);
    public List<TMtVm> findVmEventVmList(ObjectContext context, TMtCompany company, String nodeName, String innerCode, Integer province,Integer city,Integer area, Integer businessDistrictId  ,Integer[] removeIds);
    public List<TMtVm> findEventVmList(ObjectContext context, TMtCompany mtCompany,String time);
  }
