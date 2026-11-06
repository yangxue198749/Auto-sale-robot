package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.SysUserDao;
import com.kdx.vms.orm.dao.TaskVmDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by liyang on 16-12-6.
 */
@Service("taskVmDao")
public class TaskVmDaoImpl implements TaskVmDao {

    private final Logger logger = LoggerFactory.getLogger(TaskVmDao.class);
    @Resource
    private SysUserDao sysUserDao;

    public List hasTaskVm(TMtVm mtVm) {
        logger.debug("mtVm:" + mtVm);
        if (null == mtVm) return null;
        try {
            Integer day = mtVm.getCreateTaskCycle();
            if (null == day) {
                day = TMtVm.TASK_CYCLE_DEFAULT;
                mtVm.setCreateTaskCycle(TMtVm.TASK_CYCLE_DEFAULT);
            }
            Expression expression = ExpressionFactory.betweenExp(TTaskVm.TASK_TIME_PROPERTY, DateUtils.getDayBeforeMin(day - 1), DateUtils.getToDayMax());
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_TYPE_PROPERTY, TTaskVm.TYPE_DELIVERY));
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_LEVEL_PROPERTY, TTaskVm.LEVEL_AUTO));
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_VM_STATUS_PROPERTY, TTaskVm.STATUS_FINISHED));
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.MT_VM_PROPERTY, mtVm));
            SelectQuery selectQuery = new SelectQuery(TTaskVm.class, expression);
            return mtVm.getObjectContext().performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TTaskVm> findPage(ObjectContext context, TSysUser user,
                                  Integer searchField, String searchFieldValue, Integer taskVmStatus,
                                  String startTime, String endTime, Integer taskType,
                                  Integer pageSize) {
        try {
            Expression expression = ExpressionFactory.expTrue();
            TMtCompany company = user.getCompany();
            Integer level = company.getCompanyLevel();
            switch (level) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TSysUser.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TSysUser.COMPANY_PROPERTY, company));
                    expression = expression.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    expression = expression.andExp(ExpressionFactory.matchExp(TSysUser.COMPANY_PROPERTY, company));
                    break;
            }
            if (null != searchField && !StringUtil.isEmpty(searchFieldValue) && !StringUtil.isEmpty(searchFieldValue.trim())) {
                //1:售货机编号 ;2:点位名称 ; 3:负责人  4: 硬件编号（撤机）
                switch (searchField) {
                    case 1:
                        expression = ExpressionUtils.fillInnerCodeQualifier(expression, TTaskVm.MT_VM_PROPERTY + "." + TMtVm.INNER_CODE_PROPERTY, searchFieldValue);
                        break;
                    case 2:
                        expression = expression.andExp(ExpressionFactory.likeExp(TTaskVm.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + searchFieldValue.trim() + "%"));
                        break;
                    case 3:
                        List<Integer> userIdList = new ArrayList<Integer>();
                        List<TSysUser> userList = sysUserDao.findSysUserByName(context, searchFieldValue.trim());
                        for (TSysUser u : userList) {
                            userIdList.add(u.getId());
                        }
                        expression = expression.andExp(ExpressionFactory.inExp(TTaskVm.OWNER_PROPERTY, userIdList));
                        break;
                    case 4:
                        expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.MT_VM_PROPERTY + "." + TMtVm.HARDWARE_CODE_PROPERTY, "%" + searchFieldValue.trim() + "%"));
                        break;
                    default:
                        break;
                }
            }
            if (null != taskVmStatus) {
                expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_VM_STATUS_PROPERTY, taskVmStatus));
            }
            if (null != taskType) {
                expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_TYPE_PROPERTY, taskType));
            }
            if (!StringUtil.isEmpty(startTime)) {
                expression = expression.andExp(ExpressionFactory.greaterOrEqualExp(TTaskVm.TASK_TIME_PROPERTY, StringUtil.strToDate(startTime + " 00:00:00", StringUtil.Pattern)));
            }
            if (!StringUtil.isEmpty(endTime)) {
                expression = expression.andExp(ExpressionFactory.lessOrEqualExp(TTaskVm.TASK_TIME_PROPERTY, StringUtil.strToDate(endTime + " 23:59:59", StringUtil.Pattern)));
            }
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            if (IntegerUtils.isPositiveInteger(pageSize)) select.setPageSize(pageSize);
            select.addOrdering(TTaskVm.CTIME_PROPERTY, SortOrder.DESCENDING);
            return context.performQuery(select);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TTaskVm getUnDealTaskToday(ObjectContext context, TMtVm mtVm,
                                      Integer taskType, String day) {
        try {

            Expression expression = ExpressionFactory.matchExp(TTaskVm.MT_VM_PROPERTY, mtVm);
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_TYPE_PROPERTY, taskType));
            expression = expression.andExp(ExpressionFactory.noMatchExp(TTaskVm.TASK_VM_STATUS_PROPERTY, TTaskVm.STATUS_FINISHED));
            expression = expression.andExp(ExpressionFactory.noMatchExp(TTaskVm.TASK_VM_STATUS_PROPERTY, TTaskVm.STATUS_CLOSED));
            if (!StringUtil.isEmpty(day)) {
                if (taskType == TTaskVm.TYPE_DELIVERY) {
                    Date startTime = DateUtils.findTheLastDate();
                    Date endTime = new Date();
                    expression = expression.andExp(ExpressionFactory.betweenExp(TTaskVm.TASK_TIME_PROPERTY, startTime, endTime));
                } else {
                    Date startTime = StringUtil.strToDate(day + " 00:00:00", StringUtil.Pattern);
                    Date endTime = StringUtil.strToDate(day + " 23:59:59", StringUtil.Pattern);
                    expression = expression.andExp(ExpressionFactory.betweenExp(TTaskVm.TASK_TIME_PROPERTY, startTime, endTime));
                }
            }
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            select.setPageSize(1);
            List<TTaskVm> list = context.performQuery(select);
            if (list != null && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TTaskVm findByPk(ObjectContext context, Integer taskVmId) {
        try {
            return Cayenne.objectForPK(context, TTaskVm.class, taskVmId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findByOwner(ObjectContext context, Integer userId, Integer taskType) {
        try {
            Expression expression = ExpressionFactory.matchExp(TTaskVm.OWNER_PROPERTY, userId);
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_TYPE_PROPERTY, taskType));
            expression = expression.andExp(ExpressionFactory.noMatchExp(TTaskVm.TASK_VM_STATUS_PROPERTY, TTaskVm.STATUS_FINISHED));
            expression = expression.andExp(ExpressionFactory.noMatchExp(TTaskVm.TASK_VM_STATUS_PROPERTY, TTaskVm.STATUS_CLOSED));
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            select.addPrefetch(TTaskVm.MT_VM_PROPERTY);
            select.addPrefetch(TTaskVm.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY);
            List<TTaskVm> list = context.performQuery(select);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findTaskByOwnerAndDay(ObjectContext context, Integer userId, String day, Integer taskType) {
        try {

            Expression expression = ExpressionFactory.matchExp(TTaskVm.OWNER_PROPERTY, userId);
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_TYPE_PROPERTY, taskType));
            if (!StringUtil.isEmpty(day)) {
                if (taskType == TTaskVm.TYPE_DELIVERY) {
                    Date startTime = DateUtils.findTheLastDate();
                    Date endTime = new Date();
                    expression = expression.andExp(ExpressionFactory.betweenExp(TTaskVm.TASK_TIME_PROPERTY, startTime, endTime));
                } else {
                    Date startTime = StringUtil.strToDate(day + " 00:00:00", StringUtil.Pattern);
                    Date endTime = StringUtil.strToDate(day + " 23:59:59", StringUtil.Pattern);
                    expression = expression.andExp(ExpressionFactory.betweenExp(TTaskVm.TASK_TIME_PROPERTY, startTime, endTime));
                }
            }
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            return context.performQuery(select);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findFinishTaskByOwnerAndDay(ObjectContext context, Integer userId, String day, Integer taskType) {
        try {

            Expression expression = ExpressionFactory.matchExp(TTaskVm.OWNER_PROPERTY, userId);
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_TYPE_PROPERTY, taskType));
            if (!StringUtil.isEmpty(day)) {
                if (taskType == TTaskVm.TYPE_DELIVERY) {
                    Date startTime = DateUtils.findTheLastDate();
                    Date endTime = new Date();
                    expression = expression.andExp(ExpressionFactory.betweenExp(TTaskVm.TASK_TIME_PROPERTY, startTime, endTime));
                } else {
                    Date startTime = StringUtil.strToDate(day + " 00:00:00", StringUtil.Pattern);
                    Date endTime = StringUtil.strToDate(day + " 23:59:59", StringUtil.Pattern);
                    expression = expression.andExp(ExpressionFactory.betweenExp(TTaskVm.TASK_TIME_PROPERTY, startTime, endTime));
                }
            }
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            return context.performQuery(select);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TTaskVm findByVmId(ObjectContext objectContext, Integer vmId, Integer ownerId, Integer deliveryPattern) {
        try {
            Expression expression = ExpressionFactory.matchExp(TTaskVm.MT_VM_PROPERTY, vmId);
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.DELIVERY_PATTERN_PROPERTY, deliveryPattern));
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.OWNER_PROPERTY, ownerId));
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            select.addPrefetch(TTaskVm.TASK_VM_CHANNEL_PROPERTY + "." + TTaskVmChannel.SKU_PROPERTY);
            select.addPrefetch(TTaskVm.TASK_VM_CHANNEL_PROPERTY + "." + TTaskVmChannel.SKU_PROPERTY + "." + TMtSku.SKU_IMG_PROPERTY);
            select.addPrefetch(TTaskVm.TASK_VM_CHANNEL_PROPERTY + "." + TTaskVmChannel.CHANNEL_PROPERTY);
            select.addPrefetch(TTaskVm.TASK_VM_CHANNEL_PROPERTY);
            List<TTaskVm> list = objectContext.performQuery(select);
            if (null != list && list.size() > 0) {
                return list.get(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List refreshChannel(TTaskVm taskVm) {
        try {
            Expression qualifier = ExpressionUtils.fillMatchQualifier(null, TMtVm.INNER_CODE_PROPERTY, taskVm.getMtVm().getInnerCode());
            SelectQuery query = new SelectQuery(TMtVm.class, qualifier);
            query.addPrefetch(TMtVm.CHANNEL_INFO_PROPERTY);
            query.addPrefetch(TMtVm.CHANNEL_NEXT_PROPERTY);
            query.addPrefetch(TMtVm.CHANNEL_INFO_PROPERTY + "." + TMtVmChannel.SKU_PROPERTY);
            query.addPrefetch(TMtVm.CHANNEL_NEXT_PROPERTY + "." + TMtVmChannel.SKU_PROPERTY);
            return taskVm.getObjectContext().performQuery(query);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<?> findDeployTaskByVm(TMtVm mtVm) {
        try {
            Expression expression = ExpressionFactory.matchExp(TTaskVm.MT_VM_PROPERTY, mtVm);
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            return mtVm.getObjectContext().performQuery(select);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List searchFinishTask(ObjectContext context, TMtVm mtVm, Integer taskType) {
        try {
            Expression expression = ExpressionFactory.matchExp(TTaskVm.MT_VM_PROPERTY, mtVm);
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_TYPE_PROPERTY, taskType));
            expression = expression.andExp(ExpressionFactory.matchExp(TTaskVm.TASK_VM_STATUS_PROPERTY, TTaskVm.STATUS_FINISHED));
            expression = ExpressionUtils.fillBeginTimeQualifier(expression, TTaskVm.FINISH_TIME_PROPERTY, DateUtils.getTime2(DateUtils.findTheLastDate()));
            expression = ExpressionUtils.fillEndTimeQualifier(expression, TTaskVm.FINISH_TIME_PROPERTY, DateUtils.getTime2());
            SelectQuery select = new SelectQuery(TTaskVm.class, expression);
            return context.performQuery(select);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

}
