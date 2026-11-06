package com.kdx.mp.service.impl;

import com.kdx.mp.service.IMpActivateVmService;
import com.kdx.mp.util.WeChatUtils;
import com.kdx.vms.model.bean.TMtNode;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.orm.dao.MtVmDao;
import com.kdx.vms.redis.MyJedisCluster;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yangshuxi on 2016/12/21.
 */
@Service
public class MpActivateVmServiceImpl implements IMpActivateVmService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MtVmDao mtVmDao;
    @Autowired
    public void setMyJedisCluster(MyJedisCluster myJedisCluster) {
        WeChatUtils.myJedisCluster = myJedisCluster;
    }

    @Override
    public TMtVm checkActivated(ObjectContext context, String uniqueCode) {
        List list = mtVmDao.findByUniqueCode(context, uniqueCode);
        if (null != list && !list.isEmpty()) {
            return (TMtVm) list.get(0);
        }
        return null;
    }

    @Override
    public void updateNodePosition(ObjectContext context, TMtNode mtNode, String latitude, String longitude, Integer type) {
        synchronized (mtNode.getNodeName().intern()) {
            try {
                if (type == 0) {
                    mtNode.setLatitude(latitude);
                    mtNode.setLongitude(longitude);
                    context.commitChanges();
                } else {
                    mtNode.setLatitudeMars(latitude);
                    mtNode.setLongitudeMars(longitude);
                    context.commitChanges();
                }
            } catch (Exception e) {
                context.rollbackChanges();
                logger.error(e.getLocalizedMessage(), e);
            }

        }
    }
}
