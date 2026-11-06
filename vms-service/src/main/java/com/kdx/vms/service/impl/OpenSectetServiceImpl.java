package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TOpenSecret;
import com.kdx.vms.model.bean.TOpenSecretService;
import com.kdx.vms.model.bean.TOpenService;
import com.kdx.vms.orm.dao.MtCompanyDao;
import com.kdx.vms.orm.dao.OpenSecretDao;
import com.kdx.vms.orm.dao.OpenServiceDao;
import com.kdx.vms.service.IOpenSecretService;
import org.apache.cayenne.ObjectContext;
import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guoyang on 2018/2/9.
 */
@Service
public class OpenSectetServiceImpl implements IOpenSecretService {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Autowired
    private OpenSecretDao openSecretDao;
    @Autowired
    private MtCompanyDao mtCompanyDao;
    @Autowired
    private OpenServiceDao openServiceDao;

    @Override
    public Map<String,Object> createOpenApiManage(ObjectContext context, Integer[] serviceId, Integer commpanyId, Integer userId) {
        Map<String,Object> map = new HashMap<>();
        map.put("type","error");
        try {
            if (null == serviceId || serviceId.length == 0) {
                map.put("message","请选择开放接口");
                return map;
            }
            if (null == commpanyId) {
                map.put("message","请选择公司名称");
            }
            TMtCompany company = mtCompanyDao.findCompanyById(context, commpanyId);
            TOpenSecret openSecret = company.getOpensecret();
            if(openSecret != null){
                map.put("message","开放接口已分配给该公司");
                return map;
            }
            openSecret = context.newObject(TOpenSecret.class);
            openSecret.setCompany(company);
            openSecret.setCtor(userId);
            openSecret.setUtor(userId);
            openSecret.setPublicId(RandomStringUtils.randomAlphanumeric(20));
            openSecret.setSecretValue(RandomStringUtils.randomAlphanumeric(20));
            openSecret.setGlobal(0);
            openSecret.setCtime(new Date());
            openSecret.setEnable(TOpenSecret.ADD_ENABLE);
            bulidOpenSecretService(serviceId,openSecret,context);

            context.commitChanges();
            map.put("type","success");
            map.put("message","新建成功");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(),e);
            map.put("message","添加接口管理异常");
        }
        return map;
    }

    @Override
    public Map<String, Object> deleteOpenApiManage(ObjectContext context, String customerId) {
        Map<String,Object> map = new HashMap<>();
        map.put("type","error");
        if (null == customerId ) {
            map.put("message","customer_id 为空");
            return map;
        }
        try{
            TOpenSecret openSecret = openSecretDao.findByPublicId(context,customerId);
            if(TOpenSecret.GLOBAL ==openSecret.getGlobal()){
                map.put("message","此秘钥不能删除");
                return map;
            }
            List<TOpenSecretService> openService =openSecret.getOpenSecretService();
            context.deleteObjects(openService);
            context.deleteObject(openSecret);
            context.commitChanges();
            map.put("message","删除成功");
            map.put("type","success");
        }catch(Exception e){
            logger.error(e.getLocalizedMessage(),e);
            map.put("message","删除接口管理异常");
        }
        return map;
    }

    @Override
    public Map<String, Object> updateOpenApiManage(ObjectContext context, Integer[] serviceIds, String customerId, Integer userId) {
        Map<String,Object> map = new HashMap<>();
        try{
            map.put("type","error");
            if (null == serviceIds || serviceIds.length == 0) {
                map.put("message","请选择开放接口");
                return map;
            }
            if (null == customerId) {
                map.put("message","customer_id 为空");
                return map;
            }
            TOpenSecret openSecret =openSecretDao.findByPublicId(context,customerId);
            if(openSecret == null){
                map.put("message","没有该用户");
                return map;
            }
            List<TOpenSecretService> openService = openSecret.getOpenSecretService();
            if(openService != null){
                context.deleteObjects(openService);
            }
            bulidOpenSecretService(serviceIds,openSecret,context);
            openSecret.setUtor(userId);
            openSecret.setUtime(new Date());
            context.commitChanges();
            map.put("type","success");
            map.put("message","修改成功");
        }catch(Exception e){
            logger.error(e.getLocalizedMessage(),e);
            map.put("message","修改接口管理异常");
        }

        return map;
    }

    @Override
    public List<TOpenSecret> findOpenApiInfo(ObjectContext context, Integer pageSize) {
        return openSecretDao.findTOpenSecret(context, pageSize);
    }

    private void bulidOpenSecretService (Integer[] serviceIds, TOpenSecret openSecret, ObjectContext context){
        for (Integer serviceId : serviceIds) {
            TOpenService OpenService =openServiceDao.findById(context,serviceId);
            TOpenSecretService openSecretService = context.newObject(TOpenSecretService.class);
            openSecretService.setOpenSecret(openSecret);
            openSecretService.setOpenServcie(OpenService);
        }
    }

}
