package com.kdx.vms.model.bean;

import org.apache.cayenne.Cayenne;

import com.kdx.vms.model.bean.auto._TStVmStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TStVmStrategy extends _TStVmStrategy {

    public static final int TYPE_ST_VM_CHANNEL     = 1;
    public static final int TYPE_ST_VM_ENERGY_SAVE = 2;
    public static final int TYPE_ST_VM_EVENT       = 3;
    public static final int TYPE_ST_VM_PRICE       = 4;
    public static final int TYPE_ST_VM_SHORTCUTS   = 5;
    public static final int TYPE_ST_VM_SKU_SORT    = 6;
    public static final int TYPE_ST_VM_TEXT        = 7;

    private Integer strategyId;
    
	public Integer getStrategyId() {
		return Cayenne.intPKForObject(this);
	}


    /**
     * 两层key,key1是保温箱编号，key2是节能种类
     *
     * @return
     */
    public Map<Integer, TStVmEnergySave> getEnergySaveMapNew(Integer energySaveId) {
        Map<Integer, TStVmEnergySave> hashmap = new HashMap<>();
        List<TStVmEnergySave> list = super.getStEnergySave();
        if (null != list && !list.isEmpty()) {
            for (TStVmEnergySave tStVmEnergySave : list) {
                if(null != energySaveId && tStVmEnergySave.getEnergySaveId().equals(energySaveId)) continue;
                hashmap.put(tStVmEnergySave.getEnergySaveId(), tStVmEnergySave);
            }
        }
        return hashmap;
    }

}
