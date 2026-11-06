package com.kdx.vms.schedule;

import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtExport;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.dao.MtExportDao;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2017/12/6.
 */
@Service
public class ExportReportSchedule {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MtExportDao exportDao;

    public void clearEarlyReport() {
        logger.info("clear early report and stock");
        try {
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            List<TMtExport> list = exportDao.findEarlyExportData(context).stream().map(TMtExport.class::cast).collect(Collectors.toList());
            list.forEach(tMtExport -> {
                if (AliyunOssUtils.delete(tMtExport.getAddress())) {
                    context.deleteObject(tMtExport);
                }
            });
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public void clearEarlyStock() {
        logger.info("clear early auto stock");
        try {
            ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
            List<TMtExport> list = exportDao.findEarlyStockData(context).stream().map(TMtExport.class::cast).collect(Collectors.toList());
            list.forEach(tMtExport -> {
                if (AliyunOssUtils.delete(tMtExport.getAddress())) {
                    context.deleteObject(tMtExport);
                }
            });
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }

    }
}
