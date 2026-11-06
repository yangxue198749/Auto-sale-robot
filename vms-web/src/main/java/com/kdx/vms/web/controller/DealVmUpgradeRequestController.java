package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.kdx.vms.commons.util.Md5Util;
import com.kdx.vms.commons.util.PropertiesLoader;
import com.kdx.vms.commons.util.StringUtil;
import com.kdx.vms.commons.util.WebUtils;
import com.kdx.vms.model.bean.TMtUpgradePlan;
import com.kdx.vms.model.bean.TMtVm;
import com.kdx.vms.model.context.ObjectContextFactory;
import com.kdx.vms.service.IDealVmUpgradeRequestService;
import com.kdx.vms.service.IMtVmService;
import com.kdx.vms.utils.VmVersion;
import org.apache.cayenne.ObjectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by yangshuxi on 2017/6/21.
 */
@Controller
public class DealVmUpgradeRequestController {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    // （重要）不可用此context做提交和改变对象的操作！！！
    private static ObjectContext context = ObjectContextFactory.getInstance().newObjectContext();
    private static String urLPrefix = (String) PropertiesLoader.map.get("imgUrlPrefix");
    @Resource
    private IMtVmService mtVmService;
    @Resource
    private IDealVmUpgradeRequestService upgradeService;

    @RequestMapping(value = "api/upgrade", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public String upgrade(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Content-type", "text/html;charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(600);
            String json = getStreamParam(request.getInputStream(), WebUtils.DEFAULT_CHARSET);
            LOGGER.info("received upgrade request message：" + json);
            if (StringUtil.isEmpty(json)) return null;

            RequestMsg msg = JSON.parseObject(json, RequestMsg.class);
            if (null == msg) return null;
            String innerCode = msg.getInnerCode();
            String key = msg.getKey();
            ReqVersion[] versions = msg.getVersions();
            if (StringUtil.isEmpty(innerCode) || StringUtil.isEmpty(key) || null == versions) return null;
            TMtVm mtVm = mtVmService.findMtVmByInnerCode(context, innerCode);
            if (null == mtVm) return null;
            String unique = mtVm.getUniqueCode();
            String serverMd5 = Md5Util.md5(unique + innerCode);
            if (!key.equalsIgnoreCase(serverMd5)) {
                LOGGER.error("[upgrade] valid failed: uniqueCode:[" + unique + "], serverMd5:[" + serverMd5 + "], key:[" + key + "]");
                return null;
            }
            //获取上报版本
            String reqVmBoxVersion = "";
            String reqCommunicationVersion = "";
            String reqVendOutVersion = "";

            for (ReqVersion version : versions) {
                if (version.getType().equals(VmVersion.VM_BOX.getType())) {
                    reqVmBoxVersion = version.getVersion();
                } else if (version.getType().equals(VmVersion.COMMUNICATION_BOARD.getType())) {
                    reqCommunicationVersion = version.getVersion();
                } else if (version.getType().equals(VmVersion.VEND_OUT_BOARD.getType())) {
                    reqVendOutVersion = version.getVersion();
                }
            }
            //获取配置最高的版本
            TMtUpgradePlan vmBoxPlan = upgradeService.findHighestVersion(mtVm, reqVmBoxVersion, VmVersion.VM_BOX);
            TMtUpgradePlan communicationPlan = upgradeService.findHighestVersion(mtVm, reqCommunicationVersion, VmVersion.COMMUNICATION_BOARD);
            TMtUpgradePlan vendOutPlan = upgradeService.findHighestVersion(mtVm, reqVendOutVersion, VmVersion.VEND_OUT_BOARD);
            if (null == vmBoxPlan && null == communicationPlan && null == vendOutPlan) {
                LOGGER.info(innerCode + " all software's version are equals or higher than setting version,need not to upgrade");
                return null;
            }
            response.setStatus(200);
            ResponseMsg responseMsg = new ResponseMsg();
            responseMsg.setInnerCode(innerCode);
            if (null != vmBoxPlan) {
                //增量更新，获取给相同机型下发过的历次升级计划，从小到大排序，获取第一个大于当前版本的升级计划
                TMtUpgradePlan vmBoxNextPlan = upgradeService.findNextVmBoxVersion(mtVm, reqVmBoxVersion);
                RespVersion version = new RespVersion(vmBoxNextPlan, VmVersion.VM_BOX);
                responseMsg.addVersion(version);
            }
            if (null != communicationPlan) {
                RespVersion version = new RespVersion(communicationPlan, VmVersion.COMMUNICATION_BOARD);
                responseMsg.addVersion(version);
            }
            if (null != vendOutPlan) {
                RespVersion version = new RespVersion(vendOutPlan, VmVersion.VEND_OUT_BOARD);
                responseMsg.addVersion(version);
            }
            LOGGER.info(innerCode + " issue upgrade message" + JSON.toJSONString(responseMsg));
            return JSON.toJSONString(responseMsg);
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }


    @RequestMapping(value = "api/downloadResult", method = RequestMethod.POST)
    @ResponseBody
    public String downloadResult(HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setStatus(600);
            String json = getStreamParam(request.getInputStream(), WebUtils.DEFAULT_CHARSET);
            LOGGER.info("received downloaded result message：" + json);
            if (StringUtil.isEmpty(json)) return null;
            DownloadResultMsg msg = JSON.parseObject(json, DownloadResultMsg.class);
            if (null == msg) return null;
            String innerCode = msg.getInnerCode();
            String key = msg.getKey();
            Integer type = msg.getType();
            String version = msg.getVersion();
            if (StringUtil.isEmpty(innerCode) || StringUtil.isEmpty(key) || null == type || StringUtil.isEmpty(version))
                return null;
            TMtVm mtVm = mtVmService.findMtVmByInnerCode(ObjectContextFactory.getInstance().newObjectContext(), innerCode);
            if (null == mtVm) return null;
            String unique = mtVm.getUniqueCode();
            String serverMd5 = Md5Util.md5(unique + innerCode);
            if (!key.equalsIgnoreCase(serverMd5)) {
                LOGGER.error("[downloadResult] valid failed: uniqueCode:[" + unique + "], serverMd5:[" + serverMd5 + "], key:[" + key + "]");
                return null;
            }
//            VmVersion vmVersion = null;
//            if (type.equals(VmVersion.VM_BOX.getType())) {
//                vmVersion = VmVersion.VM_BOX;
//            } else if (type.equals(VmVersion.COMMUNICATION_BOARD.getType())) {
//                vmVersion = VmVersion.COMMUNICATION_BOARD;
//            } else if (type.equals(VmVersion.VEND_OUT_BOARD.getType())) {
//                vmVersion = VmVersion.VEND_OUT_BOARD;
//            }
            String result = upgradeService.updateDownloadedVersion(mtVm, version, type);
            if (StringUtil.isEmpty(result)) {
                response.setStatus(200);
            } else {
                LOGGER.error(result);
            }
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return null;
    }

    private static class DownloadResultMsg {
        private String innerCode;
        private String key;
        private Integer type;
        private String version;

        public String getInnerCode() {
            return innerCode;
        }

        public String getKey() {
            return key;
        }

        public Integer getType() {
            return type;
        }

        public String getVersion() {
            return version;
        }

        public void setInnerCode(String innerCode) {
            this.innerCode = innerCode;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setType(Integer type) {
            this.type = type;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }


    private static class RequestMsg {
        private String innerCode;
        private String key;
        private ReqVersion[] versions;

        public String getInnerCode() {
            return innerCode;
        }

        public String getKey() {
            return key;
        }

        public ReqVersion[] getVersions() {
            return versions;
        }

        public void setInnerCode(String innerCode) {
            this.innerCode = innerCode;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public void setVersions(ReqVersion[] versions) {
            this.versions = versions;
        }


    }

    private static class ReqVersion {
        private Integer type;
        private String version;

        public Integer getType() {
            return type;
        }

        public String getVersion() {
            return version;
        }

        public ReqVersion setType(Integer type) {
            this.type = type;
            return this;
        }

        public ReqVersion setVersion(String version) {
            this.version = version;
            return this;
        }


    }

    private static class ResponseMsg {
        private String innerCode;
        private List<RespVersion> versions = Lists.newArrayList();

        public void addVersion(RespVersion version) {
            versions.add(version);
        }

        public void setInnerCode(String innerCode) {
            this.innerCode = innerCode;
        }

        public String getInnerCode() {
            return innerCode;
        }

        public List<RespVersion> getVersions() {
            return versions;
        }
    }


    private static class RespVersion {
        private Integer type;
        private int level;
        private String version;
        private String url;
        private String md5;

        public RespVersion(TMtUpgradePlan plan, VmVersion type) {
            this.type = type.getType();
            this.level = type.getLevel();
            this.version = plan.getUpgradePackage().getPackageVersion();
            this.url = urLPrefix + plan.getUpgradePackage().getResource().getResourceUrl();
            this.md5 = plan.getUpgradePackage().getResource().getResourceMd5();
        }

        public Integer getType() {
            return type;
        }

        public RespVersion setType(Integer type) {
            this.type = type;
            return this;
        }

        public int getLevel() {
            return level;
        }

        public RespVersion setLevel(int level) {
            this.level = level;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public RespVersion setVersion(String version) {
            this.version = version;
            return this;
        }

        public String getUrl() {
            return url;
        }

        public RespVersion setUrl(String url) {
            this.url = url;
            return this;
        }

        public String getMd5() {
            return md5;
        }

        public RespVersion setMd5(String md5) {
            this.md5 = md5;
            return this;
        }
    }


    private String getStreamParam(InputStream in, String encoding) throws IOException {
        BufferedReader rd = null;
        String line;
        StringBuilder stringBuffer = new StringBuilder();
        try {
            rd = new BufferedReader(new InputStreamReader(in, encoding));
            while ((line = rd.readLine()) != null) {
                stringBuffer.append(line);
            }
        } finally {
            if (in != null) in.close();
            if (rd != null) rd.close();
        }
        return stringBuffer.toString();
    }
}
