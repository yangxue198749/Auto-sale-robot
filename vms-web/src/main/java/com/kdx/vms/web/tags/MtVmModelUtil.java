package com.kdx.vms.web.tags;

import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;

import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.context.ObjectContextFactory;

/**
 * 售货机型号
 * @author zhangguodong
 *
 */
public class MtVmModelUtil {
	private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

	public static String getVmModelName(Integer id) {
        if (null == id || id <= 0) return null;
        return Cayenne.objectForPK(context, TMtVmModel.class, id).getModelName();
    }
    public static Integer getVmShortcuts(Integer id) {
        if (null == id || id <= 0) return null;
        return Cayenne.objectForPK(context, TMtVmModel.class, id).getShortcutsQty();
    }
    public static Integer getVmTemperatureHandlerMode(Integer id) {
        if (null == id || id <= 0) return null;
        return Cayenne.objectForPK(context, TMtVmModel.class, id).getTemperatureHandlerMode();
    }
    public static Integer getVmScreenHave(Integer id) {
        if (null == id || id <= 0) return null;
        return Cayenne.objectForPK(context, TMtVmModel.class, id).getScreenHave();
    }
    public static Boolean getVmCombinatorialPattern(Integer id) {
        Boolean flag = false;
        if (null == id || id <= 0) return flag;
        Integer patternId = Cayenne.objectForPK(context, TMtVmModel.class, id).getCombinatorialPattern();
        switch (patternId){
            case TMtVmModel.COMBINATORIAL_SINGLE:
                flag = true;
                break;
            case TMtVmModel.COMBINATORIAL_MAIN:
                flag = true;
                break;
            case TMtVmModel.COMBINATORIAL_EXTENTION:
                flag = false;
                break;
        }
        return flag;
    }
}
