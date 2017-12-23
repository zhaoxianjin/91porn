package com.u91porn.ui.update;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.utils.CallBackWrapper;

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

    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private final static String CHECK_UPDATE_URL = "https://github.com/techGay/91porn/blob/master/version.txt";
    private Gson gson;

    public UpdatePresenter(NoLimit91PornServiceApi noLimit91PornServiceApi, Gson gson) {
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
        this.gson = gson;
    }

    @Override
    public void checkUpdate(int versionCode) {
        checkUpdate(versionCode, null);
    }

    public void checkUpdate(final int versionCode, final UpdateListener updateListener) {
        noLimit91PornServiceApi.checkUpdate(CHECK_UPDATE_URL)
                .map(new Function<String, UpdateVersion>() {
                    @Override
                    public UpdateVersion apply(String s) throws Exception {
                        Document doc = Jsoup.parse(s);
                        String text = doc.select("table.highlight").text();
                        Logger.d(text);
                        return gson.fromJson(text, UpdateVersion.class);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CallBackWrapper<UpdateVersion>() {
                    @Override
                    public void onBegin(Disposable d) {

                    }

                    @Override
                    public void onSuccess(UpdateVersion updateVersion) {
                        if (updateVersion.getVersionCode() > versionCode) {
                            if (isViewAttached()) {
                                getView().needUpdate(updateVersion);
                            } else if (updateListener != null) {
                                updateListener.needUpdate(updateVersion);
                            }
                        } else {
                            if (isViewAttached()) {
                                getView().noNeedUpdate();
                            } else if (updateListener != null) {
                                updateListener.noNeedUpdate();
                            }
                        }
                    }

                    @Override
                    public void onError(String msg, int code) {
                        if (isViewAttached()) {
                            getView().checkUpdateError(msg);
                        } else if (updateListener != null) {
                            updateListener.checkUpdateError(msg);
                        }
                    }
                });
    }

    public interface UpdateListener {
        void needUpdate(UpdateVersion updateVersion);

        void noNeedUpdate();

        void checkUpdateError(String message);
    }
}
