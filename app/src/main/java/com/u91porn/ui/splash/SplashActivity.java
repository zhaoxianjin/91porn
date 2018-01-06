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
import com.u91porn.data.model.User;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.user.UserPresenter;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SPUtils;

import io.rx_cache2.Reply;

public class SplashActivity extends MvpActivity<SplashView, SplashPresenter> implements SplashView {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private int retryTime = 3;
    private String password;
    private String username;
    private String captcha = "3124";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //防止重复开启程序造成多次登录
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            Logger.t(TAG).d("重复打开了....");
            finish();
            return;
        }
        User user = MyApplication.getInstace().getUser();
        if (user != null) {
            startMain();
        }
        setContentView(R.layout.activity_splash);
        username = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_USERNAME, "");
        String ep = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
        if (!TextUtils.isEmpty(ep)) {
            password = new String(Base64.decode(ep.getBytes(), Base64.DEFAULT));
        }

        boolean isAutoLogin = (boolean) SPUtils.get(this, Keys.KEY_SP_USER_AUTO_LOGIN, false);

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && isAutoLogin) {
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
        presenter.login(username, password, f, f2, captcha, acl, x, y, HeaderUtils.getUserHeader("login"));
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityWithAnimotion(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @NonNull
    @Override
    public SplashPresenter createPresenter() {
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        UserPresenter userPresenter = new UserPresenter(noLimit91PornServiceApi,provider);
        return new SplashPresenter(userPresenter);
    }

    @Override
    public void loginSuccess() {
        startMain();
    }

    @Override
    public void loginError(String message) {
        //访问很容易超时，失败后重试
        if (retryTime <= 3) {
            retryTime++;
            login(username, password, captcha);
        } else {
            startMain();
        }

    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg,int type) {
        super.showMessage(msg,type);
    }
}
