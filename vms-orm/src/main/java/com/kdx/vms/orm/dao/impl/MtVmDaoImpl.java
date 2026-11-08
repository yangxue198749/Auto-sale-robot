package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.orm.dao.MtVmDao;
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

import java.util.Collections;
import java.util.List;

@Service("mtVmDao")
public class MtVmDaoImpl implements MtVmDao {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public TMtVm findByVmId(ObjectContext context, Integer vmId) {
        try {
            return Cayenne.objectForPK(context, TMtVm.class, vmId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtVm> findListByCondition(ObjectContext context,
                                           Integer filedType, String filedValue, Integer modelId,
                                           Integer vmStatus, Integer province, Integer city, Integer country,
                                           Integer bussinessId, TSysUser user, Integer pageSize, Boolean showPattern) {
        try {
            TMtCompany company = user.getCompany();
            int level = company.getCompanyLevel();
            Expression qualifier = ExpressionFactory.expTrue();
            qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtVm.DELETE_MARK_PROPERTY, DeleteMark.EXIST.getCode());
            switch (level) {
                case TMtCompany.LEVEL_ONE:
                    break;
                case TMtCompany.LEVEL_TWO:
                    Expression expression = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    expression = expression.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(expression);
                    break;
                case TMtCompany.LEVEL_THREE:
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    break;

                default:
                    break;
            }
            if (!StringUtil.isEmpty(filedValue)) {
                if (null != filedType) {
                    switch (filedType) {
                        case 1:
                            //1售货机编码，2点位，3硬件编码
                            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TMtVm.INNER_CODE_PROPERTY, filedValue.trim());
                            break;
                        case 2:
                            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + filedValue.trim() + "%"));
                            break;
                        case 3:
                            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVm.HARDWARE_CODE_PROPERTY, "%" + filedValue.trim() + "%"));
                            break;
                        case 4:
                            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVm.COMPANY_PROPERTY + "." + TMtCompany.COMPANY_NAME_PROPERTY, "%" + filedValue.trim() + "%"));
                            break;
                        default:
                            break;
                    }
                }
            }
            if (null != modelId) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.VM_MODEL_ID_PROPERTY, modelId));
            }
            if (null != vmStatus) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_PROPERTY, vmStatus));
            }
            if (null != province) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.PROVINCE_ID_PROPERTY, province));
            }
            if (null != city) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.CITY_ID_PROPERTY, city));
            }
            if (null != country) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.AREA_ID_PROPERTY, country));
            }
            if (null != bussinessId) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.BUSINESS_DISTRICT_ID_PROPERTY, bussinessId));
            }
            if (Boolean.TRUE.equals(showPattern)) {
                Expression q1 = ExpressionFactory.matchExp(TMtVm.VM_TYPE_PROPERTY, TMtVmModel.COMBINATORIAL_SINGLE);
                Expression q2 = ExpressionFactory.matchExp(TMtVm.VM_TYPE_PROPERTY, TMtVmModel.COMBINATORIAL_MAIN);
                q1 = q1.orExp(q2);
                qualifier = qualifier.andExp(q1);
            }


            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.addPrefetch(TMtVm.NODE_PROPERTY);
            select.addPrefetch(TMtVm.SIM_PROPERTY);
            select.setPageSize(pageSize);
            select.addOrdering(TMtVm.INNER_CODE_PROPERTY, SortOrder.ASCENDING);
            List<TMtVm> list = context.performQuery(select);
            return list;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtVm> findAllMtVm(ObjectContext context, TSysUser user) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, user.getCompany());
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, DeleteMark.EXIST.getCode()));
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            List<TMtVm> list = context.performQuery(select);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findAllMtVmByStatus(ObjectContext context, int vmStatus) {
        logger.debug("context:" + context + ", vmStatus:" + vmStatus);
        if (null == context) return null;
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, 0);
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_PROPERTY, vmStatus));
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.addPrefetch(TMtVm.NODE_PROPERTY);
            select.addPrefetch(TMtVm.NODE_PROPERTY + "." + TMtNode.DELIVERY_PROPERTY);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findAllMtVmByDelivery(ObjectContext context) {
        logger.debug("context:" + context);
        if (null == context) return null;
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, 0);
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_PROPERTY, TMtVm.STATUS_SALE));
            qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.DELIVERY_PROPERTY, null));
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.addPrefetch(TMtVm.NODE_PROPERTY + "." + TMtNode.DELIVERY_PROPERTY);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List getAllVm(ObjectContext context) {
        logger.debug("context:" + context);
        if (null == context) return null;
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, 0)
                    .andExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_PROPERTY, TMtVm.STATUS_SALE))
                    . andExp(ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY,DeleteMark.EXIST.getCode()));
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List<TMtVm> findAllMtVmByCompanyId(ObjectContext context, Integer companyId) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, companyId)
                    .andExp(ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY,DeleteMark.EXIST.getCode()));
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            List<TMtVm> list = context.performQuery(select);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<TMtVm> findAllMtVmByCompany(ObjectContext context, TMtCompany company) {
        SelectQuery query = new SelectQuery(TMtVm.class);
        switch (company.getCompanyLevel()) {
            case TMtCompany.LEVEL_TWO:
                Expression inExp = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                inExp = inExp.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                query.andQualifier(inExp);
                break;
            case TMtCompany.LEVEL_THREE:
                query.andQualifier(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                break;

            default:
                break;
        }
        query.addOrdering(TMtVm.INNER_CODE_PROPERTY, SortOrder.ASCENDING);
        return context.performQuery(query);
    }

    @Override
    public List findPage(ObjectContext context, TSysUser user,
                         String searchNodeName, String searchInnerCode, Integer pageSize, Integer vmStatus) {
        try {
            TMtCompany company = user.getCompany();
            int level = company.getCompanyLevel();
            Expression qualifier = ExpressionFactory.expTrue();
            qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtVm.DELETE_MARK_PROPERTY, DeleteMark.EXIST.getCode());
            switch (level) {
                case TMtCompany.LEVEL_ONE:
                    break;
                case TMtCompany.LEVEL_TWO:
                    Expression expression = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    expression = expression.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(expression);
                    break;
                case TMtCompany.LEVEL_THREE:
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    break;

                default:
                    break;
            }
            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TMtVm.INNER_CODE_PROPERTY, searchInnerCode);
            if (!StringUtil.isEmpty(searchNodeName)) {
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + searchNodeName.trim() + "%"));
            }
            if (null != vmStatus) {
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_PROPERTY, vmStatus));
            }
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.setPageSize(pageSize);
            List<TMtVm> list = context.performQuery(select);
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public List findVmForStatus(TSysUser user, String innerCode, String nodeName, Integer netStatus, Integer vmcStatus, Integer taskStatus,Integer tempStatus, Integer pageSize) {
        logger.debug("user:" + user + ", innerCode:" + innerCode + ", nodeName:" + nodeName + ", netStatus:" + netStatus + ", vmcStatus:" + vmcStatus + ", taskStatus:" + taskStatus + ", tempStatus:" + tempStatus + ", pageSize:" + pageSize);
        if (null == user || null == pageSize) return null;
        try {
            Expression expression = ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, 0);
            expression = ExpressionUtils.fillCompanyQualifier(expression, TMtVm.COMPANY_PROPERTY, user);
            expression = ExpressionUtils.fillInnerCodeQualifier(expression, TMtVm.INNER_CODE_PROPERTY, innerCode);
            if (!StringUtil.isEmpty(nodeName)) {
                expression = expression.andExp(ExpressionFactory.likeExp(TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + nodeName + "%"));
            }
            if (null != netStatus) {
                expression = ExpressionUtils.fillMatchQualifier(expression, TMtVm.VM_STATUS_LOG_PROPERTY + "." + TVmStatus.ONLINE_STATUS_PROPERTY, netStatus);
            }
            if (null != vmcStatus) {
                expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_LOG_PROPERTY + "." + TVmStatus.VM_STATUS_PROPERTY, vmcStatus));
            }
            if (null != taskStatus) {
                Expression e = ExpressionFactory.matchExp(TMtVm.VM_STATUS_LOG_PROPERTY + "." + TVmStatus.TASK_STATUS_PROPERTY, taskStatus);
                if (taskStatus == 0) {
                    e = e.orExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_LOG_PROPERTY + "." + TVmStatus.TASK_STATUS_PROPERTY, null));
                }
                expression = expression.andExp(e);
//                expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_LOG_PROPERTY + "." + TVmStatus.TASK_STATUS_PROPERTY, taskStatus));
            }
            //温度状态
            if (null != tempStatus){
                Expression e = ExpressionFactory.matchExp(TMtVm.VM_STATUS_LOG_PROPERTY + "." + TVmStatus.TEMP_STATUS_PROPERTY, tempStatus);
                if (tempStatus == 3) {
                    e = e.orExp(ExpressionFactory.matchExp(TMtVm.VM_STATUS_LOG_PROPERTY + "." + TVmStatus.TEMP_STATUS_PROPERTY, null));
                }
                expression = expression.andExp(e);
            }
            SelectQuery selectQuery = new SelectQuery(TMtVm.class, expression);
            selectQuery.setPageSize(pageSize);
            selectQuery.addPrefetch(TMtVm.NODE_PROPERTY);
            selectQuery.addPrefetch(TMtVm.VM_STATUS_LOG_PROPERTY);
            selectQuery.addPrefetch(TMtVm.ENERGY_SAVE_PROPERTY);
            selectQuery.addOrdering(TMtVm.INNER_CODE_PROPERTY, SortOrder.ASCENDING);
            return user.getObjectContext().performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public List<TMtVm> findVmForStocks(TMtCompany company, String innerCode, String nodeName, Integer pageSize) {
        logger.debug("company:" + company + ", innerCode:" + innerCode + ", nodeName:" + nodeName + ", pageSize:" + pageSize);
        if (null == company || null == pageSize) return Collections.emptyList();
        try {
            Expression expression = ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, 0);
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    expression = expression.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    break;
                default:
                    return Collections.emptyList();
            }
            expression = ExpressionUtils.fillInnerCodeQualifier(expression, TMtVm.INNER_CODE_PROPERTY, innerCode);
            if (!StringUtil.isEmpty(nodeName)) {
                expression = expression.andExp(ExpressionFactory.likeExp(TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + nodeName + "%"));
            }

            expression = expression.andExp(ExpressionFactory.betweenExp(TMtVm.VM_STATUS_PROPERTY, TMtVm.STATUS_SALE, TMtVm.STATUS_UNSLAE));
            SelectQuery selectQuery = new SelectQuery(TMtVm.class, expression);
            selectQuery.setPageSize(pageSize);
            selectQuery.addPrefetch(TMtVm.NODE_PROPERTY);
            selectQuery.addOrdering(TMtVm.INNER_CODE_PROPERTY, SortOrder.ASCENDING);
            return company.getObjectContext().performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }

    @Override
    public List findVmByTime(ObjectContext context, TMtCompany company, String startTime, String endTime) {
        if (null == company) return null;
        try {
            Expression qualifier = ExpressionFactory.expTrue();
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
            if (!StringUtil.isEmpty(startTime)) {
                qualifier = qualifier.andExp(ExpressionFactory.greaterOrEqualExp(TMtVm.CTIME_PROPERTY, StringUtil.strToDate(startTime + " 00:00:00", StringUtil.Pattern)));
            }
            if (!StringUtil.isEmpty(endTime)) {
                qualifier = qualifier.andExp(ExpressionFactory.lessOrEqualExp(TMtVm.CTIME_PROPERTY, StringUtil.strToDate(endTime + " 23:59:59", StringUtil.Pattern)));
            }
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findOnSaleVm(ObjectContext context, TMtCompany company) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.VM_STATUS_PROPERTY, TMtVm.STATUS_SALE);
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.addPrefetch(TMtVm.CHANNEL_INFO_PROPERTY);
            select.addPrefetch(TMtVm.VM_STATUS_LOG_PROPERTY);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findOnSaleVm(TSysUser sysUser, String innerCode) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.VM_STATUS_PROPERTY, TMtVm.STATUS_SALE);
            qualifier = ExpressionUtils.fillCompanyQualifier(qualifier, TMtVm.COMPANY_PROPERTY, sysUser);
            qualifier = ExpressionUtils.fillMatchQualifier(qualifier, TMtVm.DELETE_MARK_PROPERTY, DeleteMark.EXIST.getCode());
            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TMtVm.INNER_CODE_PROPERTY, innerCode);
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.addPrefetch(TMtVm.CHANNEL_INFO_PROPERTY);
            select.addPrefetch(TMtVm.VM_STATUS_LOG_PROPERTY);
            select.addOrdering(TMtVm.INNER_CODE_PROPERTY, SortOrder.ASCENDING);
            return sysUser.getObjectContext().performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findAllVm(TSysUser sysUser, String innerCode) {
        try {
            Expression qualifier = ExpressionUtils.fillCompanyQualifier(null, TMtVm.COMPANY_PROPERTY, sysUser);
            qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TMtVm.INNER_CODE_PROPERTY, innerCode);
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.addOrdering(TMtVm.INNER_CODE_PROPERTY, SortOrder.ASCENDING);
            return sysUser.getObjectContext().performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findByUniqueCode(ObjectContext context, String uniqueCode) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.UNIQUE_CODE_PROPERTY, uniqueCode);
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }


    /*public static void main(String[] args) {
        ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
        TMtCompany company = Cayenne.objectForPK(context, TMtCompany.class, 1);
        List list = new MtVmDaoImpl().findVmForStatus(company, null, null, null, null, 10);
        System.out.println(list.size());
        for (Object obj : list) {
            TMtVm mtVm = (TMtVm) obj;
            System.out.println(mtVm.getAreaName());
            System.out.println(mtVm.getLastTime());
        }
    }*/

    @Override
    public TMtVm findByInnerCode(ObjectContext context, String innerCode) {
        try {
            Expression qualifier = ExpressionFactory.matchExp(TMtVm.INNER_CODE_PROPERTY, innerCode);
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            List<TMtVm> list = context.performQuery(select);
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
    public List<TMtVm> findByInnerCodeAndNodeName(ObjectContext context,
                                                  String innerCode, TSysUser user, String nodeName) {
        try {
            Expression expression = ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, 0);
            TMtCompany company = user.getCompany();
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    expression = expression.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
            expression = ExpressionUtils.fillInnerCodeQualifier(expression, TMtVm.INNER_CODE_PROPERTY, innerCode);
            if (!StringUtil.isEmpty(nodeName)) {
                expression = expression.andExp(ExpressionFactory.likeExp(TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + nodeName + "%"));
            }
            SelectQuery selectQuery = new SelectQuery(TMtVm.class, expression);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtVm> findForUpGrade(ObjectContext context, Integer vmModelId, Integer province, Integer city, Integer country, Integer pageSize) {
        try {
            Expression expression = ExpressionFactory.expTrue();
            if (null != vmModelId) {
                expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.VM_MODEL_ID_PROPERTY, vmModelId));
            }
            if (null != province) {
                expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.PROVINCE_ID_PROPERTY, province));
            }
            if (null != city) {
                expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.CITY_ID_PROPERTY, city));
            }
            if (null != country) {
                expression = expression.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.AREA_ID_PROPERTY, country));
            }
            SelectQuery selectQuery = new SelectQuery(TMtVm.class, expression);
            if (null != pageSize) {
                selectQuery.setPageSize(pageSize);
            }
            selectQuery.addPrefetch(TMtVm.VM_STATUS_LOG_PROPERTY);
            return context.performQuery(selectQuery);

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtVm> findUnInitStockStatusList(ObjectContext objectContext) {
        try {
            Expression expression = ExpressionFactory.matchExp(TMtVm.STOCK_STATUS_PROPERTY, null);
            SelectQuery selectQuery = new SelectQuery(TMtVm.class, expression);
            selectQuery.addPrefetch(TMtVm.CHANNEL_INFO_PROPERTY);
            return objectContext.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtPayType> findPayTypeByVmId(ObjectContext context, Integer vmId) {

        try {
            Expression expression = ExpressionFactory.expTrue();
            expression = expression.andExp(ExpressionFactory.matchExp(TMtPayType.MT_VM_PROPERTY, vmId));
            SelectQuery selectQuery = new SelectQuery(TMtPayType.class, expression);
            return context.performQuery(selectQuery);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List findStockMessage(ObjectContext context, TMtCompany company) {
        try {
            Expression qualifier = ExpressionFactory.expTrue();
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                    break;
                case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                    Expression companyExp = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                    companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    qualifier = qualifier.andExp(companyExp);
                    break;
                case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                    qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, company));
                    break;
                default:
                    return null;
            }
            SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
            select.addPrefetch(TMtVm.CHANNEL_INFO_PROPERTY);
            select.addPrefetch(TMtVm.VM_STATUS_LOG_PROPERTY);
            return context.performQuery(select);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}
