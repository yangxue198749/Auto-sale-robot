package com.kdx.vms.orm.utils;

import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.enumeration.PayType;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * Created by yangshuxi on 2017/11/15.
 */
public class ExpressionUtils {
    public static Expression fillInnerCodeQualifier(Expression expression, String path, String innerCode) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (!StringUtil.isEmpty(innerCode)) {
            innerCode = innerCode.replaceAll("，", ",");
            Expression e;
            if (innerCode.contains(",")) {
                e = ExpressionFactory.inExp(path, Arrays.asList(innerCode.split(",")));
            } else {
                e = ExpressionFactory.likeExp(path, "%" + innerCode + "%");
            }
            expression = expression.andExp(e);
        }
        return expression;
    }

    public static Expression fillCompanyQualifier(Expression expression, String path, TSysUser sysUser) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (null == sysUser) return null;
        TMtCompany company = sysUser.getCompany();
        switch (company.getCompanyLevel()) {
            case TMtCompany.LEVEL_ONE: // 一级(系统内置)公司，直接查询所有
                break;
            case TMtCompany.LEVEL_TWO: // 二级公司，查询该公司及其子公司
                Expression companyExp = ExpressionFactory.inExp(path, company.getChildCompany());
                companyExp = companyExp.orExp(ExpressionFactory.matchExp(path, company));
                expression = expression.andExp(companyExp);
                break;
            case TMtCompany.LEVEL_THREE: // 三级公司，只查询该公司
                expression = expression.andExp(ExpressionFactory.matchExp(path, company));
                break;
            default:
                return null;
        }
        return expression;
    }

    public static Expression fillMatchQualifier(Expression expression, String path, String str) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (!StringUtil.isEmpty(str)) {
            return expression.andExp(ExpressionFactory.matchExp(path, str));
        }
        return expression;
    }

    public static Expression fillMatchQualifier(Expression expression, String path, Integer integer) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (null != integer) {
            return expression.andExp(ExpressionFactory.matchExp(path, integer));
        }
        return expression;
    }

    public static Expression fillMatchQualifier(Expression expression, String path, Object obj) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (null != obj) {
            return expression.andExp(ExpressionFactory.matchExp(path, obj));
        }
        return expression;
    }

    public static Expression fillInQualifier(Expression expression, String path, Object... objs) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (null != objs) {
            return expression.andExp(ExpressionFactory.inExp(path, objs));
        }
        return expression;
    }


    public static Expression fillLikeQualifier(Expression expression, String path, String str) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (!StringUtil.isEmpty(str)) {
            return expression.andExp(ExpressionFactory.likeExp(path, "%" + str + "%"));
        }
        return expression;
    }

    public static Expression fillBeginTimeQualifier(Expression expression, String path, String beginTime) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (!StringUtil.isEmpty(beginTime)) {
            Date beginDate = DateUtils.getDate2(beginTime + " 00:00:00");
            return expression.andExp(ExpressionFactory.greaterOrEqualExp(path, beginDate));
        }
        return expression;
    }

    public static Expression fillEndTimeQualifier(Expression expression, String path, String endTime) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (!StringUtil.isEmpty(endTime)) {
            Date endDate = DateUtils.getDate2(endTime + " 23:59:59.999");
            return expression.andExp(ExpressionFactory.lessOrEqualExp(path, endDate));
        }
        return expression;
    }

    public static Expression fillPayTypeQualifier(Expression expression, String path, Integer payType) {
        if (null == expression) expression = ExpressionFactory.expTrue();
        if (null != payType) {
            if (Objects.equals(payType, PayType.RECHARGE_CARD.getCode())) {
                return expression.andExp(ExpressionFactory.inExp(path, payType, PayType.RECHARGE_CARD0));
            } else {
                return expression.andExp(ExpressionFactory.matchExp(path, payType));
            }
        }
        return expression;
    }
}
