package com.kdx.vms.web.controller;

import com.alibaba.fastjson.JSON;
import com.kdx.vms.commons.util.oss.LocalFileStorage;
import com.kdx.vms.model.bean.TMtBeneficiaryInfo;
import com.kdx.vms.model.bean.TSysUser;
import com.kdx.vms.model.custom.beneficiary.WxPayInfo;
import com.kdx.vms.model.enumeration.PayType;
import com.kdx.vms.service.IMtBeneficiaryService;
import com.kdx.vms.web.oauth.SSOToken;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 私有 bucket 文件鉴权下载 REST API。
 *
 * 设计决策（安全取舍）：不提供"任意 key 读取"的通用端点——私有目录当前仅存
 * 微信支付 p12 证书（pay/wx/&lt;mchId&gt;.p12，收款人配置页上传），属支付域敏感文件。
 * 端点改为无参数窄接口：证书路径由服务端从当前用户**本公司**收款人记录推导
 * （IMtBeneficiaryService.findPayInfo 本身按用户公司过滤），天然携带公司数据范围，
 * 杜绝跨公司读取与路径探测面。
 *
 * 后续私有 bucket 若新增文件类型，应各建带数据范围校验的窄端点，而非放宽本端点。
 */
@Controller
@RequestMapping("/vms/api/files")
public class FilesApiController {

    private static final Logger logger = LoggerFactory.getLogger(FilesApiController.class);

    @Resource
    private IMtBeneficiaryService mtBeneficiaryService;

    private SSOToken currentToken() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) return null;
        Object principal = subject.getPrincipal();
        if (!(principal instanceof SSOToken)) return null;
        return (SSOToken) principal;
    }

    private static void requireAuth() {
        if (!SecurityUtils.getSubject().isAuthenticated()) {
            throw new UnauthorizedException("未登录");
        }
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String msg) { super(msg); }
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map<String, Object> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 401);
        err.put("message", ex.getMessage());
        return err;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, Object> handleNotFound(NotFoundException ex) {
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 404);
        err.put("message", ex.getMessage());
        return err;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception ex) {
        logger.error("Internal error", ex);
        Map<String, Object> err = new LinkedHashMap<>();
        err.put("code", 500);
        err.put("message", "服务器内部错误");
        return err;
    }

    /**
     * 下载本公司的微信支付 p12 证书（流式输出）。
     * 404 = 本公司未配置微信收款人或证书文件缺失。
     */
    @RequestMapping(value = "/private/payCert", method = RequestMethod.GET)
    public void payCert(HttpServletResponse response) throws Exception {
        requireAuth();
        SSOToken token = currentToken();
        if (token == null) throw new UnauthorizedException("用户信息获取失败");
        TSysUser user = token.getSysUser();

        TMtBeneficiaryInfo wxPay = mtBeneficiaryService.findPayInfo(user).get(PayType.WECHAT_PAY.getCode());
        if (wxPay == null || wxPay.getBeneficiaryInfo() == null) {
            throw new NotFoundException("本公司未配置微信支付收款人");
        }
        WxPayInfo info = JSON.parseObject(wxPay.getBeneficiaryInfo(), WxPayInfo.class);
        if (info == null || info.getCertificate() == null || info.getCertificate().trim().isEmpty()) {
            throw new NotFoundException("收款人记录未登记证书路径");
        }

        InputStream in = LocalFileStorage.read(LocalFileStorage.privateRoot(), info.getCertificate());
        if (in == null) {
            throw new NotFoundException("证书文件不存在");
        }

        String fileName = (info.getMchId() != null ? info.getMchId() : "wx-pay-cert") + ".p12";
        response.setContentType("application/x-pkcs12");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        response.setHeader("Cache-Control", "no-store");
        try {
            OutputStream out = response.getOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            out.flush();
        } finally {
            try { in.close(); } catch (Exception ignore) { }
        }
    }
}
