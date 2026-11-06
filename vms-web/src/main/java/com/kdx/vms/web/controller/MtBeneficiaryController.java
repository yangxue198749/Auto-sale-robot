package com.kdx.vms.web.controller;

import com.kdx.vms.commons.util.CheckUtils;
import com.kdx.vms.commons.util.WebUtils;
import com.kdx.vms.commons.util.oss.AliyunOssUtils;
import com.kdx.vms.model.bean.TMtBeneficiaryInfo;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.beneficiary.AliPayInfo;
import com.kdx.vms.model.custom.beneficiary.PayInfo;
import com.kdx.vms.model.custom.beneficiary.WxPayInfo;
import com.kdx.vms.model.enumeration.DeleteMark;
import com.kdx.vms.model.enumeration.PayType;
import com.kdx.vms.service.IMtBeneficiaryService;
import com.kdx.vms.web.tags.TokenUtil;
import org.apache.cayenne.Cayenne;
import org.apache.cayenne.ObjectId;
import org.apache.cayenne.query.ObjectIdQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by yangshuxi on 2018/2/26.
 */
@Controller
@RequestMapping(value = "mtBeneficiary")
public class MtBeneficiaryController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private IMtBeneficiaryService mtBeneficiaryService;

    @RequestMapping("list")
    public String list(HttpServletRequest request) {
        try {
            TSysUser user = TokenUtil.getToken().getSysUser();
            Map<Integer, TMtBeneficiaryInfo> map = mtBeneficiaryService.findPayInfo(user);
            TMtBeneficiaryInfo wxPay = map.get(PayType.WECHAT_PAY.getCode());
            TMtBeneficiaryInfo aliPay = map.get(PayType.ALI_PAY.getCode());
            Integer wxStatus = Optional.ofNullable(wxPay)
                    .map(TMtBeneficiaryInfo::getEnableMark)
                    .orElse(0);
            Integer aliStatus = Optional.ofNullable(aliPay)
                    .map(TMtBeneficiaryInfo::getEnableMark)
                    .orElse(0);
            String wxAppId = Optional.ofNullable(wxPay)
                    .map(TMtBeneficiaryInfo::getPayInfo)
                    .map(PayInfo::showId)
                    .orElse("");
            String aliAppId = Optional.ofNullable(aliPay)
                    .map(TMtBeneficiaryInfo::getPayInfo)
                    .map(PayInfo::showId)
                    .orElse("");
            request.setAttribute("wxStatus", wxStatus == 1 ? "已启用" : "未启用");
            request.setAttribute("aliStatus", aliStatus == 1 ? "已启用" : "未启用");
            request.setAttribute("wxAppId", wxAppId);
            request.setAttribute("aliAppId", aliAppId);
            if (CheckUtils.check(wxPay) && wxStatus == 0) {
                request.setAttribute("wxInfoId", wxPay.getId());
            }
            if (CheckUtils.check(aliPay) && aliStatus == 0) {
                request.setAttribute("aliInfoId", aliPay.getId());
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
        }
        return "beneficiary/mt_beneficiary_list";
    }

    @RequestMapping("wxHelp")
    public String wxHelp() {
        return "beneficiary/help/wx_help";
    }

    @RequestMapping("aliHelp")
    public String aliHelp() {
        return "beneficiary/help/ali_help";
    }

    @RequestMapping("addWxInfoDialog")
    public String addWxInfoDialog() {
        return "beneficiary/dialog/wx_add_dialog";
    }

    @RequestMapping("addAliInfoDialog")
    public String addAliInfoDialog() {
        return "beneficiary/dialog/ali_add_dialog";
    }

    @RequestMapping("saveWxInfo")
    @ResponseBody
    public Map<String, Object> saveWxInfo(HttpServletRequest request, String appId, String appSecret, String mchId, String mchPwd) {
        Map<String, Object> respMap = new HashMap<>();
        TSysUser user = TokenUtil.getToken().getSysUser();
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile cert = multipartRequest.getFile("cert");
            if (!CheckUtils.check(appId, appSecret, mchId, mchPwd, cert)) {
                respMap.put("type", "error");
                respMap.put("message", "参数填写不完整");
                return respMap;
            }
            Map<Integer, TMtBeneficiaryInfo> map = mtBeneficiaryService.findPayInfo(user);
            TMtBeneficiaryInfo wxPay = map.get(PayType.WECHAT_PAY.getCode());
            if (null != wxPay) {
                respMap.put("type", "error");
                respMap.put("message", "已配置不允许修改");
                return respMap;
            } else {
                String checkResult = mtBeneficiaryService.checkPayInfo(user);
                if(CheckUtils.check(checkResult)){
                    respMap.put("type", "error");
                    respMap.put("message", checkResult);
                    return respMap;
                }
                String fileType = cert.getOriginalFilename().substring(cert.getOriginalFilename().lastIndexOf("."));
                if (!fileType.equals(".p12")) {
                    respMap.put("type", "error");
                    respMap.put("message", "证书格式错误，请上传p12格式证书");
                    return respMap;
                }
                String fileName = "pay/wx/" + mchId + ".p12";
                if (AliyunOssUtils.uploadToPrivate(fileName, cert.getInputStream())) {
                    WxPayInfo wxPayInfo = new WxPayInfo();
                    wxPayInfo.setAppId(appId).setAppSecret(appSecret).setMchId(mchId).setCertificate(fileName).setMchPwd(mchPwd);
                    TMtBeneficiaryInfo beneficiaryInfo = user.getObjectContext().newObject(TMtBeneficiaryInfo.class);
                    beneficiaryInfo.setDeleteMark(DeleteMark.EXIST.getCode());
                    beneficiaryInfo.setEnableMark(0);
                    beneficiaryInfo.setBeneficiaryType(PayType.WECHAT_PAY.getCode());
                    beneficiaryInfo.setCompany(user.getCompany());
                    beneficiaryInfo.setBeneficiaryInfo(wxPayInfo.toJson());
                    beneficiaryInfo.setCtor(user.getId());
                    beneficiaryInfo.setCtime(new Date());
                    user.getObjectContext().commitChanges();
                    respMap.put("type", "success");
                    respMap.put("message", "配置成功");
                    return respMap;
                } else {
                    logger.error("上传文件到oss失败。");
                    respMap.put("type", "error");
                    respMap.put("message", "上传证书失败");
                    return respMap;
                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            user.getObjectContext().rollbackChanges();
        }
        respMap.put("type", "error");
        respMap.put("message", "系统内部错误");
        return respMap;
    }

    @RequestMapping("saveAliInfo")
    @ResponseBody
    public Map<String, Object> saveAliInfo(HttpServletRequest request, String appId, String pid) {
        Map<String, Object> respMap = new HashMap<>();
        TSysUser user = TokenUtil.getToken().getSysUser();
        try {
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            MultipartFile privateKey = multipartRequest.getFile("privateKey");
            MultipartFile publicKey = multipartRequest.getFile("publicKey");
            if (!CheckUtils.check(appId, pid, privateKey, publicKey)) {
                respMap.put("type", "error");
                respMap.put("message", "参数填写不完整");
                return respMap;
            }
            Map<Integer, TMtBeneficiaryInfo> map = mtBeneficiaryService.findPayInfo(user);
            TMtBeneficiaryInfo wxPay = map.get(PayType.ALI_PAY.getCode());
            if (null != wxPay) {
                respMap.put("type", "error");
                respMap.put("message", "已配置不允许修改");
                return respMap;
            } else {
                String checkResult = mtBeneficiaryService.checkPayInfo(user);
                if(CheckUtils.check(checkResult)){
                    respMap.put("type", "error");
                    respMap.put("message", checkResult);
                    return respMap;
                }
                /*String publicFileType = publicKey.getOriginalFilename().substring(publicKey.getOriginalFilename().lastIndexOf("."));
                String privateFileType = privateKey.getOriginalFilename().substring(privateKey.getOriginalFilename().lastIndexOf("."));
                if (!publicFileType.equals(".pem") || !privateFileType.equals(".pem")) {
                    respMap.put("type", "error");
                    respMap.put("message", "秘钥文件格式错误，请上传pem格式秘钥文件");
                    return respMap;
                }*/
                String publicKeyText = WebUtils.getStreamAsString(publicKey.getInputStream(), WebUtils.DEFAULT_CHARSET);
                String privateKeyText = WebUtils.getStreamAsString(privateKey.getInputStream(), WebUtils.DEFAULT_CHARSET);
                if (!CheckUtils.check(publicKeyText, privateKeyText)) {
                    respMap.put("type", "error");
                    respMap.put("message", "上传的秘钥文件有误，请上传正确的秘钥文件");
                    return respMap;
                }
                if(publicKeyText.length()>1000||privateKeyText.length()>1000){
                    respMap.put("type", "error");
                    respMap.put("message", "文件过大，请确认后重新上传");
                    return respMap;
                }
//                String publicFileName = "pay/ali/" + pid + "_public" + ".pem";
//                String privateFileName = "pay/ali/" + pid + "_private" + ".pem";
//                if (AliyunOssUtils.uploadToPrivate(publicFileName, publicKey.getInputStream()) && AliyunOssUtils.uploadToPrivate(privateFileName, privateKey.getInputStream())) {
                AliPayInfo aliPayInfo = new AliPayInfo();
//                    aliPayInfo.setAppId(appId).setSignType("RSA").setPid(pid).setPrivateKey(privateFileName).setPublicKey(publicFileName);
                aliPayInfo.setAppId(appId).setSignType("RSA").setPid(pid).setPrivateKey(privateKeyText).setPublicKey(publicKeyText);
                TMtBeneficiaryInfo beneficiaryInfo = user.getObjectContext().newObject(TMtBeneficiaryInfo.class);
                beneficiaryInfo.setDeleteMark(DeleteMark.EXIST.getCode());
                beneficiaryInfo.setEnableMark(0);
                beneficiaryInfo.setBeneficiaryType(PayType.ALI_PAY.getCode());
                beneficiaryInfo.setCompany(user.getCompany());
                beneficiaryInfo.setBeneficiaryInfo(aliPayInfo.toJson());
                beneficiaryInfo.setCtor(user.getId());
                beneficiaryInfo.setCtime(new Date());
                user.getObjectContext().commitChanges();
                respMap.put("type", "success");
                respMap.put("message", "配置成功");
                return respMap;
//                } else {
//                    logger.error("上传文件到oss失败。");
//                    respMap.put("type", "error");
//                    respMap.put("message", "上传秘钥文件失败");
//                    return respMap;
//                }
            }
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            user.getObjectContext().rollbackChanges();
        }
        respMap.put("type", "error");
        respMap.put("message", "系统内部错误");
        return respMap;
    }

    @RequestMapping("deletePayInfo")
    @ResponseBody
    public Map<String, Object> deletePayInfo(HttpServletRequest request, Integer payInfoId) {
        Map<String, Object> respMap = new HashMap<>();
        TSysUser user = TokenUtil.getToken().getSysUser();
        try {
            ObjectId id = new ObjectId(TMtBeneficiaryInfo.class.getSimpleName(), TMtBeneficiaryInfo.BENEFICIARY_ID_PK_COLUMN, payInfoId);
            // 表示刷新缓存，从表中从新获取数据
            ObjectIdQuery query = new ObjectIdQuery(id, false, ObjectIdQuery.CACHE_REFRESH);
            TMtBeneficiaryInfo beneficiaryInfo = (TMtBeneficiaryInfo) Cayenne.objectForQuery(user.getObjectContext(), query);
            if (!CheckUtils.check(beneficiaryInfo)) {
                respMap.put("type", "error");
                respMap.put("message", "收款信息不存在");
                return respMap;
            }
            beneficiaryInfo.setUtime(new Date());
            beneficiaryInfo.setUtor(user.getId());
            beneficiaryInfo.setDeleteMark(DeleteMark.DELETE.getCode());
            user.getObjectContext().commitChanges();
            respMap.put("type", "success");
            respMap.put("message", "删除成功");
            return respMap;
        } catch (Exception e) {
            logger.error(e.getLocalizedMessage(), e);
            user.getObjectContext().rollbackChanges();
        }
        respMap.put("type", "error");
        respMap.put("message", "系统内部错误");
        return respMap;
    }

}
