package com.u91porn.ui.user;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.u91porn.MyApplication;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.User;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.utils.ParseUtils;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;

import io.reactivex.disposables.Disposable;

/**
 * 用户登录
 *
 * @author flymegoc
 * @date 2017/12/10
 */

public class UserPresenter extends MvpBasePresenter<UserView> implements IUser {
    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private LifecycleProvider<ActivityEvent> provider;

    public UserPresenter(NoLimit91PornServiceApi noLimit91PornServiceApi, LifecycleProvider<ActivityEvent> provider) {
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
        this.provider = provider;
    }

    @Override
    public void login(String username, String password, String fingerprint, String fingerprint2, String captcha, String actionlogin, String x, String y, String referer) {
        login(username, password, fingerprint, fingerprint2, captcha, actionlogin, x, y, referer, null);
    }

    public void login(String username, String password, String fingerprint, String fingerprint2, String captcha, String actionlogin, String x, String y, String referer, final LoginListener loginListener) {
        noLimit91PornServiceApi.login(username, password, fingerprint, fingerprint2, captcha, actionlogin, x, y, referer)
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<UserView>() {
                            @Override
                            public void run(@NonNull UserView view) {
                                if (loginListener == null) {
                                    view.showLoading(true);
                                }
                            }
                        });
                    }

                    @Override
                    public void onSuccess(String s) {
                        if (!s.contains("登录") || !s.contains("注册") || s.contains("退出")) {
                            User user = ParseUtils.parseUserInfo(s);
                            MyApplication.getInstace().setUser(user);
                            if (loginListener != null) {
                                loginListener.loginSuccess();
                            } else {
                                ifViewAttached(new ViewAction<UserView>() {
                                    @Override
                                    public void run(@NonNull UserView view) {
                                        view.showContent();
                                        view.loginSuccess();
                                    }
                                });
                            }
                        } else {
                            final String errorinfo = ParseUtils.parseErrorInfo(s);
                            if (loginListener != null) {
                                loginListener.loginFailure(errorinfo);
                            } else {
                                ifViewAttached(new ViewAction<UserView>() {
                                    @Override
                                    public void run(@NonNull UserView view) {
                                        view.showContent();
                                        view.loginError(errorinfo);
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (loginListener != null) {
                            loginListener.loginFailure(msg);
                        } else {
                            ifViewAttached(new ViewAction<UserView>() {
                                @Override
                                public void run(@NonNull UserView view) {
                                    view.showContent();
                                    view.loginError(msg);
                                }
                            });
                        }
                    }
                });
    }

    @Override
    public void register(String next, String username, String password1, String password2, String email, String captchaInput, String fingerprint, String vip, String actionSignup, String submitX, String submitY, String ipAddress, String referer) {
        noLimit91PornServiceApi.register(next, username, password1, password2, email, captchaInput, fingerprint, vip, actionSignup, submitX, submitY, referer, ipAddress)
                .retryWhen(new RetryWhenProcess(2))
                .compose(RxSchedulersHelper.<String>ioMainThread())
                .compose(provider.<String>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new CallBackWrapper<String>() {
                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<UserView>() {
                            @Override
                            public void run(@NonNull UserView view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(String s) {
                        Logger.d(s);
                        if (!s.contains("登录") || !s.contains("注册") || s.contains("退出")) {
                            User user = ParseUtils.parseUserInfo(s);
                            MyApplication.getInstace().setUser(user);
                            ifViewAttached(new ViewAction<UserView>() {
                                @Override
                                public void run(@NonNull UserView view) {
                                    view.showContent();
                                    view.registerSuccess();
                                }
                            });
                        } else {
                            final String errorinfo = ParseUtils.parseErrorInfo(s);
                            ifViewAttached(new ViewAction<UserView>() {
                                @Override
                                public void run(@NonNull UserView view) {
                                    view.showContent();
                                    view.registerFailure(errorinfo);
                                }
                            });
                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<UserView>() {
                            @Override
                            public void run(@NonNull UserView view) {
                                view.showContent();
                                view.registerFailure(msg);
                            }
                        });
                    }
                });
    }

    public interface LoginListener {
        void loginSuccess();

        void loginFailure(String message);
    }
}
