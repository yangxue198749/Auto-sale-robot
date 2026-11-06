package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.MtVmDao;
import com.kdx.vms.orm.dao.StVmStrategyDao;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmEnergySaveService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by yangshuxi on 2016/12/12.
 */
@Service
public class MtVmEnergySaveServiceImpl implements IMtVmEnergySaveService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MtVmDao mtVmDao;
    @Resource
    private StVmStrategyDao stVmStrategyDao;
    @Resource
    private IMcsService mcsService;

    /**
     * 新增
     */
    @Override
    public Map<String, Object> saveEnergySave(TSysUser currentUser, ObjectContext context, Integer targetValue, Integer vmId, Integer energySaveType, String startTime, String endTime, String remark, Integer tempBoxNo) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            if (energySaveType != TStVmEnergySave.SCREEN) {
                if (targetValue == null) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请填写节能目标值");
                    return resultMap;
                }
                if (targetValue > TStVmEnergySave.TEMP_MAX) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可大于" + TStVmEnergySave.TEMP_MAX + "℃");
                    return resultMap;
                }
                if (targetValue < TStVmEnergySave.TEMP_MIN) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可小于" + TStVmEnergySave.TEMP_MIN + "℃");
                    return resultMap;
                }
                if (!IntegerUtils.isPositiveInteger(tempBoxNo)) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请选择保温箱编号");
                    return resultMap;
                }
            }
            //关闭屏幕把保温箱编号置为0
            if (energySaveType == TStVmEnergySave.SCREEN) {
                tempBoxNo = 0;
            }
            //判断节能时间冲突
            Map<Integer, TMtVmEnergySave> energySaveDoubleMap = mtVm.getEnergySaveMapNew(null);
            String err = formatEnergySaveType(energySaveDoubleMap,startTime,endTime,energySaveType,tempBoxNo);
            if (null != err) {
                resultMap.put("type", "error");
                resultMap.put("message", err);
                return resultMap;
            }
            add(currentUser, context, targetValue, vmId, energySaveType, startTime, endTime, remark, tempBoxNo);
            context.commitChanges();
            resultMap.put("type", "success");
            resultMap.put("message", "添加节能成功");
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
        }
        return resultMap;
    }

    /**
     * 修改
     */
    @Override
    public Map<String, Object> saveEnergySave(TSysUser currentUser, ObjectContext context, Integer mtVmEnergySaveId, Integer targetValue, Integer vmId, Integer energySaveType, String startTime, String endTime, String remark, Integer tempBoxNo) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            TMtVmEnergySave energySave = Cayenne.objectForPK(context, TMtVmEnergySave.class, mtVmEnergySaveId);
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            if (energySaveType != TStVmEnergySave.SCREEN) {
                if (targetValue == null) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请填写节能目标值");
                    return resultMap;
                }
                if (targetValue > TStVmEnergySave.TEMP_MAX) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可大于" + TStVmEnergySave.TEMP_MAX + "℃");
                    return resultMap;
                }
                if (targetValue < TStVmEnergySave.TEMP_MIN) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可小于" + TStVmEnergySave.TEMP_MIN + "℃");
                    return resultMap;
                }
                if (!IntegerUtils.isPositiveInteger(tempBoxNo)) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请选择保温箱编号");
                    return resultMap;
                }
            }
            //关闭屏幕把保温箱编号置为0
            if (energySaveType == TStVmEnergySave.SCREEN) {
                tempBoxNo = 0;
            }
            //判断节能时间冲突
            Map<Integer, TMtVmEnergySave> energySaveDoubleMap = mtVm.getEnergySaveMapNew(energySave);
            String err = formatEnergySaveType(energySaveDoubleMap,startTime,endTime,energySaveType,tempBoxNo);
            if (null != err) {
                resultMap.put("type", "error");
                resultMap.put("message", err);
                return resultMap;
            }
            update(currentUser, context, targetValue, vmId, energySaveType, startTime, endTime, remark, energySave, tempBoxNo);
            context.commitChanges();
            resultMap.put("type", "success");
            resultMap.put("message", "修改节能成功");
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> delete(TSysUser currentUser, ObjectContext context, Integer[] ids) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            for (Integer mtVmEnergySaveId : ids) {
                TMtVmEnergySave energySave = Cayenne.objectForPK(context, TMtVmEnergySave.class, mtVmEnergySaveId);
                context.deleteObject(energySave);
            }
            context.commitChanges();
            resultMap.put("type", "success");
            resultMap.put("message", "删除成功");
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
        }
        return resultMap;
    }

    private void add(TSysUser currentUser, ObjectContext context, Integer targetValue, Integer vmId, Integer energySaveType, String startTime, String endTime, String remark, Integer tempBoxNo) {
        TMtVmEnergySave energySave = context.newObject(TMtVmEnergySave.class);
        energySave.setCtime(new Date());
        energySave.setCtor(currentUser.getId());
        energySave.setMtVm(Cayenne.objectForPK(context, TMtVm.class, vmId));
        energySave.setStartTime(startTime);
        energySave.setEndTime(endTime);
        energySave.setEnergySaveType(energySaveType);
        energySave.setTargetValue(targetValue);
        energySave.setRemark(remark);
        energySave.setTempBoxNo(tempBoxNo);
    }

    private void update(TSysUser currentUser, ObjectContext context, Integer targetValue, Integer vmId, Integer energySaveType, String startTime, String endTime, String remark, TMtVmEnergySave energySave, Integer tempBoxNo) {
        energySave.setUtime(new Date());
        energySave.setUtor(currentUser.getId());
        energySave.setMtVm(Cayenne.objectForPK(context, TMtVm.class, vmId));
        energySave.setStartTime(startTime);
        energySave.setEndTime(endTime);
        energySave.setEnergySaveType(energySaveType);
        energySave.setTargetValue(targetValue);
        energySave.setRemark(remark);
        energySave.setTempBoxNo(tempBoxNo);
    }

    @Override
    public String makeTableString(ObjectContext context, Integer mtVmId) {
        StringBuffer buffer = new StringBuffer();
        try {
            TMtVm mtVm = mtVmDao.findByVmId(context, mtVmId);
            if (mtVm != null) {
                List<TMtVmEnergySave> energySaveList = mtVm.getEnergySave();
                if (energySaveList != null && energySaveList.size() > 0) {
                    for (TMtVmEnergySave tMtVmEnergySave : energySaveList) {
                        buffer.append("<tr>");
                        buffer.append("<td class='wh3'>");
                        buffer.append("<input type='checkbox' value='" + tMtVmEnergySave.getEnergySaveId() + "'>");
                        buffer.append("</td>");

                        buffer.append("<td class='wh17'>");
                        switch (tMtVmEnergySave.getEnergySaveType()) {
                            case TStVmEnergySave.HEATING:
                                buffer.append("<span>制热</span>");
                                break;
                            case TStVmEnergySave.REFRIGERATION:
                                buffer.append("<span>制冷</span>");
                                break;
                            case TStVmEnergySave.SCREEN:
                                buffer.append("<span>屏幕</span>");
                                break;
                            default:
                                buffer.append("<span>  </span>");
                                break;
                        }
                        buffer.append("</td>");

                        buffer.append("<td class='wh20'>");
                        buffer.append(" <span>" + tMtVmEnergySave.getStartTime() + "</span>");
                        buffer.append("</td>");
                        buffer.append("<td class='wh20'>");
                        buffer.append(" <span>" + tMtVmEnergySave.getEndTime() + "</span>");
                        buffer.append("</td>");

                        buffer.append("<td class='wh20'>");
                        if (!IntegerUtils.isPositiveInteger(tMtVmEnergySave.getTempBoxNo())) {
                            buffer.append(" <span></span>");
                        } else {
                            buffer.append(" <span>" + (tMtVmEnergySave.getTempBoxNo() == null ? "" : tMtVmEnergySave.getTempBoxNo()) + "</span>");
                        }

                        buffer.append("</td>");
                        buffer.append("<td class='wh20'>");
                        buffer.append("<span>" + (tMtVmEnergySave.getTargetValue() == null ? "" : tMtVmEnergySave.getTargetValue()) + "</span>");
                        buffer.append("</td>");

                        buffer.append("</tr>");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return buffer.toString();
    }

    @Override
    public String selectSt(ObjectContext context, Integer[] mtVmIds,
                           Integer strategyId, TSysUser user) {
        String errMsg = null;
        try {
            if (null == mtVmIds || mtVmIds.length == 0) {
                return "请选择售货机";
            }
            if (null == strategyId) {
                return "请选择节能策略";
            }
            TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
            if (null == strategy) {
                return "节能策略不存在";
            }
            List<TStVmEnergySave> stEnergySaveList = strategy.getStEnergySave();

            List<TMtVm> mtVmList = new ArrayList<>();
            for (Integer vmId : mtVmIds) {
                mtVmList.add(mtVmDao.findByVmId(context, vmId));
            }
            for (TMtVm tMtVm : mtVmList) {
                List<TMtVmEnergySave> energySaveList = tMtVm.getEnergySave();
                if (null == energySaveList) {
                    logger.debug("售货机在应用前没有节能策略 vmId = " + tMtVm.getId());
                    for (TStVmEnergySave stEnergySave : stEnergySaveList) {
                        createBySt(context, user, tMtVm, stEnergySave);
                    }
                } else {
                    logger.debug("删除原有节能策略 vmId =  " + tMtVm.getId() + "; 删除策略详情 ：" + energySaveList);
                    context.deleteObjects(energySaveList);
                    for (TStVmEnergySave stEnergySave : stEnergySaveList) {
                        createBySt(context, user, tMtVm, stEnergySave);
                    }
                }

                List<TMtVmStrategy> stList = tMtVm.getStrategy();
                boolean flag = false;
                for (TMtVmStrategy mtVmStrategy : stList) {
                    if (mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE)) {
                        mtVmStrategy.update(user.getId(), strategy.getStrategyName());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    TMtVmStrategy mtVmStrategy = context.newObject(TMtVmStrategy.class);
                    mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_ENERGY_SAVE);
                    mtVmStrategy.setMtVm(tMtVm);
                    mtVmStrategy.update(user.getId(), strategy.getStrategyName());
                }


            }
            context.commitChanges();
            logger.debug("应用节能策略成功  mtVmIds = " + Arrays.toString(mtVmIds) + "应用后节能策略详情：" + stEnergySaveList);
            String mcsMsg = pushToMCS(context, mtVmIds);
            String newMcsMsg = pushToMCSNew(context, mtVmIds);
            if (!StringUtil.isEmpty(mcsMsg) || !StringUtil.isEmpty(newMcsMsg)) {
                errMsg = ReturnCode.MCS_FAIL.getMessage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    private void createBySt(ObjectContext context, TSysUser user, TMtVm tMtVm,
                            TStVmEnergySave stEnergySave) {
        TMtVmEnergySave mtEnergySave = context.newObject(TMtVmEnergySave.class);
        mtEnergySave.setCtime(new Date());
        mtEnergySave.setCtor(user.getCtor());
        mtEnergySave.setEndTime(stEnergySave.getEndTime());
        mtEnergySave.setEnergySaveType(stEnergySave.getEnergySaveType());
        mtEnergySave.setMtVm(tMtVm);
        mtEnergySave.setRemark(stEnergySave.getRemark());
        mtEnergySave.setStartTime(stEnergySave.getStartTime());
        mtEnergySave.setTargetValue(stEnergySave.getTargetValue());
        mtEnergySave.setTempBoxNo(stEnergySave.getTempBoxNo());
    }

    @Override
    public String pushToMCS(ObjectContext context, Integer[] vmIds) {
        String errMsg = "";
        try {
            errMsg = mcsService.pushEnergy(context, Arrays.asList(vmIds), null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }


    private String formatEnergySaveType(Map<Integer, TMtVmEnergySave> saveMap,
                                 String StartTimeStr, String EndTimeStr, Integer saveType, Integer tempBoxNo) {
        String errMsg = null;
        String[] heatStartArr = StartTimeStr.split(":");
        String[] heatEndArr = EndTimeStr.split(":");
        int heatStart = Integer.parseInt(heatStartArr[0] + heatStartArr[1]);
        int heatEnd = Integer.parseInt(heatEndArr[0] + heatEndArr[1]);

        if (heatStart >= heatEnd && saveType.equals(TStVmEnergySave.HEATING)) {
            errMsg = "制热开始时间必须小于结束时间，保存节能策略失败";
        }if (heatStart >= heatEnd && saveType.equals(TStVmEnergySave.REFRIGERATION )) {
            errMsg = "制冷开始时间必须小于结束时间，保存节能策略失败";
        }if (heatStart >= heatEnd && saveType.equals(TStVmEnergySave.HEATING )) {
            errMsg = "屏幕开始时间必须小于结束时间，保存节能策略失败";
        }
        for(TMtVmEnergySave tMtVmEnergySave:saveMap.values()){
            String refriStartTimeStr = tMtVmEnergySave.getStartTime();
            String refriEndTimeStr = tMtVmEnergySave.getEndTime();

            String[] refriStartArr = refriStartTimeStr.split(":");
            String[] refriEndArr = refriEndTimeStr.split(":");
            int refriStart = Integer.parseInt(refriStartArr[0] + refriStartArr[1]);
            int refriEnd = Integer.parseInt(refriEndArr[0] + refriEndArr[1]);


            if ((heatStart <= refriStart && refriStart <= heatEnd && tMtVmEnergySave.getTempBoxNo().equals(tempBoxNo)) || (heatStart <= refriEnd && refriEnd <= heatEnd && tMtVmEnergySave.getTempBoxNo().equals(tempBoxNo)) || (heatStart >= refriStart && heatEnd <= refriEnd && tMtVmEnergySave.getTempBoxNo().equals(tempBoxNo))) {
                errMsg = "节能时间冲突，保存节能策略失败";
                return errMsg;
            }
        }
        return errMsg;
    }
    @Override
    public String pushToMCSNew(ObjectContext context, Integer[] vmIds) {
        String errMsg = "";
        try {
            errMsg = mcsService.pushEnergyNew(context, Arrays.asList(vmIds), null);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }
}
