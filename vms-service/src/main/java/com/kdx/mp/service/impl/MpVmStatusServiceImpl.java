package com.kdx.mp.service.impl;

import com.kdx.mp.service.IMpVmStatusService;
import com.kdx.vms.commons.util.DateUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.custom.VmStockInfo;
import com.kdx.vms.orm.dao.MtVmDao;
import com.kdx.vms.orm.dao.VmStatusDao;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yangshuxi on 2016/12/28.
 */
@Service
public class MpVmStatusServiceImpl implements IMpVmStatusService {
    private final Logger logger = (Logger) LoggerFactory.getLogger(getClass());
    @Autowired
    private MtVmDao mtVmDao;
    @Autowired
    private VmStatusDao vmStatusDao;

    @Override
    public List findVmList(TSysUser user,String innerCode) {
        return mtVmDao.findOnSaleVm(user, innerCode);
    }

    @Override
    public Map<String, String> getFaultTime(ObjectContext context, TMtVm mtVm) {
        Map<String, String> resMap = new HashMap<>();
        resMap.put("dateTime", "");
        resMap.put("passTime", "");
        if (mtVm.getVmcStatus().equals("0") || mtVm.getVmcStatus().equals("3")) {
            return resMap;
        }
        TVmStatusHis vmStatusHis = vmStatusDao.findLastNormalHis(context, mtVm.getId());
        if (null == vmStatusHis) {
            return resMap;
        }
        Date time = vmStatusHis.getCtime();
        resMap.put("dateTime", DateUtils.getTime2(time));
        long time1 = time.getTime();
        long time2 = System.currentTimeMillis();
        String passTime = DateUtils.formatDateTime(time2 - time1);
        resMap.put("passTime", passTime);
        return resMap;
    }

    @Override
    public List<VmStockInfo> findStockList(ObjectContext context, TMtVm mtVm) {
        List<VmStockInfo> stockInfoList = new ArrayList<>();
        List<TMtVmSkuSort> sortList = mtVm.getSkuSort();
        List<TMtVmChannel> list = mtVm.getChannelInfo();
        for (TMtVmSkuSort mtVmSkuSort : sortList) {
            if (!IntegerUtils.isPositiveInteger(mtVmSkuSort.getSkuId())) continue;
            TMtSku sku = Cayenne.objectForPK(context, TMtSku.class, mtVmSkuSort.getSkuId());
            VmStockInfo vmStockInfo = filterBySku(list, sku);
            if (null != vmStockInfo) stockInfoList.add(vmStockInfo);
        }
        return stockInfoList;
    }

    private VmStockInfo filterBySku(List<TMtVmChannel> list, TMtSku sku) {
        try {
            if (null == list || null == sku) return null;
            Expression expression = ExpressionFactory.matchExp(TMtVmChannel.SKU_PROPERTY, sku);
            List<TMtVmChannel> mtVmChannelList = expression.filterObjects(list);
            if (null == mtVmChannelList || mtVmChannelList.size() == 0) return null;
            VmStockInfo vmStockInfo = VmStockInfo.newInstance();
            for (TMtVmChannel mtVmChannel : mtVmChannelList) {
                if (null == vmStockInfo.getSkuId())
                    vmStockInfo.setSkuId(sku.getId());
                if (null == vmStockInfo.getName())
                    vmStockInfo.setName(sku.getSkuName());
                vmStockInfo.addCurrCount(mtVmChannel.getCurrNum());
                vmStockInfo.addMaxCount(mtVmChannel.getTempMaxCapacity());
            }
            return vmStockInfo;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }
}
