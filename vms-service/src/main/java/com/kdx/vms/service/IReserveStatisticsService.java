package com.kdx.vms.service;

import com.kdx.vms.model.bean.TReserveStatistics;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by zhangguodong on 2017/8/15.
 */
public interface IReserveStatisticsService {
    /**
     * 查询预定统计单
     * @param context
     * @param innerCode 售货机编号
     * @param skuName 商品名称
     * @param statisticsDate 取货日期
     * @param pageSize 每页条数
     * @param user 当前用户
     * @return
     */
    public List<TReserveStatistics> findByPage(ObjectContext context, String innerCode, String skuName, String statisticsDate, Integer pageSize, TSysUser user);
}
