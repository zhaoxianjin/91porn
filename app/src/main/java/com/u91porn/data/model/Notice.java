package com.u91porn.data.model;

import java.io.Serializable;

/**
 * 公告
 *
 * @author flymegoc
 * @date 2018/1/26
 */

public class Notice implements Serializable {
    private static final long serialVersionUID = 1L;

    private String noticeTitle;
    private String noticeTime;
    private String noticeer;
    private String noticeMessage;
    private int versionCode;

    public String getNoticeTitle() {
        return noticeTitle;
    }

    public void setNoticeTitle(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public String getNoticeTime() {
        return noticeTime;
    }

    public void setNoticeTime(String noticeTime) {
        this.noticeTime = noticeTime;
    }

    public String getNoticeer() {
        return noticeer;
    }

    public void setNoticeer(String noticeer) {
        this.noticeer = noticeer;
    }

    public String getNoticeMessage() {
        return noticeMessage;
    }

    public void setNoticeMessage(String noticeMessage) {
        this.noticeMessage = noticeMessage;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }
}
