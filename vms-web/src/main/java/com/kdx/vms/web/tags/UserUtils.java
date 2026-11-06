package com.kdx.vms.web.tags;

import com.kdx.vms.model.bean.TSysRole;
import com.kdx.vms.model.bean.TSysRoleElement;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.bean.TSysUserRole;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Created by yangshuxi on 2016/11/30.
 */
public class UserUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

    public static TSysUser getUser() {
        SSOToken token = TokenUtil.getToken();
        return token.getSysUser();
    }

    public static TSysUser get(Integer id){
        if (null == id || id <= 0 ) return null;
        return Cayenne.objectForPK(context, TSysUser.class, id);
    }
    public static void main(String[] args) {
        ObjectContext context= ObjectContextFactory.getInstance().newObjectContext();
        Integer userId=1101;
        TSysUser sysUser=Cayenne.objectForPK(context,TSysUser.class,userId);
//        System.out.println(sysUser.getAllPermission());
        Expression expression = ExpressionFactory.matchExp(TSysUserRole.USER_PROPERTY, sysUser);
        SelectQuery selectQuery = new SelectQuery(TSysUserRole.class, expression);
        selectQuery.addPrefetch(TSysUserRole.ROLE_PROPERTY);
        selectQuery.addPrefetch(TSysUserRole.ROLE_PROPERTY+"."+ TSysRole.ROLE_ELEMENT_PROPERTY);
        selectQuery.addPrefetch(TSysUserRole.ROLE_PROPERTY+"."+TSysRole.ROLE_ELEMENT_PROPERTY+"."+ TSysRoleElement.ELEMENT_PROPERTY);
        sysUser.getObjectContext().performQuery(selectQuery);
        System.out.println("==========================================================");
        System.out.println(sysUser.getAllPermission());
    }

}
