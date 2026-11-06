package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.model.bean.TMtBeneficiaryInfo;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtBeneficiaryDao;
import com.kdx.vms.service.IMtBeneficiaryService;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2018/2/26.
 */
@Service
public class MtBeneficiaryServiceImpl implements IMtBeneficiaryService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MtBeneficiaryDao mtBeneficiaryDao;

    @Override
    public Map<Integer, TMtBeneficiaryInfo> findPayInfo(TSysUser sysUser) {
        try {
            Map<Integer, TMtBeneficiaryInfo> map = new HashMap<>();
            List<TMtBeneficiaryInfo> list = mtBeneficiaryDao.findBeneficiaryInfo(sysUser).stream().map(TMtBeneficiaryInfo.class::cast).collect(Collectors.toList());
            list.forEach(beneficiaryInfo -> map.put(beneficiaryInfo.getBeneficiaryType(), beneficiaryInfo));
            return map;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyMap();
    }

    @Override
    public String checkPayInfo(TSysUser sysUser) {
        try {
            TMtCompany company = sysUser.getCompany();
            List<TMtBeneficiaryInfo> list = null;
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE:
                    return null;
//                    return "一级公司不可配置独立收款";
                case TMtCompany.LEVEL_TWO:
                    list = mtBeneficiaryDao.findBeneficiaryInfoByCompanies(sysUser.getObjectContext(), sysUser.getCompany().getChildCompany()).stream().map(TMtBeneficiaryInfo.class::cast).collect(Collectors.toList());
                    if (CheckUtils.check(list)) {
                        return "您的下级公司开启了独立收款，不可重复开启";
                    } else {
                        return null;
                    }
                case TMtCompany.LEVEL_THREE:
                    List<TMtCompany> companyList = new ArrayList<>();
                    TMtCompany parentCompany = sysUser.getCompany().getParentCompany();
                    if (parentCompany.getCompanyLevel() == TMtCompany.LEVEL_TWO) {
                        companyList.add(sysUser.getCompany().getParentCompany());
                        list = mtBeneficiaryDao.findBeneficiaryInfoByCompanies(sysUser.getObjectContext(), companyList).stream().map(TMtBeneficiaryInfo.class::cast).collect(Collectors.toList());
                        if (CheckUtils.check(list)) {
                            return "您的上级公司开启了独立收款，不可重复开启";
                        } else {
                            return null;
                        }
                    } else {
                        return null;
                    }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "系统错误";
    }

    @Override
    public List<Map<String, String>> findEnabledCompany(ObjectContext context) {
        try {
            List<TMtBeneficiaryInfo> list = mtBeneficiaryDao.findEnabledBeneficiary(context).stream().map(TMtBeneficiaryInfo.class::cast).collect(Collectors.toList());
            Map<TMtCompany, List<TMtBeneficiaryInfo>> map = list.stream().collect(Collectors.groupingBy(TMtBeneficiaryInfo::getCompany));
            Set<TMtCompany> companySet = map.keySet();
            List<Map<String, String>> resultList = new ArrayList<>();
            companySet.forEach(company -> {
                Map<String, String> result = new HashMap<>();
                result.put("companyName", company.getCompanyName());
                result.put("enableTime", DateUtils.getTime2(map.get(company).get(0).getEnableTime()));
                resultList.add(result);
            });
            return resultList;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return Collections.emptyList();
    }
}
