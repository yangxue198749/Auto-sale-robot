package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TMtVmAlarmSubscription;
import com.kdx.vms.orm.dao.TMtVmAlarmSubscriptionDao;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("tMtVmAlarmSubscriptionDao")
public class TMtVmAlarmSubscriptionDaoImpl implements TMtVmAlarmSubscriptionDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

}
