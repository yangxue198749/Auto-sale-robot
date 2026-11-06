package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtVmEventMasterDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by yangshuxi on 2016/12/5.
 */
@Repository
public class MtVmEventMasterDaoImpl implements MtVmEventMasterDao {
    @Override
    public List<TMtVmEventMaster> findMtEventMaster(ObjectContext context, TMtCompany company, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId, Integer pageSize) {
        Expression qualifier = ExpressionFactory.greaterOrEqualExp(TMtVmEventMaster.END_DATE_PROPERTY, new Date());
        switch (company.getCompanyLevel()) {
            case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                break;
            case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                Expression companyExp = ExpressionFactory.inExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.COMPANY_PROPERTY, company.getChildCompany());
                companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.COMPANY_PROPERTY, company));
                qualifier = qualifier.andExp(companyExp);
                break;
            case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.COMPANY_PROPERTY, company));
                break;
            default:
                return null;
        }
        qualifier = qualifier.andExp(ExpressionFactory.greaterOrEqualExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.DELETE_MARK_PROPERTY, 0));
        qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.INNER_CODE_PROPERTY, innerCode);
        if (!StringUtil.isEmpty(nodeName)) {
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + nodeName + "%"));
        }
        if (null != province) {
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.PROVINCE_ID_PROPERTY, province));
        }
        if (null != city) {
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.CITY_ID_PROPERTY, city));
        }
        if (null != area) {
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.AREA_ID_PROPERTY, area));
        }
        if (null != businessDistrictId && businessDistrictId > 0) {
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY + "." + TMtNode.BUSINESS_DISTRICT_ID_PROPERTY, businessDistrictId));
        }
        SelectQuery select = new SelectQuery(TMtVmEventMaster.class, qualifier);
        select.setPageSize(pageSize);
        select.addPrefetch(TMtVmEventMaster.MT_VM_PROPERTY);
        select.addPrefetch(TMtVmEventMaster.MT_VM_PROPERTY + "." + TMtVm.NODE_PROPERTY);
        select.addPrefetch(TMtVmEventMaster.EVENTS_PROPERTY);
        List list = context.performQuery(select);
        return list;
    }

    @Override
    public List<TMtVm> findSkuEventVmList(ObjectContext context, TMtCompany company, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId, Integer[] removeIds) {
        List<Integer> idList = new ArrayList<>();
        if (removeIds != null && removeIds.length > 0) idList = Arrays.asList(removeIds);
        List<TMtVm> vmList = this.findVmList(context, company, nodeName, innerCode, province, city, area, businessDistrictId);
        for (int i = 0; i < vmList.size(); i++) {
            TMtVm mtVm = vmList.get(i);
            if (hasEvent(mtVm, TStVmEvent.EVENT_TYPE_SKU) || idList.contains(mtVm.getId())) {
                vmList.remove(i);
                i--;
            }
        }
        return vmList;
    }

    @Override
    public List<TMtVm> findVmEventVmList(ObjectContext context, TMtCompany company, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId, Integer[] removeIds) {
        List<Integer> idList = new ArrayList<>();
        if (removeIds != null && removeIds.length > 0) idList = Arrays.asList(removeIds);
        List<TMtVm> vmList = this.findVmList(context, company, nodeName, innerCode, province, city, area, businessDistrictId);
        for (int i = 0; i < vmList.size(); i++) {
            TMtVm mtVm = vmList.get(i);
            if (hasEvent(mtVm, TStVmEvent.EVENT_TYPE_VM) || idList.contains(mtVm.getId())) {
                vmList.remove(i);
                i--;
            }
        }
        return vmList;
    }

    @Override
    public List<TMtVm> findEventVmList(ObjectContext context, TMtCompany mtCompany, String time) {
        Expression qualifier = ExpressionFactory.matchExp(TMtVm.DELETE_MARK_PROPERTY, 0);
        switch (mtCompany.getCompanyLevel()) {
            case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                break;
            case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                Expression companyExp = ExpressionFactory.inExp(TMtVm.COMPANY_PROPERTY, mtCompany.getChildCompany());
                companyExp = companyExp.orExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, mtCompany));
                qualifier = qualifier.andExp(companyExp);
                break;
            case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.COMPANY_PROPERTY, mtCompany));
                break;
            default:
                return null;
        }
        if (!StringUtil.isEmpty(time)) {
            qualifier = qualifier.andExp(ExpressionFactory.lessOrEqualExp(TMtVm.EVENT_PROPERTY + "." + TMtVmEventMaster.BEGIN_DATE_PROPERTY, StringUtil.strToDate(time + " 23:59:59", StringUtil.Pattern)));
        }
        if (!StringUtil.isEmpty(time)) {
            qualifier = qualifier.andExp(ExpressionFactory.greaterOrEqualExp(TMtVm.EVENT_PROPERTY + "." + TMtVmEventMaster.END_DATE_PROPERTY, StringUtil.strToDate(time + " 00:00:00", StringUtil.Pattern)));
        }
        SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
        List<TMtVm> list = context.performQuery(select);
        return list;
    }

    private List<TMtVm> findVmList(ObjectContext context, TMtCompany company, String nodeName, String innerCode, Integer province, Integer city, Integer area, Integer businessDistrictId) {
        Expression qualifier = ExpressionFactory.greaterOrEqualExp(TMtVm.DELETE_MARK_PROPERTY, 0);
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
        qualifier = ExpressionUtils.fillInnerCodeQualifier(qualifier, TMtVm.INNER_CODE_PROPERTY, innerCode);
        if (!StringUtil.isEmpty(nodeName)) {
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVm.NODE_PROPERTY + "." + TMtNode.NODE_NAME_PROPERTY, "%" + nodeName + "%"));
        }
        if (null != province) {
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.PROVINCE_ID_PROPERTY, province));
        }
        if (null != city) {
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.CITY_ID_PROPERTY, city));
        }
        if (null != area) {
            qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtVm.NODE_PROPERTY + "." + TMtNode.AREA_ID_PROPERTY, area));
        }
        if (null != businessDistrictId && businessDistrictId > 0) {
            qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtVm.NODE_PROPERTY + "." + TMtNode.BUSINESS_DISTRICT_ID_PROPERTY, businessDistrictId));
        }
        qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(TMtVm.VM_TYPE_PROPERTY , TMtVmModel.COMBINATORIAL_EXTENTION));
        SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
        select.addPrefetch(TMtVm.NODE_PROPERTY);
        select.addPrefetch(TMtVm.EVENT_PROPERTY);
        List<TMtVm> list = context.performQuery(select);

        return list;
    }

    private boolean hasEvent(TMtVm mtVm, Integer eventType) {
        List<TMtVmEventMaster> list = mtVm.getEvent();
        if (null == list || list.isEmpty()) return false;
        for (TMtVmEventMaster mtVmEventMaster : list) {
            if (null != mtVmEventMaster.getEventType() && mtVmEventMaster.getEventType().equals(eventType) && null != mtVmEventMaster.getEndDate() && mtVmEventMaster.getEndDate().after(new Date())) {
                return true;
            }
        }
        return false;
    }
}
