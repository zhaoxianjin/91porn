package com.u91porn.data.model;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author flymegoc
 * @date 2018/1/20
 */

public class ProxyModel {

    public static final int TYPE_HTTP = 1;
    public static final int TYPE_HTTPS = 2;
    public static final int TYPE_SOCKS = 3;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_HTTP, TYPE_HTTPS, TYPE_SOCKS})
    @interface ProxyType {

    }

    private String proxyIp;
    private String proxyPort;
    private String anonymous;
    @ProxyType
    private int type;
    private String location;
    private String responseTime;
    private String validateTime;
    private String liveTime;

    public String getProxyIp() {
        return proxyIp;
    }

    public void setProxyIp(String proxyIp) {
        this.proxyIp = proxyIp;
    }

    public String getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(String proxyPort) {
        this.proxyPort = proxyPort;
    }

    public String getAnonymous() {
        return anonymous;
    }

    public void setAnonymous(String anonymous) {
        this.anonymous = anonymous;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getResponseTime() {
        return responseTime;
    }

    public void setResponseTime(String responseTime) {
        this.responseTime = responseTime;
    }

    public String getValidateTime() {
        return validateTime;
    }

    public void setValidateTime(String validateTime) {
        this.validateTime = validateTime;
    }

    public String getLiveTime() {
        return liveTime;
    }

    public void setLiveTime(String liveTime) {
        this.liveTime = liveTime;
    }
}
