package com.u91porn.ui.user;

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

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.ui.MvpActivity;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 */
public class UserLoginActivity extends MvpActivity<UserView, UserPresenter> implements UserView {

    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.wb_captcha)
    SimpleDraweeView simpleDraweeView;
    @BindView(R.id.bt_user_login)
    Button btUserLogin;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
    private AlertDialog alertDialog;
    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setContentInsetStartWithNavigation(0);
        setTitle("用户登录（仅本次有效）");
        loadCaptcha();
        btUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etAccount.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                String captcha = etCaptcha.getText().toString().trim();
                login(username, password, captcha);
            }
        });
        alertDialog = DialogUtils.initLodingDialog(this, "登录中，请稍后...");
        setUpUserInfo();
    }

    private void setUpUserInfo() {
        username = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_USERNAME, "");
        String ep = (String) SPUtils.get(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
        if (!TextUtils.isEmpty(ep)) {
            password = new String(Base64.decode(ep.getBytes(), Base64.DEFAULT));
        }
        etAccount.setText(username);
        etPassword.setText(password);
    }

    private void login(String username, String password, String captcha) {
        String f = "2192328484";
        String f2 = "2a0e17836fe7a3af469c00456b506eb9";
        String acl = "Log In";
        String x = "47";
        String y = "12";
        if (TextUtils.isEmpty(username)) {
            showMessage("请填写用户名");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showMessage("请填写密码");
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            showMessage("请填写验证码");
            return;
        }
        presenter.login(username, password, f, f2, captcha, acl, x, y);
    }

    /**
     * 加载验证码，目前似乎是非必须，不填也是可以登录的
     */
    private void loadCaptcha() {
        Uri uri = Uri.parse(MyApplication.getInstace().getHost() + "captcha.php");
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        imagePipeline.evictFromCache(uri);
        simpleDraweeView.setImageURI(uri);

        //创建DraweeController
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                //加载的图片URI地址
                .setUri(uri)
                //设置点击重试是否开启
                .setTapToRetryEnabled(true)
                //设置旧的Controller
                .setOldController(simpleDraweeView.getController())
                //构建
                .build();

        //设置DraweeController
        simpleDraweeView.setController(controller);
    }

    @NonNull
    @Override
    public UserPresenter createPresenter() {
        return new UserPresenter(noLimit91PornServiceApi);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void loginSuccess() {
        saveUserInfoPrf(username, password);
        showMessage("登录成功");
        setResult(RESULT_OK);
        onBackPressed();
    }

    private void saveUserInfoPrf(String username, String password) {
        SPUtils.put(this, Keys.KEY_SP_USER_LOGIN_USERNAME, username);
        SPUtils.put(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
    }

    @Override
    public void loginError() {
        showMessage("登录失败");
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
    public void showMessage(String msg) {
        super.showMessage(msg);
    }

    @Override
    public LifecycleTransformer<Reply<String>> bindView() {
        return bindToLifecycle();
    }
}
