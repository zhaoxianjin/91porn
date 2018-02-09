package com.u91porn.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;

import com.orhanobut.logger.Logger;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.User;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.user.UserPresenter;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.UserHelper;
import com.u91porn.utils.constants.Keys;

/**
 * @author flymegoc
 */
public class SplashActivity extends MvpActivity<SplashView, SplashPresenter> implements SplashView {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //防止重复开启程序造成多次登录
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            //结束你的activity
            Logger.t(TAG).d("重复打开了....");
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            return;
        }

        if (UserHelper.isUserInfoComplete(user)) {
            startMain();
        }
        setContentView(R.layout.activity_splash);
        String username = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_USERNAME, "");
        String ep = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
        if (!TextUtils.isEmpty(ep)) {
            password = new String(Base64.decode(ep.getBytes(), Base64.DEFAULT));
        }

        boolean isAutoLogin = (boolean) SPUtils.get(this, Keys.KEY_SP_USER_AUTO_LOGIN, false);

        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && isAutoLogin) {
            String captcha = UserHelper.randomCaptcha();
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
        if (AddressHelper.getInstance().isEmpty(Keys.KEY_SP_CUSTOM_ADDRESS)) {
            return;
        }
        presenter.login(username, password, f, f2, captcha, acl, x, y, HeaderUtils.getUserHeader("login"));
    }

    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @NonNull
    @Override
    public SplashPresenter createPresenter() {
        getActivityComponent().inject(this);
        NoLimit91PornServiceApi noLimit91PornServiceApi = null;
        if (!AddressHelper.getInstance().isEmpty(Keys.KEY_SP_CUSTOM_ADDRESS)) {
            noLimit91PornServiceApi = apiManager.getNoLimit91PornService();
        }
        UserPresenter userPresenter = new UserPresenter(noLimit91PornServiceApi, provider);
        return new SplashPresenter(userPresenter);
    }

    @Override
    public void loginSuccess(User user) {
        user.copyProperties(this.user);
        startMain();
    }

    @Override
    public void loginError(String message) {
        startMain();
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
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }
}
