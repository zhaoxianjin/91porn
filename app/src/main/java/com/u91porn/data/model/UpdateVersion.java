package com.u91porn.data.model;

import java.io.Serializable;

/**
 * 版本升级
 *
 * @author flymegoc
 * @date 2017/12/22
 */

public class UpdateVersion implements Serializable {

    private static final long serialVersionUID = 1L;

    private int versionCode;
    private String versionName;
    private String updateMessage;
    private String apkDownloadUrl;

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateMessage() {
        return updateMessage;
    }

    public void setUpdateMessage(String updateMessage) {
        this.updateMessage = updateMessage;
    }

    public String getApkDownloadUrl() {
        return apkDownloadUrl;
    }

    public void setApkDownloadUrl(String apkDownloadUrl) {
        this.apkDownloadUrl = apkDownloadUrl;
    }

    @Override
    public String toString() {
        return "UpdateVersion{" +
                "versionCode=" + versionCode +
                ", versionName='" + versionName + '\'' +
                ", updateMessage='" + updateMessage + '\'' +
                ", apkDownloadUrl='" + apkDownloadUrl + '\'' +
                '}';
    }
}
