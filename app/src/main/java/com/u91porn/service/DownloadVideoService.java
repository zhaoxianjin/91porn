package com.u91porn.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.format.Formatter;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u91porn.R;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.download.DownloadActivity;
import com.u91porn.utils.constants.Constants;
import com.u91porn.utils.DownloadManager;

import java.util.List;

/**
 * @author flymegoc
 */
public class DownloadVideoService extends Service implements DownloadManager.DownloadStatusUpdater {

    private int id = Constants.VIDEO_DOWNLOAD_NOTIFICATION_ID;
    private DataBaseManager dataBaseManager;

    public DownloadVideoService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dataBaseManager = dataBaseManager.getInstance();
        DownloadManager.getImpl().addUpdater(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_NOT_STICKY;
    }

    private void startNotification(String videoName, int progress, String fileSize, int speed) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, String.valueOf(id));
        builder.setContentTitle("正在下载");
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setProgress(100, progress, false);
        builder.setContentText(fileSize + "--" + speed + "KB/s");
        builder.setContentInfo(videoName);
        Intent intent = new Intent(this, DownloadActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        startForeground(id, notification);
    }

    @Override
    public void onDestroy() {
        DownloadManager.getImpl().removeUpdater(this);
        stopForeground(true);
        super.onDestroy();
    }

    @Override
    public void complete(BaseDownloadTask task) {

    }

    @Override
    public void update(BaseDownloadTask task) {
        updateNotification(task, task.getSmallFileSoFarBytes(), task.getSmallFileTotalBytes());
    }

    private void updateNotification(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        int progress = (int) (((float) soFarBytes / totalBytes) * 100);
        String fileSize = Formatter.formatFileSize(DownloadVideoService.this, soFarBytes).replace("MB", "") + "/ " + Formatter.formatFileSize(DownloadVideoService.this, totalBytes);
        UnLimit91PornItem unLimit91PornItem = dataBaseManager.findByDownloadId(task.getId());
        if (unLimit91PornItem != null) {
            if (task.getStatus() == FileDownloadStatus.completed) {
                List<UnLimit91PornItem> unLimit91PornItemList = dataBaseManager.findByDownloadStatus(FileDownloadStatus.progress);
                if (unLimit91PornItemList.size() == 0) {
                    stopForeground(true);
                }
            } else {
                startNotification(unLimit91PornItem.getTitle(), progress, fileSize, task.getSpeed());
            }
        } else {
            List<UnLimit91PornItem> unLimit91PornItemList = dataBaseManager.loadDownloadingData();
            if (unLimit91PornItemList.size() == 0) {
                stopForeground(true);
            }
        }
    }
}
