package com.u91porn.data.model;

import java.util.List;

/**
 * 视频评论
 *
 * @author flymegoc
 * @date 2017/12/26
 */

public class VideoComment {
    private String uid;
    private String uName;
    private String replyTime;
    private String replyId;
    private String titleInfo;
    private List<String> commentQuoteList;
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(String replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getTitleInfo() {
        return titleInfo;
    }

    public void setTitleInfo(String titleInfo) {
        this.titleInfo = titleInfo;
    }

    public List<String> getCommentQuoteList() {
        return commentQuoteList;
    }

    public void setCommentQuoteList(List<String> commentQuoteList) {
        this.commentQuoteList = commentQuoteList;
    }

    @Override
    public String toString() {
        return "VideoComment{" +
                "uid='" + uid + '\'' +
                ", uName='" + uName + '\'' +
                ", replyTime='" + replyTime + '\'' +
                ", replyId='" + replyId + '\'' +
                ", titleInfo='" + titleInfo + '\'' +
                ", commentQuoteList=" + commentQuoteList +
                '}';
    }
}
