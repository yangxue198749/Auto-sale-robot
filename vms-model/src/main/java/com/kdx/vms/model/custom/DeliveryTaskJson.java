package com.kdx.vms.model.custom;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/11/27.
 */
public class DeliveryTaskJson {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private Integer taskId;
    private Integer taskStatus;
    private List<DeliveryObject> fillList;
    private List<DeliveryObject> cleanList;
    private List<DeliveryObject> adjustList;

    public Integer getTaskId() {
        return taskId;
    }

    public DeliveryTaskJson setTaskId(Integer taskId) {
        this.taskId = taskId;
        return this;
    }

    public static DeliveryTaskJson newInstance() {
        DeliveryTaskJson deliveryTaskJson = new DeliveryTaskJson();
        deliveryTaskJson.setFillList(new ArrayList<>());
        deliveryTaskJson.setCleanList(new ArrayList<>());
        deliveryTaskJson.setAdjustList(new ArrayList<>());
        return deliveryTaskJson;
    }

    public static DeliveryTaskJson of(String content) {
        return JSON.parseObject(content, DeliveryTaskJson.class);
    }

    public boolean check(DeliveryTaskJson deliveryTaskJson) {
        logger.info("delivery size in=" + size() + "out=" + deliveryTaskJson.size());
        return size() == deliveryTaskJson.size();
    }

    public void formatDate() {
        this.fillList.stream()
                .filter(deliveryObject -> null != deliveryObject.getTermInfo() && deliveryObject.getTermInfo().equals(1))
                .forEach(deliveryObject -> deliveryObject.setManufactureDate(this.formatDate(deliveryObject.getManufactureDate())));
        this.adjustList.stream()
                .filter(deliveryObject -> null != deliveryObject.getTermInfo() && deliveryObject.getTermInfo().equals(1))
                .forEach(deliveryObject -> deliveryObject.setManufactureDate(this.formatDate(deliveryObject.getManufactureDate())));
    }

    public boolean checkDate() {
        int size1 = this.fillList.stream()
                .filter(deliveryObject -> null != deliveryObject.getTermInfo() && deliveryObject.getTermInfo().equals(1))
                .filter(deliveryObject -> deliveryObject.getMarginCount() > 0)
                .collect(Collectors.toList())
                .size();//应该有的数量
        int size2 = this.fillList.stream()
                .filter(deliveryObject -> null != deliveryObject.getTermInfo() && deliveryObject.getTermInfo().equals(1))
                .filter(deliveryObject -> deliveryObject.getManufactureDate() != null)
                .map(DeliveryObject::getManufactureDate)
                .map(DateUtils::getDate1)
                .map(date -> null != date)
                .collect(Collectors.toList())
                .size();//能正确转换成日期的数量
        logger.info("应有日期项=" + size1 + "，实际有日期项=" + size2);
        return size1 <= size2;
    }

    private String formatDate(String date) {
        if (StringUtil.isEmpty(date) || date.length() != 8) return null;
        String year = date.substring(0, 4);
        String month = date.substring(4, 6);
        String day = date.substring(6);
        return year + "-" + month + "-" + day;
    }

    public int size() {
        int size = 0;
        if (CheckUtils.check(fillList)) size += fillList.size();
        if (CheckUtils.check(cleanList)) size += cleanList.size();
        if (CheckUtils.check(adjustList)) size += adjustList.size();
        return size;
    }

    public List<DeliveryObject> getFillList() {
        return fillList;
    }

    public DeliveryTaskJson setFillList(List<DeliveryObject> fillList) {
        this.fillList = fillList;
        return this;
    }

    public List<DeliveryObject> getCleanList() {
        return cleanList;
    }

    public DeliveryTaskJson setCleanList(List<DeliveryObject> cleanList) {
        this.cleanList = cleanList;
        return this;
    }

    public List<DeliveryObject> getAdjustList() {
        return adjustList;
    }

    public DeliveryTaskJson setAdjustList(List<DeliveryObject> adjustList) {
        this.adjustList = adjustList;
        return this;
    }

    public DeliveryTaskJson addFillObject(DeliveryObject deliveryObject) {
        this.fillList.add(deliveryObject);
        return this;
    }

    public DeliveryTaskJson addCleanObject(DeliveryObject deliveryObject) {
        this.cleanList.add(deliveryObject);
        return this;
    }

    public DeliveryTaskJson addAdjustObject(DeliveryObject deliveryObject) {
        this.adjustList.add(deliveryObject);
        return this;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public DeliveryTaskJson setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
        return this;
    }
}
