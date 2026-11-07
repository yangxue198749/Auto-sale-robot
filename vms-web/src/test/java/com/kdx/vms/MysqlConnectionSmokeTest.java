package com.kdx.vms;

import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.junit.Test;

import java.util.List;

/**
 * Phase 0 冒烟测试：验证 Cayenne 能通过 MySQL 驱动连接本地数据库并执行查询。
 * 不写入数据，只做读取，可重复执行。
 */
public class MysqlConnectionSmokeTest {

    @Test
    public void testCayenneConnectsToMysql() {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        List<TSysUser> users = context.performQuery(new SelectQuery(TSysUser.class));
        System.out.println("[SMOKE] Cayenne connected. T_SYS_USER row count = " + users.size());
        // 空表也代表连接 + 方言识别成功
        System.out.println("[SMOKE] OK: MySQL datasource reachable through Cayenne");
    }
}
