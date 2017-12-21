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

    @Id
    public long id;

    private String videoUrl;

    @Index
    private String videoId;
    private String ownnerId;
    private String thumbImgUrl;

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
                '}';
    }
}
