package com.kdx.vms.web.tags;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.IntegerUtils;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.*;
import com.kdx.vms.model.context.ObjectContextFactory;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.exp.Expression;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.cayenne.query.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by yangshuxi on 2016/12/6.
 */
public class SkuUtils {

    private static final Logger logger = LoggerFactory.getLogger(SkuUtils.class);

    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();

    public static List getSkuList(Integer skuTypeId, Integer skuTypeGroupId, Integer[] removeIds, String skuName, String skuCode, boolean loadAll) {
        List<Integer> removeList = null != removeIds ? Arrays.asList(removeIds) : new ArrayList<>();
        Stream<?> stream;
        if (loadAll) {
            stream = getAllSkuList().parallelStream();
        } else {
            stream = getLocalSkuList().parallelStream();
        }
        return stream
                .map(TMtSku.class::cast)
                .filter(mtSku -> !removeList.contains(mtSku.getId()))
                .filter(mtSku -> {
                    if (CheckUtils.check(skuTypeId)) {
                        return Optional.ofNullable(mtSku.getSkuType()).map(TMtSkuType::getId).map(id -> Objects.equals(id, skuTypeId)).orElse(false);
                    }
                    return true;
                })
                .filter(mtSku -> {
                    if (CheckUtils.check(skuTypeGroupId)) {
                        return Optional.ofNullable(mtSku.getSkuType()).map(TMtSkuType::getParent).map(TMtSkuType::getId).map(id -> Objects.equals(id, skuTypeGroupId)).orElse(false);
                    }
                    return true;
                })
                .filter(mtSku -> {
                    if (CheckUtils.check(skuName)) {
                        return Optional.of(mtSku).map(TMtSku::getSkuName).map(s -> s.contains(skuName)).orElse(false);
                    }
                    return true;
                })
                .filter(mtSku -> {
                    if (CheckUtils.check(skuCode)) {
                        return Optional.of(mtSku).map(TMtSku::getSkuCode).map(s -> s.contains(skuCode)).orElse(false);
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private static List getAllSkuList() {
        Expression qualifier = ExpressionFactory.expTrue();
        SelectQuery select = new SelectQuery(TMtSku.class, qualifier);
        ObjectContext userContext = TokenUtil.getToken().getObjectContext();
        return userContext.performQuery(select);
    }

    private static List getLocalSkuList() {
        try {
            ObjectContext userContext = TokenUtil.getToken().getObjectContext();
            TMtCompany company = TokenUtil.getToken().getSysUser().getCompany();
            if (null == company || null == company.getCompanyLevel()) return new ArrayList();
            switch (company.getCompanyLevel()) {
                case TMtCompany.LEVEL_ONE:
                    return getAllSkuList();
                case TMtCompany.LEVEL_TWO:
                    return getAllSkuList();
                case TMtCompany.LEVEL_THREE:
                    Expression qualifier = ExpressionFactory.matchExp(TMtSkuCompany.COMPANY_PROPERTY, company);
                    SelectQuery select = new SelectQuery(TMtSkuCompany.class, qualifier);
                    select.addPrefetch(TMtSkuCompany.SKU_PROPERTY);
                    select.addPrefetch(TMtSkuCompany.SKU_PROPERTY + "." + TMtSku.SKU_IMG_PROPERTY);
                    List list = userContext.performQuery(select);
                    if (!list.isEmpty()) {
                        List<TMtSku> skuList = new ArrayList<>();
                        for (Object mtSkuCompany : list) {
                            skuList.add(((TMtSkuCompany) mtSkuCompany).getSku());
                        }
                        return skuList;
                    }
                    return new ArrayList();
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            return new ArrayList();
        }
        return new ArrayList();
    }

    public static String getTypeJson() {
        JSONArray array = new JSONArray();
        List<TMtSkuType> list = context.performQuery(new SelectQuery(TMtSkuType.class));
        for (TMtSkuType mtSkuType : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", Cayenne.intPKForObject(mtSkuType));
            jsonObject.put("name", mtSkuType.getTypeName());
            jsonObject.put("pid", mtSkuType.getParent() == null ? 0 : Cayenne.intPKForObject(mtSkuType.getParent()));
            array.add(jsonObject);
        }
        return array.toString();
    }

    public static TMtSku getSkuById(Integer skuId) {
        try {
            ObjectContext userContext = TokenUtil.getToken().getObjectContext();
            if (IntegerUtils.isPositiveInteger(skuId)) return Cayenne.objectForPK(userContext, TMtSku.class, skuId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    public static String getSkuImgById(Integer skuId) {
        try {
            ObjectContext userContext = TokenUtil.getToken().getObjectContext();
            if (IntegerUtils.isPositiveInteger(skuId)) {
                Expression expression = ExpressionFactory.matchExp(TMtSkuImg.SKU_PROPERTY, skuId);
                expression = expression.andExp(ExpressionFactory.matchExp(TMtSkuImg.IMG_INDEX_PROPERTY, TMtSkuImg.IMG_DESC_URL));
                SelectQuery query = new SelectQuery(TMtSkuImg.class, expression);
                List<TMtSkuImg> list = userContext.performQuery(query);
                if(null != list && list.size() > 0){
                    String imgUrl = list.get(0).getImgUrl();
                    if (CheckUtils.check(imgUrl)) {
                        if (CheckUtils.check(imgUrl)) {
                            return PropertiesLoader.map.get("imgUrlPrefix") + imgUrl;
                        }
                    }
                    return PropertiesLoader.map.get("imgUrlPrefix") + "default.png";
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

}