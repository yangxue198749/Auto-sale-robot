package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 * Created by yangshuxi on 2017/8/3.
 */
public interface ReserveStatisticsDao {
    /**
     * 查找今天的预定统计单
     * @param context
     * @param mtVm 售货机
     * @return
     */
    public List<?> findTodayReserveStatisticsByVm(ObjectContext context,TMtVm mtVm);

    /**
     * 查询预定统计订单
     * @param context
     * @param innerCode 售货机编号
     * @param skuName 商品名称
     * @param statisticsDate 预定日期
     * @param pageSize 每页数量
     * @param user 当前用户
     * @return
     */
    public List findPage(ObjectContext context, String innerCode, String skuName, String statisticsDate, Integer pageSize, TSysUser user);

}
