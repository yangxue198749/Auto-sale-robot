package com.kdx.vms.orm.cache;

import com.kdx.vms.commons.DoubleMap;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangshuxi on 2017/3/10.
 */
public class DictCache {
    private static final Logger logger = LoggerFactory.getLogger(DictCache.class);
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private static List dictList = null;
    private static List dictList1 = null;
    private static Map<String, TSysTypeInfo> dictMap = new HashMap<>();
    private static DoubleMap<String,String,TSysDictionary> dictMap1 = new DoubleMap<>();

    static {
        try {
            SelectQuery select = new SelectQuery(TSysTypeInfo.class);
            select.addPrefetch(TSysTypeInfo.DICTIONARY_PROPERTY);
            dictList = context.performQuery(select);
            for (Object sysTypeInfo : dictList) {
                dictMap.put(((TSysTypeInfo) sysTypeInfo).getTypeCode(), (TSysTypeInfo) sysTypeInfo);
            }

            SelectQuery select1 = new SelectQuery(TSysDictionary.class);
            dictList1 = context.performQuery(select1);
            for (Object sysDictionary : dictList1) {

                dictMap1.put(((TSysDictionary) sysDictionary).getTypeInfo().getTypeCode(),((TSysDictionary) sysDictionary).getDictionaryCode(), (TSysDictionary) sysDictionary);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static void refreshDictCache() {
        try {
            SelectQuery select = new SelectQuery(TSysTypeInfo.class);
            select.addPrefetch(TSysTypeInfo.DICTIONARY_PROPERTY);
            dictList = context.performQuery(select);
            dictMap.clear();
            for (Object sysTypeInfo : dictList) {
                dictMap.put(((TSysTypeInfo) sysTypeInfo).getTypeCode(), (TSysTypeInfo) sysTypeInfo);
            }

            SelectQuery select1 = new SelectQuery(TSysDictionary.class);
            dictList1 = context.performQuery(select1);
            dictMap1.clear();
            for (Object sysDictionary : dictList1) {
                dictMap1.put(((TSysDictionary) sysDictionary).getTypeInfo().getTypeCode(),((TSysDictionary) sysDictionary).getDictionaryCode(), (TSysDictionary) sysDictionary);
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    public static List getDictList() {
        return dictList;
    }

    public static Map<String, TSysTypeInfo> getDictMap() {
        return dictMap;
    }

    public static TSysTypeInfo getDictByCode(String code) {
        return dictMap.get(code);
    }

    public static TSysDictionary getDictionaryByCode(String typeCode,String dictCode) {
        return dictMap1.get(typeCode,dictCode);
    }


}
