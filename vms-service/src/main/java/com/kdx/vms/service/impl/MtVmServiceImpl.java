package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.StockExport;
import com.kdx.vms.orm.dao.*;
import com.kdx.vms.service.IMcsService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.utils.InnerCodeFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("mtVmService")
public class MtVmServiceImpl implements IMtVmService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MtVmDao mtVmDao;
    @Resource
    private MtNodeDao mtNodeDao;
    @Resource
    private MtVmModelDao mtVmModelDao;
    @Resource
    private MtSkuDao mtSkuDao;
    @Resource
    private IMcsService mcsService;
    @Resource
    private MtVmSkuSortDao mtVmSkuSortDao;

    @Override
    public String addMtVm(ObjectContext context, Integer nodeId,
                          Integer modelId, Integer mtVmId, String vmDesc, TSysUser user, Integer printer, Integer reserve, Integer[] payType) {
        String errMsg = null;
        try {
            //获取点位信息，获取售货机型号信息，
            if (null == nodeId) {
                return "请选择点位，新建售货机失败";
            }
            if (null == payType) {
                return "请选择支付方式，新建售货机失败";
            }
            TMtNode mtNode = Cayenne.objectForPK(context, TMtNode.class, nodeId);
            if (null == mtNode) {
                return "点位信息不存在，新建售货机失败";
            }
            TMtArea area = Cayenne.objectForPK(context, TMtArea.class, mtNode.getCityId());
            if (null == area) {
                return "点位区域信息有误，新建售货机失败";
            }
            if (null == modelId) {
                return "请选择售货机型号，新建售货机失败";
            }
            TMtVmModel vmModel = Cayenne.objectForPK(context, TMtVmModel.class, modelId);
            if (null == vmModel) {
                return "售货机型号信息不存在，新建售货机失败";
            }
            TMtCompany company = user.getCompany();
            Integer companyLevel = company.getCompanyLevel();
            if (null == companyLevel || companyLevel != TMtCompany.LEVEL_THREE) {
                return "只有三级公司可以新建售货机，新建售货机失败";
            }
            Integer maxVmCapacity = company.getMaxVmCapacity();
            if (null == maxVmCapacity || 0 == maxVmCapacity) {
                return "没有可分配的售货机名额，新建售货机失败";
            }

            List<TMtVm> allMtVmList = mtVmDao.findAllMtVm(context, user);
            if (null != allMtVmList && maxVmCapacity <= allMtVmList.size()) {
                return "没有可分配的售货机名额，新建售货机失败!";
            }
            TMtVm oldMtVm = null;
            if (null != mtVmId) {
                oldMtVm = Cayenne.objectForPK(context, TMtVm.class, mtVmId);
                if (null == oldMtVm) {
                    return "复制策略失败，被复制售货机不存在，新建售货机失败";
                }
                Integer oldModelId = oldMtVm.getVmModelId();
                if (!modelId.equals(oldModelId)) {
                    return "复制策略失败，售货机型号不一致，新建售货机失败";
                }
            }
            //创建售货机
            TMtVm mtVm = createMtVm(context, modelId, vmDesc, user, mtNode, area);
            mtVm.setPrinter(printer);
            mtVm.setReserve(reserve);
            mtVm.setIsReserve(TMtVm.IS_RESERVE_TRUE);
            mtVm.setVmType(vmModel.getCombinatorialPattern());
            mtVm.setVmExtentionNum(1);
            List<TMtVmModelChannel> modelChannels = vmModel.getModelChannels();
            for (TMtVmModelChannel tMtVmModelChannel : modelChannels) {
                //售货机货道信息
                TMtVmChannel mtVmChannel = formatMtVmChannel(context, user, tMtVmModelChannel);
                //售货机将要应用货道信息
                TMtVmChannelNext mtVmChannelNext = formatMtVmChannelNext(context, user, tMtVmModelChannel);

                mtVm.addToChannelInfo(mtVmChannel);
                mtVm.addToChannelNext(mtVmChannelNext);
            }
            //生成闪购键
            createShortCutsForAddVm(context, vmModel, mtVm);

            TMtVmText mtVmText = context.newObject(TMtVmText.class);
            mtVm.addToText(mtVmText);
            context.commitChanges();
            if (null != mtVmId) {

                //复制策略  复制即将应用的策略

                //复制货道策略
                copyChannel(user, mtVm, oldMtVm);

                //复制闪购键策略5.0取消
//                copyShortCuts(user, mtVm, oldMtVm);

                //复制列表页策略5.0取消
//                copySortNextForAddVm(context, user, mtVm, oldMtVm);
                //复制节能5.0取消

//                copyEnergySaveForAddVm(context, user, mtVm, oldMtVm);
            }
            //创建投放工单

            createLaunchForAddVm(context, user, mtVm);

            //创建支付方式
            for (Integer type : payType) {
                createPayType(context, type, mtVm, user);
            }
            //创建售货机是否预定
            mtVm.setIsReserve(TMtVm.IS_RESERVE_TRUE);
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
            errMsg = "系统内部出现异常";
        }
        return errMsg;
    }

    @Override
    public String extendMtVm(TMtVm mainVm, TSysUser user, Integer copyVmId, Integer modelId) {
        String errMsg = null;
        if (null == mainVm) return "所选售货机不存在";
        ObjectContext context = mainVm.getObjectContext();
        try {
            if (!mainVm.getVmType().equals(TMtVmModel.COMBINATORIAL_MAIN)) {
                return "非主机无法扩展";
            }
            if (null == modelId) {
                return "请选择售货机型号，新建售货机失败";
            }
            TMtVmModel vmModel = Cayenne.objectForPK(context, TMtVmModel.class, modelId);
            if (null == vmModel) {
                return "售货机型号信息不存在，新建售货机失败";
            }
            TMtCompany company = user.getCompany();
            Integer companyLevel = company.getCompanyLevel();
            if (null == companyLevel || companyLevel != TMtCompany.LEVEL_THREE) {
                return "只有三级公司可以新建售货机，新建售货机失败";
            }
            Integer maxVmCapacity = company.getMaxVmCapacity();
            if (null == maxVmCapacity || 0 == maxVmCapacity) {
                return "没有可分配的售货机名额，新建售货机失败";
            }

            List<TMtVm> allMtVmList = mtVmDao.findAllMtVm(context, user);
            if (null != allMtVmList && maxVmCapacity <= allMtVmList.size()) {
                return "没有可分配的售货机名额，新建售货机失败!";
            }
            TMtVm oldMtVm = null;
            if (null != copyVmId) {
                oldMtVm = Cayenne.objectForPK(context, TMtVm.class, copyVmId);
                if (null == oldMtVm) {
                    return "复制策略失败，被复制售货机不存在，新建售货机失败";
                }
                Integer oldModelId = oldMtVm.getVmModelId();
                if (!modelId.equals(oldModelId)) {
                    return "复制策略失败，售货机型号不一致，新建售货机失败";
                }
            }
            logger.info("extend vm: id=" + mainVm.getId());
            TMtVm mtVm = createExtend(context, modelId, mainVm, user);
            mtVm.setVmType(vmModel.getCombinatorialPattern());
            List<TMtVmModelChannel> modelChannels = vmModel.getModelChannels();
            for (TMtVmModelChannel tMtVmModelChannel : modelChannels) {
                //售货机货道信息
                TMtVmChannel mtVmChannel = formatMtVmChannel(context, user, tMtVmModelChannel);
                //售货机将要应用货道信息
                TMtVmChannelNext mtVmChannelNext = formatMtVmChannelNext(context, user, tMtVmModelChannel);

                mtVm.addToChannelInfo(mtVmChannel);
                mtVm.addToChannelNext(mtVmChannelNext);
            }
            context.commitChanges();
            if (null != oldMtVm) {
                //复制货道策略
                copyChannel(user, mtVm, oldMtVm);
            }
            //创建投放工单
            createLaunchForAddVm(context, user, mtVm);
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            context.rollbackChanges();
            errMsg = "系统内部出现异常";
        }

        return errMsg;
    }

    private void createShortCutsForAddVm(ObjectContext context,
                                         TMtVmModel vmModel, TMtVm mtVm) {
        Integer shortcutsQty = vmModel.getShortcutsQty();//闪购键数量
        //生成闪购键
        for (int i = 0; i < shortcutsQty; i++) {
            TMtVmShortcuts mtVmShortcuts = context.newObject(TMtVmShortcuts.class);
            mtVmShortcuts.setShortcutsCode(i + 1 + "");
            mtVm.addToShortcuts(mtVmShortcuts);
        }
    }

    private TMtVm createMtVm(ObjectContext context, Integer modelId,
                             String vmDesc, TSysUser user, TMtNode mtNode, TMtArea area) {
        TMtVm mtVm = context.newObject(TMtVm.class);
        mtVm.setCompany(user.getCompany());
        mtVm.setCtime(new Date());
        mtVm.setCtor(user.getId());
        mtVm.setDeleteMark(0);
        mtVm.setStockStatus(TMtVm.STOCK_NORMAL);
        mtVm.setInnerCode(InnerCodeFactory.getInnerCode(area.getAreaCode()));
        mtVm.setNode(mtNode);
        mtVm.setVmDesc(vmDesc);
        mtVm.setVmModelId(modelId);
        mtVm.setVmStatus(TMtVm.STATUS_UNDEPLOY);
        mtVm.setCreateTaskCycle(TMtVm.TASK_CYCLE_DEFAULT);
        return mtVm;
    }

    private TMtVm createExtend(ObjectContext context, Integer modelId, TMtVm mainVm, TSysUser user) {
        TMtVm mtVm = context.newObject(TMtVm.class);
        mtVm.setCompany(user.getCompany());
        mtVm.setCtime(new Date());
        mtVm.setCtor(user.getId());
        mtVm.setDeleteMark(0);
        mtVm.setIsReserve(TMtVm.IS_RESERVE_TRUE);
        mtVm.setReserve(mainVm.getReserve());
        mtVm.setStockStatus(TMtVm.STOCK_NORMAL);
        Integer extentionNum = InnerCodeFactory.getExtentionNum(mainVm);
        mtVm.setInnerCode(mainVm.getInnerCode() + "-" + extentionNum);
        mtVm.setNode(mainVm.getNode());
        mtVm.setVmModelId(modelId);
        mtVm.setParentVm(mainVm);
        mtVm.setVmStatus(TMtVm.STATUS_UNDEPLOY);
        mtVm.setVmExtentionNum(extentionNum);
        mtVm.setCreateTaskCycle(TMtVm.TASK_CYCLE_DEFAULT);
        return mtVm;
    }

    private void copySortNextForAddVm(ObjectContext context, TSysUser user,
                                      TMtVm mtVm, TMtVm oldMtVm) {
        List<TMtVmSkuSort> oldSortList = oldMtVm.getSkuSort();
        if (null != oldSortList && oldSortList.size() > 0) {
            for (TMtVmSkuSort tMtVmSkuSort : oldSortList) {
                TMtVmSkuSortNext sortNext = context.newObject(TMtVmSkuSortNext.class);
                sortNext.setCtime(new Date());
                sortNext.setCtor(user.getId());
                sortNext.setSkuId(tMtVmSkuSort.getSkuId());
                sortNext.setSort(tMtVmSkuSort.getSort());
                mtVm.addToSkuSortNext(sortNext);
            }
        }
    }

    private void copyEnergySaveForAddVm(ObjectContext context, TSysUser user,
                                        TMtVm mtVm, TMtVm oldMtVm) {
        List<TMtVmEnergySave> oldMtVmEnergySaveList = oldMtVm.getEnergySave();
        if (null != oldMtVmEnergySaveList && oldMtVmEnergySaveList != null) {
            for (TMtVmEnergySave oldMtVmEnergySave : oldMtVmEnergySaveList) {

                TMtVmEnergySave currEnergySave = context.newObject(TMtVmEnergySave.class);
                currEnergySave.setCtime(new Date());
                currEnergySave.setCtor(user.getId());
                currEnergySave.setEndTime(oldMtVmEnergySave.getEndTime());
                currEnergySave.setEnergySaveType(oldMtVmEnergySave.getEnergySaveType());
                currEnergySave.setRemark(oldMtVmEnergySave.getRemark());
                currEnergySave.setStartTime(oldMtVmEnergySave.getStartTime());
                currEnergySave.setTargetValue(oldMtVmEnergySave.getTargetValue());
                currEnergySave.setTempBoxNo(oldMtVmEnergySave.getTempBoxNo());
                mtVm.addToEnergySave(currEnergySave);

            }
        }
    }

    private void createLaunchForAddVm(ObjectContext context, TSysUser user, TMtVm mtVm) {
        TTaskVm taskVm = context.newObject(TTaskVm.class);
        taskVm.setCtime(new Date());
        taskVm.setCtor(user.getId());
        taskVm.setTaskLevel(TTaskVm.LEVEL_ROUTINE);
        taskVm.setTaskTitle("新建售货机系统自动生成工单" + mtVm.getInnerCode());
        taskVm.setTaskType(TTaskVm.TYPE_DEPLOY);
        taskVm.setTaskVmStatus(TTaskVm.STATUS_INITIALIZATION);
        taskVm.setMtVm(mtVm);
        taskVm.setCompany(mtVm.getCompany());
    }

    private void copyShortCuts(TSysUser user, TMtVm mtVm, TMtVm oldMtVm) {
        List<TMtVmShortcuts> oldShortCuts = oldMtVm.getShortcuts();
        Map<String, TMtVmShortcuts> oldShortCutsMap = new HashMap<String, TMtVmShortcuts>();
        for (TMtVmShortcuts tMtVmShortcuts : oldShortCuts) {
            oldShortCutsMap.put(tMtVmShortcuts.getShortcutsCode(), tMtVmShortcuts);
        }
        List<TMtVmShortcuts> MtShortCutsList = mtVm.getShortcuts();
        for (TMtVmShortcuts currMtVmShortcuts : MtShortCutsList) {
            TMtVmShortcuts oldShortCut = oldShortCutsMap.get(currMtVmShortcuts.getShortcutsCode());
            currMtVmShortcuts.setUtime(new Date());
            currMtVmShortcuts.setUtor(user.getId());
            currMtVmShortcuts.setSku(oldShortCut.getSku());
        }
    }

    private void copyChannel(TSysUser user, TMtVm mtVm, TMtVm oldMtVm) {
        List<TMtVmChannel> oldChannelList = oldMtVm.getChannelInfo();
        Map<String, TMtVmChannelNext> channelNextMap = mtVm.getChannelNext();
        for (TMtVmChannel oldChannel : oldChannelList) {
            TMtVmChannelNext currMtVmChannelNext = channelNextMap.get(oldChannel.getChannelCode());
            currMtVmChannelNext.setMaxCapacity(oldChannel.getMaxCapacity());
            currMtVmChannelNext.setMergeQty(oldChannel.getMergeQty());
            currMtVmChannelNext.setSku(oldChannel.getSku());
            currMtVmChannelNext.setAdjustMaxCapacity(oldChannel.getAdjustMaxCapacity());
            if (null != oldChannel.getMergePid()) {
                TMtVmChannel pMtVmChannel = Cayenne.objectForPK(mtVm.getObjectContext(), TMtVmChannel.class, oldChannel.getMergePid());
                String channelCode = pMtVmChannel.getChannelCode();
                Map<String, TMtVmChannelNext> map = mtVm.getChannelNext();
                TMtVmChannelNext pTMtVmChannelNext = map.get(channelCode);
                currMtVmChannelNext.setMergePid(pTMtVmChannelNext.getChannelId());
            }
            currMtVmChannelNext.setUtime(new Date());
            currMtVmChannelNext.setUtor(user.getId());
        }
    }

    private TMtVmChannelNext formatMtVmChannelNext(ObjectContext context,
                                                   TSysUser user, TMtVmModelChannel tMtVmModelChannel) {
        TMtVmChannelNext mtVmChannelNext = context.newObject(TMtVmChannelNext.class);
        mtVmChannelNext.setChannelCode(tMtVmModelChannel.getChannelCode());
        mtVmChannelNext.setChannelHeight(tMtVmModelChannel.getChannelHeight());
        mtVmChannelNext.setChannelLong(tMtVmModelChannel.getChannelLength());
        mtVmChannelNext.setChannelWidth(tMtVmModelChannel.getChannelWidth());
        mtVmChannelNext.setChannelStatus(0);
        mtVmChannelNext.setCtime(new Date());
        mtVmChannelNext.setCtor(user.getId());
        mtVmChannelNext.setAcceptWidthMax(tMtVmModelChannel.getAcceptWidthMax());
        mtVmChannelNext.setDisplayMode(tMtVmModelChannel.getDisplayMode());
        mtVmChannelNext.setIsFixedCapacity(tMtVmModelChannel.getIsFixedCapacity());
        mtVmChannelNext.setFixedCapacity(tMtVmModelChannel.getFixedCapacity());
        mtVmChannelNext.setMergeQty(1);
        mtVmChannelNext.setMaxCapacity(0);
        return mtVmChannelNext;
    }

    private TMtVmChannel formatMtVmChannel(ObjectContext context,
                                           TSysUser user, TMtVmModelChannel tMtVmModelChannel) {
        TMtVmChannel mtVmChannel = context.newObject(TMtVmChannel.class);
        mtVmChannel.setChannelCode(tMtVmModelChannel.getChannelCode());
        mtVmChannel.setChannelHeight(tMtVmModelChannel.getChannelHeight());
        mtVmChannel.setChannelLong(tMtVmModelChannel.getChannelLength());
        mtVmChannel.setChannelWidth(tMtVmModelChannel.getChannelWidth());
        mtVmChannel.setChannelStatus(0);
        mtVmChannel.setCtime(new Date());
        mtVmChannel.setCtor(user.getId());
        mtVmChannel.setAcceptWidthMax(tMtVmModelChannel.getAcceptWidthMax());
        mtVmChannel.setDisplayMode(tMtVmModelChannel.getDisplayMode());
        mtVmChannel.setIsFixedCapacity(tMtVmModelChannel.getIsFixedCapacity());
        mtVmChannel.setFixedCapacity(tMtVmModelChannel.getFixedCapacity());
        mtVmChannel.setMergeQty(1);
        mtVmChannel.setCurrNum(0);
        mtVmChannel.setMaxCapacity(0);
        return mtVmChannel;
    }

    @Override
    public List<TMtVm> findMtVmList(ObjectContext context, Integer filedType,
                                    String filedValue, Integer modelId, Integer vmStatus,
                                    Integer province, Integer city, Integer country,
                                    Integer bussinessId, TSysUser user, Integer pageSize, Boolean showPattern) {
        try {
            return mtVmDao.findListByCondition(context, filedType, filedValue, modelId, vmStatus, province, city, country, bussinessId, user, pageSize, showPattern);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtVm> findMtVmListByCompanyId(ObjectContext context, Integer companyId) {
        try {
            return mtVmDao.findAllMtVmByCompanyId(context, companyId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public List<TMtVm> findMtVmListByUser(TSysUser user, String innerCode) {
        return mtVmDao.findAllVm(user, innerCode);
    }

    @Override
    public String makeMtVmChannelTableString(ObjectContext context,
                                             Integer mtVmId) {
        logger.debug("mtVmId = " + mtVmId);
        String errMsg = null;
        try {
            StringBuffer buffer = new StringBuffer();
            TMtVm mtVm = mtVmDao.findByVmId(context, mtVmId);
            List<TMtVmChannel> channelList = mtVm.getChannelInfo();
            Map<String, TMtVmChannel> channelMap = new HashMap<String, TMtVmChannel>();
            for (TMtVmChannel tStVmChannel : channelList) {
                channelMap.put(tStVmChannel.getChannelCode(), tStVmChannel);
            }
            TMtVmModel vmModel = mtVmModelDao.findMtVmModelById(context, mtVm.getVmModelId());

            int row = vmModel.getChannelLayerQty();
            String col = vmModel.getChannelPerLayer();
            String cols[] = col.split(",");
//            int index = 0;
            int channelIndex = 0;
            for (int i = 1; i <= row; i++) {
                buffer.append("<ul>");
                int colInt = Integer.parseInt(cols[i - 1]);
//                index = (i - 1) * colInt;
                Integer demp = Integer.parseInt(cols[i - 1]);//当前行有多少列
                for (int n = 1; n <= colInt; n++) {
//                    if (index + n - 1 <= channelList.size()) {
                    String channelCode = "";
                    String skuName = "";
                    String skuUnit = "";
                    Integer channelId = 0;
                    Integer mergeQty = 0;

                    Integer curr = 0;
                    Integer max = 0;
                    boolean empty = false;
                    mergeQty = channelMap.get(i + "-" + n).getMergeQty();
                    channelCode = channelMap.get(i + "-" + n).getChannelCode();
                    String imgUrl = "";
                    TMtSku sku = channelMap.get(i + "-" + n).getSku();
                    if (sku != null) {
                        imgUrl = sku.getImgUrl();
                        skuName = sku.getSkuName();
                        skuUnit = sku.getSkuUnit();
                    }
                    curr = channelMap.get(i + "-" + n).getCurrNum();
                    max = channelMap.get(i + "-" + n).getTempMaxCapacity();
                    if (null != channelMap.get(i + "-" + n).getHasNextStock() && channelMap.get(i + "-" + n).getHasNextStock().equals(0)) {
                        empty = true;
                    } else if (null != curr && curr.equals(0) && sku != null) {
                        empty = true;
                    }
                    if (mergeQty == 0) continue;

                    if (null == skuName) skuName = "";
                    buffer.append("<li class='wh" + 100 / demp * mergeQty + "'> <input type='hidden' class='channelCode' rol='" + i + "' index='" + channelIndex + "' code='" + channelCode + "' value='" + channelId + "'>");
                    buffer.append(" <p class=\"txtleft\"><span>" + channelCode + "</span></p>");
                    buffer.append("<img src='" + imgUrl + "'>");
                    if (null != sku) {
                        buffer.append("<label class=\"bt0\"><span class=\"cf_red\">余" + curr + "</span><span class=\"mlf5\">/</span><span class=\"cf_green\">满" + max + "</span></label>");
                    }
                    buffer.append("<label><a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + skuName + skuUnit + "\" data-original-title=\"" + skuName + skuUnit + "\"><span>" + skuName + "</span></a></label>");
                    if (empty) {
                        buffer.append("<i class=\"icon_emptytxt\">空</i>");
                    }
                    buffer.append("</li>");
                    channelIndex++;
//                    }
                }
                buffer.append("</ul>");
            }

            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            errMsg = "货道策略详情异常，系统内部出现异常";
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String makeSkuSortTableString(ObjectContext context,
                                         Integer mtVmId) {
        StringBuffer buffer = new StringBuffer();
        try {
            TMtVm mtVm = mtVmDao.findByVmId(context, mtVmId);
            if (null != mtVm) {

                //5.0.0a主机的列表页展示主机商品+副机商品的集合
                List<TMtVmSkuSort> sortLists = mtVmSkuSortDao.findSkuSort(mtVm).stream().map(TMtVmSkuSort.class::cast).collect(Collectors.toList());
//                List<TMtVmSkuSort> sortLists = new ArrayList<>();
//                if (null != mtVm.getSkuSort() && mtVm.getSkuSort().size() > 0) {
//                    List<TMtVmSkuSort> skuSort = mtVm.getSkuSort();
//                    for(TMtVmSkuSort tMtVmSkuSort : skuSort){
//                        sortLists.add(tMtVmSkuSort);
//                    }
//                }
//                if(null != mtVm.getChildrenVm() && mtVm.getChildrenVm().size() >0){
//                    List<TMtVm> list =  mtVm.getChildrenVm();
//                    for (TMtVm tMtVm : list){
//                        List<TMtVmSkuSort> skuSorts = tMtVm.getSkuSort();
//                        for(TMtVmSkuSort tMtVmSkuSort : skuSorts){
//                            sortLists.add(tMtVmSkuSort);
//                        }
//                    }
//                }



                if (null != sortLists && sortLists.size() > 0) {
                    Map<Integer, TMtVmSkuSort> sortMap = new HashMap<Integer, TMtVmSkuSort>();
                    Integer[] indexs = new Integer[sortLists.size()];
                    buffer.append("<ul>");
                    for (int i = 0; i < sortLists.size(); i++) {
                        TMtVmSkuSort sort = sortLists.get(i);
                        sortMap.put(sort.getSort(), sort);
                        indexs[i] = sort.getSort();
                    }
                    Arrays.sort(indexs);
                    int i = 0;
                    for (Integer index : indexs) {
                        if (i % 15 == 0 && i != indexs.length) {
                            int page = i / 15 + 1;
                            //buffer.append("<br>");
                            buffer.append("<div class='sortPageDivTitle clearfix'> 第" + page + "页</div>");
                        }
                        TMtVmSkuSort sort = sortMap.get(index);
                        TMtSku sku = mtSkuDao.findByPk(context, sort.getSkuId());
                        buffer.append("<li>");
                        buffer.append("<section>");
                        buffer.append("<img src='" + sku.getImgUrl() + "'>");
                        buffer.append("</section>");
                        buffer.append("<i>");
                        buffer.append("</i>");
//                        buffer.append("<p>" + sku.getSkuName() + "</p>");
                        buffer.append("<p><a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + sku.getSkuName() + sku.getSkuUnit() + "\" data-original-title=\"" + sku.getSkuName() + sku.getSkuUnit() + "\"><span>" + sku.getSkuName() + "</span></a></p>");
                        buffer.append("</li>");
                        i++;
                    }
                    buffer.append("</ul>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return buffer.toString();
    }

    public List getMtVmForStatus(TSysUser user, String innerCode, String nodeName, Integer netStatus, Integer vmcStatus,Integer taskStatus, Integer tempStatus, Integer pageSize) {
        logger.info("user:" + user + ", innerCode:" + innerCode + ", nodeName:" + nodeName + ", netStatus:" + netStatus + ", vmcStatus:" + vmcStatus + ", taskStatus:" + taskStatus+ ", tempStatus:" + tempStatus + ", pageSize:" + pageSize);
        return mtVmDao.findVmForStatus(user, innerCode, nodeName, netStatus, vmcStatus,taskStatus,tempStatus,pageSize);
    }

    public List<TMtVm> getMtVmForStocks(TMtCompany company, String innerCode, String nodeName, Integer pageSize) {
        logger.info("company:" + company + ", innerCode:" + innerCode + ", nodeName:" + nodeName + ", pageSize:" + pageSize);
        return mtVmDao.findVmForStocks(company, innerCode, nodeName, pageSize);
    }

    public TMtVm getMtVm(ObjectContext context, Integer vmId) {
        logger.info("context:" + context + ", vmId:" + vmId);
        if (null == context || !IntegerUtils.isPositiveInteger(vmId)) return null;
        return mtVmDao.findByVmId(context, vmId);
    }

    @Override
    public TMtVm findMtVmByInnerCode(ObjectContext context, String innerCode) {
        return mtVmDao.findByInnerCode(context, innerCode);
    }

    @Override
    public String editMtVm(ObjectContext objectContext, Integer vmId,
                           String vmDesc, Integer printer, Integer reserve, Integer[] payType, TSysUser user) {
        String errMsg = "";
        try {
            if (null == vmId) {
                return "请选择售货机";
            }
            TMtVm mtVm = mtVmDao.findByVmId(objectContext, vmId);
            if (null == mtVm) {
                return "售货机不存在";
            }
            Integer oldPrinter = mtVm.getPrinter();
            mtVm.setVmDesc(vmDesc);
            mtVm.setPrinter(null == printer ? 0 : printer);
            mtVm.setReserve(null == reserve ? 0 : reserve);
            if(null != mtVm.getChildrenVm() && mtVm.getChildrenVm().size() >0){
                List<TMtVm> tMtVms = mtVm.getChildrenVm();
                tMtVms.forEach(tMtVm -> {
                    tMtVm.setReserve(mtVm.getReserve());
                });
            }
            mtVm.setUtime(new Date());
            mtVm.setUtor(user.getId());

            List<TMtPayType> list = mtVmDao.findPayTypeByVmId(objectContext, vmId);
            if (list.size() == 0) {
                if (null != payType) {
                    for (Integer type : payType) {
                        createPayType(objectContext, type, mtVm, user);
                    }
                }
            } else {

                Integer[] codes = new Integer[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    codes[i] = list.get(i).getCode();
                }
                Arrays.sort(payType);
                Arrays.sort(codes);
                if (!Arrays.equals(payType, codes)) {
                    for (TMtPayType tMtPayType : list) {
                        objectContext.deleteObject(tMtPayType);
                    }
                    //创建支付方式
                    for (Integer type : payType) {
                        createPayType(objectContext, type, mtVm, user);
                    }
                }
                if (!mtVm.getPrinter().equals(oldPrinter) || !Arrays.equals(payType, codes)) {
                    String result = mcsService.sendVmBaseInfo(objectContext, mtVm.getInnerCode(), "vmBox", null);
                    if (null != result) {
                        objectContext.rollbackChanges();
                        return result;
                    }
                }
            }
            //5.0 副机具体地址同步修改
            if (CheckUtils.check(mtVm.getChildrenVm())) {
                mtVm.getChildrenVm().forEach(vm -> vm.setVmDesc(vmDesc));
            }
            objectContext.commitChanges();
        } catch (Exception e) {
            objectContext.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public String changeSale(ObjectContext objectContext, Integer vmId) {
        String errMsg = "";
        try {
            if (null == vmId) {
                return "请选择售货机";
            }
            TMtVm mtVm = mtVmDao.findByVmId(objectContext, vmId);
            if (null == mtVm) {
                return "售货机不存在";
            }
            Integer status = mtVm.getVmStatus();

            if (status != TMtVm.STATUS_SALE && status != TMtVm.STATUS_UNSLAE) {
                return "当前售货机状态不允许操作";
            }
            if (status == TMtVm.STATUS_SALE) {
                mtVm.setVmStatus(TMtVm.STATUS_UNSLAE);
            }
            if (status == TMtVm.STATUS_UNSLAE) {
                mtVm.setVmStatus(TMtVm.STATUS_SALE);
            }
            objectContext.commitChanges();
        } catch (Exception e) {
            objectContext.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public Map<String, Object> unbind(TSysUser currentUser, ObjectContext context, Integer[] vmIds) {
        try {
            if (null != vmIds && currentUser != null) {
                for (Integer vmId : vmIds) {
                    TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
                    if (null != mtVm) {
                        mtVm.setUniqueCode(null);
                        mtVm.setUtime(new Date());
                        mtVm.setUtor(currentUser.getId());
                    }
                }
                context.commitChanges();
                return ReturnCode.SUCCESS.toMap();
            } else {
                return ReturnCode.NO_SELECT_VM.toMap();
            }
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            return ReturnCode.CONTEXT_COMMIT_FAIL.toMap();
        }
    }

    @Override
    public List<TMtVm> findForUpGrade(ObjectContext objectContext, Integer vmModelId, Integer province, Integer city, Integer country, Integer pageSize) {
        return mtVmDao.findForUpGrade(objectContext, vmModelId, province, city, country, pageSize);
    }

    @Override
    public List<StockExport> findVmChannelMessage(ObjectContext context, TMtCompany company) {
        try {
            List<TMtVm> vmList = mtVmDao.findStockMessage(context, company);
            List<StockExport> list = new ArrayList<>();
            for (TMtVm tMtVm : vmList) {
                List<TMtVmChannel> channelList = tMtVm.getChannelInfo();
                for (TMtVmChannel channel : channelList) {
                    TMtSku sku = channel.getSku();
                    if (sku == null) {
                        logger.warn("goods is null,connitue.compayId=" + company.getId());
                        continue;
                    }
                    StockExport export = new StockExport();
                    export.setSkuName(sku.getSkuName());
                    export.setSpecification(sku.getSkuUnit());
                    export.setInnerCode(tMtVm.getInnerCode());
                    export.setNodeName(tMtVm.getNodeName());
                    export.setCurCount(channel.getCurrNum());
                    export.setMaxCount(channel.getTempMaxCapacity());
                    export.setChannelCode(channel.getChannelCode());
                    Integer dayCount = channel.getManufactureEndDate() == null ? null : DateUtils.daysBetween(new Date(), channel.getManufactureEndDate());
                    export.setDayCount(dayCount);
                    list.add(export);
                }

            }
            return list;
        } catch (Exception e) {
            logger.error("process all vm stock data is Exception,company=" + company + ",cause by=" + e.getMessage(), e);
        }
        return Collections.emptyList();
    }


    private TMtPayType createPayType(ObjectContext objectContext, Integer type, TMtVm mtVm, TSysUser user) {
        TMtPayType tMtPayType = objectContext.newObject(TMtPayType.class);
        tMtPayType.setCode(type);
        tMtPayType.setMtVm(mtVm);
        tMtPayType.setUtor(user.getId());
        tMtPayType.setUtime(new Date());
        return tMtPayType;
    }

}
