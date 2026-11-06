package com.kdx.vms.web.tags;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtBusinessDistrict;
import com.kdx.vms.model.bean.TSysDictionary;
import com.kdx.vms.model.bean.TSysTypeInfo;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.orm.cache.DictCache;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;

import java.util.List;

/**
 * Created by yangshuxi on 2016/12/5.
 */
public class DictUtils {
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    public static String getDesc (String dictValue,String dictType){
        if(dictType.equals("discountType")){
            if(dictValue.equals("1"))return "百分比";
            else return "绝对值";
        }
        return "";
    }
    public static String getSymbol (String dictValue,String dictType){
        if(dictType.equals("discountType")){
            if(dictValue.equals("1"))return "%";
            else return "分";
        }
        return "";
    }

    public static String getDictNameById(Integer dictId){
    	if(dictId == null) return "";
        TSysDictionary sysDictionary= Cayenne.objectForPK(context,TSysDictionary.class,dictId);
        if(null==sysDictionary)return "";
        return sysDictionary.getDictionaryName();
    }
    public static String getDescByCode(String dictCode,String typeCode,String defaultDesc){
        Expression qualifier = ExpressionFactory.matchExp(TSysDictionary.DICTIONARY_CODE_PROPERTY,dictCode);
        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TSysDictionary.TYPE_INFO_PROPERTY+"."+ TSysTypeInfo.TYPE_CODE_PROPERTY,typeCode));
        SelectQuery select = new SelectQuery(TSysDictionary.class, qualifier);
        List<TSysDictionary> list=context.performQuery(select);
        if(list==null||list.isEmpty()){
            return defaultDesc;
        }
        return list.get(0).getDictionaryName();
    }

    public static String getIdByCode(String dictCode,String typeCode,String defaultId){
        Expression qualifier = ExpressionFactory.matchExp(TSysDictionary.DICTIONARY_CODE_PROPERTY,dictCode);
        qualifier = qualifier.andExp(ExpressionFactory.matchExp(TSysDictionary.TYPE_INFO_PROPERTY+"."+ TSysTypeInfo.TYPE_CODE_PROPERTY,typeCode));
        SelectQuery select = new SelectQuery(TSysDictionary.class, qualifier);
        List<TSysDictionary> list=context.performQuery(select);
        if(list==null||list.isEmpty()){
            return defaultId;
        }
        return list.get(0).getId().toString();
    }

    public static String getNameByCode(String typeCode,String dictCode,String defaultName){
        TSysDictionary sysDictionary =  DictCache.getDictionaryByCode(typeCode,dictCode);
        if(null==sysDictionary|| StringUtil.isEmpty(sysDictionary.getDictionaryName()))return defaultName;
        return sysDictionary.getDictionaryName();
    }
}
