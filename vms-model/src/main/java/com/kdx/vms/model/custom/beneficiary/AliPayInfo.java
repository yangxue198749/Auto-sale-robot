package com.kdx.vms.model.custom.beneficiary;

/**
 * Created by yangshuxi on 2018/2/26.
 */
public class AliPayInfo extends PayInfo{
    private String pid;
    private String appId;
    private String signType;
    private String privateKey;
    private String publicKey;

    public String getPid() {
        return pid;
    }

    public AliPayInfo setPid(String pid) {
        this.pid = pid;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public AliPayInfo setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getSignType() {
        return signType;
    }

    public AliPayInfo setSignType(String signType) {
        this.signType = signType;
        return this;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public AliPayInfo setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public AliPayInfo setPublicKey(String publicKey) {
        this.publicKey = publicKey;
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
