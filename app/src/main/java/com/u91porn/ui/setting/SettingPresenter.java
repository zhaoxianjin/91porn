package com.u91porn.ui.setting;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.ApiManager;
import com.u91porn.exception.MessageException;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.ui.MvpBasePresenter;
import com.u91porn.utils.CheckResultUtils;
import com.u91porn.utils.HeaderUtils;

import javax.inject.Inject;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2018/2/6
 */

public class SettingPresenter extends MvpBasePresenter<SettingView> implements ISetting {

    private ApiManager apiManager;

    @Inject
    public SettingPresenter(LifecycleProvider<Lifecycle.Event> provider, ApiManager apiManager) {
        super(provider);
        this.apiManager = apiManager;
    }

    @Override
    public void test91PornVideo(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        apiManager.init91PornRetrofitService(baseUrl, true)
                .indexPhp(HeaderUtils.getIndexHeader())
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        if (!CheckResultUtils.check91PronVideoConnectIsSuccess(s)) {
                            throw new MessageException("很遗憾，测试失败了");
                        }
                        return "恭喜，测试成功了";
                    }
                })
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindToLifecycle())
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTesting(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testSuccess(s, qmuiCommonListItemView, key);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }


    @Override
    public void test91PornForum(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        apiManager.initForum91RetrofitService(baseUrl)
                .index()
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        if (!CheckResultUtils.check91PornForumConnectIsSuccess(s)) {
                            throw new MessageException("很遗憾，测试失败了");
                        }
                        return "恭喜，测试成功了";
                    }
                })
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindToLifecycle())
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTesting(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testSuccess(s, qmuiCommonListItemView, key);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }

    @Override
    public void testPigAv(String baseUrl, final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        apiManager.initPigAvRetrofitService(baseUrl)
                .video(baseUrl)
                .map(new Function<String, String>() {
                    @Override
                    public String apply(String s) throws Exception {
                        if (!CheckResultUtils.checkPigAvVideoConnectIsSuccess(s)) {
                            throw new MessageException("很遗憾，测试失败了");
                        }
                        return "恭喜，测试成功了";
                    }
                })
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindToLifecycle())
                .subscribe(new CallBackWrapper<String>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.showTesting(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final String s) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testSuccess(s, qmuiCommonListItemView, key);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<SettingView>() {
                            @Override
                            public void run(@NonNull SettingView view) {
                                view.testFailure(msg, qmuiCommonListItemView, key);
                            }
                        });
                    }
                });
    }
}
