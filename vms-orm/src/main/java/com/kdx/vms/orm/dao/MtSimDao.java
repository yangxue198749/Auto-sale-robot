package com.kdx.vms.orm.dao;

import com.kdx.vms.model.bean.TMtSim;
import com.kdx.vms.model.bean.TSysUser;

import org.apache.cayenne.ObjectContext;

import java.util.List;

/**
 *
 * Created by liyang on 2016/11/28.
 */
public interface MtSimDao {

    /**
     * 查询全部列表(分页)
     * @param pageSize 分页
     * @return List<TMtSim>
     */
    List findMtSimPage(ObjectContext context, Integer pageSize);

    /**
     * 主键查询 优先从缓存中取
     * @param pk 主键
     * @return TMtSim
     */
    TMtSim findMtSimPk(ObjectContext context, Integer pk);

    /**
     * sim卡号 或 sim卡编号 其中之一返回，用来做已存在验证
     * @param simPhone sim卡号(电话号码)
     * @param simNumber sim卡编号
     * @return
     */
    List findMtSim(ObjectContext context, String simPhone, String simNumber);

    /**
     *
     * @param simPhone sim卡号(电话号码)
     * @param simNumber sim卡编号
     * @param serviceProvider 运营商
     * @param use 是否使用中
     * @param pageSize 分页
     * @return List<TMtSim>
     */
    public List findMtSim(ObjectContext context ,TSysUser sysUser ,String simPhone, String simNumber, Integer serviceProvider, Boolean use, Integer pageSize);

}
