package com.kdx.vms.utils;

import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.model.bean.TMtUpgradePlan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;

/**
 * Created by yangshuxi on 2017/6/22.
 * 版本号从大到小排序，支持日期型版本号和三段式版本号
 */
public class VersionComparator implements Comparator<TMtUpgradePlan> {
    private static VersionComparator instance = new VersionComparator();
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public static VersionComparator getInstance() {
        return instance;
    }

    @Override
    public int compare(TMtUpgradePlan plan1, TMtUpgradePlan plan2) {
        try {
            String version1 = plan1.getUpgradePackage().getPackageVersion();
            String version2 = plan2.getUpgradePackage().getPackageVersion();
            return compareVersionNum(version1, version2);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return 0;
        }
    }


    public int compareVersionNum(String version1, String version2) {
        try {
            if (version1.contains(".") && version2.contains(".")) {
                String[] v1 = version1.split("\\.");
                String[] v2 = version2.split("\\.");
                if (v1.length != v2.length) return 0;
                for (int i = 0; i < v1.length; i++) {
                    int result = compareVersionNum(v1[i], v2[i]);
                    if (result != 0) return result;
                }
                return 0;
            } else {
                return StringUtil.getIntValue(version2, 0) - StringUtil.getIntValue(version1, 0);
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
            return 0;
        }
    }

    public static void main(String[] args) {
        System.out.println(VersionComparator.getInstance().compareVersionNum("123","23"));
    }

}
