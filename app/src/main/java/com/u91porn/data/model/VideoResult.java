package com.u91porn.data.model;

import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;

/**
 * 视频信息
 *
 * @author flymegoc
 * @date 2017/12/20
 */
@Entity
public class VideoResult {
    /**
     * 游客超过每天观看次数
     */
    public static final int OUT_OF_WATCH_TIMES = -1;
    @Id
    public long id;

    private String videoUrl;

    @Index
    private String videoId;
    private String ownnerId;
    private String thumbImgUrl;

    private String ownnerName;
    private String addDate;
    private String userOtherInfo;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getOwnnerId() {
        return ownnerId;
    }

    public void setOwnnerId(String ownnerId) {
        this.ownnerId = ownnerId;
    }

    public String getThumbImgUrl() {
        return thumbImgUrl;
    }

    public void setThumbImgUrl(String thumbImgUrl) {
        this.thumbImgUrl = thumbImgUrl;
    }

    public String getOwnnerName() {
        return ownnerName;
    }

    public void setOwnnerName(String ownnerName) {
        this.ownnerName = ownnerName;
    }

    public String getAddDate() {
        return addDate;
    }

    public void setAddDate(String addDate) {
        this.addDate = addDate;
    }

    public String getUserOtherInfo() {
        return userOtherInfo;
    }

    public void setUserOtherInfo(String userOtherInfo) {
        this.userOtherInfo = userOtherInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        VideoResult that = (VideoResult) o;

        return videoId != null ? videoId.equals(that.videoId) : that.videoId == null;
    }

    @Override
    public int hashCode() {
        return videoId != null ? videoId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "VideoResult{" +
                "id=" + id +
                ", videoUrl='" + videoUrl + '\'' +
                ", videoId='" + videoId + '\'' +
                ", ownnerId='" + ownnerId + '\'' +
                ", thumbImgUrl='" + thumbImgUrl + '\'' +
                ", ownnerName='" + ownnerName + '\'' +
                ", addDate='" + addDate + '\'' +
                ", userOtherInfo='" + userOtherInfo + '\'' +
                '}';
    }
}
