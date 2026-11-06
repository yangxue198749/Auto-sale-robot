package com.kdx.vms.utils;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.apache.cayenne.query.SortOrder;

import java.util.List;
import java.util.stream.Collectors;

public class InnerCodeFactory {

    private static ObjectContext objectContext = ObjectContextFactory.getInstance().newObjectContext();

    public synchronized static String getInnerCode(String areaCode) {
        String innerCode = "";
        Expression qualifier = ExpressionFactory.likeExp(TMtVm.INNER_CODE_PROPERTY, areaCode + "%");
        qualifier = qualifier.andExp(ExpressionFactory.noMatchExp(TMtVm.VM_TYPE_PROPERTY, TMtVmModel.COMBINATORIAL_EXTENTION));
        SelectQuery select = new SelectQuery(TMtVm.class, qualifier);
        select.setPageSize(1);
        select.addOrdering(TMtVm.INNER_CODE_PROPERTY, SortOrder.DESCENDING);
        List<TMtVm> list = objectContext.performQuery(select);
        if (null != list && list.size() > 0) {
            String maxInnerCode = list.get(0).getInnerCode();
            int length = maxInnerCode.length();

            maxInnerCode = maxInnerCode.substring(areaCode.length());

            int innerCodeInt = Integer.parseInt(maxInnerCode);

            innerCodeInt += 1;
            String currInnerCode = innerCodeInt + "";
            for (int i = currInnerCode.length(); i < length - areaCode.length(); i++) {
                currInnerCode = "0" + currInnerCode;
            }
            innerCode = areaCode + currInnerCode;
        } else {
            innerCode = areaCode + "00001";
        }
        return innerCode;
    }

    /**
     * 获取扩展编号，从2开始，如果中间有撤机的副机，则新扩展的机器占用被撤机器的位置
     *
     * @param mtVm
     * @return
     */
    public synchronized static Integer getExtentionNum(TMtVm mtVm) {
        List<TMtVm> list = mtVm.getChildrenVm();
        List<Integer> numList = list.stream().filter(vm -> vm.getDeleteMark() == 0).map(TMtVm::getVmExtentionNum).sorted().collect(Collectors.toList());
        for (int i = 0; i < numList.size(); i++) {
            if (numList.get(i) - i - 2 > 0) {
                return i + 2;
            }
        }
        return numList.size() + 2;
    }
}
