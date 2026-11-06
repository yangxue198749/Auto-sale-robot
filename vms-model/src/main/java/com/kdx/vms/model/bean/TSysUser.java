package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.auto._TSysUser;
import org.apache.cayenne.Cayenne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TSysUser extends _TSysUser {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public final static int DELETE_MARK_EXIST = 0;
    public final static int DELETE_MARK_DELETE = 1;

    public int getId() {
        return Cayenne.intPKForObject(this);
    }

    public TMtCompany getCompany() {
        try {
            return super.getCompany();
        } catch (Exception e) {
            return null;
        }
    }

    public List<TMtCompany> getAllCompany() {
        TMtCompany mtCompany = getCompany();
        if (null == mtCompany) return null;
        return getChildCompany(mtCompany);
    }

    private List<TMtCompany> getChildCompany(TMtCompany mtCompany) {
        if (null == mtCompany) return null;
        List<TMtCompany> list = new ArrayList<>();
        list.add(mtCompany);
        if (null != mtCompany.getCompanyLevel() && mtCompany.getCompanyLevel() != TMtCompany.LEVEL_THREE) {
            List<TMtCompany> childCompanyList = mtCompany.getChildCompany();
            for (TMtCompany childCompany : childCompanyList) {
                list.addAll(getChildCompany(childCompany));
            }
        }
        return list;
    }

    public Set<String> getAllPermission() {
        List<TSysUserRole> list = getUserRole();
        List<TSysRoleElement> roleElements = new ArrayList<>();
        Set<String> elementSet = new HashSet<>();
        for (TSysUserRole sysUserRole : list) {
            roleElements.addAll(sysUserRole.getRole().getRoleElement());
        }
        for (TSysRoleElement sysRoleElements : roleElements) {
            elementSet.add(sysRoleElements.getElement().getElementUrl());
        }
        return elementSet;
    }

    public String getOpenId() {
        List<TSysUserBind> list = getBind();
        for (TSysUserBind sysUserBind : list) {
            if (sysUserBind.getBindType().equals(TSysUserBind.BIND_TYPE_WECHAT)) {
                return sysUserBind.getOuterId();
            }
        }
        return null;
    }

    private String mpHeadImgUrl;

    public String getMpHeadImgUrl() {
        return mpHeadImgUrl;
    }

    public void setMpHeadImgUrl(String mpHeadImgUrl) {
        this.mpHeadImgUrl = mpHeadImgUrl;
    }


    public boolean noGiroAccount() {
        TMtCustomer customer = this.getCompany().getCustomer();
        return StringUtil.isEmpty(customer.getGiroAccount()) || StringUtil.isEmpty(customer.getGiroAccountName());
    }

    public boolean isAdmin() {
        TMtCompany mtCompany = this.getCompany();
        int roleType = TSysRole.ROLE_TYPE_SYSTEM;
        if (!mtCompany.getCompanyLevel().equals(TMtCompany.LEVEL_ONE)) {
            roleType = TSysRole.ROLE_TYPE_PUBLIC;
        }
        List<TSysUserRole> roleList = this.getUserRole();
        for (TSysUserRole sysUserRole : roleList) {
            if (sysUserRole.getRole().getRoleLevel().equals(mtCompany.getCompanyLevel()) && sysUserRole.getRole().getRoleType().equals(roleType)) {
                return true;
            }
        }
        return false;
    }
}
