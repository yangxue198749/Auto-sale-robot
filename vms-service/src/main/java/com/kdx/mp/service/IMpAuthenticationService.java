package com.kdx.mp.service;

import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

/**
 * Created by yangshuxi on 2016/12/20.
 */
public interface IMpAuthenticationService {
    public TSysUser getMpUser(ObjectContext context,String openId);
    public TSysUser getMpUser(ObjectContext context,String username,String password);
    public String bindMpUser(ObjectContext context,String openId,TSysUser mpUser);
}
