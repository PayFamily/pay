package com.cyberbay.frog.pay.wxpay.wx.sdk.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.cyberbay.frog.pay.wxpay.wx.sdk.IWXPayDomain;
import com.cyberbay.frog.pay.wxpay.wx.sdk.WXPayConfig;


public class WXPayConfigImpl extends  WXPayConfig{

    private byte[] certData;
    private static WXPayConfigImpl INSTANCE;

    private static Map<String, String> params;

    private WXPayConfigImpl() {
  
    }

    public static WXPayConfigImpl getInstance(Map<String, String> params) throws Exception{
        if (INSTANCE == null) {
            synchronized (WXPayConfigImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WXPayConfigImpl();
                    WXPayConfigImpl.params = params;
                    WXPayConfigImpl.notifyUrl = params.get("weixin.notify.url");
                    WXPayConfigImpl.spbillCreateIp = params.get("weixin.sp.bill.create.ip");
                }
            }
        }
        return INSTANCE;
    }

    public String getAppID() {
       // return "wxab8acb865bb1637e";
        //return "wx843af3a30651854c";
        return params.get("weixin.app.id");
    }

    public String getMchID() {
       // return "11473623";
        //return "1458210002";
        return params.get("weixin.merchant.id");
    }
    
    public String getKey() {
       // return "2ab9071b06b9f739b950ddb41db2690d";
        //return "yuhuixunjiefuligouweixinpay12345";
        return params.get("weixin.api.key");
    }

    public InputStream getCertStream() {
        ByteArrayInputStream certBis;
        certBis = new ByteArrayInputStream(this.certData);
        return certBis;
    }


    public int getHttpConnectTimeoutMs() {
        return 2000;
    }

    public int getHttpReadTimeoutMs() {
        return 10000;
    }

    public IWXPayDomain getWXPayDomain() {
        return WXPayDomainImpl.instance();
    }

    public String getPrimaryDomain() {
        return "api.mch.weixin.qq.com";
    }

    public String getAlternateDomain() {
        return "api2.mch.weixin.qq.com";
    }

    @Override
    public int getReportWorkerNum() {
        return 1;
    }

    @Override
    public int getReportBatchSize() {
        return 2;
    }
}
