package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtSkuCompanyDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 商品库实现
 * Created by zhangguodong on 2017/4/25.
 */
@Service("mtSkuCompanyDao")
public class MtSkuCompanyDaoImpl implements MtSkuCompanyDao {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Override
    public List<TMtSkuCompany> getList(TSysUser user, Integer skuTypeGroup, Integer skuType, String skuName, Integer pageSize, String skuCode) {

        try {

            TMtCompany company = user.getCompany();
            //只有三级公司有商品库
            if(null == company || null == company.getCompanyLevel() || company.getCompanyLevel() != TMtCompany.LEVEL_THREE)return null;
            Expression qualifier =  ExpressionFactory.matchExp(TMtSkuCompany.COMPANY_PROPERTY, company);
            if(null != skuType){
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSkuCompany.SKU_PROPERTY+"."+ TMtSku.SKU_TYPE_PROPERTY,skuType));
            }
            if(null != skuTypeGroup){
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSkuCompany.SKU_PROPERTY+"."+ TMtSku.SKU_TYPE_PROPERTY+"."+ TMtSkuType.PARENT_PROPERTY,skuTypeGroup));
            }
            if(null != skuCode && !"".equals(skuCode)){
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSkuCompany.SKU_PROPERTY+"."+ TMtSku.SKU_CODE_PROPERTY,skuCode));
            }
            if(null != skuName && !"".equals(skuName)){
                qualifier = qualifier.andExp(ExpressionFactory.likeExp(TMtSkuCompany.SKU_PROPERTY+"."+ TMtSku.SKU_NAME_PROPERTY,"%"+skuName+"%"));
            }
            SelectQuery query = new SelectQuery(TMtSkuCompany.class,qualifier);
            if(null != pageSize){
                query.setPageSize(pageSize);
            }
            query.addPrefetch(TMtSkuCompany.SKU_PROPERTY+"."+TMtSku.SKU_TYPE_PROPERTY);
            query.addPrefetch(TMtSkuCompany.SKU_PROPERTY+"."+TMtSku.SKU_TYPE_PROPERTY+"."+TMtSkuType.PARENT_PROPERTY);
            query.addOrdering(TMtSkuCompany.CTIME_PROPERTY , SortOrder.DESCENDING);
            return user.getObjectContext().performQuery(query);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public TMtSkuCompany getByPk(TSysUser user, Integer id) {

        try {
            TMtCompany company = user.getCompany();
            if(null == company || null == company.getCompanyLevel() || company.getCompanyLevel() != TMtCompany.LEVEL_THREE)return null;
            TMtSkuCompany mtSkuCompany = Cayenne.objectForPK(user.getObjectContext(),TMtSkuCompany.class,id);
            if(null != mtSkuCompany && company.getId().equals(mtSkuCompany.getCompany().getId())){
                return mtSkuCompany;
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public TMtSkuCompany getBySkuIdAndCompany(TSysUser user, Integer skuId) {
        try {
            TMtCompany company = user.getCompany();
            //只有三级公司有商品库
            if(null == company || null == company.getCompanyLevel() || company.getCompanyLevel() != TMtCompany.LEVEL_THREE)return null;
            synchronized (company.getId().toString().intern()){
                Expression qualifier =  ExpressionFactory.matchExp(TMtSkuCompany.COMPANY_PROPERTY, company);
                qualifier = qualifier.andExp(ExpressionFactory.matchExp(TMtSkuCompany.SKU_PROPERTY,skuId));
                SelectQuery query = new SelectQuery(TMtSkuCompany.class,qualifier);
                query.addPrefetch(TMtSkuCompany.SKU_PROPERTY+"."+TMtSku.SKU_TYPE_PROPERTY);
                query.addPrefetch(TMtSkuCompany.SKU_PROPERTY+"."+TMtSku.SKU_TYPE_PROPERTY+"."+TMtSkuType.PARENT_PROPERTY);
                query.addOrdering(TMtSkuCompany.CTIME_PROPERTY , SortOrder.DESCENDING);
                List<TMtSkuCompany> list = user.getObjectContext().performQuery(query);
                if(null != list && list.size()>0){
                    return list.get(0);
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

}
