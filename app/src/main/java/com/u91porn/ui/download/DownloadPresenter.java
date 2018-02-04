package com.u91porn.ui.download;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.danikula.videocache.HttpProxyCacheServer;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.liulishuo.filedownloader.util.FileDownloadUtils;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.utils.DownloadManager;
import com.u91porn.utils.SDCardUtils;
import com.u91porn.utils.VideoCacheFileNameGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import de.greenrobot.common.io.FileUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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

    private DataBaseManager dataBaseManager;
    private LifecycleProvider<Lifecycle.Event> provider;
    private HttpProxyCacheServer proxy;
    private File videoCacheDir;

    public DownloadPresenter(DataBaseManager dataBaseManager, LifecycleProvider<Lifecycle.Event> provider, HttpProxyCacheServer proxy, File videoCacheDir) {
        this.dataBaseManager = dataBaseManager;
        this.provider = provider;
        this.proxy = proxy;
        this.videoCacheDir = videoCacheDir;
    }

    @Override
    public void favorite(String cpaintFunction, String uId, String videoId, String ownnerId, String responseType, String referer) {

    }

    @Override
    public void downloadVideo(UnLimit91PornItem unLimit91PornItem, boolean isDownloadNeedWifi, boolean isForceReDownload) {
        downloadVideo(unLimit91PornItem, isDownloadNeedWifi, isForceReDownload, null);
    }

    @Override
    public void downloadVideo(UnLimit91PornItem unLimit91PornItem, boolean isDownloadNeedWifi, boolean isForceReDownload, DownloadListener downloadListener) {
        UnLimit91PornItem tmp = dataBaseManager.findByViewKey(unLimit91PornItem.getViewKey());
        if (tmp == null || tmp.getVideoResultId() == 0) {
            if (downloadListener != null) {
                downloadListener.onError("还未解析成功视频地址");
            } else {
                ifViewAttached(new ViewAction<DownloadView>() {
                    @Override
                    public void run(@NonNull DownloadView view) {
                        view.showMessage("还未解析成功视频地址", TastyToast.WARNING);
                    }
                });
            }
            return;
        }
        VideoResult videoResult = tmp.getVideoResult();
        //先检查文件
        File toFile = new File(tmp.getDownLoadPath());
        if (toFile.exists() && toFile.length() > 0) {
            if (downloadListener != null) {
                downloadListener.onError("已经下载过了，请查看下载目录");
            } else {
                ifViewAttached(new ViewAction<DownloadView>() {
                    @Override
                    public void run(@NonNull DownloadView view) {
                        view.showMessage("已经下载过了，请查看下载目录", TastyToast.INFO);
                    }
                });
            }
            return;
        }
        //如果已经缓存完成，直接使用缓存代理完成
        if (proxy.isCached(videoResult.getVideoUrl())) {
            try {
                copyCacheFile(videoCacheDir, tmp, downloadListener);
            } catch (IOException e) {
                if (downloadListener != null) {
                    downloadListener.onError("缓存文件错误，无法拷贝");
                } else {
                    ifViewAttached(new ViewAction<DownloadView>() {
                        @Override
                        public void run(@NonNull DownloadView view) {
                            view.showMessage("缓存文件错误，无法拷贝", TastyToast.ERROR);
                        }
                    });
                }
                e.printStackTrace();
            }
            return;
        }
        //检查当前状态
        if (tmp.getStatus() == FileDownloadStatus.progress && tmp.getDownloadId() != 0 && !isForceReDownload) {
            if (downloadListener != null) {
                downloadListener.onError("已经在下载了");
            } else {
                ifViewAttached(new ViewAction<DownloadView>() {
                    @Override
                    public void run(@NonNull DownloadView view) {
                        view.showMessage("已经在下载了", TastyToast.SUCCESS);
                    }
                });
            }
            return;
        }
        Logger.d("视频连接：" + videoResult.getVideoUrl());
        String path = SDCardUtils.DOWNLOAD_VIDEO_PATH + unLimit91PornItem.getViewKey() + ".mp4";
        Logger.d(path);
        int id = DownloadManager.getImpl().startDownload(videoResult.getVideoUrl(), path, isDownloadNeedWifi, isForceReDownload);
        if (tmp.getAddDownloadDate() == null) {
            tmp.setAddDownloadDate(new Date());
        }
        tmp.setDownloadId(id);
        dataBaseManager.update(tmp);
        if (downloadListener != null) {
            downloadListener.onSuccess("开始下载");
        } else {
            ifViewAttached(new ViewAction<DownloadView>() {
                @Override
                public void run(@NonNull DownloadView view) {
                    view.showMessage("开始下载", TastyToast.SUCCESS);
                }
            });
        }
    }

    @Override
    public void loadDownloadingData() {

        Observable
                .create(new ObservableOnSubscribe<List<UnLimit91PornItem>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<UnLimit91PornItem>> emitter) throws Exception {
                        List<UnLimit91PornItem> unLimit91PornItems = dataBaseManager.loadDownloadingData();
                        emitter.onNext(unLimit91PornItems);
                        emitter.onComplete();
                    }
                })
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItemList) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.setDownloadingData(unLimit91PornItemList);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });

    }

    @Override
    public void loadFinishedData() {

        Observable
                .create(new ObservableOnSubscribe<List<UnLimit91PornItem>>() {
                    @Override
                    public void subscribe(ObservableEmitter<List<UnLimit91PornItem>> emitter) throws Exception {
                        List<UnLimit91PornItem> unLimit91PornItems = dataBaseManager.loadFinishedData();
                        emitter.onNext(unLimit91PornItems);
                        emitter.onComplete();
                    }
                })
                .compose(RxSchedulersHelper.<List<UnLimit91PornItem>>ioMainThread())
                .compose(provider.<List<UnLimit91PornItem>>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<List<UnLimit91PornItem>>() {
                    @Override
                    public void onSuccess(final List<UnLimit91PornItem> unLimit91PornItemList) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.setFinishedData(unLimit91PornItemList);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<DownloadView>() {
                            @Override
                            public void run(@NonNull DownloadView view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }

    @Override
    public void deleteDownloadingTask(UnLimit91PornItem unLimit91PornItem) {
        unLimit91PornItem.setDownloadId(0);
        dataBaseManager.update(unLimit91PornItem);
    }

    @Override
    public void deleteDownloadedTask(UnLimit91PornItem unLimit91PornItem, boolean isDeleteFile) {
        if (!isDeleteFile) {
            deleteWithoutFile(unLimit91PornItem);
        } else {
            deleteWithFile(unLimit91PornItem);
        }
    }

    @Override
    public UnLimit91PornItem findUnLimit91PornItemByDownloadId(int downloadId) {
        return dataBaseManager.findByDownloadId(downloadId);
    }

    /**
     * 只删除记录，不删除文件
     *
     * @param unLimit91PornItem
     */
    private void deleteWithoutFile(UnLimit91PornItem unLimit91PornItem) {
        unLimit91PornItem.setDownloadId(0);
        dataBaseManager.update(unLimit91PornItem);
    }

    /**
     * 连同文件一起删除
     *
     * @param unLimit91PornItem
     */
    private void deleteWithFile(UnLimit91PornItem unLimit91PornItem) {
        File file = new File(unLimit91PornItem.getDownLoadPath());
        if (file.delete()) {
            unLimit91PornItem.setDownloadId(0);
            dataBaseManager.update(unLimit91PornItem);
        } else {
            ifViewAttached(new ViewAction<DownloadView>() {
                @Override
                public void run(@NonNull DownloadView view) {
                    view.showMessage("删除文件失败", TastyToast.ERROR);
                }
            });
        }
    }


    /**
     * 直接拷贝缓存好的视频即可
     *
     * @param unLimit91PornItem
     */
    private void copyCacheFile(final File videoCacheDir, final UnLimit91PornItem unLimit91PornItem, final DownloadListener downloadListener) throws IOException {
        Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) throws Exception {
                VideoCacheFileNameGenerator myFileNameGenerator = new VideoCacheFileNameGenerator();
                String cacheFileName = myFileNameGenerator.generate(unLimit91PornItem.getVideoResult().getVideoUrl());
                File fromFile = new File(videoCacheDir, cacheFileName);
                if (!fromFile.exists() || fromFile.length() <= 0) {
                    e.onError(new Exception("缓存文件错误，无法拷贝"));
                }
                e.onNext(fromFile);
                e.onComplete();
            }
        }).map(new Function<File, UnLimit91PornItem>() {
            @Override
            public UnLimit91PornItem apply(File fromFile) throws Exception {
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
                unLimit91PornItem.setDownloadId(FileDownloadUtils.generateId(unLimit91PornItem.getVideoResult().getVideoUrl(), unLimit91PornItem.getDownLoadPath()));
                dataBaseManager.update(unLimit91PornItem);
                return "下载完成";
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<String>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final String s) {
                        if (downloadListener != null) {
                            downloadListener.onSuccess(s);
                        } else {
                            ifViewAttached(new ViewAction<DownloadView>() {
                                @Override
                                public void run(@NonNull DownloadView view) {
                                    view.showMessage(s, TastyToast.SUCCESS);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (downloadListener != null) {
                            downloadListener.onError(msg);
                        } else {
                            ifViewAttached(new ViewAction<DownloadView>() {
                                @Override
                                public void run(@NonNull DownloadView view) {
                                    view.showMessage(msg, TastyToast.ERROR);
                                }
                            });
                        }
                    }
                });
    }

    public interface DownloadListener {
        void onSuccess(String message);

        void onError(String message);
    }
}
