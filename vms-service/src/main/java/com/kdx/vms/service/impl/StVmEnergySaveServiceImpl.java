package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TStVmEnergySave;
import com.kdx.vms.model.bean.TStVmStrategy;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.orm.dao.MtVmModelDao;
import com.kdx.vms.orm.dao.StVmEnergySaveDao;
import com.kdx.vms.orm.dao.StVmStrategyDao;
import com.kdx.vms.service.IStVmEnergySaveService;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("stVmEnergtSaveService")
public class StVmEnergySaveServiceImpl implements IStVmEnergySaveService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private StVmEnergySaveDao stVmEnergySaveDao;
    @Resource
    private StVmStrategyDao stVmStrategyDao;
    @Resource
    private MtVmModelDao mtVmModelDao;

    @Override
    public List<TStVmStrategy> findPage(ObjectContext context, TSysUser user,
                                        String stVmName, Integer modelId, Integer pageSize) {
        try {
            return stVmStrategyDao.findStVmEnergyPage(context, user, stVmName, modelId, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TStVmEnergySave> findByStrategyId(ObjectContext context,
                                                  Integer strategyId) {
        try {
            return stVmEnergySaveDao.findByStrategyId(context, strategyId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public String deleteStVmEnergySaveByStrategyId(ObjectContext context,
                                                   Integer[] strategyIds, TSysUser user) {
        logger.debug("strategyId = " + Arrays.toString(strategyIds) + ", user = " + user);
        String errMsg = null;
        try {
            if (null == strategyIds || strategyIds.length == 0) {
                return "请选中一个节能策略进行删除";
            }
            for (Integer strategyId : strategyIds) {
                TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
                if (null != strategy) {
                    List<TStVmEnergySave> saveList = strategy.getStEnergySave();
//                    if (saveList != null && saveList.size() > 0) {
//                        for (TStVmEnergySave tStVmEnergySave : saveList) {
//                            context.deleteObject(tStVmEnergySave);
//                        }
//                    }
                    context.deleteObjects(saveList);
                    context.deleteObject(strategy);
                }
            }
            context.commitChanges();
        } catch (Exception e) {
            context.commitChanges();
            errMsg = "系统内部出现异常，删除节能策略失败";
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String deleteStVmEnergyByEnergyId(ObjectContext context,
                                             Integer energySaveId, TSysUser user) {
        logger.debug("energySaveId = " + energySaveId + ",user = " + user);
        String errMsg = null;
        try {
            if (null == energySaveId) {
                return "请选择一条节能策略";
            }
            TStVmEnergySave save = stVmEnergySaveDao.findByPk(context, energySaveId);
            if (null != save) {
                context.commitChanges();
            }
        } catch (Exception e) {
            context.rollbackChanges();
            errMsg = "系统内部出现异常，删除节能策略失败";
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public Map<String, Object> addStVmStrategy(ObjectContext context, String stVmName,
                                               String stVmDesc, Integer vmModelId, TSysUser user) {
        logger.debug("stVmName = " + stVmName + ",stVmDesc = " + stVmDesc + ", vmModelId = " + vmModelId);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        if (StringUtil.isEmpty(stVmName)) {
            resultMap.put("type", "error");
            resultMap.put("message", "策略名称不能为空，保存节能策略失败");
            return resultMap;
        }
        if (null == vmModelId) {
            resultMap.put("type", "error");
            resultMap.put("message", "售货机型号不能为空，保存节能策略失败");
            return resultMap;
        }

        TMtVmModel vmModel = mtVmModelDao.findMtVmModelById(context, vmModelId);
        if (null == vmModel) {
            resultMap.put("type", "error");
            resultMap.put("message", "售货机型号错误，保存节能策略失败");
            return resultMap;
        }
        try {
            TStVmStrategy stVmStrategy = context.newObject(TStVmStrategy.class);
            stVmStrategy.setCompany(user.getCompany());
            stVmStrategy.setCtime(new Date());
            stVmStrategy.setCtor(user.getId());
            stVmStrategy.setStrategyDesc(stVmDesc);
            stVmStrategy.setStrategyName(stVmName);
            stVmStrategy.setStrategyType(stVmStrategy.TYPE_ST_VM_ENERGY_SAVE);
            stVmStrategy.setVmModel(vmModel);
            context.commitChanges();
            resultMap.put("type", "success");
            resultMap.put("message", "新建成功");
            resultMap.put("strategyId", stVmStrategy.getStrategyId());
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
    public Map<String, Object> addStVmEnergySave(ObjectContext context,
                                                 TSysUser user, Integer strategyId, Integer addTargetValue,
                                                 Integer addEnergySaveType, String addStartTime, String addEndTime,
                                                 String addRemark, Integer addTempBoxNo) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (strategyId == null) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择节能策略");
                return resultMap;
            }
            if (addEnergySaveType == null) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择节能类型");
                return resultMap;
            }
            if (addEnergySaveType != TStVmEnergySave.SCREEN) {
                if (addTargetValue == null) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请填写节能目标值");
                    return resultMap;
                }
                if (addTargetValue > TStVmEnergySave.TEMP_MAX) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可大于" + TStVmEnergySave.TEMP_MAX + "℃");
                    return resultMap;
                }
                if (addTargetValue < TStVmEnergySave.TEMP_MIN) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可小于" + TStVmEnergySave.TEMP_MIN + "℃");
                    return resultMap;
                }
                if (!IntegerUtils.isPositiveInteger(addTempBoxNo)) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请选择保温箱编号");
                    return resultMap;
                }
            }
            TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
            if (null == strategy) {
                resultMap.put("type", "error");
                resultMap.put("message", "节能策略不存在");
                return resultMap;
            }
            //关闭屏幕把保温箱编号置为0
            if (addEnergySaveType == TStVmEnergySave.SCREEN) {
                addTempBoxNo = 0;
            }
            //判断节能时间冲突
            Map<Integer, TStVmEnergySave> energySaveDoubleMap = strategy.getEnergySaveMapNew(null);
            String err = formatEnergySaveType(energySaveDoubleMap, addStartTime, addEndTime, addEnergySaveType, addTempBoxNo);
            if (null != err) {
                resultMap.put("type", "error");
                resultMap.put("message", err);
                return resultMap;
            }
            createEnergySave(context, user, addTargetValue,
                    addEnergySaveType, addStartTime,
                    addEndTime, addRemark, strategy, addTempBoxNo);
            context.commitChanges();
            resultMap.put("type", "success");
            resultMap.put("message", "添加节能策略成功");
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
        }
        return resultMap;
    }

    private void createEnergySave(ObjectContext context, TSysUser user,
                                  Integer addTargetValue, Integer addEnergySaveType,
                                  String addStartTime, String addEndTime, String addRemark,
                                  TStVmStrategy strategy, Integer addTempBoxNo) {
        TStVmEnergySave newSave = context.newObject(TStVmEnergySave.class);
        newSave.setCtime(new Date());
        newSave.setCtor(user.getId());
        newSave.setEndTime(addEndTime);
        newSave.setEnergySaveType(addEnergySaveType);
        newSave.setStartTime(addStartTime);
        newSave.setTargetValue(addTargetValue);
        newSave.setRemark(addRemark);

        newSave.setTempBoxNo(addTempBoxNo);
        strategy.addToStEnergySave(newSave);
        strategy.setUtime(new Date());
        strategy.setUtor(user.getId());
    }

    @Override
    public String makeTableString(ObjectContext context, Integer strategyId) {
        StringBuffer buffer = new StringBuffer();
        try {
            TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
            List<TStVmEnergySave> energySaveList = strategy.getStEnergySave();
            if (null != energySaveList && energySaveList.size() > 0) {
                buffer.append("<table> <tbody class='auto'>");

                for (TStVmEnergySave tStVmEnergySave : energySaveList) {
                    buffer.append("<tr>");
                    buffer.append("<td class='wh3'>");
                    buffer.append("<input type='checkbox' value='" + tStVmEnergySave.getEnergySaveId() + "'>");
                    buffer.append("</td>");

                    buffer.append("<td class='wh17'>");
                    switch (tStVmEnergySave.getEnergySaveType()) {
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
                    buffer.append(" <span>" + tStVmEnergySave.getStartTime() + "</span>");
                    buffer.append("</td>");
                    buffer.append("<td class='wh20'>");
                    buffer.append(" <span>" + tStVmEnergySave.getEndTime() + "</span>");
                    buffer.append("</td>");
                    buffer.append("<td class='wh20'>");
                    if (!IntegerUtils.isPositiveInteger(tStVmEnergySave.getTempBoxNo())) {
                        buffer.append(" <span></span>");
                    } else {
                        buffer.append(" <span>" + tStVmEnergySave.getTempBoxNo() + "</span>");
                    }
                    buffer.append("</td>");
                    buffer.append("<td class='wh20'>");
                    buffer.append("<span>" + (tStVmEnergySave.getTargetValue() == null ? "" : tStVmEnergySave.getTargetValue()) + "</span>");
                    buffer.append("</td>");
                    buffer.append("</tr>");
                }
                buffer.append(" </tbody>  </table>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return buffer.toString();
    }

    @Override
    public String deleteEnergySave(ObjectContext context, Integer[] ids,
                                   TSysUser sysUser) {
        String errMsg = null;
        TStVmStrategy stVmStrategy = null;
        try {
            if (null != ids && ids.length > 0) {
                for (Integer id : ids) {
                    TStVmEnergySave energySave = stVmEnergySaveDao.findByPk(context, id);
                    if (null != energySave) {
                        if (null == stVmStrategy) {
                            stVmStrategy = energySave.getStrategy();
                        }
                        context.deleteObject(energySave);
                    }
                }
                if (null != stVmStrategy) {
                    stVmStrategy.setUtime(new Date());
                    stVmStrategy.setUtor(sysUser.getId());
                }
                context.commitChanges();
            }
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public TStVmEnergySave findByEnergySaveId(ObjectContext objectContext,
                                              Integer energySaveId) {
        try {
            return stVmEnergySaveDao.findByPk(objectContext, energySaveId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public Map<String, Object> editEnergySave(ObjectContext context,
                                              TSysUser user, Integer energySaveId, Integer addTargetValue,
                                              Integer addEnergySaveType, String addStartTime, String addEndTime,
                                              String addRemark, Integer addTempBoxNo) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (energySaveId == null) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择节能策略");
                return resultMap;
            }
            if (addEnergySaveType == null) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择节能类型");
                return resultMap;
            }
            if (addEnergySaveType != TStVmEnergySave.SCREEN) {
                if (addTargetValue == null) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请填写节能目标值");
                    return resultMap;
                }
                if (addTargetValue > TStVmEnergySave.TEMP_MAX) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可大于" + TStVmEnergySave.TEMP_MAX + "℃");
                    return resultMap;
                }
                if (addTargetValue < TStVmEnergySave.TEMP_MIN) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "不可小于" + TStVmEnergySave.TEMP_MIN + "℃");
                    return resultMap;
                }
                if (!IntegerUtils.isPositiveInteger(addTempBoxNo)) {
                    resultMap.put("type", "error");
                    resultMap.put("message", "请选择保温箱编号");
                    return resultMap;
                }
            }

            TStVmEnergySave energySave = stVmEnergySaveDao.findByPk(context, energySaveId);
            if (null == energySave) {
                resultMap.put("type", "error");
                resultMap.put("message", "节能策略不存在");
                return resultMap;
            }
            TStVmStrategy strategy = energySave.getStrategy();
            if (null == strategy) {
                resultMap.put("type", "error");
                resultMap.put("message", "节能策略不存在");
                return resultMap;
            }

            //关闭屏幕把保温箱编号置为0
            if (addEnergySaveType == TStVmEnergySave.SCREEN) {
                addTempBoxNo = 0;
            }

            //判断节能时间冲突
            Map<Integer, TStVmEnergySave> energySaveDoubleMap = strategy.getEnergySaveMapNew(energySaveId);
            String err = formatEnergySaveType(energySaveDoubleMap, addStartTime, addEndTime, addEnergySaveType, addTempBoxNo);
            if (null != err) {
                resultMap.put("type", "error");
                resultMap.put("message", err);
                return resultMap;
            }

            editEnergySave(user, addTargetValue,
                    addEnergySaveType, addStartTime,
                    addEndTime, addRemark, energySave, addTempBoxNo);

            context.commitChanges();
            resultMap.put("type", "success");
            resultMap.put("message", "修改节能策略成功");
            resultMap.put("strategyId", strategy.getStrategyId());
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return resultMap;
    }

    private void editEnergySave(TSysUser user, Integer addTargetValue,
                                Integer addEnergySaveType, String addStartTime, String addEndTime,
                                String addRemark, TStVmEnergySave energySave, Integer addTempBoxNo) {
        energySave.setUtime(new Date());
        energySave.setUtor(user.getId());
        energySave.setEndTime(addEndTime);
        energySave.setEnergySaveType(addEnergySaveType);
        energySave.setStartTime(addStartTime);
        energySave.setTargetValue(addTargetValue);
        energySave.setRemark(addRemark);

        energySave.setTempBoxNo(addTempBoxNo);
        TStVmStrategy strategy = energySave.getStrategy();
        strategy.setUtime(new Date());
        strategy.setUtor(user.getId());
    }

    private String formatEnergySaveType(Map<Integer, TStVmEnergySave> saveMap,
                                        String StartTimeStr, String EndTimeStr, Integer saveType, Integer tempBoxNo) {
        String errMsg = null;
        String[] heatStartArr = StartTimeStr.split(":");
        String[] heatEndArr = EndTimeStr.split(":");
        int heatStart = Integer.parseInt(heatStartArr[0] + heatStartArr[1]);
        int heatEnd = Integer.parseInt(heatEndArr[0] + heatEndArr[1]);

        if (heatStart >= heatEnd && saveType.equals(TStVmEnergySave.HEATING)) {
            errMsg = "制热开始时间必须小于结束时间，保存节能策略失败";
        }
        if (heatStart >= heatEnd && saveType.equals(TStVmEnergySave.REFRIGERATION)) {
            errMsg = "制冷开始时间必须小于结束时间，保存节能策略失败";
        }
        if (heatStart >= heatEnd && saveType.equals(TStVmEnergySave.HEATING)) {
            errMsg = "屏幕开始时间必须小于结束时间，保存节能策略失败";
        }
        for (TStVmEnergySave tStVmEnergySave : saveMap.values()) {
            String refriStartTimeStr = tStVmEnergySave.getStartTime();
            String refriEndTimeStr = tStVmEnergySave.getEndTime();

            String[] refriStartArr = refriStartTimeStr.split(":");
            String[] refriEndArr = refriEndTimeStr.split(":");
            int refriStart = Integer.parseInt(refriStartArr[0] + refriStartArr[1]);
            int refriEnd = Integer.parseInt(refriEndArr[0] + refriEndArr[1]);

            if ((heatStart <= refriStart && refriStart <= heatEnd && tStVmEnergySave.getTempBoxNo().equals(tempBoxNo)) || (heatStart <= refriEnd && refriEnd <= heatEnd && tStVmEnergySave.getTempBoxNo().equals(tempBoxNo)) || (heatStart >= refriStart && heatEnd <= refriEnd && tStVmEnergySave.getTempBoxNo().equals(tempBoxNo))) {
                errMsg = "节能时间冲突，保存节能策略失败";
                return errMsg;
            }
        }
        return errMsg;
    }

}

