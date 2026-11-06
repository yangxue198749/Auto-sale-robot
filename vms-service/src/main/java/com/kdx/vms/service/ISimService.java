package com.kdx.vms.service;

import com.kdx.vms.commons.util.SystemCode;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 16-11-30.
 */
public interface ISimService {

    List getSimList(ObjectContext context ,TSysUser sysUser ,String simPhone, String simNumber, Integer serviceProvider, Boolean use, Integer pageSize);

    List getSimList(ObjectContext context, Integer pageSize);

    SystemCode createSim(String simPhone, String simNumber, Integer serviceProvider, TSysUser currUser);

    SystemCode editSim(Integer sinId, String simPhone, String simNumber, Integer serviceProvider, TSysUser currUser);

    SystemCode deleteSim(List<Integer> simIds, TSysUser currUser);

}
