package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据范围服务
 * 根据公司层级提供相应的数据过滤表达式
 *
 * 一级公司：可看所有数据
 * 二级公司：可看本公司及子公司数据
 * 三级公司：只能看本公司数据
 */
@Service
public class DataScopeService {

    private static final Logger logger = LoggerFactory.getLogger(DataScopeService.class);

    /**
     * 获取公司范围过滤表达式
     *
     * @param user 当前用户
     * @param companyPropertyPath 公司属性路径（如 "company" 或 "task.company"）
     * @return Cayenne Expression，如果是一级公司则返回 null（表示不过滤）
     */
    public Expression getCompanyScopeExpression(TSysUser user, String companyPropertyPath) {
        if (user == null || user.getCompany() == null) {
            logger.warn("User or company is null, returning empty expression");
            return ExpressionFactory.expFalse();
        }

        TMtCompany company = user.getCompany();
        Integer level = company.getCompanyLevel();

        if (level == null) {
            logger.warn("Company level is null for company: {}, returning empty expression", company.getId());
            return ExpressionFactory.expFalse();
        }

        Expression expression = null;

        switch (level) {
            case TMtCompany.LEVEL_ONE:
                // 一级公司：不过滤，返回所有数据
                logger.debug("User {} belongs to LEVEL_ONE company, no scope filter applied", user.getId());
                return null;

            case TMtCompany.LEVEL_TWO:
                // 二级公司：本公司及子公司
                List<TMtCompany> childCompanies = company.getChildCompany();
                if (childCompanies != null && !childCompanies.isEmpty()) {
                    expression = ExpressionFactory.inExp(companyPropertyPath, childCompanies);
                    expression = expression.orExp(ExpressionFactory.matchExp(companyPropertyPath, company));
                    logger.debug("User {} belongs to LEVEL_TWO company {}, filtering {} companies",
                        user.getId(), company.getId(), childCompanies.size() + 1);
                } else {
                    expression = ExpressionFactory.matchExp(companyPropertyPath, company);
                    logger.debug("User {} belongs to LEVEL_TWO company {} with no children",
                        user.getId(), company.getId());
                }
                break;

            case TMtCompany.LEVEL_THREE:
                // 三级公司：只本公司
                expression = ExpressionFactory.matchExp(companyPropertyPath, company);
                logger.debug("User {} belongs to LEVEL_THREE company {}, filtering only this company",
                    user.getId(), company.getId());
                break;

            default:
                logger.warn("Unknown company level: {} for company: {}", level, company.getId());
                return ExpressionFactory.expFalse();
        }

        return expression;
    }

    /**
     * 检查用户是否可以访问指定公司的数据
     *
     * @param user 当前用户
     * @param targetCompany 目标公司
     * @return true 如果可以访问，false 否则
     */
    public boolean canAccessCompany(TSysUser user, TMtCompany targetCompany) {
        if (user == null || user.getCompany() == null || targetCompany == null) {
            return false;
        }

        TMtCompany userCompany = user.getCompany();
        Integer level = userCompany.getCompanyLevel();

        if (level == null) {
            return false;
        }

        switch (level) {
            case TMtCompany.LEVEL_ONE:
                // 一级公司可以访问所有
                return true;

            case TMtCompany.LEVEL_TWO:
                // 二级公司可以访问自己及子公司
                if (userCompany.getId().equals(targetCompany.getId())) {
                    return true;
                }
                List<TMtCompany> children = userCompany.getChildCompany();
                if (children != null) {
                    for (TMtCompany child : children) {
                        if (child.getId().equals(targetCompany.getId())) {
                            return true;
                        }
                    }
                }
                return false;

            case TMtCompany.LEVEL_THREE:
                // 三级公司只能访问自己
                return userCompany.getId().equals(targetCompany.getId());

            default:
                return false;
        }
    }

    /**
     * 获取用户可以访问的公司 ID 列表
     *
     * @param user 当前用户
     * @return 公司 ID 列表，如果是一级公司则返回 null（表示所有）
     */
    public List<Integer> getAccessibleCompanyIds(TSysUser user) {
        if (user == null || user.getCompany() == null) {
            return null;
        }

        TMtCompany company = user.getCompany();
        Integer level = company.getCompanyLevel();

        if (level == null) {
            return null;
        }

        switch (level) {
            case TMtCompany.LEVEL_ONE:
                // 一级公司返回 null 表示所有
                return null;

            case TMtCompany.LEVEL_TWO:
                // 二级公司返回自己及子公司 ID
                List<TMtCompany> children = company.getChildCompany();
                java.util.List<Integer> ids = new java.util.ArrayList<>();
                ids.add(company.getId());
                if (children != null) {
                    for (TMtCompany child : children) {
                        ids.add(child.getId());
                    }
                }
                return ids;

            case TMtCompany.LEVEL_THREE:
                // 三级公司只返回自己 ID
                return java.util.Collections.singletonList(company.getId());

            default:
                return null;
        }
    }
}
