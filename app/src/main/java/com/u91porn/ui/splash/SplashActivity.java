package com.u91porn.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.user.UserPresenter;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SPUtils;

import io.rx_cache2.Reply;

public class SplashActivity extends MvpActivity<SplashView, SplashPresenter> implements SplashView {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private int retryTime = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        String username = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_USERNAME, "");
        String ep = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
        String password = null;
        if (!TextUtils.isEmpty(ep)) {
            password = new String(Base64.decode(ep.getBytes(), Base64.DEFAULT));
        }
        String captcha = "3124";
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            login(username, password, captcha);
        } else {
            startMain();
        }
    }

    private void login(String username, String password, String captcha) {
        String f = "2192328484";
        String f2 = "2a0e17836fe7a3af469c00456b506eb9";
        String acl = "Log In";
        String x = "47";
        String y = "12";
        presenter.login(username, password, f, f2, captcha, acl, x, y);
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityWithAnimotion(intent);
        finish();
    }

    @NonNull
    @Override
    public SplashPresenter createPresenter() {
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        UserPresenter userPresenter = new UserPresenter(noLimit91PornServiceApi);
        return new SplashPresenter(userPresenter);
    }

    @Override
    public void loginSuccess() {
        startMain();
    }

    @Override
    public void loginError(String message) {
        String username = "flymegoc";
        String password = "ngl3100757105";
        String captcha = "5345";
        if (retryTime <= 3) {
            retryTime++;
            login(username, password, captcha);
        } else {
            startMain();
        }

    }

    @Override
    public String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg) {

    }

    @Override
    public LifecycleTransformer<Reply<String>> bindView() {
        return bindToLifecycle();
    }
}
