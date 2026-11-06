package com.kdx.vms.service.impl;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.*;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.model.custom.ChannelConfigCache;
import com.kdx.vms.orm.dao.MtVmChannelDao;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.service.IMtVmChannelService;
import com.kdx.vms.service.ISysLogService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

import static com.kdx.vms.webapi.WebApiTask.context;

/**
 * Created by yangshuxi on 2016/12/14.
 */
@Service
public class MtVmChannelServiceImpl implements IMtVmChannelService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final static String KEY_CODE = "code";
    @Resource
    private MtVmChannelDao mtVmChannelDao;
    @Resource
    private ISysLogService sysLogService;
    @Resource
    private MyJedisCluster myJedisCluster;

    @Override
    public String makeStVmChannelTableString(TMtVm mtVm, ChannelConfigCache cache) {
        String errMsg;
        try {
            StringBuilder builder = new StringBuilder();
            builder.append("<input name='vmId' value='").append(mtVm.getId()).append("' type='hidden'> ");
            Map<String, ChannelConfigCache.ChannelConfig> channelMap = cache.loadCodeMap();
            TMtVmModel vmModel = Cayenne.objectForPK(mtVm.getObjectContext(), TMtVmModel.class, mtVm.getVmModelId());
            int row = vmModel.getChannelLayerQty();
            String col = vmModel.getChannelPerLayer();
            String cols[] = col.split(",");
            int channelIndex = 0;
            for (int i = 1; i <= row; i++) {
                builder.append("<ul>");
                int colInt = Integer.parseInt(cols[i - 1]);
                Integer demp = Integer.parseInt(cols[i - 1]);//当前行有多少列
                for (int n = 1; n <= colInt; n++) {
                    String channelCode;
                    String skuName = "";
                    String skuUnit = "";
                    Integer skuReserve = 0;
                    Integer channelId;
                    Integer mergeQty;
                    mergeQty = channelMap.get(i + "-" + n).getMergeQty();
                    channelId = channelMap.get(i + "-" + n).getChannelId();
                    channelCode = channelMap.get(i + "-" + n).getChannelCode();
                    Integer skuId = channelMap.get(i + "-" + n).getSkuId();
                    Integer maxCapacity = channelMap.get(i + "-" + n).getTempMaxCapacity();
                    TMtSku sku;
                    String imgUrl = "";
                    if (CheckUtils.check(skuId)) {
                        sku = getSku(skuId);
                        if (null != sku) {
                            imgUrl = sku.getImgUrl();
                            skuName = sku.getSkuName();
                            skuUnit = sku.getSkuUnit();
                            skuReserve = sku.getReserve();
                        }
                    }
                    if (mergeQty == 0) continue;

                    if (null == skuName) skuName = "";
                    builder.append("<li class='wh" + 100 / demp * mergeQty + "'> <input type='hidden' class='channelCode' rol='" + i + "' index='" + channelIndex + "' code='" + channelCode + "' value='" + channelId + "'>");
                    builder.append(" <p><span class='icon_state sm'>" + channelCode + "</span></p>");
                    builder.append("<img src='" + imgUrl + "'>");
                    builder.append("<input type='hidden' class='skuReserve' value='" + skuReserve + "'>");
                    if (!StringUtil.isEmpty(skuName)) {
                        builder.append("<label class='bt0'><span>满" + "     " + maxCapacity + "</span><button class='glyphicon glyphicon-pencil' id='setFullSkuDialog' data-toggle='modal' href='/mtVmChannel/showFullDialog?channelId=" + channelId + "&vmId=" + mtVm.getId() + " ' data-target='#setFullSku' ></button></label>");
                    } else {
                        builder.append("<label class='bt0'><span></span></label>");
                    }
                    builder.append("<label><a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + skuName + skuUnit + "\" data-original-title=\"" + skuName + skuUnit + "\"><span>" + skuName + "</span></a></label>");
                    builder.append("</li>");
                    channelIndex++;
                }
                builder.append("</ul>");
            }

            return builder.toString();
        } catch (Exception e) {
            errMsg = "货道策略详情异常，系统内部出现异常";
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String mergeChannel(Integer firstChannelId, Integer secondChannelId, ChannelConfigCache cache, TMtVm mtVm) {
        logger.debug("firstChannelId = " + firstChannelId + ",secondChannelId = " + secondChannelId);
        String errMsg = null;
        try {
            if (null == firstChannelId || null == secondChannelId) {
                return "请选择货道进行合并";
            }
            Map<Integer, ChannelConfigCache.ChannelConfig> map = cache.loadIdMap();
            ChannelConfigCache.ChannelConfig firstChannel = map.get(firstChannelId);
            ChannelConfigCache.ChannelConfig secondChannel = map.get(secondChannelId);
            if (null == firstChannel) {
                return "第一个货道不存在";
            }
            if (null == secondChannel) {
                return "第二个货道不存在";
            }
            TMtVmModel vmModel = Cayenne.objectForPK(context, TMtVmModel.class, mtVm.getVmModelId());
            if (null == vmModel) {
                return "售货机型号错误";
            }
            //判断售货机型号是否可被合并
            if (vmModel.getAllowMerge() == 1) {
                //不可被合并
                return "该售货机型号货道不可被合并";
            }

            //先取消关联
            cancelRelateSku(cache, Arrays.asList(firstChannelId, secondChannelId));

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
                    setMergePId(firstChannelId, secondChannelId, cache);
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
                    setMergePId(secondChannelId, firstChannelId, cache);
                }
                firstChannel.setMergePid(secondChannel.getChannelId());
                firstChannel.setMergeQty(0);
                secondChannel.setMergeQty(mergeQty);
            }
        } catch (Exception e) {
            errMsg = "合并失败，系统内部出现异常";
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public String cancelRelateSku(ChannelConfigCache cache, List<Integer> channelIds) {
        String errMsg = null;
        try {
            if (null == channelIds || channelIds.size() == 0) {
                return "请选择需要取消关联的货道";
            }
            Map<Integer, ChannelConfigCache.ChannelConfig> map = cache.loadIdMap();
            for (Integer channelId : channelIds) {
                ChannelConfigCache.ChannelConfig channel = map.get(channelId);
                if (null != channel) {
                    if (channel.getSkuId() != null) {
                        channel.setSkuId(null);
                        channel.setMaxCapacity(null);
                        channel.setAdjustMaxCapacity(null);
                    }
                }
            }
        } catch (Exception e) {
            errMsg = "系统内部异常";
            logger.error(e.getLocalizedMessage(), e);
        }
        return errMsg;
    }

    @Override
    public Map<String, Object> relateSku(TSysUser user, ChannelConfigCache cache, Integer[] channelIds, Integer skuId) {
        Map<String, Object> respMap = new HashMap<>();
        try {
            if (null == channelIds) {
                respMap.put("type", "error");
                respMap.put("message", "请选择货道");
                return respMap;
            }
            if (null == skuId) {
                respMap.put("type", "error");
                respMap.put("message", "请选择商品");
                return respMap;
            }
            //检测能否放下
            TMtSku sku = Cayenne.objectForPK(user.getObjectContext(), TMtSku.class, skuId);
            if (null == sku) {
                respMap.put("type", "error");
                respMap.put("message", "商品不存在");
                return respMap;
            }
            Map<String, Object> checkMap = checkSku(user, cache, channelIds, skuId);
            if (checkMap.get("type").equals("error")) {
                respMap.put("type", "error");
                respMap.put("message", checkMap.get("message"));
                return respMap;
            }
            //先取消关联
            String error = cancelRelateSku(cache, Arrays.asList(channelIds));
            Map<Integer, ChannelConfigCache.ChannelConfig> map = cache.loadIdMap();
            if (StringUtil.isEmpty(error)) {
                //取消关联成功之后，添加新的关联
                for (Integer channelId : channelIds) {
                    ChannelConfigCache.ChannelConfig channel = map.get(channelId);
                    channel.setSkuId(sku.getId());
                    int standardLength = channel.getChannelLong();
                    if (channel.getIsFixedCapacity() != TStVmChannel.IS_FIXED_CAPACITY) {
                        //非固定容量 计算最大容量
                        channel.setMaxCapacity(standardLength / sku.getSkuLength());
                        channel.setAdjustMaxCapacity(standardLength / sku.getSkuLength());
                    }
                }
                respMap.put("type", "success");
                respMap.put("vmId", cache.getVmId());
                respMap.put("message", "关联商品成功");
            } else {
                respMap.put("type", "error");
                respMap.put("message", error);
                return respMap;
            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return respMap;
    }

    @Override
    public Map<String, Object> checkSku(TSysUser sysUser, ChannelConfigCache cache, Integer[] channelIds, Integer skuId) {
        Map<String, Object> resultMap = new HashMap<>();
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
            TMtSku sku = Cayenne.objectForPK(sysUser.getObjectContext(), TMtSku.class, skuId);
            if (null == sku) {
                resultMap.put("type", "error");
                resultMap.put("message", "商品不存在");
                resultMap.put("capacity", "0");
                return resultMap;
            }
            int maxcapaticy = 0;
            Map<Integer, ChannelConfigCache.ChannelConfig> map = cache.loadIdMap();
            for (Integer channelId : channelIds) {
                ChannelConfigCache.ChannelConfig channel = map.get(channelId);
                if (channel != null) {
                    Integer displayMode = channel.getDisplayMode();//摆放方式
                    int standardWidth = channel.getChannelWidth();//货道标准宽度
                    int standardHeight = channel.getChannelHeight();
                    int standardLength = channel.getChannelLong();
                    int maxWidth = channel.getAcceptWidthMax();

                    if (channel.getMergeQty() > 1) {
                        //合并货道
                        List<ChannelConfigCache.ChannelConfig> mergeList = cache.findMergedList(channelId);
                        mergeList.forEach(channelConfig -> {
                        });
                        for (ChannelConfigCache.ChannelConfig channelConfig : mergeList) {
                            standardWidth += channelConfig.getChannelWidth();
                            maxWidth += channelConfig.getAcceptWidthMax();
                        }
                    }

                    if (displayMode == TStVmChannel.HORIZONTAL) {
                        //横放
                        if (sku.getSkuHeight() <= standardWidth) {
                            //计算最大容量
                            maxcapaticy = getMaxCapacity(sku, channel, standardLength);
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
                                maxcapaticy = getMaxCapacity(sku, channel, standardLength);
                            } else if (sku.getSkuWidth() > standardWidth && sku.getSkuWidth() <= maxWidth) {
                                maxcapaticy = getMaxCapacity(sku, channel, standardLength);
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
                    logger.error("根据货道标识没有查到货道数据   stvmChannelId = " + channelId);
                    resultMap.put("type", "error");
                    resultMap.put("message", "货道数据异常");
                    resultMap.put("capacity", "0");
                    return resultMap;
                }
            }

            resultMap.put("capacity", maxcapaticy);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
            resultMap.put("capacity", "0");
        }
        return resultMap;
    }

    @Override
    public String cancelMergeChannel(ChannelConfigCache cache, Integer channelId) {
        String errMsg = null;
        try {
            if (null == channelId) {
                return "请选择货道进行取消";
            }
            Map<Integer, ChannelConfigCache.ChannelConfig> idMap = cache.loadIdMap();
            Map<String, ChannelConfigCache.ChannelConfig> codeMap = cache.loadCodeMap();
            ChannelConfigCache.ChannelConfig channel = idMap.get(channelId);
            if (null == channel) {
                return "货道不存在";
            }
            Integer mergeQty = channel.getMergeQty();

            if (mergeQty == 1) {
                return "请选择已合并货道进行拆分";
            }
            List<ChannelConfigCache.ChannelConfig> list = cache.findMergedList(channelId);
            if (null == list || list.size() == 0) {
                logger.error("拆分货道策略失败。未查到合并货道数据 channelId = " + channelId);
                return "货道数据异常";
            }
            List<Integer> channelIdList = new ArrayList<>();
            for (ChannelConfigCache.ChannelConfig channelConfig : list) {
                channelConfig.setMergeQty(1);
                channelConfig.setMergePid(null);
                channelIdList.add(channelConfig.getChannelId());
            }
            channelIdList.add(channelId);
            //取消关联
            cancelRelateSku(cache, channelIdList);
            channel.setMergeQty(1);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            errMsg = "系统内部异常";
        }
        return errMsg;
    }

    @Override
    public String makeStringSort(ObjectContext context, Integer vmId) {
        StringBuffer buffer = new StringBuffer();
        try {
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            if (null != mtVm) {
//                List<TStVmSkuSort> sortList = strategy.getStSkuSort();
                List<TMtVmSkuSortNext> sortList = mtVm.getSkuSortNext();
                if (null != sortList && sortList.size() > 0) {
                    Map<Integer, TMtVmSkuSortNext> sortMap = new HashMap<>();
                    Integer[] indexes = new Integer[sortList.size()];
                    buffer.append("<ul>");
                    for (int i = 0; i < sortList.size(); i++) {
                        TMtVmSkuSortNext sort = sortList.get(i);
                        sortMap.put(sort.getSort(), sort);
                        indexes[i] = sort.getSort();
                    }
                    Arrays.sort(indexes);
                    int i = 0;
                    for (Integer index : indexes) {
                        if (i % 15 == 0 && i != indexes.length) {
                            int page = i / 15 + 1;
                            //buffer.append("<br>");
                            buffer.append("<div class='sortPageDivTitle clearfix'> 第" + page + "页</div>");
                        }
                        TMtVmSkuSortNext sort = sortMap.get(index);
                        TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, sort.getSkuId());
                        buffer.append("<li>");
                        buffer.append("<section>");
                        buffer.append("<img src='" + sku.getImgUrl() + "'>");
                        buffer.append("</section>");
                        buffer.append("<i>");
                        buffer.append("</i>");
//                        buffer.append("<p>"+sku.getSkuName()+"</p>");
                        buffer.append("<p><a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + sku.getSkuName() + sku.getSkuUnit() + "\" data-original-title=\"" + sku.getSkuName() + sku.getSkuUnit() + "\"><span>" + sku.getSkuName() + "</span></a></p>");
                        buffer.append("<input type='hidden' value='" + sort.getSort() + "'>");
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
    public Map<String, Object> changeSort(ObjectContext context, TSysUser sysUser, Integer firstNumber, Integer secondNumber, Integer vmId) {
        logger.debug("交换列表页   firstNumber = " + firstNumber + "secondNumber = " + secondNumber);
        Map<String, Object> resultMap = new HashMap<>();
        try {
            if (null == firstNumber || null == secondNumber) {
                resultMap.put("type", "error");
                resultMap.put("message", "请选择两个列表页进行交换");
                return resultMap;
            }
            TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
            TMtVmSkuSortNext firstSort = mtVm.getTemporarySort(firstNumber);
            if (null == firstSort) {
                resultMap.put("type", "error");
                resultMap.put("message", "列表页不存在");
                return resultMap;
            }
            TMtVmSkuSortNext secondSort = mtVm.getTemporarySort(secondNumber);
            if (null == secondSort) {
                resultMap.put("type", "error");
                resultMap.put("message", "列表页不存在");
                return resultMap;
            }
            firstSort.setSort(secondNumber);
            secondSort.setSort(firstNumber);
            String html = makeStringSort(context, mtVm.getId());
            resultMap.put("type", "success");
            resultMap.put("html", html);
            resultMap.put("message", "交换成功");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            resultMap.put("type", "error");
            resultMap.put("message", "系统内部异常");
        }
        return resultMap;
    }

    //内部方法

    /**
     * 向列表页中添加商品
     *
     * @param context
     * @param skuId
     * @param mtVm
     * @param user
     */
    private void addToSort(ObjectContext context, Integer skuId,
                           TMtVm mtVm, TSysUser user) {
        try {
            List<TMtVmSkuSortNext> sortList = mtVm.getSkuSortNext();
            if (null != sortList && sortList.size() > 0) {
                int maxSort = 0;
                for (TMtVmSkuSortNext mtVmSkuSortNext : sortList) {
                    if (maxSort < mtVmSkuSortNext.getSort()) {
                        maxSort = mtVmSkuSortNext.getSort();
                    }
                }
                TMtVmSkuSortNext newSort = context.newObject(TMtVmSkuSortNext.class);
                newSort.setCtime(new Date());
                newSort.setCtor(user.getId());
                newSort.setSkuId(skuId);
                newSort.setSort(maxSort + 1);
                mtVm.addToSkuSortNext(newSort);
                logger.debug("向列表页中加入商品，mtVmId = " + mtVm.getId() + "添加第" + newSort.getSort() + "个商品 skuId = " + skuId);
            } else {
                TMtVmSkuSortNext newSort = context.newObject(TMtVmSkuSortNext.class);
                newSort.setCtime(new Date());
                newSort.setCtor(user.getId());
                newSort.setSkuId(skuId);
                newSort.setSort(1);
                mtVm.addToSkuSortNext(newSort);
                logger.debug("向列表页中加入商品，列表页不存在，mtVmId = " + mtVm.getId() + "添加第一个商品 skuId = " + skuId);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 从列表页中移除一个商品
     *
     * @param context
     * @param mtVm
     * @param skuId
     */
    private void removeSort(ObjectContext context, TMtVm mtVm,
                            Integer skuId) {
        try {
            List<TMtVmSkuSortNext> sortList = mtVm.getSkuSortNext();
            List<TMtVmSkuSortNext> deleteList = new ArrayList<>();
            if (null != sortList && sortList.size() > 0) {
                int size = sortList.size();
                for (int i = 0; i < size; i++) {
                    if (skuId.equals(sortList.get(i).getSkuId())) {
                        deleteList.add(sortList.get(i));
                        logger.debug("从列表页策略中移除    skuId = " + skuId + "mtVm = " + mtVm);
                    }
                }
                context.deleteObjects(deleteList);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }


//    public void copyInfoToNext(ObjectContext context, Integer vmId, TSysUser currentUser) {
//        TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
//        if (!mtVm.hasNextChannelIncludeMerge() && !mtVm.isEdit()) {
//            //货道
//            Map<String, TMtVmChannelNext> channelMap = mtVm.getChannelNext();
//            List<TMtVmChannel> channelList = mtVm.getChannelInfo();
//            for (TMtVmChannel mtVmChannel : channelList) {
//                TMtVmChannelNext mtVmChannelNext = channelMap.get(mtVmChannel.getChannelCode());
//                mtVmChannelNext.setProperties(mtVmChannel);
//                mtVmChannelNext.setUtor(currentUser.getId());
//                mtVmChannelNext.setUtime(new Date());
//            }
//            //列表
//            List<TMtVmSkuSortNext> sortList = mtVm.getSkuSortNext();
//            if (null != sortList && !sortList.isEmpty()) {
//                context.deleteObjects(sortList);
//            }
//            List<TMtVmSkuSort> list = mtVm.getSkuSort();
//            for (TMtVmSkuSort mtVmSkuSort : list) {
//                TMtVmSkuSortNext mtVmSkuSortNext = context.newObject(TMtVmSkuSortNext.class);
//                mtVmSkuSortNext.setProperties(mtVmSkuSort);
//                mtVmSkuSortNext.setCtor(currentUser.getId());
//                mtVmSkuSortNext.setCtime(new Date());
//                mtVm.addToSkuSortNext(mtVmSkuSortNext);
//            }
//            mtVm.setEdit(true);
//            mtVm.setCopy(true);
//        }
//    }

    @Override
    public Map<String, Object> saveChannel(TSysUser currentUser, ObjectContext context, Integer[] ids, Integer strategyId) {
        Map<String, Object> respMap = new HashMap<>();
        if (null == strategyId) {
            respMap.put(KEY_CODE, 102);
            return respMap;
        }
        try {
            TStVmStrategy stVmStrategy = Cayenne.objectForPK(context, TStVmStrategy.class, strategyId);
            List<TStVmChannel> channelList = stVmStrategy.getStChannel();
            List<TStVmSkuSort> sortList = stVmStrategy.getStSkuSort();
            //遍历所选机器
            for (int i = 0; i < ids.length; i++) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, ids[i]);
                Map<String, TMtVmChannelNext> map = mtVm.getChannelNext();
                for (TStVmChannel stVmChannel : channelList) {
                    TMtVmChannelNext mtVmChannelNext = map.get(stVmChannel.getChannelCode());
                    mtVmChannelNext.setProperties(stVmChannel);
                    mtVmChannelNext.setUtor(currentUser.getId());
                    mtVmChannelNext.setUtime(new Date());
                }
                List<TMtVmSkuSortNext> list = mtVm.getSkuSortNext();
                if (null != list && !list.isEmpty()) {
                    context.deleteObjects(list);
                }
                for (TStVmSkuSort stVmSkuSort : sortList) {
                    TMtVmSkuSortNext mtVmSkuSortNext = context.newObject(TMtVmSkuSortNext.class);
                    mtVmSkuSortNext.setProperties(stVmSkuSort);
                    mtVmSkuSortNext.setCtor(currentUser.getId());
                    mtVmSkuSortNext.setCtime(new Date());
                    mtVm.addToSkuSortNext(mtVmSkuSortNext);
                }
                List<TMtVmStrategy> stList = mtVm.getStrategy();
                boolean flag = false;
                for (TMtVmStrategy mtVmStrategy : stList) {
                    if (mtVmStrategy.getStrategyType().equals(TStVmStrategy.TYPE_ST_VM_CHANNEL)) {
                        mtVmStrategy.update(currentUser.getId(), stVmStrategy.getStrategyName());
                        flag = true;
                        break;
                    }
                }
                if (!flag) {
                    TMtVmStrategy mtVmStrategy = context.newObject(TMtVmStrategy.class);
                    mtVmStrategy.setStrategyType(TStVmStrategy.TYPE_ST_VM_CHANNEL);
                    mtVmStrategy.setMtVm(mtVm);
                    mtVmStrategy.update(currentUser.getId(), stVmStrategy.getStrategyName());
                }

            }

            context.commitChanges();
            respMap.put(KEY_CODE, 0);
            sysLogService.saveSysLog("应用货道策略" + strategyId + "到售货机" + StringUtil.join(",", ids), TSysLog.SAVE, SyslogEnum.MT_VM_CODE, currentUser.getId(), context);
        } catch (Exception e) {
            context.rollbackChanges();
            logger.error(e.getLocalizedMessage(), e);
            respMap.put(KEY_CODE, 101);
        }
        return respMap;
    }

    /**
     * 设置mergePId
     */
    private void setMergePId(Integer parentChannel,
                             Integer childrenChannelId,
                             ChannelConfigCache cache
    ) {
        List<ChannelConfigCache.ChannelConfig> mergeList = cache.findMergedList(childrenChannelId);

        if (null != mergeList) {
            for (ChannelConfigCache.ChannelConfig channelConfig : mergeList) {
                channelConfig.setMergePid(parentChannel);
            }
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
            TMtVmChannelNext channel = Cayenne.objectForPK(context, TMtVmChannelNext.class, channelIds[0]);
            if (null != channel) {
                Set<Integer> skuSet = new HashSet<>();
                TMtVm mtVm = channel.getMtVm();
                if (null != mtVm) {
                    Map<String, TMtVmChannelNext> channelMap = mtVm.getChannelNext();
//                    List<TStVmChannel> channelList = strategy.getStChannel();
                    if (null != channelMap && channelMap.size() > 0) {
                        List<Integer> chanelIdList = Arrays.asList(channelIds);
                        Iterator iterator = channelMap.keySet().iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next().toString();
                            TMtVmChannelNext mtVmChannelNext = channelMap.get(key);
                            if (chanelIdList.contains(mtVmChannelNext.getChannelId())) {
                                continue;
                            }
                            if (mtVmChannelNext.getSku() != null) {
                                skuSet.add(mtVmChannelNext.getSku().getId());
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

    private int getMaxCapacity(TMtSku sku, ChannelConfigCache.ChannelConfig channel,
                               int standardLength) {
        if (channel.getIsFixedCapacity() == TStVmChannel.IS_FIXED_CAPACITY) {
            //固定容量
            return channel.getFixedCapacity();
        } else {
            return standardLength / sku.getSkuLength();
        }
    }

    @Override
    public TMtVmChannel findByChannelCode(ObjectContext context, TMtVm mtVm,
                                          String channelCode) {
        return mtVmChannelDao.findByChannelCode(context, mtVm, channelCode);
    }

    @Override
    public Map<String, Object> copySort(ObjectContext context, TSysUser sysUser, Integer sortNumber, Integer vmId) {
        try {
            //复制到下一位
            synchronized (vmId.toString().intern()) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
                TMtVmSkuSortNext sort = mtVm.getTemporarySort(sortNumber);
                if (null != mtVm && null != sort) {

                    for (TMtVmSkuSortNext next : mtVm.getSkuSortNext()) {
                        if (next.getSort() > sort.getSort()) next.setSort(next.getSort() + 1);
                    }
//                    mtVm.getSkuSortNext().forEach(tMtVmSkuSortNext ->{ if(tMtVmSkuSortNext.getSort()>sort.getSort())tMtVmSkuSortNext.setSort(tMtVmSkuSortNext.getSort()+1);});
                    TMtVmSkuSortNext newSort = context.newObject(TMtVmSkuSortNext.class);
                    newSort.setCtime(new Date());
                    newSort.setCtor(sysUser.getId());
                    newSort.setMtVm(mtVm);
                    newSort.setSort(sort.getSort() + 1);
                    newSort.setSkuId(sort.getSkuId());
                    try {
//                        context.commitChanges();
                        logger.info("copy success vmId = " + vmId + " sortNumber = " + sortNumber + " userId = " + sysUser.getId());
                        Map<String, Object> resultMap = new HashMap<>();
                        String html = makeStringSort(context, mtVm.getId());
                        resultMap.put("type", "success");
                        resultMap.put("html", html);
                        resultMap.put("message", "复制成功");
                        return resultMap;
                    } catch (Exception e) {
                        logger.info("copy error vmId = " + vmId + " sortNumber = " + sortNumber + " userId = " + sysUser.getId());
//                        context.rollbackChanges();
                        logger.error(e.getLocalizedMessage(), e);
                    }
                } else {
                    return ReturnCode.FAIL.toMap();
                }
            }
        } catch (Exception e) {
            logger.error("copy error vmId = " + vmId + " sortNumber = " + sortNumber + " userId = " + sysUser.getId());
            logger.error(e.getLocalizedMessage(), e);
        }
        return ReturnCode.FAIL.toMap();
    }

    @Override
    public Map<String, Object> deleteSort(ObjectContext context, TSysUser sysUser, Integer sortNumber, Integer vmId) {
        try {
            synchronized (vmId.toString().intern()) {
                TMtVm mtVm = Cayenne.objectForPK(context, TMtVm.class, vmId);
                TMtVmSkuSortNext sort = mtVm.getTemporarySort(sortNumber);
                List<TMtVmSkuSortNext> nextList = mtVm.getSkuSortNext();
                if (null != mtVm && null != sort) {
                    boolean canDelete = false;
                    for (TMtVmSkuSortNext tMtVmSkuSortNext : nextList) {
                        if (tMtVmSkuSortNext.getSkuId().equals(sort.getSkuId()) && !tMtVmSkuSortNext.getSort().equals(sort.getSort())) {
                            context.deleteObject(sort);
                            canDelete = true;
                            break;
                        }
                    }
                    try {
                        if (canDelete) {
//                            context.commitChanges();
                            logger.info("delete success vmId = " + vmId + " sortNumber = " + sortNumber + " userId = " + sysUser.getId());
                            Map<String, Object> resultMap = new HashMap<>();
                            String html = makeStringSort(context, mtVm.getId());
                            resultMap.put("type", "success");
                            resultMap.put("html", html);
                            resultMap.put("message", "删除成功");
                            return resultMap;
                        } else {
                            Map<String, Object> resultMap = new HashMap<>();
                            resultMap.put("type", "error");
                            resultMap.put("message", "删除失败,不能删除最后一个");
                            return resultMap;
                        }

                    } catch (Exception e) {
                        logger.info("delete error vmId = " + vmId + " sortNumber = " + sortNumber + " userId = " + sysUser.getId());
//                        context.rollbackChanges();
                        logger.error(e.getLocalizedMessage(), e);
                    }
                } else {
                    return ReturnCode.FAIL.toMap();
                }
            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return ReturnCode.FAIL.toMap();
    }

    @Override
    public ChannelConfigCache getChannelCache(TSysUser sysUser, TMtVm mtVm) {
        try {
            String cache = myJedisCluster.hget("channel_cache_" + mtVm.getId(), sysUser.getId() + "");
            logger.debug(sysUser.getUserAccount() + "获取货到缓存，机器编号" + mtVm.getInnerCode() + "缓存内容" + cache);
            if (CheckUtils.check(cache)) {
                return JSON.parseObject(cache, ChannelConfigCache.class);
            } else if (mtVm.hasNextChannelIncludeMerge()) {
                ChannelConfigCache channelConfigCache = ChannelConfigCache.newInstance(mtVm.getId(), sysUser.getId());
                Map<String, TMtVmChannelNext> map = mtVm.getChannelNext();
                map.forEach((channelCode, vmChannelNext) -> channelConfigCache.addData(vmChannelNext));
                myJedisCluster.hset("channel_cache_" + mtVm.getId(), sysUser.getId() + "", channelConfigCache.toJson());
                return channelConfigCache;
            } else {
                ChannelConfigCache channelConfigCache = ChannelConfigCache.newInstance(mtVm.getId(), sysUser.getId());
                List<TMtVmChannel> list = mtVm.getChannelInfo();
                Map<String, TMtVmChannelNext> map = mtVm.getChannelNext();
                list.forEach(mtVmChannel -> {
                    Integer mergePid = Optional.of(mtVmChannel).map(TMtVmChannel::getMergePid).map(pid -> Cayenne.objectForPK(context, TMtVmChannel.class, pid)).map(TMtVmChannel::getChannelCode).map(map::get).map(TMtVmChannelNext::getChannelId).orElse(null);
                    channelConfigCache.addData(mtVmChannel, map.get(mtVmChannel.getChannelCode()).getChannelId(), mergePid);
                });
                myJedisCluster.hset("channel_cache_" + mtVm.getId(), sysUser.getId() + "", channelConfigCache.toJson());
                return channelConfigCache;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public void deleteChannelCache(Integer vmId) {
        try {
            myJedisCluster.delKey("channel_cache_" + vmId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void updateChannelCache(ChannelConfigCache cache) {
        try {
            myJedisCluster.hset("channel_cache_" + cache.getVmId(), cache.getUserId() + "", cache.toJson());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void saveChannelConfig(TMtVm mtVm, TSysUser sysUser, ChannelConfigCache cache) {
        try {
            logger.info("单独保存货道设置，userId=" + sysUser.getId() + "，vmId=" + mtVm.getId());
            Map<String, TMtVmChannelNext> map = mtVm.getChannelNext();
            cache.loadCodeMap().forEach((channelCode, channelConfig) -> {
                TMtVmChannelNext channelNext = map.get(channelCode);
                channelNext.setProperties(channelConfig);
                channelNext.setUtime(new Date());
                channelNext.setUtor(sysUser.getId());
            });
            mtVm.getObjectContext().commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            mtVm.getObjectContext().rollbackChanges();
            throw e;
        }
    }

    @Override
    public Map<String, Object> adjustCapacity(ChannelConfigCache cache, Integer channelId, Integer adjustNum) {
        Map<String, Object> respMap = new HashMap<>();
        respMap.put(KEY_CODE, 0);
        try {
            Map<Integer, ChannelConfigCache.ChannelConfig> map = cache.loadIdMap();
            ChannelConfigCache.ChannelConfig channelConfig = map.get(channelId);
            channelConfig.setAdjustMaxCapacity(adjustNum);
        } catch (Exception e) {
            respMap.put(KEY_CODE, 102);
            logger.error(e.getLocalizedMessage(), e);
        }
        return respMap;
    }

    @Override
    public Map<String, Object> setFullSku(ObjectContext context, TMtVm mtVm, String channelCode, Integer fullval) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Map<String, TMtVmChannelNext> channelNextMap = mtVm.getChannelNext();
            TMtVmChannelNext channelNext = channelNextMap.get(channelCode);
            channelNext.setAdjustMaxCapacity(fullval);
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

    private static ObjectContext privateContext = ObjectContextFactory.getInstance().newObjectContext();

    private static TMtSku getSku(Integer skuId) {
        if (IntegerUtils.isPositiveInteger(skuId)) {
            return Cayenne.objectForPK(privateContext, TMtSku.class, skuId);
        }
        return null;
    }

    static {
        Expression qualifier = ExpressionFactory.expTrue();
        SelectQuery select = new SelectQuery(TMtSku.class, qualifier);
        privateContext.performQuery(select);
    }
}
