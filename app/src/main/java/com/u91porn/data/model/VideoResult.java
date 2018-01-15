package com.u91porn.data.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;

/**
 * 视频信息
 *
 * @author flymegoc
 * @date 2017/12/20
 */
@Entity
public class VideoResult implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 游客超过每天观看次数
     */
    @Transient
    public static final Long OUT_OF_WATCH_TIMES = -1L;
    @Id
    public Long id;
    private String videoUrl;

    @Index
    private String videoId;
    private String ownnerId;
    private String thumbImgUrl;
    private String videoName;
    private String ownnerName;
    private String addDate;
    private String userOtherInfo;

    @Generated(hash = 567305003)
    public VideoResult(Long id, String videoUrl, String videoId, String ownnerId, String thumbImgUrl,
            String videoName, String ownnerName, String addDate, String userOtherInfo) {
        this.id = id;
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.ownnerId = ownnerId;
        this.thumbImgUrl = thumbImgUrl;
        this.videoName = videoName;
        this.ownnerName = ownnerName;
        this.addDate = addDate;
        this.userOtherInfo = userOtherInfo;
    }

    @Generated(hash = 121136283)
    public VideoResult() {
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

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVideoName() {
        return this.videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }
}
