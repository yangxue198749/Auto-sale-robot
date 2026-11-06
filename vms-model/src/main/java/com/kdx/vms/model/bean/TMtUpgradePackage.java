package com.kdx.vms.model.bean;

import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.model.bean.auto._TMtUpgradePackage;
import org.apache.cayenne.Cayenne;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TMtUpgradePackage extends _TMtUpgradePackage {

    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public Integer getId() {
        return Cayenne.intPKForObject(this);
    }

    /** vmbox */
    public final static Integer VMBOX_TYPE = 1;
    /** 通讯板 */
    public final static Integer COMMUNICATION_BOARD_TYPE = 2;
    /** 出货板 */
    public final static Integer DELIVERY_BOARD_TYPE = 3;
    /** 禁用 */
    public final static Integer DISABLE = 0;
    /** 启用 */
    public final static Integer ENABLE  = 1;

    public String getTypeHtml() {
        Integer type = getPackageType();
        if (VMBOX_TYPE.equals(type)) return "vmBox";
        if (COMMUNICATION_BOARD_TYPE.equals(type)) return "通讯板";
        if (DELIVERY_BOARD_TYPE.equals(type)) return "出货版";
        return "";
    }

    public String getDownloadUrlHtml() {
        try {
            String html = "<a href=\"" + PropertiesLoader.map.get("imgUrlPrefix") + getResource().getResourceUrl() + "\" target=\"_blank\" title=\"下载\">下载</a>";
            LOGGER.info("html:" + html);
            return html;
        } catch (Exception e) {
            LOGGER.info(e.getLocalizedMessage());
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return "";
    }

    public String getStatusHtml() {
        if (ENABLE.equals(getDisable())) {
            return "<span class=\"icon_state green StatusReview\">启用</span>";
        } else {
            return "<span class=\"icon_state red StatusReview\">禁用</span>";
        }
    }

    public String getBtnName() {
        if (ENABLE.equals(getDisable())) {
            return "禁用";
        } else {
            return "启用";
        }
    }

}
