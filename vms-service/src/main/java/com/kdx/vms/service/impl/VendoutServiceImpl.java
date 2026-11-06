package com.kdx.vms.service.impl;


import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.model.bean.TOrderDetailVendout;
import com.kdx.vms.model.bean.TOrderMaster;
import com.kdx.vms.orm.dao.VendoutDao;
import com.kdx.vms.service.VendoutService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * create by zhouxing on 2017/11/23
 */
@Service
public class VendoutServiceImpl implements VendoutService{

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    private VendoutDao vendoutDao;

    @Override
    public Map<String, Object> updateMemoById(ObjectContext context, String vendoutId, String memo) {

        try {
            TOrderDetail tOrderDetail = vendoutDao.findById(context,vendoutId);
            tOrderDetail.setMemo(memo);
            context.commitChanges();
            return ReturnCode.SUCCESS.toMap();
        }catch (Exception e){
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
        }
        return ReturnCode.FAIL.toMap();
    }

    @Override
    public TOrderMaster findByDetailId(ObjectContext context, Integer detailId) {
        return vendoutDao.findByDetailId(context, detailId);
    }


}