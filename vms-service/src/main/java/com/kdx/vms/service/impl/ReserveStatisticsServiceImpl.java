package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TReserveStatistics;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.ReserveStatisticsDao;
import com.kdx.vms.service.IReserveStatisticsService;
import org.apache.cayenne.ObjectContext;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by zhangguodong on 2017/8/15.
 */
@Service("reserveStatisticsService")
public class ReserveStatisticsServiceImpl implements IReserveStatisticsService {
    @Resource
    private ReserveStatisticsDao reserveStatisticsDao;
    @Override
    public List<TReserveStatistics> findByPage(ObjectContext context, String innerCode, String skuName, String statisticsDate, Integer pageSize,TSysUser user) {
        return reserveStatisticsDao.findPage(context,innerCode,skuName,statisticsDate,pageSize,user);
    }
}
