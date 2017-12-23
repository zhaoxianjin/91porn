package com.u91porn.ui.user;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.User;
import com.u91porn.utils.ParseUtils;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * 用户登录
 *
 * @author flymegoc
 * @date 2017/12/10
 */

public class UserPresenter extends MvpBasePresenter<UserView> implements IUser {
    private NoLimit91PornServiceApi noLimit91PornServiceApi;

    public UserPresenter(NoLimit91PornServiceApi noLimit91PornServiceApi) {
        this.noLimit91PornServiceApi = noLimit91PornServiceApi;
    }

    @Override
    public void login(String username, String password, String fingerprint, String fingerprint2, String captcha, String actionlogin, String x, String y) {
        login(username, password, fingerprint, fingerprint2, captcha, actionlogin, x, y, null);
    }

    public void login(String username, String password, String fingerprint, String fingerprint2, String captcha, String actionlogin, String x, String y, final LoginListener loginListener) {
        noLimit91PornServiceApi.login(username, password, fingerprint, fingerprint2, captcha, actionlogin, x, y)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (isViewAttached() && loginListener == null) {
                            getView().showLoading(true);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        if (!s.contains("登录") || !s.contains("注册") || s.contains("退出")) {
                            User user = ParseUtils.parseUserInfo(s);
                            MyApplication.getInstace().setUser(user);
                            if (isViewAttached()) {
                                getView().loginSuccess();
                            } else if (loginListener != null) {
                                loginListener.loginSuccess();
                            }
                        } else {
                            String errorinfo = ParseUtils.parseErrorLoginInfo(s);
                            if (isViewAttached()) {
                                getView().loginError(errorinfo);
                            } else if (loginListener != null) {
                                loginListener.loginFailure(errorinfo);
                            }
                        }
                        if (isViewAttached()) {
                            getView().showContent();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached()) {
                            getView().loginError(e.getMessage());
                        } else if (loginListener != null) {
                            loginListener.loginFailure(e.getMessage());
                        }
                        if (isViewAttached()) {
                            getView().showContent();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void register(String next, String username, String password1, String password2, String email, String captchaInput, String fingerprint, String vip, String actionSignup, String submitX, String submitY, String ipAddress) {
        noLimit91PornServiceApi.register(next, username, password1, password2, email, captchaInput, fingerprint, vip, actionSignup, submitX, submitY, ipAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        if (isViewAttached()) {
                            getView().showLoading(true);
                        }
                    }

                    @Override
                    public void onNext(String s) {
                        Logger.d(s);
                        if (!s.contains("登录") || !s.contains("注册") || s.contains("退出")) {
                            User user = ParseUtils.parseUserInfo(s);
                            MyApplication.getInstace().setUser(user);
                            if (isViewAttached()) {
                                getView().registerSuccess();
                            }
                        } else {
                            String errorinfo = ParseUtils.parseErrorLoginInfo(s);
                            if (isViewAttached()) {
                                getView().registerFailure(errorinfo);
                            }
                        }
                        if (isViewAttached()) {
                            getView().showContent();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (isViewAttached()) {
                            getView().registerFailure(e.getMessage());
                        }
                        if (isViewAttached()) {
                            getView().showContent();
                        }
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public interface LoginListener {
        void loginSuccess();

        void loginFailure(String message);
    }
}
