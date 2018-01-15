package com.u91porn.ui.about;

import android.content.Context;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.utils.AppCacheUtils;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.utils.GlideApp;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @author flymegoc
 * @date 2017/12/23
 */

public class AboutPresenter extends MvpBasePresenter<AboutView> implements IAbout {
    private UpdatePresenter updatePresenter;
    private LifecycleProvider<ActivityEvent> provider;

    public AboutPresenter(UpdatePresenter updatePresenter, LifecycleProvider<ActivityEvent> provider) {
        this.updatePresenter = updatePresenter;
        this.provider = provider;
    }

    @Override
    public void checkUpdate(int versionCode) {
        updatePresenter.checkUpdate(versionCode, new UpdatePresenter.UpdateListener() {
            @Override
            public void needUpdate(final UpdateVersion updateVersion) {
                ifViewAttached(new ViewAction<AboutView>() {
                    @Override
                    public void run(@NonNull AboutView view) {
                        view.needUpdate(updateVersion);
                        view.showContent();
                    }
                });
            }

            @Override
            public void noNeedUpdate() {
                ifViewAttached(new ViewAction<AboutView>() {
                    @Override
                    public void run(@NonNull AboutView view) {
                        view.noNeedUpdate();
                        view.showContent();
                    }
                });
            }

            @Override
            public void checkUpdateError(final String message) {
                ifViewAttached(new ViewAction<AboutView>() {
                    @Override
                    public void run(@NonNull AboutView view) {
                        view.checkUpdateError(message);
                        view.showContent();
                    }
                });
            }
        });
    }


    @Override
    public void cleanCacheFile(final List<File> fileDirList, final Context context) {
        Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                boolean result = true;
                for (File fileDir : fileDirList) {
                    if (fileDir.getAbsolutePath().contains("glide")) {
                        Logger.d("开始清图片缓存");
                        GlideApp.get(context).clearDiskCache();
                        result = true;
                        break;
                    } else {
                        result = AppCacheUtils.cleanCacheFile(fileDir);
                    }
                }
                if (result) {
                    emitter.onNext(true);
                    emitter.onComplete();
                } else {
                    emitter.onError(new Throwable("clean cache file failure"));
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<Boolean>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new CallBackWrapper<Boolean>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<AboutView>() {
                            @Override
                            public void run(@NonNull AboutView view) {
                                view.showCleanDialog("清除缓存中，请稍后...");
                            }
                        });
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        ifViewAttached(new ViewAction<AboutView>() {
                            @Override
                            public void run(@NonNull AboutView view) {
                                view.cleanCacheSuccess("清除缓存成功！");
                            }
                        });
                    }

                    @Override
                    public void onError(String msg, int code) {
                        ifViewAttached(new ViewAction<AboutView>() {
                            @Override
                            public void run(@NonNull AboutView view) {
                                view.cleanCacheFailure("清除缓存失败！");
                            }
                        });
                    }
                });
    }
}
