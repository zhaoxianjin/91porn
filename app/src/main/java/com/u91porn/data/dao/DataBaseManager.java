package com.u91porn.data.dao;

import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u91porn.data.model.Category;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/22
 * @describe
 */

public class DataBaseManager {

    private static final String TAG = DataBaseManager.class.getSimpleName();
    private static DataBaseManager dataBaseManager;
    private static UnLimit91PornItemDao unLimit91PornItemDao;
    private static VideoResultDao videoResultDao;
    private static CategoryDao categoryDao;

    private DataBaseManager(DaoSession daoSession) {
        unLimit91PornItemDao = daoSession.getUnLimit91PornItemDao();
        videoResultDao = daoSession.getVideoResultDao();
        categoryDao = daoSession.getCategoryDao();
        initCategory();
    }

    public static void init(DaoSession daoSession) {
        if (dataBaseManager == null) {
            synchronized (DataBaseManager.class) {
                dataBaseManager = new DataBaseManager(daoSession);
            }
        }
    }

    public static DataBaseManager getInstance() {
        return dataBaseManager;
    }


    private void initCategory() {
        int length = Category.CATEGORY_DEFAULT_91PORN_VALUE.length;
        List<Category> categoryList = categoryDao.loadAll();
        if (categoryList.size() == length) {
            return;
        }
        for (int i = 0; i < length; i++) {
            Category category = new Category();
            category.setCategoryName(Category.CATEGORY_DEFAULT_91PORN_NAME[i]);
            category.setCategoryValue(Category.CATEGORY_DEFAULT_91PORN_VALUE[i]);
            category.setCategoryType(Category.TYPE_91PORN);
            category.setIsShow(true);
            category.setSortId(i);
            categoryList.add(category);
        }
        categoryDao.insertOrReplaceInTx(categoryList);
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

    public List<Category> loadAllCategoryData() {
        categoryDao.detachAll();
        return categoryDao.queryBuilder().where(CategoryDao.Properties.CategoryType.eq(Category.TYPE_91PORN)).orderAsc(CategoryDao.Properties.SortId).build().list();
    }

    public List<Category> loadCategoryData() {
        categoryDao.detachAll();
        return categoryDao.queryBuilder().where(CategoryDao.Properties.CategoryType.eq(Category.TYPE_91PORN), CategoryDao.Properties.IsShow.eq(true)).orderAsc(CategoryDao.Properties.SortId).build().list();
    }

    public void updateCategoryData(List<Category> categoryList) {
        categoryDao.updateInTx(categoryList);
    }

    public Category findCategoryById(Long id) {
        categoryDao.detachAll();
        return categoryDao.load(id);
    }
}
