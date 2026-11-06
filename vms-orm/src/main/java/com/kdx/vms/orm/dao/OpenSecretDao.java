package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TOpenSecret;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by guoyang on 2018/2/9.
 */
public interface OpenSecretDao {

    List<TOpenSecret> findTOpenSecret(ObjectContext context, Integer pageSize);
    TOpenSecret findByPublicId(ObjectContext context, String customerId);

}
