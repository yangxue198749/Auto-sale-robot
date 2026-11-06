package com.kdx.vms.model.bean;

import com.kdx.vms.model.bean.auto._TMtVmStrategy;

import java.util.Date;

public class TMtVmStrategy extends _TMtVmStrategy {
    public void clear(Integer userId){
        this.setUtime(new Date());
        this.setUtor(userId);
        this.setSysnMark(0);
        this.setStrategyName("");
    }

    public void update(Integer userId,String strategyName){
        this.setUtime(new Date());
        this.setUtor(userId);
        this.setSysnMark(0);
        this.setStrategyName(strategyName);
    }


}
