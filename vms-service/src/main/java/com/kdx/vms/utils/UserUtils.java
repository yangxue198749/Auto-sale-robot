package com.kdx.vms.utils;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 *
 * Created by liyang on 16-12-12.
 */
public class UserUtils {

    private static final Logger logger = LoggerFactory.getLogger(UserUtils.class);

    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

    private static TSysUser vmsSystem;
    private static TSysUser erpSystem;



    /**
     * 获取系统用户
     * 由系统创建或修改的数据CTOR或UTOR使用该用户
     * @return
     */
    public static TSysUser getVmsSystem() {
        if (null == vmsSystem) {
            synchronized (UserUtils.class) {
                try {
                    Expression expression = ExpressionFactory.matchExp(TSysUser.USER_ACCOUNT_PROPERTY, "vms_system");
                    SelectQuery selectQuery = new SelectQuery(TSysUser.class, expression);
                    List list = context.performQuery(selectQuery);
                    if (null != list) {
                        if (list.size() >= 1) {
                            vmsSystem = (TSysUser) list.get(0);
                        } else {
                            TSysUser vmsSystemTemp = context.newObject(TSysUser.class);
                            vmsSystemTemp.setCtime(new Date());
                            vmsSystemTemp.setUserName("vms");
                            vmsSystemTemp.setUserAccount("vms_system");
                            vmsSystemTemp.setLockMark(1);
                            context.commitChanges();
                            vmsSystem = vmsSystemTemp;
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage(), e);
                }
            }
        }
        return vmsSystem;
    }

    /**
     * 获取系统用户
     * 由系统创建或修改的数据CTOR或UTOR使用该用户
     * @return
     */
    public static TSysUser getErpSystem() {
        if (null == erpSystem) {
            synchronized (UserUtils.class) {
                try {
                    Expression expression = ExpressionFactory.matchExp(TSysUser.USER_ACCOUNT_PROPERTY, "erp_system");
                    SelectQuery selectQuery = new SelectQuery(TSysUser.class, expression);
                    List list = context.performQuery(selectQuery);
                    if (null != list) {
                        if (list.size() >= 1) {
                            erpSystem = (TSysUser) list.get(0);
                        } else {
                            TSysUser erpSystemTemp = context.newObject(TSysUser.class);
                            erpSystemTemp.setCtime(new Date());
                            erpSystemTemp.setUserName("erp");
                            erpSystemTemp.setUserAccount("erp_system");
                            erpSystemTemp.setLockMark(1);
                            context.commitChanges();
                            erpSystem = erpSystemTemp;
                        }
                    }
                } catch (Exception e) {
                    logger.error(e.getLocalizedMessage(), e);
                }
            }
        }
        return erpSystem;
    }

}
