package com.kdx.vms.model.custom.beneficiary;

/**
 * Created by yangshuxi on 2018/2/26.
 */
public class WxPayInfo extends PayInfo {
    private String appId;
    private String appSecret;
    private String mchId;
    private String mchPwd;
    private String certificate;

    public String getAppId() {
        return appId;
    }

    public WxPayInfo setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public WxPayInfo setAppSecret(String appSecret) {
        this.appSecret = appSecret;
        return this;
    }

    public String getMchId() {
        return mchId;
    }

    public WxPayInfo setMchId(String mchId) {
        this.mchId = mchId;
        return this;
    }

    public String getMchPwd() {
        return mchPwd;
    }

    public WxPayInfo setMchPwd(String mchPwd) {
        this.mchPwd = mchPwd;
        return this;
    }

    public String getCertificate() {
        return certificate;
    }

    public WxPayInfo setCertificate(String certificate) {
        this.certificate = certificate;
        return this;
    }

    @Override
    public String showId() {
        String showId = "";
        for (int i = 0; i < appId.length(); i++) {
            if (i < 4 || appId.length() - i < 5) {
                showId += appId.charAt(i);
            } else {
                showId += "*";
            }
        }
        return showId;
    }
}
