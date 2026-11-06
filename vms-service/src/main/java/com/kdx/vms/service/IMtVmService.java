package com.kdx.vms.service;

import com.kdx.vms.model.bean.TMtCompany;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.StockExport;
import org.apache.cayenne.ObjectContext;

import java.util.List;
import java.util.Map;


public interface IMtVmService {

    /**
     * 新增售货机
     *
     * @param context
     * @param nodeId  点位标识
     * @param modelId 售货机型号标识
     * @param mtVmId  复制的售货机标识
     * @param vmDesc  售货机描述
     * @param user    操作人信息
     * @param reserve 是否预定
     * @return 成功返回null, 失败返回失败信息
     */
    String addMtVm(ObjectContext context, Integer nodeId, Integer modelId, Integer mtVmId, String vmDesc, TSysUser user, Integer printer, Integer reserve, Integer[] payType);

    String extendMtVm(TMtVm mainVm, TSysUser user, Integer copyVmId, Integer modelId);

    /**
     * 查询售货机列表
     *
     * @param context
     * @param filedType   搜索字段类型   1售货机编码，2点位，3硬件编码 4所属公司
     * @param filedValue  搜索字段值
     * @param modelId     售货机型号标识
     * @param vmStatus    售货机状态
     * @param province    点位所在省
     * @param city        点位所在市
     * @param country     点位所在区
     * @param bussinessId 场景标识
     * @param user        登录用户
     * @param pageSize    每页条数
     * @param showPattern 只展示主机或单机类型 true false
     * @return
     */
    List<TMtVm> findMtVmList(ObjectContext context, Integer filedType, String filedValue, Integer modelId, Integer vmStatus, Integer province, Integer city, Integer country, Integer bussinessId, TSysUser user, Integer pageSize, Boolean showPattern);

    /**
     * 查询三级公司下的所有已使用售货机
     *
     * @param context
     * @param companyId 公司id
     * @return List<TMtVm>
     * @date 2016年12月9日 上午10:01:48
     * @author chenifan
     */
    public List<TMtVm> findMtVmListByCompanyId(ObjectContext context, Integer companyId);

    /**
     * 查询公司下的所有已使用售货机
     */
    public List<TMtVm> findMtVmListByUser(TSysUser user, String innerCode);

    /**
     * 售货机正在应用货道配置
     *
     * @param context
     * @param mtVmId
     * @return
     */
    public String makeMtVmChannelTableString(ObjectContext context, Integer mtVmId);

    /**
     * 售货机正在应用的列表页
     *
     * @param objectContext
     * @param mtVmId
     * @return
     */
    String makeSkuSortTableString(ObjectContext objectContext, Integer mtVmId);

    List getMtVmForStatus(TSysUser user, String innerCode, String nodeName, Integer netStatus, Integer vmcStatus,Integer taskStatus, Integer tempStatus, Integer pageSize);

    List<TMtVm> getMtVmForStocks(TMtCompany company, String innerCode, String nodeName, Integer pageSize);

    TMtVm getMtVm(ObjectContext context, Integer vmId);

    /**
     * 查询售货机
     *
     * @param context
     * @param innerCode 售货机编码
     * @return
     */
    TMtVm findMtVmByInnerCode(ObjectContext context, String innerCode);

    /**
     * 修改售货机详细描述
     *
     * @param objectContext
     * @param vmId          售货机标识
     * @param vmDesc        详情描述
     * @return
     */
    String editMtVm(ObjectContext objectContext, Integer vmId, String vmDesc, Integer printer, Integer reserve, Integer[] payType, TSysUser user);

    /**
     * 切换售货机销售状态
     *
     * @param objectContext
     * @param vmId
     * @return
     */
    String changeSale(ObjectContext objectContext, Integer vmId);

    Map<String, Object> unbind(TSysUser currentUser, ObjectContext context, Integer[] vmIds);

    /**
     * 查询售货机列表，不区分公司。提供给升级计划
     *
     * @param objectContext
     * @param vmModelId
     * @param province
     * @param city
     * @param country
     * @param pageSize
     * @return
     */
    public List<TMtVm> findForUpGrade(ObjectContext objectContext, Integer vmModelId, Integer province, Integer city, Integer country, Integer pageSize);

    /**
     * 所有售货机的库存明细数据
     *
     * @param context
     * @param company
     * @return
     */
    List<StockExport> findVmChannelMessage(ObjectContext context, TMtCompany company);
}
