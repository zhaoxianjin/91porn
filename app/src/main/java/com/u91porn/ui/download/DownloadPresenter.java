package com.u91porn.ui.download;

import android.text.TextUtils;

import com.danikula.videocache.HttpProxyCacheServer;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.Constants;
import com.u91porn.utils.DownloadManager;
import com.u91porn.utils.MyFileNameGenerator;

import org.greenrobot.essentials.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public class DownloadPresenter extends MvpBasePresenter<DownloadView> implements IDownload {

    private Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);

    @Override
    public void favorite(UnLimit91PornItem unLimit91PornItem) {

    }

    @Override
    public void downloadVideo(UnLimit91PornItem unLimit91PornItem) {
        unLimit91PornItem.setStatus(FileDownloadStatus.INVALID_STATUS);
        unLimit91PornItemBox.put(unLimit91PornItem);
        downloadVideo(unLimit91PornItem, null);
        loadFinishedData();
    }

    @Override
    public void downloadVideo(UnLimit91PornItem unLimit91PornItem, DownloadListener downloadListener) {
        HttpProxyCacheServer proxy = MyApplication.getInstace().getProxy();
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        UnLimit91PornItem tmp = BoxQureyHelper.findByViewKey(unLimit91PornItem.getViewKey());
        if (tmp == null) {
            if (isViewAttached() && downloadListener == null) {
                getView().showMessage("还未解析成功视频地址");
            } else {
                downloadListener.onSuccess("还未解析成功视频地址");
            }
            return;
        }
        String videoUrl = tmp.getVideoUrl();
        if (TextUtils.isEmpty(tmp.getVideoUrl())) {
            if (isViewAttached() && downloadListener == null) {
                getView().showMessage("还未解析成功视频地址");
            }
            return;
        }
        //先检查文件
        File toFile = new File(tmp.getDownLoadPath());
        if (toFile.exists() && toFile.length() > 0) {
            if (isViewAttached() && downloadListener == null) {
                getView().showMessage("已经下载过了，请查看下载目录");
            } else {
                downloadListener.onError("已经下载过了，请查看下载目录");
            }
        }
        //如果已经缓存完成，直接使用缓存代理完成
        if (proxy.isCached(videoUrl)) {
            try {
                copyCacheFile(videoUrl, downloadListener);
            } catch (IOException e) {
                if (isViewAttached() && downloadListener == null) {
                    getView().showMessage("缓存文件错误，无法拷贝");
                } else {
                    if (downloadListener != null) {
                        downloadListener.onError("缓存文件错误，无法拷贝");
                    }
                }
                e.printStackTrace();
            }
            return;
        }
        //检查当前状态
        if (tmp.getStatus() == FileDownloadStatus.progress) {
            if (isViewAttached() && downloadListener == null) {
                getView().showMessage("已经在下载了");
            } else {
                if (downloadListener != null) {
                    downloadListener.onError("已经在下载了");
                }
            }
            return;
        }
        Logger.d("视频连接：" + videoUrl);
        String path = Constants.DOWNLOAD_PATH + unLimit91PornItem.getViewKey() + ".mp4";
        Logger.d(path);
        int id = DownloadManager.getImpl().startDownload(videoUrl, path);
        tmp.setAddDownloadDate(new Date());
        tmp.setDownloadId(id);
        unLimit91PornItemBox.put(tmp);
        if (isViewAttached() && downloadListener == null) {
            getView().showMessage("开始下载");
        } else {
            if (downloadListener != null) {
                downloadListener.onSuccess("开始下载");
            }
        }
    }

    @Override
    public void loadDownloadingData() {
        List<UnLimit91PornItem> unLimit91PornItems = unLimit91PornItemBox.query().notEqual(UnLimit91PornItem_.status, FileDownloadStatus.completed).and().notEqual(UnLimit91PornItem_.downloadId, 0).orderDesc(UnLimit91PornItem_.addDownloadDate).build().find();
        if (isViewAttached()) {
            getView().setDownloadingData(unLimit91PornItems);
        }
    }

    @Override
    public void loadFinishedData() {
        List<UnLimit91PornItem> unLimit91PornItems = unLimit91PornItemBox.query().equal(UnLimit91PornItem_.status, FileDownloadStatus.completed).orderDesc(UnLimit91PornItem_.finshedDownloadDate).build().find();
        if (isViewAttached()) {
            getView().setFinishedData(unLimit91PornItems);
        }
    }

    @Override
    public void deleteDownloadingTask(UnLimit91PornItem unLimit91PornItem) {
        unLimit91PornItemBox.remove(unLimit91PornItem.getId());
    }

    @Override
    public void deleteDownloadedTask(UnLimit91PornItem unLimit91PornItem, boolean isDeleteFile) {
        if (!isDeleteFile) {
            deleteWithoutFile(unLimit91PornItem);
        } else {
            deleteWithFile(unLimit91PornItem);
        }
    }

    /**
     * 只删除记录，不删除文件
     *
     * @param unLimit91PornItem
     */
    private void deleteWithoutFile(UnLimit91PornItem unLimit91PornItem) {
        unLimit91PornItemBox.remove(unLimit91PornItem.getId());
    }

    /**
     * 连同文件一起删除
     *
     * @param unLimit91PornItem
     */
    private void deleteWithFile(UnLimit91PornItem unLimit91PornItem) {
        File file = new File(unLimit91PornItem.getDownLoadPath());
        if (file.delete()) {
            unLimit91PornItemBox.remove(unLimit91PornItem.getId());
        } else {
            if (isViewAttached()) {
                getView().showMessage("删除文件失败");
            }
        }
    }


    /**
     * 直接拷贝缓存好的视频即可
     *
     * @param videoUrl
     */
    private void copyCacheFile(final String videoUrl, final DownloadListener downloadListener) throws IOException {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                MyFileNameGenerator myFileNameGenerator = new MyFileNameGenerator();
                String cacheFileName = myFileNameGenerator.generate(videoUrl);
                File fromFile = new File(MyApplication.getInstace().getExternalCacheDir() + "/video-cache/" + cacheFileName);
                if (!fromFile.exists() || fromFile.length() <= 0) {
                    e.onError(new Exception("缓存文件错误，无法拷贝"));
                }
                e.onNext(fromFile);
                e.onComplete();
            }
        }).map(new Function<File, UnLimit91PornItem>() {
            @Override
            public UnLimit91PornItem apply(File fromFile) throws Exception {
                UnLimit91PornItem unLimit91PornItem = BoxQureyHelper.findByVideoUrl(videoUrl);
                File toFile = new File(unLimit91PornItem.getDownLoadPath());
                if (toFile.exists() && toFile.length() > 0) {
                    throw new Exception("已经下载过了");
                } else {
                    if (!toFile.createNewFile()) {
                        throw new Exception("创建文件失败");
                    }
                }
                FileUtils.copyFile(fromFile, toFile);
                unLimit91PornItem.setTotalFarBytes((int) fromFile.length());
                unLimit91PornItem.setSoFarBytes((int) fromFile.length());
                return unLimit91PornItem;
            }
        }).map(new Function<UnLimit91PornItem, String>() {
            @Override
            public String apply(UnLimit91PornItem unLimit91PornItem) throws Exception {
                unLimit91PornItem.setStatus(FileDownloadStatus.completed);
                unLimit91PornItem.setProgress(100);
                unLimit91PornItem.setFinshedDownloadDate(new Date());
                unLimit91PornItem.setDownloadId(FileDownloadUtils.generateId(unLimit91PornItem.getVideoUrl(), unLimit91PornItem.getDownLoadPath()));
                MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class).put(unLimit91PornItem);
                return "下载完成";
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        if (isViewAttached() && downloadListener == null) {
                            getView().showMessage(s);
                        } else {
                            if (downloadListener != null) {
                                downloadListener.onSuccess(s);
                            }
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached() && downloadListener == null) {
                            getView().showMessage(e.getMessage());
                        } else {
                            if (downloadListener != null) {
                                downloadListener.onError(e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
//        UnLimit91PornItem unLimit91PornItem = BoxQureyHelper.findByVideoUrl(videoUrl);
//        MyFileNameGenerator myFileNameGenerator = new MyFileNameGenerator();
//        String cacheFileName = myFileNameGenerator.generate(videoUrl);
//        File fromFile = new File(MyApplication.getInstace().getExternalCacheDir() + "/video-cache/" + cacheFileName);
//        if (!fromFile.exists() || fromFile.length() <= 0) {
//            if (isViewAttached() && downloadListener == null) {
//                getView().showMessage("缓存文件错误，无法拷贝");
//            } else {
//                downloadListener.onSuccess("缓存文件错误，无法拷贝");
//            }
//            return;
//        }
//        File toFile = new File(unLimit91PornItem.getDownLoadPath());
//        if (toFile.exists() && toFile.length() > 0) {
//            if (isViewAttached() && downloadListener == null) {
//                getView().showMessage("已经下载过了");
//            } else {
//                downloadListener.onError("已经下载过了");
//            }
//            return;
//        } else {
//            toFile.delete();
//            toFile.createNewFile();
//        }
//        FileUtils.copyFile(fromFile, toFile);
//        unLimit91PornItem.setStatus(FileDownloadStatus.completed);
//        unLimit91PornItem.setTotalFarBytes((int) fromFile.length());
//        unLimit91PornItem.setSoFarBytes((int) fromFile.length());
//        unLimit91PornItem.setProgress(100);
//        unLimit91PornItem.setFinshedDownloadDate(new Date());
//        unLimit91PornItem.setDownloadId(FileDownloadUtils.generateId(unLimit91PornItem.getVideoUrl(), unLimit91PornItem.getDownLoadPath()));
//        MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class).put(unLimit91PornItem);
//        if (isViewAttached() && downloadListener == null) {
//            getView().showMessage("下载完成");
//        } else {
//            downloadListener.onSuccess("下载完成");
//        }
    }

    public interface DownloadListener {
        void onSuccess(String message);

        void onError(String message);
    }
}
