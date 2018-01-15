package com.u91porn.data.dao;

import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/22
 * @describe
 */

public class GreenDaoHelper {

    private static final String TAG = GreenDaoHelper.class.getSimpleName();
    private static GreenDaoHelper greenDaoHelper;
    private static UnLimit91PornItemDao unLimit91PornItemDao;
    private static VideoResultDao videoResultDao;

    private GreenDaoHelper(DaoSession daoSession) {
        unLimit91PornItemDao = daoSession.getUnLimit91PornItemDao();
        videoResultDao = daoSession.getVideoResultDao();
    }

    public static void init(DaoSession daoSession) {
        if (greenDaoHelper == null) {
            synchronized (GreenDaoHelper.class) {
                greenDaoHelper = new GreenDaoHelper(daoSession);
            }
        }
    }

    public static GreenDaoHelper getInstance() {
        return greenDaoHelper;
    }

    public void update(UnLimit91PornItem unLimit91PornItem) {
        unLimit91PornItemDao.update(unLimit91PornItem);
    }

    public List<UnLimit91PornItem> loadDownloadingData() {
        return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.Status.notEq(FileDownloadStatus.completed), UnLimit91PornItemDao.Properties.DownloadId.notEq(0)).orderDesc(UnLimit91PornItemDao.Properties.AddDownloadDate).build().list();
    }

    public List<UnLimit91PornItem> loadFinishedData() {
        return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.Status.eq(FileDownloadStatus.completed), UnLimit91PornItemDao.Properties.DownloadId.notEq(0)).orderDesc(UnLimit91PornItemDao.Properties.FinshedDownloadDate).build().list();
    }

    public List<UnLimit91PornItem> loadHistoryData(int page, int pageSize) {
        return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.ViewHistoryDate.isNotNull()).orderDesc(UnLimit91PornItemDao.Properties.ViewHistoryDate).offset((page - 1) * pageSize).limit(pageSize).build().list();
    }

    public long insertOrReplaceInTx(UnLimit91PornItem unLimit91PornItem) {
        return unLimit91PornItemDao.insertOrReplace(unLimit91PornItem);
    }

    public long insertOrReplaceInTx(VideoResult videoResult) {
        return videoResultDao.insertOrReplace(videoResult);
    }

    public UnLimit91PornItem findByViewKey(String viewKey) {

        return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.ViewKey.eq(viewKey)).build().unique();
    }

    public VideoResult getVideoResultByViewKey(String viewKey) {
        UnLimit91PornItem unLimit91PornItem = findByViewKey(viewKey);
        if (unLimit91PornItem == null) {
            return null;
        }
        return videoResultDao.load(unLimit91PornItem.getVideoResultId());
    }

    public UnLimit91PornItem findByDownloadId(int downloadId) {
        return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.DownloadId.eq(downloadId)).build().unique();
    }

    public List<UnLimit91PornItem> loadAllLimit91PornItems() {
        return unLimit91PornItemDao.loadAll();
    }

    public List<UnLimit91PornItem> findByDownloadStatus(int status) {
        return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.Status.eq(status)).build().list();
    }

    public List<UnLimit91PornItem> findByNotDownloadStatus(int status) {
        return unLimit91PornItemDao.queryBuilder().where(UnLimit91PornItemDao.Properties.Status.notEq(status), UnLimit91PornItemDao.Properties.DownloadId.notEq(0)).build().list();
    }

    public void updateInTx(List<UnLimit91PornItem> unLimit91PornItemList) {
        unLimit91PornItemDao.updateInTx(unLimit91PornItemList);
    }
}
