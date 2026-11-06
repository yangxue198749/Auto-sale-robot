package com.kdx.vms.model.custom;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2018/2/7.
 */
public class ShelfConfigCache {
    private List<Integer> data;
    private Integer vmId;
    private Integer userId;

    public static ShelfConfigCache newInstance(Integer vmId, Integer userId) {
        ShelfConfigCache cache = new ShelfConfigCache();
        cache.setData(new LinkedList<>()).setVmId(vmId).setUserId(userId);
        return cache;
    }

    public List<Integer> getData() {
        return data;
    }

    public ShelfConfigCache setData(List<Integer> data) {
        this.data = data;
        return this;
    }

    public Integer getVmId() {
        return vmId;
    }

    public ShelfConfigCache setVmId(Integer vmId) {
        this.vmId = vmId;
        return this;
    }

    public Integer getUserId() {
        return userId;
    }

    public ShelfConfigCache setUserId(Integer userId) {
        this.userId = userId;
        return this;
    }

    public String toJson() {
        return JSON.toJSONString(this);
    }

    public Map<String, Object> exchange(Integer index1, Integer index2) {
        Map<String, Object> respMap = new HashMap<>();
        if (index1 >= data.size() || index2 >= data.size()) {
            respMap.put("type", "error");
            respMap.put("message", "参数错误");
            return respMap;
        }
        Integer skuId1 = data.get(index1);
        Integer skuId2 = data.get(index2);
        data.set(index1, skuId2);
        data.set(index2, skuId1);
        respMap.put("type", "success");
        respMap.put("message", "交换成功");
        return respMap;
    }

    public Map<String, Object> copy(Integer index) {
        Map<String, Object> respMap = new HashMap<>();
        if (index >= data.size()) {
            respMap.put("type", "error");
            respMap.put("message", "参数错误");
            return respMap;
        }
        data.add(index, data.get(index));
        respMap.put("type", "success");
        respMap.put("message", "复制成功");
        return respMap;
    }

    public Map<String, Object> delete(Integer index) {
        Map<String, Object> respMap = new HashMap<>();
        if (index >= data.size()) {
            respMap.put("type", "error");
            respMap.put("message", "参数错误");
            return respMap;
        }
        Integer skuId = data.get(index);
        if (data.stream().filter(skuId::equals).collect(Collectors.toList()).size() > 1) {
            data.remove(index.intValue());
            respMap.put("type", "success");
            respMap.put("message", "删除成功");
            return respMap;
        }
        respMap.put("type", "error");
        respMap.put("message", "删除失败,不能删除最后一个");
        return respMap;
    }

}
