package com.u91porn.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/23
 */

public class Forum91PronItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String folder;
    private String icon;

    private long tid;
    private String title;
    private List<String> imageList;
    private String agreeCount;

    private String author;
    private String authorPublishTime;
    private long replyCount;
    private long viewCount;
    private String lastPostAuthor;
    private String lastPostTime;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public String getAgreeCount() {
        return agreeCount;
    }

    public void setAgreeCount(String agreeCount) {
        this.agreeCount = agreeCount;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorPublishTime() {
        return authorPublishTime;
    }

    public void setAuthorPublishTime(String authorPublishTime) {
        this.authorPublishTime = authorPublishTime;
    }

    public long getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(long replyCount) {
        this.replyCount = replyCount;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public String getLastPostAuthor() {
        return lastPostAuthor;
    }

    public void setLastPostAuthor(String lastPostAuthor) {
        this.lastPostAuthor = lastPostAuthor;
    }

    public String getLastPostTime() {
        return lastPostTime;
    }

    public void setLastPostTime(String lastPostTime) {
        this.lastPostTime = lastPostTime;
    }
}
