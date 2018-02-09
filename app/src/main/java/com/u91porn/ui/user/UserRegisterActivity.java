package com.u91porn.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.util.QMUIKeyboardHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.User;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.GlideApp;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.UserHelper;
import com.u91porn.utils.constants.Keys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Cookie;

/**
 * @author flymegoc
 */
public class UserRegisterActivity extends MvpActivity<UserView, UserPresenter> implements UserView {
    private static final String TAG = UserRegisterActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_email)
    EditText etEmail;
    @BindView(R.id.et_password_one)
    EditText etPasswordOne;
    @BindView(R.id.et_password_two)
    EditText etPasswordTwo;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.wb_captcha)
    ImageView wbCaptcha;
    @BindView(R.id.bt_user_signup)
    Button btUserSignup;

    private AlertDialog alertDialog;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        loadCaptcha();

        btUserSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etAccount.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String passwordOne = etPasswordOne.getText().toString().trim();
                String passwordTwo = etPasswordTwo.getText().toString().trim();
                String captcha = etCaptcha.getText().toString().trim();
                password = passwordOne;
                register(username, email, passwordOne, passwordTwo, captcha);
            }
        });
        wbCaptcha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCaptcha();
            }
        });
        alertDialog = DialogUtils.initLodingDialog(this, "注册中，请稍后...");

        apiManager.cleanCookies();
        List<Cookie> cookieList = apiManager.getSharedPrefsCookiePersistor().loadAll();
        for (Cookie cookie : cookieList) {
            Logger.t(TAG).d(cookie.toString());
        }
    }

    /**
     * 跳转主界面
     */
    private void startMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivityWithAnimotion(intent);
        finish();
    }

    private void register(String username, String email, String passwordOne, String passwordTwo, String captcha) {
        startMain();
        if (TextUtils.isEmpty(username)) {
            showMessage("用户名不能为空", TastyToast.INFO);
            return;
        }
        //服务器根本不会验证邮箱格式，貌似只要有@符号和.就可以通过注册了,不过如果后期验证邮箱....
        if (TextUtils.isEmpty(email)) {
            showMessage("邮箱不能为空", TastyToast.INFO);
            return;
        }
        if (TextUtils.isEmpty(passwordOne)) {
            showMessage("密码不能为空", TastyToast.INFO);
            return;
        }
        if (TextUtils.isEmpty(passwordTwo)) {
            showMessage("确认密码不能为空", TastyToast.INFO);
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            showMessage("验证码不能为空", TastyToast.INFO);
            return;
        }
        if (!passwordOne.equals(passwordTwo)) {
            showMessage("密码不一致，请检查", TastyToast.INFO);
            return;
        }
        String next = "";
//        String fingerprint = "2192328486";
        String fingerprint = UserHelper.randomFingerprint();
        String vip = "";
        String actionSignUp = "Sign Up";
        String submitX = "45";
        String submitY = "13";
        String ipAddress = AddressHelper.getRandomIPAddress();
        QMUIKeyboardHelper.hideKeyboard(getCurrentFocus());
        presenter.register(next, username, passwordOne, passwordTwo, email, captcha, fingerprint, vip, actionSignUp, submitX, submitY, ipAddress, HeaderUtils.getUserHeader("signup"));
    }

    @NonNull
    @Override
    public UserPresenter createPresenter() {
        getActivityComponent().inject(this);
        NoLimit91PornServiceApi noLimit91PornServiceApi = apiManager.getNoLimit91PornService();
        return new UserPresenter(noLimit91PornServiceApi, provider);
    }

    /**
     * 加载验证码，目前似乎是非必须，不填也是可以登录的
     */
    private void loadCaptcha() {
        String url = AddressHelper.getInstance().getVideo91PornAddress() + "captcha2.php";
        Logger.t(TAG).d("验证码链接：" + url);
        Uri uri = Uri.parse(url);
        GlideApp.with(this).load(uri).placeholder(R.drawable.placeholder).transition(new DrawableTransitionOptions().crossFade(300)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(wbCaptcha);
    }

    @Override
    public void loginSuccess(User user) {
        user.copyProperties(this.user);
    }

    @Override
    public void loginError(String message) {

    }

    @Override
    public void registerSuccess(User user) {
        user.copyProperties(this.user);
        saveUserInfoPrf(username, password);
        startMain();
        showMessage("注册成功", TastyToast.SUCCESS);
    }

    /**
     * 注册成功，默认保存用户名和密码
     */
    private void saveUserInfoPrf(String username, String password) {
        SPUtils.put(this, Keys.KEY_SP_USER_LOGIN_USERNAME, username);
        //记住密码
        SPUtils.put(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
    }

    @Override
    public void registerFailure(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        if (alertDialog == null) {
            return;
        }
        alertDialog.show();
    }

    @Override
    public void showContent() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

}
