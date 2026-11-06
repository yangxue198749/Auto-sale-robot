package com.kdx.vms.service.impl;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.model.bean.TMtSku;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmSkuSort;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.ShelfConfigCache;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.service.IMtVmShelfService;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2018/2/7.
 */

@Service
public class MtVmShelfServiceImpl implements IMtVmShelfService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private MyJedisCluster myJedisCluster;

    @Override
    public ShelfConfigCache getShelfCache(TSysUser sysUser, TMtVm mtVm) {
        try {
            String cache = myJedisCluster.hget("shelf_cache_" + mtVm.getId(), sysUser.getId() + "");
            logger.info(sysUser.getUserAccount() + "获取列表页缓存，机器编号" + mtVm.getInnerCode() + "缓存内容" + cache);
            if (CheckUtils.check(cache)) {
                return JSON.parseObject(cache, ShelfConfigCache.class);
            } else {
                ShelfConfigCache shelfConfigCache = ShelfConfigCache.newInstance(mtVm.getId(), sysUser.getId());
                List<Integer> data = mtVm.getSkuSort()
                        .stream()
                        .sorted((o1, o2) -> o1.getSort() - o2.getSort())
                        .map(TMtVmSkuSort::getSkuId)
                        .collect(Collectors.toList());
                shelfConfigCache.setData(data);
                myJedisCluster.hset("shelf_cache_" + mtVm.getId(), sysUser.getId() + "", shelfConfigCache.toJson());
                return shelfConfigCache;
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    @Override
    public void deleteShelfCache(Integer vmId) {
        try {
            myJedisCluster.delKey("shelf_cache_" + vmId);
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void updateShelfCache(ShelfConfigCache cache) {
        try {
            myJedisCluster.hset("shelf_cache_" + cache.getVmId(), cache.getUserId() + "", cache.toJson());
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    @Override
    public String makeShelfTable(TMtVm mtVm, ShelfConfigCache cache) {
        StringBuilder builder = new StringBuilder();
        builder.append("<input type=\"hidden\" name=\"vmId\" value=\"").append(mtVm.getId()).append("\">");
        try {
            List<Integer> skuIdList = cache.getData();
            if (CheckUtils.check(skuIdList)) {
                builder.append("<ul>");
                for (int i = 0; i < skuIdList.size(); i++) {
                    if (i % 15 == 0 && i != skuIdList.size()) {
                        int page = i / 15 + 1;
                        builder.append("<div class='sortPageDivTitle clearfix'> 第").append(page).append("页</div>");
                    }
                    TMtSku sku = Cayenne.objectForPK(mtVm.getObjectContext(), TMtSku.class, skuIdList.get(i));
                    builder.append("<li>");
                    builder.append("<section>");
                    builder.append("<img src='").append(sku.getImgUrl()).append("'>");
                    builder.append("</section>");
                    builder.append("<i>");
                    builder.append("</i>");
                    builder.append("<p><a href=\"#\" data-toggle=\"tooltip\" data-placement=\"top\" title=\"").append(sku.getSkuName()).append(sku.getSkuUnit()).append("\" data-original-title=\"").append(sku.getSkuName()).append(sku.getSkuUnit()).append("\"><span>").append(sku.getSkuName()).append("</span></a></p>");
                    builder.append("<input type='hidden' value='").append(i).append("'>");
                    builder.append("</li>");
                }
            }
            builder.append("</ul>");
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return builder.toString();
    }

    @Override
    public void saveShelf(TMtVm mtVm, ShelfConfigCache cache) {
        try {
            ObjectContext context = mtVm.getObjectContext();
            context.deleteObjects(mtVm.getSkuSort());
            List<Integer> skuIdList = cache.getData();
            for (int i = 0; i < skuIdList.size(); i++) {
                TMtVmSkuSort sort = context.newObject(TMtVmSkuSort.class);
                sort.setCtor(cache.getUserId());
                sort.setCtime(new Date());
                sort.setMtVm(mtVm);
                sort.setSkuId(skuIdList.get(i));
                sort.setSort(i + 1);
            }
            context.commitChanges();
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            mtVm.getObjectContext().rollbackChanges();
            throw e;
        }
    }
}
