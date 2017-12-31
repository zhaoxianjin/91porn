package com.u91porn.ui.splash;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.ui.user.UserPresenter;

/**
 * @author flymegoc
 * @date 2017/12/21
 */

public class SplashPresenter extends MvpBasePresenter<SplashView> implements ISplash {

    private UserPresenter userPresenter;

    public SplashPresenter(UserPresenter userPresenter) {
        this.userPresenter = userPresenter;
    }

    @Override
    public void login(String username, String password, String fingerprint, String fingerprint2, String captcha, String actionlogin, String x, String y) {
        userPresenter.login(username, password, fingerprint, fingerprint2, captcha, actionlogin, x, y, new UserPresenter.LoginListener() {
            @Override
            public void loginSuccess() {
                ifViewAttached(new ViewAction<SplashView>() {
                    @Override
                    public void run(@NonNull SplashView view) {
                        view.loginSuccess();
                    }
                });
            }

            @Override
            public void loginFailure(final String message) {
                ifViewAttached(new ViewAction<SplashView>() {
                    @Override
                    public void run(@NonNull SplashView view) {
                        view.loginError(message);
                    }
                });
            }
        });
    }
}
