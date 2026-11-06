package com.kdx.vms.service.impl;

import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.bean.TMtVmModel;
import com.kdx.vms.model.bean.TOrderDetail;
import com.kdx.vms.orm.dao.OrderDetailDao;
import com.kdx.vms.redis.MyJedisCluster;
import com.kdx.vms.service.IExtractCodeService;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by yangshuxi on 2018/4/16.
 */
@Service
public class ExtractCodeServiceImpl implements IExtractCodeService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Resource
    private OrderDetailDao orderDetailDao;
    @Resource
    private MyJedisCluster myJedisCluster;

    @Override
    public void createExtractCode(TMtVm mtVm) {
        try {
            if (mtVm.getVmType() == TMtVmModel.COMBINATORIAL_EXTENTION) {
                mtVm = mtVm.getParentVm();
            }
            String key = "EXTRACT_CODE_" + mtVm.getInnerCode();
            Long size = myJedisCluster.llen(key);
            Long num = 3000 - size;
            List<String> list = myJedisCluster.lrange(key, 0, -1);
            Set<String> codeSet = orderDetailDao.findTodayOrder(mtVm)
                    .stream()
                    .map(TOrderDetail.class::cast)
                    .map(TOrderDetail::getExtractCode)
                    .collect(Collectors.toSet());
            int max = 999999;
            Set<String> passSet = new HashSet<>(Arrays.asList("111111", "222222", "333333", "444444", "555555", "666666", "777777", "888888", "999999", "123456", "654321", "123123", "321321"));
            codeSet.addAll(passSet);
            codeSet.addAll(list);
            Set<String> set = new HashSet<>();
            while (set.size() < num) {
                String extractCode = String.valueOf(RandomUtils.nextInt(100000, max));
                if (codeSet.add(extractCode)) {
                    set.add(extractCode);
                }
            }
            String[] array = set.toArray(new String[set.size()]);
            if (array.length > 0) {
                myJedisCluster.rpush(key, set.toArray(new String[set.size()]));
            }

        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }


}
