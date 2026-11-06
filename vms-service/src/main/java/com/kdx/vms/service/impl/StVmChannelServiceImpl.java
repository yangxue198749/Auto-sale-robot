package com.kdx.vms.service.impl;

import com.kdx.vms.commons.util.ReturnCode;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.orm.dao.*;
import com.kdx.vms.service.IStVmChannelService;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service("stVmChannelService")
public class StVmChannelServiceImpl implements IStVmChannelService {

    @Resource
    private MtVmModelDao mtVmModelDao;
    @Resource
    private StVmStrategyDao stVmStrategyDao;
    @Resource
    private StVmChannelDao stVmChannelDao;
    @Resource
    private MtSkuDao mtSkuDao;
    @Resource
    private StVmSkuSortDao stVmSkuSortDao;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String addStVmChannel(ObjectContext context, TSysUser user,
                                 Integer vmModelId, String stVmName, String stVmDesc) {
        logger.debug("user = " + user + ",vmModelId = " + vmModelId + ",stVmName = " + stVmName + ", stVmDesc = " + stVmDesc);
        String message = "";
        try {
            if (null == vmModelId) {
                message = "请选择售货机型号";
                return message;
            }
            if (StringUtil.isEmpty(stVmName)) {
                message = "请填写策略名称";
                return message;
            }

            TMtVmModel vmModel = mtVmModelDao.findMtVmModelById(context, vmModelId);
            if (null == vmModel) {
                message = "售货机型号不存在";
                return message;
            }
            List<TMtVmModelChannel> modelChannelList = vmModel.getModelChannels();
            if (null == modelChannelList || modelChannelList.size() == 0) {
                message = "售货机型号货道配置不存在";
                return message;
            }
            TStVmStrategy strategy = context.newObject(TStVmStrategy.class);

            formatAddStrategy(user, stVmName, stVmDesc, vmModel, strategy);
            for (TMtVmModelChannel tMtVmModelChannel : modelChannelList) {
                TStVmChannel stVmChannel = context.newObject(TStVmChannel.class);
                stVmChannel.setMergeQty(1);
                BeanUtils.copyProperties(tMtVmModelChannel, stVmChannel, "objectId", "persistenceState");
                stVmChannel.setChannelLong(tMtVmModelChannel.getChannelLength());
                strategy.addToStChannel(stVmChannel);
            }
            context.commitChanges();
            return makeStVmChanenlTableString(context, strategy.getStrategyId(), false);
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            message = "系统内部异常";
        }
        return message;
    }

    private void formatAddStrategy(TSysUser user, String stVmName,
                                   String stVmDesc, TMtVmModel vmModel, TStVmStrategy strategy) {
        strategy.setCtime(new Date());
        strategy.setCtor(user.getId());
        strategy.setCompany(user.getCompany());
        strategy.setStrategyDesc(stVmDesc);
        strategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_CHANNEL);
        strategy.setStrategyName(stVmName);
        strategy.setVmModel(vmModel);
    }

    @Override
    public String makeStVmChanenlTableString(ObjectContext context,
                                             Integer strategyId, Boolean showChannel) {
        logger.debug("strategyId = " + strategyId);
        String errMsg = null;
        try {
            StringBuffer buffer = new StringBuffer();
            TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
            buffer.append("<input name='relateStrategyId' value='" + strategyId + "' type='hidden'> ");
            List<TStVmChannel> channelList = strategy.getStChannel();
            Map<String, TStVmChannel> channelMap = new HashMap<String, TStVmChannel>();
            for (TStVmChannel tStVmChannel : channelList) {
                channelMap.put(tStVmChannel.getChannelCode(), tStVmChannel);
            }
            TMtVmModel vmModel = strategy.getVmModel();

            int row = vmModel.getChannelLayerQty();
            String col = vmModel.getChannelPerLayer();
            String cols[] = col.split(",");
//			int index = 0;
            int channelIndex = 0;
            for (int i = 1; i <= row; i++) {
                buffer.append("<ul>");
                int colInt = Integer.parseInt(cols[i - 1]);
//				index = (i-1) * colInt;
                Integer demp = Integer.parseInt(cols[i - 1]);//当前行有多少列
                for (int n = 1; n <= colInt; n++) {
//					if(index + n - 1 <= channelList.size()){
                    String channelCode = "";
                    String skuName = "";
                    String skuUnit = "";
                    Integer channelId = 0;
                    Integer mergeQty = 0;
                    Integer skuId = 0;

                    mergeQty = channelMap.get(i + "-" + n).getMergeQty();
                    channelId = channelMap.get(i + "-" + n).getChannelId();
                    channelCode = channelMap.get(i + "-" + n).getChannelCode();
                    skuId = channelMap.get(i + "-" + n).getSkuId();
                    Integer maxCapacity = channelMap.get(i + "-" + n).getAdjustMaxCapacity() == null ? channelMap.get(i + "-" + n).getMaxCapacity() : channelMap.get(i + "-" + n).getAdjustMaxCapacity();
                    String imgUrl = "";
                    if (skuId != null && skuId != 0) {
                        TMtSku sku = mtSkuDao.findByPk(context, skuId);
                        imgUrl = sku.getImgUrl();
//								imgUrl = PropertiesLoader.map.get("imgUrlPrefix")+sku.getImgUrl();
                        skuName = sku.getSkuName();
                        skuUnit = sku.getSkuUnit();
                    }
                    if (mergeQty == 0) continue;

                    if (null == skuName) skuName = "";
                    buffer.append("<li class='wh" + 100 / demp * mergeQty + "'> <input type='hidden' class='channelCode' rol='" + i + "' index='" + channelIndex + "' code='" + channelCode + "' value='" + channelId + "'>");
                    buffer.append(" <p><span class='icon_state sm'>" + channelCode + "</span></p>");
                    buffer.append("<img src='" + imgUrl + "'>");
                    if (!StringUtil.isEmpty(skuName)) {
                        if (showChannel) {
                            buffer.append("<label><span>满" + "     " + maxCapacity + "</span></label>");
                        } else {
                            buffer.append("<label><span>满" + "     " + maxCapacity + "</span><button class='glyphicon glyphicon-pencil' id='setFullSkuDialog' data-toggle='modal' href='/stVmChannel/showFullDialog?channelId=" + channelId + " ' data-target='#setFullSku' ></button></label>");
                        }
                    } else {
                        buffer.append("<label class='bt0'><span></span></label>");
                    }
//						buffer.append("<label><span>"+skuName+"</span></label>");
                    buffer.append("<label><a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + skuName + skuUnit + "\" data-original-title=\"" + skuName + skuUnit + "\"><span>" + skuName + "</span></a></label>");

                    buffer.append("</li>");
                    channelIndex++;
//					}
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
    public String mergeChannel(ObjectContext context, Integer firstChannelId, Integer secondChannelId, TSysUser user) {
        logger.debug("firstChannelId = " + firstChannelId + ",secondChannelId = " + secondChannelId);
        String errMsg = null;
        try {
            if (null == firstChannelId || null == secondChannelId) {
                return "请选择货道进行合并";
            }
            TStVmChannel firstChannel = stVmChannelDao.findByPk(context, firstChannelId);
            TStVmChannel secondChannel = stVmChannelDao.findByPk(context, secondChannelId);
            if (null == firstChannel) {
                return "第一个货道不存在";
            }
            if (null == secondChannel) {
                return "第二个货道不存在";
            }
            TStVmStrategy strategy = firstChannel.getStrategy();
            TMtVmModel vmModel = strategy.getVmModel();
            if (null == vmModel) {
                return "售货机型号错误";
            }
            //判断售货机型号是否可被合并
            if (vmModel.getAllowMerge() == 1) {
                //不可被合并
                return "该售货机型号货道不可被合并";
            }

            //先取消关联
            cancelRelateSku(context, user, Arrays.asList(new Integer[]{firstChannelId, secondChannelId}), false);

            boolean isLeft = true;//true:靠左合并；false:靠右合并
            Integer secondIndex = Integer.parseInt(secondChannel.getChannelCode().split("-")[1]);
            //计算临界值
            String layer = vmModel.getChannelPerLayer();
            if (StringUtil.isEmpty(layer)) {
                return "售货机型号各层货道数为空";
            }
            int layerInt = Integer.parseInt(layer.split(",")[0]);//取第一层货道数量
            int dempNumber = layerInt % 2 > 0 ? layerInt / 2 + 1 : layerInt / 2;
            if (secondIndex > dempNumber) {
                //第6个货道以前靠左合并，第6个货道之后靠右合并，只要firstChannel比6大，就靠右合并，如果比6小，就靠左
                isLeft = false;
            }
            int mergeQty = firstChannel.getMergeQty() + secondChannel.getMergeQty();
            if (isLeft) {
                //靠左合并，左侧货道合并数量= 左侧货道合并数量+右侧货道合并数量
                //右侧合并货道数量变为0
                if (secondChannel.getMergeQty() != null && secondChannel.getMergeQty() > 1) {
                    //如果右侧货道为合并货道，将右侧货道下合并的所有货道，的合并指针指向左侧货道
                    setMergePId(context, firstChannel, secondChannel);
                }

                secondChannel.setMergePid(firstChannel.getChannelId());

                firstChannel.setMergeQty(mergeQty);
                secondChannel.setMergeQty(0);
            } else {
                //靠右合并
                //右侧货道合并数量 = 右侧货道合并数量+ 左侧货道合并数量
                //左侧货道合并数量变为0
                if (firstChannel.getMergeQty() != null && firstChannel.getMergeQty() > 1) {
                    //如果右侧货道为合并货道，将右侧货道下合并的所有货道，的合并指针指向左侧货道
                    setMergePId(context, secondChannel, firstChannel);
                }
                firstChannel.setMergePid(secondChannel.getChannelId());
                firstChannel.setMergeQty(0);
                secondChannel.setMergeQty(mergeQty);
            }

            if (firstChannel.getIsFixedCapacity() == 1) {
                //不是固定容量,将最大容量调整为0
                firstChannel.setMaxCapacity(0);
            }
            if (secondChannel.getIsFixedCapacity() == 1) {
                //不是固定容量,将最大容量调整为0
                secondChannel.setMaxCapacity(0);
            }
            firstChannel.getStrategy().setUtime(new Date());
            firstChannel.getStrategy().setUtor(user.getId());
            context.commitChanges();
        } catch (Exception e) {
            context.rollbackChanges();
            errMsg = "合并失败，系统内部出现异常";
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    /**
     * 设置mergePId
     *
     * @param context
     * @param parentChannel
     * @param childrenChannel
     */
    private void setMergePId(ObjectContext context, TStVmChannel parentChannel,
                             TStVmChannel childrenChannel) {
        List<TStVmChannel> mergeList = stVmChannelDao.findByMergeChannelId(context, childrenChannel.getChannelId());
        if (null != mergeList) {
            for (TStVmChannel tStVmChannel : mergeList) {
                tStVmChannel.setMergePid(parentChannel.getChannelId());
            }
        }
    }

    /**
     * 从列表页中移除一个商品
     *
     * @param context
     * @param strategy
     * @param skuId
     */
    private void removeSort(ObjectContext context, TStVmStrategy strategy,
                            Integer skuId) {
        try {
            List<TStVmSkuSort> sortList = strategy.getStSkuSort();
            List<TStVmSkuSort> deleteList = new ArrayList<>();

            if (null != sortList && sortList.size() > 0) {
                int size = sortList.size();
                for (int i = 0; i < size; i++) {
                    if (skuId.equals(sortList.get(i).getSkuId())) {
                        deleteList.add(sortList.get(i));
                        logger.debug("从列表页策略中移除    skuId = " + skuId + "strategy = " + strategy);
                    }
                }
                context.deleteObjects(deleteList);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String cancelRelateSku(ObjectContext context, TSysUser user,
                                  List<Integer> channelIds, boolean submit) {
        boolean isChange = false;
        String errMsg = null;
        try {
            if (null == channelIds || channelIds.size() == 0) {
                return "请选择需要取消关联的货道";
            }
            for (Integer channelId : channelIds) {
                TStVmChannel channel = stVmChannelDao.findByPk(context, channelId);
                if (null != channel) {
                    if (channel.getSkuId() != null && 0 != channel.getSkuId()) {
                        //有关联商品，取消关联，同时判断货道配置中是否还有此商品，如没有，移除列表页
                        boolean hasSku = false;
                        List<TStVmChannel> channelList = channel.getStrategy().getStChannel();
                        for (TStVmChannel tStVmChannel : channelList) {
                            if (!channelId.equals(tStVmChannel.getChannelId()) && channel.getSkuId().equals(tStVmChannel.getSkuId())) {
                                hasSku = true;
                            }
                        }
                        if (!hasSku) {
                            isChange = true;
                            //其他货道配置中没有,移除列表页中此商品
                            removeSort(context, channel.getStrategy(), channel.getSkuId());
                        }
                        channel.setMaxCapacity(null);
                        channel.setSkuId(null);
                        channel.setUtime(new Date());
                        channel.setUtor(user.getId());

                        channel.getStrategy().setUtor(user.getId());
                        channel.getStrategy().setUtime(new Date());
                    }
                }
            }
            if (submit && isChange) {
                context.commitChanges();
            }
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            errMsg = "系统内部异常";
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public Map<String, Object> relateSku(ObjectContext context, TSysUser user,
                                         Integer[] channelIds, Integer skuId) {
        Map<String, Object> resultmMap = new HashMap<String, Object>();
        String errMsg = null;
        try {
            if (null == channelIds) {
                resultmMap.put("type", "error");
                resultmMap.put("message", "请选择货道");
                return resultmMap;
            }
            if (null == skuId) {
                resultmMap.put("type", "error");
                resultmMap.put("message", "请选择商品");
                return resultmMap;
            }
            //检测能否放下
            TMtSku sku = mtSkuDao.findByPk(context, skuId);
            if (null == sku) {
                resultmMap.put("type", "error");
                resultmMap.put("message", "商品不存在");
                return resultmMap;
            }
            Map<String, Object> checkMap = checkSku(context, channelIds, skuId);
            if (checkMap.get("type").equals("error")) {
                resultmMap.put("type", "error");
                resultmMap.put("message", checkMap.get("message"));
                return resultmMap;
            }
            //先取消关联
            Integer strategyId = null;
            String error = cancelRelateSku(context, user, Arrays.asList(channelIds), true);
            if (StringUtil.isEmpty(error)) {
                //取消关联成功之后，添加新的关联
                for (Integer channelId : channelIds) {
                    TStVmChannel channel = stVmChannelDao.findByPk(context, channelId);
                    channel.setSkuId(skuId);
                    channel.setUtime(new Date());
                    channel.setUtor(user.getId());
                    strategyId = channel.getStrategy().getStrategyId();
                    int standardLength = channel.getChannelLong();

                    if (channel.getIsFixedCapacity() != TStVmChannel.IS_FIXED_CAPACITY) {
                        //非固定容量 计算最大容量
                        channel.setMaxCapacity(standardLength / sku.getSkuLength());
                        channel.setAdjustMaxCapacity(standardLength / sku.getSkuLength());
                    } else {
                        channel.setAdjustMaxCapacity(channel.getFixedCapacity());
                    }

                    boolean hasSku = false;
                    List<TStVmChannel> channelList = channel.getStrategy().getStChannel();
                    for (TStVmChannel tStVmChannel : channelList) {
                        if (channelId != tStVmChannel.getChannelId() && skuId.equals(tStVmChannel.getSkuId())) {
                            hasSku = true;
                        }
                    }
                    if (!hasSku) {
                        //不存在该商品
                        addToSort(context, skuId, channel.getStrategy(), user);
                    }
                    channel.getStrategy().setUtime(new Date());
                    channel.getStrategy().setUtor(user.getId());
                }
                context.commitChanges();
                resultmMap.put("type", "success");
                resultmMap.put("strategyId", strategyId);
                resultmMap.put("message", "关联商品成功");
            } else {
                resultmMap.put("type", "error");
                resultmMap.put("message", error);
                return resultmMap;
            }
        } catch (Exception e) {
            e.printStackTrace();
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return resultmMap;
    }

    /**
     * 向列表页中添加商品
     *
     * @param context
     * @param skuId
     * @param strategy
     * @param user
     */
    private void addToSort(ObjectContext context, Integer skuId,
                           TStVmStrategy strategy, TSysUser user) {
        try {
            List<TStVmSkuSort> sortList = strategy.getStSkuSort();

            if (null != sortList && sortList.size() > 0) {
                int maxSort = 0;
                for (TStVmSkuSort tStVmSkuSort : sortList) {
                    if (maxSort < tStVmSkuSort.getSort()) {
                        maxSort = tStVmSkuSort.getSort();
                    }
                }
                TStVmSkuSort newSort = context.newObject(TStVmSkuSort.class);
                newSort.setCtime(new Date());
                newSort.setCtor(user.getId());
                newSort.setSkuId(skuId);
                newSort.setSort(maxSort + 1);
                strategy.addToStSkuSort(newSort);
                logger.debug("向列表页中加入商品，strategyId = " + strategy.getStrategyId() + "添加第" + newSort.getSort() + "个商品 skuId = " + skuId);
            } else {
                TStVmSkuSort newSort = context.newObject(TStVmSkuSort.class);
                newSort.setCtime(new Date());
                newSort.setCtor(user.getId());
                newSort.setSkuId(skuId);
                newSort.setSort(1);
                strategy.addToStSkuSort(newSort);
                logger.debug("向列表页中加入商品，列表页不存在，strategyId = " + strategy.getStrategyId() + "添加第一个商品 skuId = " + skuId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Map<String, Object> checkSku(ObjectContext context,
                                        Integer[] channelIds, Integer skuId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("type", "success");
        try {
            //判断能不能放下
            //是否合并货道
            //横放还是竖放
            if (null == channelIds) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择货道");
                resultMap.put("capacity", "0");
                return resultMap;
            }
            if (null == skuId) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择商品");
                resultMap.put("capacity", "0");
                return resultMap;
            }
            logger.debug("channelIds = " + Arrays.toString(channelIds) + "skuId = " + skuId);
            //检测是否超过24种类
//			boolean is24Type = checkChannelMdseType(context,skuId,channelIds);
//			if(is24Type){
//				resultMap.put("type", "error");
//				resultMap.put("message", "商品数量超过上限");
//				resultMap.put("capacity", "0");
//				return resultMap;
//			}
            TMtSku sku = mtSkuDao.findByPk(context, skuId);
            if (null == sku) {
                resultMap.put("type", "error");
                resultMap.put("message", "商品不存在");
                resultMap.put("capacity", "0");
                return resultMap;
            }
            int maxcapaticy = 0;
            for (Integer channelId : channelIds) {
                TStVmChannel channel = stVmChannelDao.findByPk(context, channelId);
                if (channel != null) {
                    Integer displayMode = channel.getDisplayMode();//摆放方式
                    int standardWidth = channel.getChannelWidth();//货道标准宽度
                    int standardHeight = channel.getChannelHeight();
                    int standardLength = channel.getChannelLong();
                    int maxWidth = channel.getAcceptWidthMax();

                    if (channel.getMergeQty() > 1) {
                        //合并货道
                        List<TStVmChannel> mergeList = stVmChannelDao.findByMergeChannelId(context, channel.getChannelId());
                        for (TStVmChannel tStVmChannel : mergeList) {
                            standardWidth += tStVmChannel.getChannelWidth();
                            maxWidth += tStVmChannel.getAcceptWidthMax();
                        }
                    }

                    if (displayMode == TStVmChannel.HORIZONTAL) {
                        //横放
                        if (sku.getSkuHeight() <= standardWidth) {
                            //计算最大容量
                            maxcapaticy = getMaxcapacity(sku, channel, standardLength);
                        } else {
                            resultMap.put("type", "error");
                            resultMap.put("message", "您选择的商品无法适应货道");
                            resultMap.put("capacity", "0");
                            return resultMap;
                        }

                    } else {
                        //竖放
                        if (sku.getSkuHeight() <= standardHeight) {
                            if (sku.getSkuWidth() <= standardWidth) {
                                maxcapaticy = getMaxcapacity(sku, channel, standardLength);
                            } else if (sku.getSkuWidth() > standardWidth && sku.getSkuWidth() <= maxWidth) {
                                maxcapaticy = getMaxcapacity(sku, channel, standardLength);
                                resultMap.put("type", "warn");
                                resultMap.put("message", "超出标准宽度相邻货道宽度会被压缩");
                            } else if (sku.getSkuWidth() > maxWidth) {
                                resultMap.put("type", "error");
                                resultMap.put("message", "您选择的商品无法适应货道");
                                resultMap.put("capacity", "0");
                                return resultMap;
                            }
                        } else {
                            resultMap.put("type", "error");
                            resultMap.put("message", "您选择的商品无法适应货道");
                            resultMap.put("capacity", "0");
                            return resultMap;
                        }
                    }
                } else {
                    logger.error("根据货道标识没有查到货道数据   stvmChannelId = " + channel);
                    resultMap.put("type", "error");
                    resultMap.put("message", "货道数据异常");
                    resultMap.put("capacity", "0");
                    return resultMap;
                }
            }

            resultMap.put("capacity", maxcapaticy);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
            resultMap.put("capacity", "0");
        }
        return resultMap;
    }

    private int getMaxcapacity(TMtSku sku, TStVmChannel channel,
                               int standardLength) {
        if (channel.getIsFixedCapacity() == TStVmChannel.IS_FIXED_CAPACITY) {
            //固定容量
            return channel.getFixedCapacity();
        } else {
            return standardLength / sku.getSkuLength();
        }
    }

    /**
     * 检测商品有没有超过24种
     *
     * @param context
     * @param skuId
     * @param channelIds
     * @return
     */
    private boolean checkChannelMdseType(ObjectContext context, Integer skuId,
                                         Integer[] channelIds) {
        try {
            TStVmChannel channel = stVmChannelDao.findByPk(context, channelIds[0]);
            if (null != channel) {
                Set<Integer> skuSet = new HashSet<Integer>();
                TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, channel.getStrategy().getStrategyId());
                if (null != strategy) {
                    List<TStVmChannel> channelList = strategy.getStChannel();
                    if (null != channelList && channelList.size() > 0) {
                        List<Integer> chanelIdList = Arrays.asList(channelIds);
                        for (TStVmChannel tStVmChannel : channelList) {
                            if (chanelIdList.contains(tStVmChannel.getChannelId())) {
                                continue;
                            }
                            if (tStVmChannel.getSkuId() != null && tStVmChannel.getSkuId() != 0) {
                                skuSet.add(tStVmChannel.getSkuId());
                            }
                        }
                    }
                }
                if (skuSet.size() < 24 || (skuSet.size() == 24 && skuSet.contains(skuId))) {
                    return false;
                } else {
                    return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    @Override
    public String cancelMergeChannel(ObjectContext context, Integer channelId,
                                     TSysUser user) {
        String errMsg = null;
        try {
            if (null == channelId) {
                return "请选择货道进行取消";
            }
            TStVmChannel channel = stVmChannelDao.findByPk(context, channelId);
            if (null == channel) {
                return "货道不存在";
            }
            Integer mergeQty = channel.getMergeQty();

            if (mergeQty == 1) {
                return "请选择已合并货道进行拆分";
            }
            List<TStVmChannel> list = stVmChannelDao.findByMergeChannelId(context, channelId);
            if (null == list || list.size() == 0) {
                logger.error("拆分货道策略失败。未查到合并货道数据 channelId = " + channelId);
                return "货道数据异常";
            }
            List<Integer> channelIdList = new ArrayList<Integer>();
            for (TStVmChannel tStVmChannel : list) {
                tStVmChannel.setMergeQty(1);
                tStVmChannel.setMergePid(null);
                channelIdList.add(tStVmChannel.getChannelId());
            }
            channelIdList.add(channelId);
            //取消关联
            cancelRelateSku(context, user, channelIdList, false);
            channel.setMergeQty(1);
            channel.getStrategy().setUtime(new Date());
            channel.getStrategy().setUtor(user.getId());
            context.commitChanges();
        } catch (Exception e) {
            context.rollbackChanges();
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public String makeStringSort(ObjectContext context, Integer strategyId) {
        StringBuffer buffer = new StringBuffer();
        try {
            TStVmStrategy strategy = stVmStrategyDao.findTStVmStrategyPk(context, strategyId);
            if (null != strategy) {
                List<TStVmSkuSort> sortList = strategy.getStSkuSort();
                if (null != sortList && sortList.size() > 0) {
                    Map<Integer, TStVmSkuSort> sortMap = new HashMap<Integer, TStVmSkuSort>();
                    Integer[] indexs = new Integer[sortList.size()];
                    buffer.append("<ul>");
                    for (int i = 0; i < sortList.size(); i++) {
                        TStVmSkuSort sort = sortList.get(i);
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
                        TStVmSkuSort sort = sortMap.get(index);
                        TMtSku sku = mtSkuDao.findByPk(context, sort.getSkuId());
                        buffer.append("<li>");
                        buffer.append("<section>");
                        buffer.append("<img src='" + sku.getImgUrl() + "'>");
                        buffer.append("</section>");
                        buffer.append("<i>");
                        buffer.append("</i>");
                        buffer.append("<p>" + sku.getSkuName() + "</p>");
                        buffer.append("<input type='hidden' value='" + sort.getSortId() + "'>");
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

    @Override
    public List getPage(ObjectContext objectContext, TSysUser sysUser,
                        String searchStVmName, Integer searchVmModelId, Integer pageSize) {
        try {
            return stVmStrategyDao.findStVmChannelPage(objectContext, sysUser, searchStVmName, searchVmModelId, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public TStVmChannel findByPk(ObjectContext context,
                                 Integer firstChannelId) {
        try {
            return stVmChannelDao.findByPk(context, firstChannelId);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public Map<String, Object> changeSort(ObjectContext context,
                                          TSysUser sysUser, Integer firstSortId, Integer secondSortId) {
        logger.debug("交换列表页   firstSortId = " + firstSortId + "secondSortId = " + secondSortId);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            if (null == firstSortId || null == secondSortId) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择两个列表页进行交换");
                return resultMap;
            }
            TStVmSkuSort firstSort = stVmSkuSortDao.findByPk(context, firstSortId);
            if (null == firstSort) {
                resultMap.put("type", "error");
                resultMap.put("message", "列表页不存在");
                return resultMap;
            }
            TStVmSkuSort secondSort = stVmSkuSortDao.findByPk(context, secondSortId);
            if (null == secondSort) {
                resultMap.put("type", "error");
                resultMap.put("message", "列表页不存在");
                return resultMap;
            }
            Integer firstSortIndex = firstSort.getSort();
            Integer secondSortIndex = secondSort.getSort();
            firstSort.setSort(secondSortIndex);
            secondSort.setSort(firstSortIndex);
            secondSort.getStrategy().setUtime(new Date());
            secondSort.getStrategy().setUtor(sysUser.getId());
            context.commitChanges();
            String html = makeStringSort(context, firstSort.getStrategy().getStrategyId());
            resultMap.put("type", "success");
            resultMap.put("html", html);
            resultMap.put("message", "交换成功");
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
    public Map<String, Object> copySort(ObjectContext context, TSysUser sysUser, Integer sortId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            //复制到下一位
            TStVmSkuSort sort = stVmSkuSortDao.findByPk(context, sortId);
            if (null == sort) {
                resultMap.put("type", "error");
                resultMap.put("message", "列表页不存在");
                return resultMap;
            }
            for (TStVmSkuSort stVmSkuSort : sort.getStrategy().getStSkuSort()) {
                if (stVmSkuSort.getSort() > sort.getSort()) stVmSkuSort.setSort(stVmSkuSort.getSort() + 1);
            }
            TStVmSkuSort newSort = context.newObject(TStVmSkuSort.class);
            newSort.setCtime(new Date());
            newSort.setCtor(sysUser.getId());
            newSort.setStrategy(sort.getStrategy());
            newSort.setSort(sort.getSort() + 1);
            newSort.setSkuId(sort.getSkuId());
            context.commitChanges();
            logger.info("copy success sortId = " + sortId + " userId = " + sysUser.getId());
            String html = makeStringSort(context, sort.getStrategy().getStrategyId());
            resultMap.put("type", "success");
            resultMap.put("html", html);
            resultMap.put("message", "复制成功");
            return resultMap;
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
        }
        return ReturnCode.FAIL.toMap();
    }

    @Override
    public Map<String, Object> deleteSort(ObjectContext context, TSysUser sysUser, Integer sortId) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            TStVmSkuSort sort = stVmSkuSortDao.findByPk(context, sortId);
            if (null == sort) {
                resultMap.put("type", "error");
                resultMap.put("message", "列表页不存在");
                return resultMap;
            }
            boolean canDelete = false;
            TStVmStrategy stVmStrategy = sort.getStrategy();
            for (TStVmSkuSort stVmSkuSort : stVmStrategy.getStSkuSort()) {
                if (stVmSkuSort.getSkuId().equals(sort.getSkuId()) && !stVmSkuSort.getSort().equals(sort.getSort())) {
                    context.deleteObject(sort);
                    canDelete = true;
                    break;
                }
            }
            try {
                if (canDelete) {
                    context.commitChanges();
                    logger.info("delete success sortId = " + sortId + " userId = " + sysUser.getId());
                    String html = makeStringSort(context, stVmStrategy.getStrategyId());
                    resultMap.put("type", "success");
                    resultMap.put("html", html);
                    resultMap.put("message", "删除成功");
                    return resultMap;
                } else {
                    resultMap.put("type", "error");
                    resultMap.put("message", "删除失败,不能删除最后一个");
                    return resultMap;
                }

            } catch (Exception e) {
                context.rollbackChanges();
                logger.error(e.getLocalizedMessage(), e);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return ReturnCode.FAIL.toMap();
    }

    @Override
    public Map<String, Object> setFullSku(ObjectContext context, Integer channelId, Integer fullval) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            TStVmChannel channel = stVmChannelDao.findByPk(context, channelId);
            channel.setAdjustMaxCapacity(fullval);
            context.commitChanges();
            resultMap.put("type", "success");
            resultMap.put("message", "设置成功");
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "设置失败");
        }
        return resultMap;
    }
}
