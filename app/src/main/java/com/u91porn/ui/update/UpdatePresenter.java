package com.u91porn.ui.update;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.GitHubServiceApi;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.rxjava.CallBackWrapper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author flymegoc
 * @date 2017/12/22
 */

public class UpdatePresenter extends MvpBasePresenter<UpdateView> implements IUpdate {

    private GitHubServiceApi gitHubServiceApi;
    private final static String CHECK_UPDATE_URL = "https://github.com/techGay/91porn/blob/master/version.txt";
    private Gson gson;
    private LifecycleProvider<Lifecycle.Event> provider;

    public UpdatePresenter(GitHubServiceApi gitHubServiceApi, Gson gson, LifecycleProvider<Lifecycle.Event> provider) {
        this.gitHubServiceApi = gitHubServiceApi;
        this.gson = gson;
        this.provider = provider;
    }

    @Override
    public void checkUpdate(int versionCode) {
        checkUpdate(versionCode, null);
    }

    public void checkUpdate(final int versionCode, final UpdateListener updateListener) {
        gitHubServiceApi.checkUpdate(CHECK_UPDATE_URL)
                .map(new Function<String, UpdateVersion>() {
                    @Override
                    public UpdateVersion apply(String s) throws Exception {
                        Document doc = Jsoup.parse(s);
                        String text = doc.select("table.highlight").text();
                        return gson.fromJson(text, UpdateVersion.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(provider.<UpdateVersion>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<UpdateVersion>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(final UpdateVersion updateVersion) {
                        if (updateVersion.getVersionCode() > versionCode) {
                            if (updateListener != null) {
                                updateListener.needUpdate(updateVersion);
                            } else {
                                ifViewAttached(new ViewAction<UpdateView>() {
                                    @Override
                                    public void run(@NonNull UpdateView view) {
                                        view.needUpdate(updateVersion);
                                    }
                                });
                            }
                        } else {
                            if (updateListener != null) {
                                updateListener.noNeedUpdate();
                            } else {
                                ifViewAttached(new ViewAction<UpdateView>() {
                                    @Override
                                    public void run(@NonNull UpdateView view) {
                                        view.noNeedUpdate();
                                    }
                                });
                            }

                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (updateListener != null) {
                            updateListener.checkUpdateError(msg);
                        } else {
                            ifViewAttached(new ViewAction<UpdateView>() {
                                @Override
                                public void run(@NonNull UpdateView view) {
                                    view.checkUpdateError(msg);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                    }
                });
    }

    public interface UpdateListener {
        void needUpdate(UpdateVersion updateVersion);

        void noNeedUpdate();

        void checkUpdateError(String message);
    }
}
