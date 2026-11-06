package com.kdx.vms.service;

import com.kdx.vms.model.bean.TOpenSecret;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;

/**
 * Created by guoyang on 2018/2/9.
 */
public interface IOpenSecretService {

    List<TOpenSecret> findOpenApiInfo(ObjectContext context, Integer pageSize);
    Map<String,Object> createOpenApiManage(ObjectContext context, Integer[] serviceId, Integer commpanyId, Integer userId);
    Map<String,Object> updateOpenApiManage(ObjectContext context, Integer[] serviceId, String customerId, Integer userId);
    Map<String,Object> deleteOpenApiManage(ObjectContext context, String customerId);

}
