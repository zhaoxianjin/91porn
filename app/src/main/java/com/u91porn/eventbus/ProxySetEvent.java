package com.u91porn.eventbus;

/**
 * 代理设置成功
 *
 * @author flymegoc
 * @date 2018/1/20
 */

public class ProxySetEvent {
    private String proxyIpAddress;
    private int proxyPort;

    public ProxySetEvent(String proxyIpAddress, int proxyPort) {
        this.proxyIpAddress = proxyIpAddress;
        this.proxyPort = proxyPort;
    }

    public String getProxyIpAddress() {
        return proxyIpAddress;
    }

    public void setProxyIpAddress(String proxyIpAddress) {
        this.proxyIpAddress = proxyIpAddress;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }
}
