package com.u91porn.utils;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.data.model.UnLimit91PornItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;

/**
 * @author flymegoc
 * @date 2017/11/23
 * @describe
 */

public class DownloadManager {
    private static final String TAG = DownloadManager.class.getSimpleName();
    private Box<UnLimit91PornItem> box = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);

    private final static class HolderClass {
        private final static DownloadManager INSTANCE = new DownloadManager();
    }

    public static DownloadManager getImpl() {
        return HolderClass.INSTANCE;
    }

    private ArrayList<DownloadStatusUpdater> updaterList = new ArrayList<>();

    public int startDownload1(final String url, final String path) {
        return FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(lis)
                .setWifiRequired(false)
                .start();
    }


    public int startDownload(String url, final String path) {
        int id = FileDownloader.getImpl().create(url)
                .setPath(path)
                .setListener(lis)
                .setWifiRequired(false)
                .setAutoRetryTimes(1)
                .asInQueueTask()
                .enqueue();
        FileDownloader.getImpl().start(lis, false);
        return id;
    }

    public void addUpdater(final DownloadStatusUpdater updater) {
        if (!updaterList.contains(updater)) {
            updaterList.add(updater);
        }
    }

    public boolean removeUpdater(final DownloadStatusUpdater updater) {
        return updaterList.remove(updater);
    }


    private FileDownloadListener lis = new FileDownloadListener() {
        @Override
        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Logger.t(TAG).d("pending:"+"--status:"+task.getStatus()+"--:soFarBytes："+soFarBytes + "--:totalBytes："+totalBytes);
            saveDownloadInfo(task);
            update(task);
        }

        @Override
        protected void started(BaseDownloadTask task) {
            super.started(task);
            Logger.t(TAG).d("started:"+"--status:"+task.getStatus()+"--:soFarBytes："+task.getSmallFileSoFarBytes() + "--:totalBytes："+task.getSmallFileTotalBytes());
            saveDownloadInfo(task);
            update(task);
        }

        @Override
        protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
            super.connected(task, etag, isContinue, soFarBytes, totalBytes);
            Logger.t(TAG).d("connected:"+"--status:"+task.getStatus()+"--:soFarBytes："+soFarBytes + "--:totalBytes："+totalBytes);
            saveDownloadInfo(task);
            update(task);
        }

        @Override
        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            saveDownloadInfo(task);
            update(task);
        }

        @Override
        protected void blockComplete(BaseDownloadTask task) {
            Logger.t(TAG).d("blockComplete:"+"--status:"+task.getStatus()+"--:soFarBytes："+task.getSmallFileSoFarBytes() + "--:totalBytes："+task.getSmallFileTotalBytes());
            // blockComplete(task);
        }

        @Override
        protected void completed(BaseDownloadTask task) {
            Logger.t(TAG).d("completed:"+"--status:"+task.getStatus()+"--:soFarBytes："+task.getSmallFileSoFarBytes() + "--:totalBytes："+task.getSmallFileTotalBytes());
            Logger.d("completed");
            saveDownloadInfo(task);
            update(task);
        }

        @Override
        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
            Logger.t(TAG).d("paused:"+"--status:"+task.getStatus()+"--:soFarBytes："+soFarBytes + "--:totalBytes："+totalBytes);
            saveDownloadInfo(task);
            update(task);
        }

        @Override
        protected void error(BaseDownloadTask task, Throwable e) {
            Logger.t(TAG).d("error:"+"--status:"+task.getStatus()+"--:soFarBytes："+task.getSmallFileSoFarBytes() + "--:totalBytes："+task.getSmallFileTotalBytes());
            saveDownloadInfo(task);
            update(task);
        }

        @Override
        protected void warn(BaseDownloadTask task) {
            Logger.t(TAG).d("warn:"+"--status:"+task.getStatus()+"--:soFarBytes："+task.getSmallFileSoFarBytes() + "--:totalBytes："+task.getSmallFileTotalBytes());
            update(task);
        }
    };

    /**
     * 实时保存下载信息
     *
     * @param task
     */
    private void saveDownloadInfo(BaseDownloadTask task) {
        UnLimit91PornItem unLimit91PornItem = BoxQureyHelper.findByVideoUrl(task.getUrl());

        int soFarBytes = task.getSmallFileSoFarBytes();
        int totalBytes = task.getSmallFileTotalBytes();
        if (soFarBytes > 0) {
            unLimit91PornItem.setSoFarBytes(soFarBytes);
        }

        if (totalBytes > 0) {
            unLimit91PornItem.setTotalFarBytes(totalBytes);
        }
        if (totalBytes > 0) {
            int p = (int) (((float) soFarBytes / totalBytes) * 100);
            unLimit91PornItem.setProgress(p);
        }
        if (task.getStatus()== FileDownloadStatus.completed){
            unLimit91PornItem.setFinshedDownloadDate(new Date());
        }
        unLimit91PornItem.setSpeed(task.getSpeed());
        unLimit91PornItem.setStatus(task.getStatus());
        box.put(unLimit91PornItem);
        update(task);
    }

    private void blockComplete(final BaseDownloadTask task) {
        final List<DownloadStatusUpdater> updaterListCopy = (List<DownloadStatusUpdater>) updaterList.clone();
        for (DownloadStatusUpdater downloadStatusUpdater : updaterListCopy) {
            downloadStatusUpdater.blockComplete(task);
        }
    }

    private void update(final BaseDownloadTask task) {
        final List<DownloadStatusUpdater> updaterListCopy = (List<DownloadStatusUpdater>) updaterList.clone();
        for (DownloadStatusUpdater downloadStatusUpdater : updaterListCopy) {
            downloadStatusUpdater.update(task);
        }
    }

    public interface DownloadStatusUpdater {
        void blockComplete(BaseDownloadTask task);

        void update(BaseDownloadTask task);
    }
}
