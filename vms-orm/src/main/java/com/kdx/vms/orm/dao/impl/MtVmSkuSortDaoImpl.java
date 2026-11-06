package com.kdx.vms.orm.dao.impl;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TMtVmSkuSort;
import com.kdx.vms.orm.dao.MtVmSkuSortDao;
import com.kdx.vms.orm.utils.ExpressionUtils;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.query.SelectQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by yangshuxi on 2018/3/29.
 */
@Repository
public class MtVmSkuSortDaoImpl implements MtVmSkuSortDao {
    @Override
    public List<?> findSkuSort(TMtVm mtVm) {
        if (null != mtVm.getVmType() && mtVm.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
            mtVm = mtVm.getParentVm();
        }
        Expression expression = ExpressionUtils.fillMatchQualifier(null,TMtVmSkuSort.MT_VM_PROPERTY,mtVm);
        return mtVm.getObjectContext().performQuery(new SelectQuery(TMtVmSkuSort.class,expression));
    }
}
