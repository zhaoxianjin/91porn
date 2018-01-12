package com.u91porn.data.model;

import android.text.format.DateFormat;

import com.u91porn.MyApplication;
import com.u91porn.utils.Constants;

import java.io.Serializable;
import java.util.Date;

import io.objectbox.Box;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.Transient;
import io.objectbox.relation.ToOne;

/**
 * @author flymegoc
 * @date 2017/11/14
 * @describe
 */
@Entity
public class UnLimit91PornItem implements Serializable {

    @Transient
    public final static int FAVORITE_YES = 1;
    @Transient
    public final static int FAVORITE_NO = 0;

    @Id
    public Long id;
    @Index
    private String viewKey;
    private String title;
    private String imgUrl;
    private String duration;
    private String info;
    public ToOne<VideoResult> videoResult;
    private int downloadId;
    private int favorite;

    private int progress;
    private long speed;
    private int soFarBytes;
    private int totalFarBytes;
    private int status;
    private Date favoriteDate;
    private Date addDownloadDate;
    private Date finshedDownloadDate;
    private Date viewHistoryDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getViewKey() {
        return viewKey;
    }

    public void setViewKey(String viewKey) {
        this.viewKey = viewKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ToOne<VideoResult> getVideoResult() {
        return videoResult;
    }

    public void setVideoResult(ToOne<VideoResult> videoResult) {
        this.videoResult = videoResult;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        this.favorite = favorite;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getSoFarBytes() {
        return soFarBytes;
    }

    public void setSoFarBytes(int soFarBytes) {
        this.soFarBytes = soFarBytes;
    }

    public int getTotalFarBytes() {
        return totalFarBytes;
    }

    public void setTotalFarBytes(int totalFarBytes) {
        this.totalFarBytes = totalFarBytes;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UnLimit91PornItem that = (UnLimit91PornItem) o;

        return videoResult != null ? videoResult.equals(that.videoResult) : that.videoResult == null;
    }

    @Override
    public int hashCode() {
        return viewKey != null ? viewKey.hashCode() : 0;
    }


    public String getDownLoadPath() {
        return Constants.DOWNLOAD_PATH + getViewKey() + ".mp4";
    }

    public Date getFavoriteDate() {
        return favoriteDate;
    }

    public void setFavoriteDate(Date favoriteDate) {
        this.favoriteDate = favoriteDate;
    }

    public Date getAddDownloadDate() {
        return addDownloadDate;
    }

    public void setAddDownloadDate(Date addDownloadDate) {
        this.addDownloadDate = addDownloadDate;
    }

    public Date getFinshedDownloadDate() {
        return finshedDownloadDate;
    }

    public void setFinshedDownloadDate(Date finshedDownloadDate) {
        this.finshedDownloadDate = finshedDownloadDate;
    }

    public Date getViewHistoryDate() {
        return viewHistoryDate;
    }

    public void setViewHistoryDate(Date viewHistoryDate) {
        this.viewHistoryDate = viewHistoryDate;
    }

    public String getTitleWithDuration() {
        return getTitle() + "  (" + getDuration() + ")";
    }

    @Override
    public String toString() {
        return "UnLimit91PornItem{" +
                "id=" + id +
                ", viewKey='" + viewKey + '\'' +
                ", title='" + title + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", duration='" + duration + '\'' +
                ", info='" + info + '\'' +
                ", videoResult='" + videoResult.toString() + '\'' +
                ", downloadId=" + downloadId +
                ", favorite=" + favorite +
                ", progress=" + progress +
                ", speed=" + speed +
                ", soFarBytes=" + soFarBytes +
                ", totalFarBytes=" + totalFarBytes +
                ", status=" + status +
                ", favoriteDate=" + DateFormat.format(Constants.DATE_FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, favoriteDate == null ? new Date() : favoriteDate) +
                ", addDownloadDate=" + DateFormat.format(Constants.DATE_FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, addDownloadDate == null ? new Date() : addDownloadDate) +
                ", finshedDownloadDate=" + DateFormat.format(Constants.DATE_FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, finshedDownloadDate == null ? new Date() : finshedDownloadDate) +
                ", viewHistoryDate=" + DateFormat.format(Constants.DATE_FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND, viewHistoryDate == null ? new Date() : viewHistoryDate) +
                '}';
    }
}
