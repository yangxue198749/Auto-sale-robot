package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtSkuCompany;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtSkuCompanyDao;
import com.kdx.vms.service.IMtSkuCompanyService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 商品库业务实现
 * Created by zhangguodong on 2017/4/26.
 */
@Service("mtSkuCompanyService")
public class MtSkuCompanyServiceImpl implements IMtSkuCompanyService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MtSkuCompanyDao mtSkuCompanyDao;

    @Override
    public List<TMtSkuCompany> getList(TSysUser user, Integer skuTypeGroup, Integer skuType, String skuName, Integer pageSize, String skuCode) {
        try {
            return mtSkuCompanyDao.getList(user,skuTypeGroup,skuType,skuName,pageSize,skuCode);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public TMtSkuCompany getByPk(TSysUser user, Integer id) {
        try {
            return mtSkuCompanyDao.getByPk(user,id);
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return null;
    }

    @Override
    public String addToMtSkuCompany(TSysUser user, Integer[] skuIds) {
        String errMsg = "";
        try {
            if(null == skuIds || skuIds.length ==0){
                return "请选择商品";
            }
            TMtCompany company = user.getCompany();
            if(null == company || null == company.getCompanyLevel() || company.getCompanyLevel() != TMtCompany.LEVEL_THREE){
                return "添加失败,无权限";
            }
            ObjectContext context = user.getObjectContext();
            boolean isCommit = false;
            for (Integer skuId:skuIds){
                TMtSkuCompany mtSkuCompany = mtSkuCompanyDao.getBySkuIdAndCompany(user,skuId);
                if(null != mtSkuCompany)continue;
                isCommit = true;
                TMtSkuCompany newTMtSkuCompany = context.newObject(TMtSkuCompany.class);
                newTMtSkuCompany.setCompany(user.getCompany());
                newTMtSkuCompany.setCtime(new Date());
                newTMtSkuCompany.setCtor(user.getId());
                newTMtSkuCompany.setSku(Cayenne.objectForPK(context, TMtSku.class,skuId));
            }
            if(isCommit){
                try {
                    context.commitChanges();
                }catch (Exception e){
                    errMsg = "添加失败！";
                    logger.error(e.getLocalizedMessage(),e);
                    context.rollbackChanges();
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return errMsg;
    }

    @Override
    public String delete(TSysUser user, Integer[] skuIds) {
        String errMsg = "";
        try {
            if(null == skuIds || skuIds.length ==0){
                return "请选择商品";
            }
            TMtCompany company = user.getCompany();
            if(null == company || null == company.getCompanyLevel() || company.getCompanyLevel() != TMtCompany.LEVEL_THREE){
                return "删除失败,无权限";
            }
            ObjectContext context = user.getObjectContext();
            boolean isCommit = false;
            for (Integer skuId:skuIds){
                TMtSkuCompany mtSkuCompany = mtSkuCompanyDao.getBySkuIdAndCompany(user,skuId);
                if(null == mtSkuCompany)continue;
                isCommit = true;
                context.deleteObject(mtSkuCompany);
                logger.info("用户 user :" + user + "从商品库中删除了商品 : "  +  mtSkuCompany);
            }
            if (isCommit){
                try {
                    context.commitChanges();
                }catch (Exception e){
                    errMsg = "删除失败！";
                    logger.error(e.getLocalizedMessage(),e);
                    context.rollbackChanges();
                }
            }
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(),e);
        }
        return errMsg;
    }
}
