package com.kdx.vms.web.init;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtVmDao;
import com.kdx.vms.orm.dao.impl.MtVmDaoImpl;
import org.apache.cayenne.Cayenne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/10/9.
 */
public class InitStockStatus {
    Logger LOGGER = LoggerFactory.getLogger(getClass());
    @Resource
    private MtVmDao mtVmDao;

    private void init() {
        LOGGER.info("init stock status.....");
        try {
            List<TMtVm> mtVmList = mtVmDao.findUnInitStockStatusList(ObjectContextFactory.getInstance().newObjectContext());
            if (null != mtVmList && !mtVmList.isEmpty()) {
                List<Integer> list = mtVmList.parallelStream().map(TMtVm::updateStockStatus).collect(Collectors.toList());
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }
}
