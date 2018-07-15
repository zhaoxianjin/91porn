package com.u91porn.ui.user;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.User;
import com.u91porn.exception.MessageException;
import com.u91porn.parser.Parse91PronVideo;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RetryWhenProcess;
import com.u91porn.rxjava.RxSchedulersHelper;
import com.u91porn.utils.UserHelper;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * 用户登录
 *
 * @author flymegoc
 * @date 2017/12/10
 */

public class UserPresenter extends MvpBasePresenter<UserView> implements IUser {
    private NoLimit91PornServiceApi noLimit91PornServiceApi;
    private LifecycleProvider<Lifecycle.Event> provider;

    public UserPresenter(NoLimit91PornServiceApi noLimit91PornServiceApi, LifecycleProvider<Lifecycle.Event> provider) {
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
                .map(new Function<String, User>() {
                    @Override
                    public User apply(String s) throws Exception {
                        if (!UserHelper.isPornVideoLoginSuccess(s)) {
                            String errorInfo = Parse91PronVideo.parseErrorInfo(s);
                            throw new MessageException(errorInfo);
                        }
                        return Parse91PronVideo.parseUserInfo(s);
                    }
                })
                .compose(RxSchedulersHelper.<User>ioMainThread())
                .compose(provider.<User>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<User>() {
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
                    public void onSuccess(final User user) {
                        if (loginListener != null) {
                            loginListener.loginSuccess(user);
                        } else {
                            ifViewAttached(new ViewAction<UserView>() {
                                @Override
                                public void run(@NonNull UserView view) {
                                    view.showContent();
                                    view.loginSuccess(user);
                                }
                            });
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
                .map(new Function<String, User>() {
                    @Override
                    public User apply(String s) throws Exception {
                        if (!UserHelper.isPornVideoLoginSuccess(s)) {
                            String errorInfo = Parse91PronVideo.parseErrorInfo(s);
                            throw new MessageException(errorInfo);
                        }
                        return Parse91PronVideo.parseUserInfo(s);
                    }
                })
                .compose(RxSchedulersHelper.<User>ioMainThread())
                .compose(provider.<User>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<User>() {
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
                    public void onSuccess(final User user) {
                        ifViewAttached(new ViewAction<UserView>() {
                            @Override
                            public void run(@NonNull UserView view) {
                                view.showContent();
                                view.registerSuccess(user);
                            }
                        });
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
        void loginSuccess(User user);

        void loginFailure(String message);
    }
}
