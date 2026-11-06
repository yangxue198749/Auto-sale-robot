package com.kdx.vms.model.bean;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.MyJedisUtils;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.auto._TMtVm;
import com.kdx.vms.model.enumeration.DeleteMark;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.Format;
import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

public class TMtVm extends _TMtVm implements Comparable<TMtVm> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 未投放
     */
    public static final int STATUS_UNDEPLOY = 0;
    /**
     * 在投放
     */
    public static final int STATUS_DEPLOY = 1;
    /**
     * 在售
     */
    public static final int STATUS_SALE = 2;
    /**
     * 停售
     */
    public static final int STATUS_UNSLAE = 3;
    /**
     * 撤回中
     */
    public static final int STATUS_WITHDRAW = 4;

    public static final int TASK_CYCLE_DEFAULT = 3;

    /**
     * 保温箱编号
     */
    public static final String TEMP_BOX_NO = "tempBoxNo";
    /**
     * 保温箱温度
     */
    public static final String TEMP_VALUE = "tempValue";

    /**
     * 可预订
     */
    public static final Integer RESERVE_FALSE = 0;
    /**
     * 可预订
     */
    public static final Integer RESERVE_TRUE = 1;
    /**
     * 非工作日可预订
     */
    public static final Integer IS_RESERVE_TRUE = 1;

    /**
     * 地推
     */
    public static final Integer PROMOTION = 2;

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    private boolean edit = false;
    private boolean copy = false;

    public boolean isCopy() {
        return copy;
    }

    public void setCopy(boolean copy) {
        this.copy = copy;
    }

    private long editTimeStamp;

    public synchronized boolean isEdit() {
        if (!this.hasNextChannelIncludeMerge() && edit) {
            Map<String, TMtVmChannelNext> map = this.getChannelNext();
            for (String key : map.keySet()) {
                if (map.get(key).getUtime() != null && map.get(key).getUtime().getTime() > editTimeStamp + 1000) {
                    System.out.println("超时");
                    edit = false;
                    return false;
                }
            }
        }
        return edit;
    }

    public synchronized void setEdit(boolean edit) {
        this.editTimeStamp = System.currentTimeMillis();
        this.edit = edit;
    }

    public String getVmEvent() {
        List<TMtVmEventMaster> list = this.getEvent();
        if (null == list || list.isEmpty()) return null;
        for (TMtVmEventMaster tMtVmEventMaster : list) {
            if (tMtVmEventMaster.getEventType().equals(TStVmEvent.EVENT_TYPE_VM) && tMtVmEventMaster.getEndDate().after(new Date())) {
                return tMtVmEventMaster.getEventName();
            }
        }
        return null;
    }

    public String getSkuEvent() {
        List<TMtVmEventMaster> list = this.getEvent();
        if (null == list || list.isEmpty()) return null;
        for (TMtVmEventMaster tMtVmEventMaster : list) {
            if (tMtVmEventMaster.getEventType().equals(TStVmEvent.EVENT_TYPE_SKU) && tMtVmEventMaster.getEndDate().after(new Date())) {
                return tMtVmEventMaster.getEventName();
            }
        }
        return null;
    }


    public TMtVmText getMtText() {
        List<TMtVmText> list = super.getText();
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }


    public List<TMtVmShortcuts> getShortcutsByDB() {
        Expression qualifier = ExpressionFactory.matchExp("mtVM", this);
        SelectQuery select = new SelectQuery(TMtVmShortcuts.class, qualifier);
        select.addOrdering(TMtVmShortcuts.SHORTCUTS_CODE_PROPERTY, SortOrder.ASCENDING);
        return objectContext.performQuery(select);
    }

    public boolean hasNextChannel() {
        Map<String, TMtVmChannelNext> map = this.getChannelNext();
//        Iterator iterator = map.keySet().iterator();
//        while (iterator.hasNext()) {
//            String key = iterator.next().toString();
//            TMtVmChannelNext mtVmChannelNext = map.get(key);
//            if (mtVmChannelNext.getSku() != null) {
//                return true;
//            }
//        }
//        return false;
        return map.entrySet().stream()
                .filter(entry -> null != entry.getValue().getSku())
                .count() > 0;
    }

    public boolean hasNextChannelIncludeMerge() {
        Map<String, TMtVmChannelNext> map = this.getChannelNext();
        Iterator iterator = map.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            TMtVmChannelNext mtVmChannelNext = map.get(key);
            if (mtVmChannelNext.getSku() != null || mtVmChannelNext.getMergeQty() != 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如果将要应用列表不存在,将当前的copy一份作为将要应用
     *
     * @param sortNumber
     * @return
     */
    public TMtVmSkuSortNext getTemporarySort(Integer sortNumber) {
        List<TMtVmSkuSortNext> list = getSkuSortNext();
        if (null == list || list.size() == 0) {
            List<TMtVmSkuSort> sortList = getSkuSort();
            if (null != sortList && sortList.size() > 0) {
                list = new ArrayList<>();
                ObjectContext context = this.getObjectContext();
                for (TMtVmSkuSort mtVmSkuSort : sortList) {

                    TMtVmSkuSortNext next = context.newObject(TMtVmSkuSortNext.class);
                    next.setSort(mtVmSkuSort.getSort());
                    next.setMtVm(mtVmSkuSort.getMtVm());
                    next.setCtor(mtVmSkuSort.getCtor());
                    next.setCtime(new Date());
                    next.setSkuId(mtVmSkuSort.getSkuId());
                    list.add(next);
                }
            }
        }
        for (TMtVmSkuSortNext mtVmSkuSortNext : list) {
            if (mtVmSkuSortNext.getSort().equals(sortNumber)) {
                return mtVmSkuSortNext;
            }
        }
        return null;
    }


    /** 以下为售货机状态页面数值 */
    /**
     * 页面点位
     *
     * @return
     */
    public String getNodeName() {
        TMtNode mtNode = getNode();
        return null == mtNode ? "" : mtNode.getNodeName();
    }

    /**
     * 页面区域
     *
     * @return
     */
    public String getAreaName() {
        TMtNode mtNode = getNode();
        if (null == mtNode) return "";
        Integer areaId = mtNode.getAreaId();
        if (!IntegerUtils.isPositiveInteger(areaId)) return "";
        TMtArea mtArea = Cayenne.objectForPK(getObjectContext(), TMtArea.class, areaId);
        return null == mtArea ? "" : mtArea.getAreaName();
    }

    /**
     * 机存量=（所有商品库存余量/所有商品满仓数量）*100%
     *
     * @return
     */
    public String getCountProportion() {
        try {
            List<TMtVmChannel> channelList = getChannelInfo();
            int curNumTotal = 0;
            int capacityTotal = 0;
            for (TMtVmChannel tMtVmChannel : channelList) {
                if (tMtVmChannel.getSku() == null) {
                    continue;
                }
                if (null != tMtVmChannel.getCurrNum()) {
                    curNumTotal += tMtVmChannel.getCurrNum();
                }
                if (null != tMtVmChannel.getMaxNum()) {
                    capacityTotal += tMtVmChannel.getMaxNum();
                }
            }
            String proportion = null;
            Format format = NumberFormat.getPercentInstance();
            if (capacityTotal == 0) {
                proportion = format.format(0.0);
            } else {
                proportion = format.format((double) curNumTotal / capacityTotal);
            }
            return proportion;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 缺货商品
     * 当机内某个商品的库存总量小于等于3时，将商品名称展示在缺货商品中
     *
     * @return
     */
    public String getStockoutSku() {
        Map<String, Integer> map = new HashMap<>();
        try {
            //得到售货机下的商品列表，
            List<TMtVmChannel> channelList = getChannelInfo();
            for (TMtVmChannel tMtVmChannel : channelList) {
                TMtSku sku = tMtVmChannel.getSku();
                if (sku == null) {
                    continue;
                }
                String skuName = sku.getSkuName();
                String skuUnit = sku.getSkuUnit();
                Integer currNum = tMtVmChannel.getCurrNum();
                if (null == skuName || currNum == null) {
                    continue;
                }
                Integer skuNum = map.get(skuName + skuUnit);
                if (null != skuNum) {
                    currNum += skuNum;
                }
                map.put(skuName + skuUnit, currNum);
            }
            StringBuffer buffer = new StringBuffer();
            int count = 0;
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                if (entry.getValue() <= 3) {
                    count++;
                    if (count != 1) {
                        buffer.append(",");
                    }
                    buffer.append(entry.getKey());
                }

            }
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 页面设置温度
     *
     * @return
     */
    public String getTargetTemp() {
        String msg = "";
        List<TMtVmEnergySave> list = getEnergySave();
        if (null == list) return "";
        String time = DateUtils.getTime3();
        Expression expression = ExpressionFactory.lessExp(TMtVmEnergySave.START_TIME_PROPERTY, time);
        expression = expression.andExp(ExpressionFactory.greaterExp(TMtVmEnergySave.END_TIME_PROPERTY, time));
        List<TMtVmEnergySave> nowList = expression.filterObjects(list);
        if (null != nowList && nowList.size() > 0) {
            for (TMtVmEnergySave tMtVmEnergySave : nowList) {
                if (TStVmEnergySave.HEATING == tMtVmEnergySave.getEnergySaveType() || TStVmEnergySave.REFRIGERATION == tMtVmEnergySave.getEnergySaveType()) {
                    if (null == tMtVmEnergySave.getTempBoxNo() || StringUtil.isEmpty(tMtVmEnergySave.getTempBoxNo() + ""))
                        continue;
                    msg += tMtVmEnergySave.getTempBoxNo() + "号:" + tMtVmEnergySave.getTargetValue() + "<br>";
                }
            }
        }
        return msg;
    }

    /**
     * 页面获取当前温度
     *
     * @return
     */
    public String getNowTemp() {
        String msg = "";
        try {
            TVmStatus vmStatus = getVmStatusLog();
            if (null != vmStatus) {
                String tempValueStr = vmStatus.getTempValue();
                JSONArray array = JSONArray.parseArray(tempValueStr);
                if (null != array && array.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        if (obj.containsKey(TEMP_BOX_NO) && obj.containsKey(TEMP_VALUE)) {
                            if (null == obj.get(TEMP_BOX_NO) || StringUtil.isEmpty(obj.get(TEMP_BOX_NO) + "")) continue;
                            msg += obj.getIntValue(TEMP_BOX_NO) + "号:" + obj.getIntValue(TEMP_VALUE) + "<br>";
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }

    /**
     * 页面获取当前温度
     *
     * @return
     */
    public String getNowTempByMp() {
        String msg = "";
        try {
            TVmStatus vmStatus = getVmStatusLog();
            if (null != vmStatus) {
                String tempValueStr = vmStatus.getTempValue();
                JSONArray array = JSONArray.parseArray(tempValueStr);
                if (null != array && array.size() > 0) {
                    for (int i = 0; i < array.size(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        if (obj.containsKey(TEMP_BOX_NO) && obj.containsKey(TEMP_VALUE)) {
                            if (null == obj.get(TEMP_BOX_NO) || StringUtil.isEmpty(obj.get(TEMP_BOX_NO) + "")) continue;
                            msg += obj.getIntValue(TEMP_BOX_NO) + "号:" + obj.getIntValue(TEMP_VALUE) + ";";
                        }
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return msg;
    }



    /**
     * 页面获取vmc状态
     *
     * @return
     */
    public String getVmcStatus() {
        TVmStatus vmStatus = getVmStatusLog();
        return null == vmStatus ? "3" : vmStatus.getVmStatus() + "";
    }

    public String getTaskStatus() {
        TVmStatus vmStatus = getVmStatusLog();
        return null == vmStatus ? "" : vmStatus.getTaskStatusName();
    }

    /**
     * 售货机温度状态
     * @return
     */
    public String getTempStatus() {
        String status = "3";
        TVmStatus vmStatus = getVmStatusLog();
        if (vmStatus != null) {
            if (vmStatus.getTempStatus() != null) {
                status = vmStatus.getTempStatus() + "";
            }
        }
        return status;
    }

    /**
     * 解析温度显示字符串
     * @param tempStr "1号:9<br>2号:16<br>"
     * @return {2号=16, 1号=9}
     * @throws NumberFormatException
     */
    public static HashMap<String, Integer> getTempMap(String tempStr) throws NumberFormatException {
        HashMap<String, Integer> map = new HashMap<>();

        if (StringUtil.isEmpty(tempStr)) { //字符串为null或“”
            return map;
        }
        if (tempStr.indexOf("<br>") != -1) { // 1号:16
            String[] noStatus = tempStr.split("<br>");
            for (int i = 0; i < noStatus.length; i++) {
                String[] state = noStatus[i].split(":");
                if (state.length == 2) {
                    map.put(state[0], Integer.parseInt(state[1]));
                }
            }
        }
        return map;
    }

    /**
     * 比较售货机实际温度与设置温度
     * @param nowTempStr "1号:9<br>2号:16<br>"
     * @param targetTempStr "1号:9<br>2号:16<br>"
     * @return 正常 1;异常 2;未知 3;
     */
    public String checkTempStatus(String nowTempStr, String targetTempStr) {
        String tempStatusDesc = "1";
        HashMap<String, Integer> nowMap = getTempMap(nowTempStr);
        HashMap<String, Integer> targetMap = getTempMap(targetTempStr);
        if (nowMap.isEmpty()) {
            tempStatusDesc = "3";
            return tempStatusDesc;
        }

        tempStatusDesc = "1";
        boolean err = false;
        boolean info = false;

        Set<Map.Entry<String, Integer>> entries = targetMap.entrySet();
        Iterator<Map.Entry<String, Integer>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (nowMap.containsKey(entry.getKey())) {
                Integer temp1 = entry.getValue();
                Integer temp2 = nowMap.get(entry.getKey());
                if (Math.abs(temp1 - temp2) > 4) {
                    err = true;
                }
            } else {
                info = true;
            }
        }
        if (!err && info) {
            tempStatusDesc = "3";
        }

        if (err) {
            tempStatusDesc = "2";
        }
        return tempStatusDesc;
//        HashMap<String, Integer> longMap = null;
//        HashMap<String, Integer> shortMap = null;
//        if (nowMap.size() >= targetMap.size()) {
//            longMap = nowMap;
//            shortMap = targetMap;
//        } else {
//            longMap = targetMap;
//            shortMap = nowMap;
//        }
//            if (Math.abs(temp1 - temp2) > TEMP_DIFF) {
//                tempStatusDesc += entry.getKey() + ":异常;";
//            } else {
//                tempStatusDesc += entry.getKey() + ":正常;";
//            }
//            else{
//                tempStatusDesc += entry.getKey() + ":未知;";
//            }
    }




//    public String getErrorMessage() {
//        try {
//            TVmStatus vmStatus = getVmStatusLog();
//            if (null == vmStatus) return "";
//            Integer status = null == vmStatus.getVmStatus() ? -1 : vmStatus.getVmStatus();
//            StringBuilder builder = new StringBuilder();
//            switch (status) {
//                case 0:
//                    break;
//                case 2:
//                    builder.append("忙碌");
//                    break;
//                case 1:
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getVerticalStepperMoto())
//                        builder.append("垂直步进电机 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getHorizontalMotoStepperMoto())
//                        builder.append("水平步进电机 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getSlotMoto())
//                        builder.append("取物门电机 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getLeftPickMoto())
//                        builder.append("左侧取货电机 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getRightPickMoto())
//                        builder.append("右侧取货电机 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getLeftRotateMoto())
//                        builder.append("左侧旋转电机 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getRightRotateMoto())
//                        builder.append("右侧旋转电机 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getShipSensor())
//                        builder.append("出货传器 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getMdseOnPlatform())
//                        builder.append("取物台有货物 ");
//                    if (TVmStatus.STATUS_FALSE == vmStatus.getOutPutSlot())
//                        builder.append("取物门开关 ");
//                    if (null != vmStatus.getChannelMotorFailureCodes())
//                        builder.append(vmStatus.getChannelMotorFailureCodes());
//                    break;
//            }
//            return builder.toString();
//        } catch (Exception e) {
//            return "";
//        }
//    }

    public String getNetStatus() {
        return 0 == getNetworkStatus() ? "<i  class='icon_state green'>正常</i>" : "<i class='icon_state yellow'>异常</i>";
    }

    /**
     * 页面最后上报时间
     *
     * @return
     */
    public String getLastTime() {
        TVmStatus vmStatus = getVmStatusLog();
        if (null == vmStatus) return "";
        Date date = vmStatus.getVmTime();
        if (null == date)
            // 没有更新时间取创建时间
            date = vmStatus.getCtime();
        if (null == date) return "";
        return DateUtils.getTime2(date);
    }

    /**
     * 页面 获取型号名称
     *
     * @return
     */
    public String getModelName() {
        Integer modelId = getVmModelId();
        if (!IntegerUtils.isPositiveInteger(modelId)) return "";
        TMtVmModel mtVmModel = Cayenne.objectForPK(getObjectContext(), TMtVmModel.class, modelId);
        return null == mtVmModel ? "" : mtVmModel.getModelName();
    }

    public boolean hasSortNext() {
        List<TMtVmSkuSortNext> list = this.getSkuSortNext();
        if (list != null && list.size() > 0) {
            for (TMtVmSkuSortNext tMtVmSkuSortNext : list) {
                if (tMtVmSkuSortNext.getSkuId() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 0表示不缺货，1表示缺货
     *
     * @return
     */
//    public Integer getSkuStatus() {
//        List<TMtVmChannel> channelList = getChannelInfo();
//        List<TMtVmSkuSort> sortList = getSkuSort();
//        for (TMtVmSkuSort mtVmSkuSort : sortList) {
//            TMtSku sku = Cayenne.objectForPK(objectContext, TMtSku.class, mtVmSkuSort.getSkuId());
//            VmStockInfo vmStockInfo = filterBySku(channelList, sku);
//            if (null != vmStockInfo && vmStockInfo.getCurrCount() <= 0) {
//                return 1;
//            }
//        }
//        return 0;
//    }
    /**
     * 不缺货
     */
    public static final int STOCK_NORMAL = 0;
    /**
     * 缺货
     */
    public static final int STOCK_EMPTY = 1;

    @Override
    public Integer getStockStatus() {
        return Optional.ofNullable(super.getStockStatus()).orElse(0);
    }

    public Integer updateStockStatus() {
        try {
            logger.info("update vmStockStatus:vmId=" + this.getId());
            Map<TMtSku, List<TMtVmChannel>> map = getChannelInfo().stream().filter(tMtVmChannel -> tMtVmChannel.getSku() != null).collect(Collectors.groupingBy(TMtVmChannel::getSku));
            boolean outOfStock = false;
            for (TMtSku sku : map.keySet()) {
                List<TMtVmChannel> tMtVmChannels = map.get(sku);
                int stock = tMtVmChannels.parallelStream().mapToInt(TMtVmChannel::getCurrNum).sum();
                if (stock == 0) {
                    outOfStock = true;
                    break;
                }

            }
            if (outOfStock) {
                setStockStatus(STOCK_EMPTY);
            } else {
                setStockStatus(STOCK_NORMAL);
            }
            this.getObjectContext().commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            this.getObjectContext().rollbackChanges();
        }
        return super.getStockStatus();
    }

    /**
     * 0表示正常，1表示断开
     *
     * @return
     */
    public Integer getNetworkStatus() {
//        try {
//            String value = MyJedisUtils.hGet("vm_list", getInnerCode());
//            if (!StringUtil.isEmpty(value)) {
//                Long time = Long.parseLong(value);
//                if (System.currentTimeMillis() - time < TVmStatus.TIME_OUT_MINUTE * 65 * 1000) {
//                    return 0;
//                }
//            }
//        } catch (Exception e) {
//
//        }
//        return 1;
        return Optional.ofNullable(this.getVmStatusLog()).map(TVmStatus::getOnlineStatus).map(integer -> integer - 1).orElse(1);
    }


//    private VmStockInfo filterBySku(List<TMtVmChannel> list, TMtSku sku) {
//        try {
//            if (null == list || null == sku) return null;
//            Expression expression = ExpressionFactory.matchExp(TMtVmChannel.SKU_PROPERTY, sku);
//            List<TMtVmChannel> mtVmChannelList = expression.filterObjects(list);
//            if (null == mtVmChannelList) return null;
//            VmStockInfo vmStockInfo = VmStockInfo.newInstance();
//            for (TMtVmChannel mtVmChannel : mtVmChannelList) {
//                vmStockInfo.addCurrCount(mtVmChannel.getCurrNum());
//                vmStockInfo.addMaxCount(mtVmChannel.getMaxCapacity());
//            }
//            return vmStockInfo;
//        } catch (Exception e) {
//        }
//        return null;
//    }


    @Override
    public int compareTo(TMtVm o) {
        //先判断设备状态，优先显示故障的
        if (!o.getVmcStatus().equals("0") && this.getVmcStatus().equals("0")) return 1;
        if (o.getVmcStatus().equals("0") && !this.getVmcStatus().equals("0")) return -1;
        //再判断网络状态，优先显示故障的
        if (o.getNetworkStatus() != 0 && this.getNetworkStatus() == 0) return 1;
        if (this.getNetworkStatus() != 0 && o.getNetworkStatus() == 0) return -1;
        //再判断商品状态，优先显示缺货的
        if (o.getStockStatus() != 0 && this.getStockStatus() == 0) return 1;
        if (this.getStockStatus() != 0 && o.getStockStatus() == 0) return -1;
        //最后按照innerCode从小到大排序
        String innerCode1 = this.getInnerCode();
        String innerCode2 = o.getInnerCode();
        int i1 = StringUtil.getIntValue(innerCode1, 0);
        int i2 = StringUtil.getIntValue(innerCode2, 0);
        return i1 - i2;
    }

    public List<TMtVmShortcuts> getShortcutsAsc() {
        List<TMtVmShortcuts> list = super.getShortcuts();
        Collections.sort(list);
        return list;
    }

    public String getDoorStatus() {
        TVmStatus vmStatus = getVmStatusLog();
        if (null != vmStatus) {
            Integer doorStatus = vmStatus.getVmDoorStatus();
            if (doorStatus != null) {
                return doorStatus == 1 ? "开门" : "关门";
            }
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(MyJedisUtils.hGet("vm_list", "01000032"));
    }


    /**
     * 取出节能
     *
     * @return
     */
    public Map<Integer, TMtVmEnergySave> getEnergySaveMapNew(TMtVmEnergySave energySave) {
        Map<Integer, TMtVmEnergySave> hashmap = new HashMap<>();
        List<TMtVmEnergySave> list = super.getEnergySave();
        if (null != list && !list.isEmpty()) {
            for (TMtVmEnergySave tMtVmEnergySave : list) {
                if (null != energySave && tMtVmEnergySave.getEnergySaveId().equals(energySave.getEnergySaveId()))
                    continue;
                hashmap.put(tMtVmEnergySave.getEnergySaveId(), tMtVmEnergySave);
            }
        }
        return hashmap;
    }

    public String getDlVmBoxVer() {
        Map<Integer, TMtUpgradeVersion> map = this.getUpgradeVersion();
        if (null != map && null != map.get(TMtUpgradePackage.VMBOX_TYPE) && !StringUtil.isEmpty(map.get(TMtUpgradePackage.VMBOX_TYPE).getLastDownloadedVer())) {
            return map.get(TMtUpgradePackage.VMBOX_TYPE).getLastDownloadedVer();
        }
        return "—";
    }

    public String getDlCommunicationVer() {
        Map<Integer, TMtUpgradeVersion> map = this.getUpgradeVersion();
        if (null != map && null != map.get(TMtUpgradePackage.COMMUNICATION_BOARD_TYPE) && !StringUtil.isEmpty(map.get(TMtUpgradePackage.COMMUNICATION_BOARD_TYPE).getLastDownloadedVer())) {
            return map.get(TMtUpgradePackage.COMMUNICATION_BOARD_TYPE).getLastDownloadedVer();
        }
        return "—";
    }

    public String getDlDeliveryVer() {
        Map<Integer, TMtUpgradeVersion> map = this.getUpgradeVersion();
        if (null != map && null != map.get(TMtUpgradePackage.DELIVERY_BOARD_TYPE) && !StringUtil.isEmpty(map.get(TMtUpgradePackage.DELIVERY_BOARD_TYPE).getLastDownloadedVer())) {
            return map.get(TMtUpgradePackage.DELIVERY_BOARD_TYPE).getLastDownloadedVer();
        }
        return "—";
    }

    public boolean isReserve() {
        if (null != this.getVmType() && this.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
            return getParentVm().isReserve();
        }
        Integer reserve = this.getReserve();
        return null != reserve && reserve == 1;
    }

    public String getMtVmStatusName() {
        switch (getVmStatus()) {
            case STATUS_UNDEPLOY:
                return "未投放";
            case STATUS_DEPLOY:
                return "投放中";
            case STATUS_SALE:
                return "在售";
            case STATUS_UNSLAE:
                return "停售";
            case STATUS_WITHDRAW:
                return "撤回中";
        }
        return "";
    }

    public String getSimNumber() {
        if (null != this.getVmType() && this.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
            return "—";
        } else {
            return Optional.ofNullable(getSim()).map(TMtSim::getSimNumber).orElse("");
        }
    }

    public String getPrintStatus() {
        if (null != this.getVmType() && this.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
            return "—";
        } else {
            return getPrinter() == 0 ? "无" : "有";
        }
    }

    @Override
    public List<TMtVmSkuSort> getSkuSort() {
        if (null != this.getVmType() && this.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
            return getParentVm().getSkuSort();
        }
        return super.getSkuSort();
    }

    public TMtVm findExtention(Integer extentionCode) {
        if (null == extentionCode || extentionCode <= 1) return this;
        List<TMtVm> list = this.getChildrenVm();
        return list.stream().filter(mtVm -> mtVm.getVmExtentionNum().equals(extentionCode) && mtVm.getDeleteMark() == 0).findFirst().orElse(null);
    }

    @Override
    public String getVmDesc() {
        if (null != this.getVmType() && this.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
            return this.getParentVm().getVmDesc();
        }
        return super.getVmDesc();
    }

    @Override
    public List<TMtVm> getChildrenVm() {
        return super.getChildrenVm().stream().filter(mtVm -> Objects.equals(mtVm.getDeleteMark(), DeleteMark.EXIST.getCode())).collect(Collectors.toList());
    }

    /**
     * 售货机温度状态(create by zhouxing on 18/5/28)
     * @return
     */
    public String getTempStatusNew() {
        TVmStatus vmStatus = getVmStatusLog();
        if(vmStatus == null) return "";
        if (vmStatus.getTempStatus() == null) return "3";
        return vmStatus.getTempStatus() + "";
    }
}
