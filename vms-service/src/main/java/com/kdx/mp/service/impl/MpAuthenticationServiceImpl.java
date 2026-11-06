package com.kdx.mp.service.impl;

import com.kdx.mp.service.IMpAuthenticationService;
import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserBind;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.SysUserBindDao;
import com.kdx.vms.orm.dao.SysUserDao;
import com.kdx.vms.utils.UserUtils;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by yangshuxi on 2016/12/20.
 */
@Service
public class MpAuthenticationServiceImpl implements IMpAuthenticationService{
//    private ObjectContext context= ObjectContextFactory.getInstance().newObjectContext();
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Autowired
    private SysUserBindDao sysUserBindDao;
    @Autowired
    private SysUserDao sysUserDao;
    @Override
    public TSysUser getMpUser(ObjectContext context,String openId) {
        TSysUserBind sysUserBind=sysUserBindDao.findUserBindByOuterId(openId,TSysUserBind.BIND_TYPE_WECHAT);
        if(null!=sysUserBind)return sysUserBind.getUser();
        return null;
    }

    @Override
    public TSysUser getMpUser(ObjectContext context,String username, String password) {
        password=Md5Util.md5(password);
        List<TSysUser> list=sysUserDao.findSysUserByAccount(context,username,false);
        if(null==list||list.isEmpty())return null;
        TSysUser sysUser=list.get(0);
        if(sysUser.getUserPassword().equals(password))return sysUser;
        return null;
    }

    @Override
    public String bindMpUser(ObjectContext context,String openId, TSysUser mpUser) {
        if(StringUtil.isEmpty(openId)||mpUser==null)return null;
        try {
        TSysUserBind userBind=context.newObject(TSysUserBind.class);
        userBind.setUser(mpUser);
        userBind.setCtor(UserUtils.getVmsSystem().getId());
        userBind.setCtime(new Date());
        userBind.setBindType(TSysUserBind.BIND_TYPE_WECHAT);
        userBind.setOuterId(openId);
            context.commitChanges();
            logger.info("用户"+mpUser.getUserAccount()+"绑定openId"+openId);
            return "success";
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
            context.rollbackChanges();
        }
        return null;
    }
}
