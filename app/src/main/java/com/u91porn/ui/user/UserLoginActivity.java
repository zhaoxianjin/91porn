package com.u91porn.ui.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.u91porn.ui.favorite.FavoriteActivity;
import com.u91porn.ui.porn91video.search.SearchActivity;
import com.u91porn.ui.setting.SettingActivity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.GlideApp;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.UserHelper;
import com.u91porn.utils.constants.Keys;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class UserLoginActivity extends MvpActivity<UserView, UserPresenter> implements UserView {

    public static final int LOGIN_ACTION_FOR_LOOK_MY_FAVORITE = 1;
    public static final int LOGIN_ACTION_FOR_SEARCH_91PRON_VIDEO = 2;

    private static final String TAG = UserLoginActivity.class.getSimpleName();
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.et_captcha)
    EditText etCaptcha;
    @BindView(R.id.wb_captcha)
    ImageView simpleDraweeView;
    @BindView(R.id.bt_user_login)
    Button btUserLogin;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.cb_remenber_password)
    CheckBox cbRemenberPassword;
    @BindView(R.id.cb_auto_login)
    CheckBox cbAutoLogin;

    private AlertDialog alertDialog;
    private String username;
    private String password;
    private int loginForAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        loginForAction = getIntent().getIntExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, 0);
        if (!AddressHelper.getInstance().isEmpty(Keys.KEY_SP_CUSTOM_ADDRESS)) {
            loadCaptcha();
        }
        btUserLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = etAccount.getText().toString().trim();
                password = etPassword.getText().toString().trim();
                String captcha = etCaptcha.getText().toString().trim();
                login(username, password, captcha);
            }
        });
        simpleDraweeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadCaptcha();
            }
        });

        cbAutoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbAutoLogin.isChecked()) {
                    cbAutoLogin.setChecked(true);
                    cbRemenberPassword.setChecked(true);
                } else {
                    cbAutoLogin.setChecked(false);
                }
            }
        });

        cbRemenberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbRemenberPassword.isChecked()) {
                    cbRemenberPassword.setChecked(true);
                } else {
                    cbRemenberPassword.setChecked(false);
                    cbAutoLogin.setChecked(false);
                }
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
            cbRemenberPassword.setChecked(true);
        }
        boolean isAutoLogin = (boolean) SPUtils.get(this, Keys.KEY_SP_USER_AUTO_LOGIN, false);
        cbAutoLogin.setChecked(isAutoLogin);

        etAccount.setText(username);
        etPassword.setText(password);
    }

    private void login(String username, String password, String captcha) {
        String f = UserHelper.randomFingerprint();
        String f2 = UserHelper.randomFingerprint2();
        Logger.t(TAG).d("F:" + f);
        Logger.t(TAG).d("F2:" + f2);
        String acl = "Log In";
        String x = "47";
        String y = "12";
        if (TextUtils.isEmpty(username)) {
            showMessage("请填写用户名", TastyToast.INFO);
            return;
        }
        if (TextUtils.isEmpty(password)) {
            showMessage("请填写密码", TastyToast.INFO);
            return;
        }
        if (TextUtils.isEmpty(captcha)) {
            showMessage("请填写验证码", TastyToast.INFO);
            return;
        }
        QMUIKeyboardHelper.hideKeyboard(getCurrentFocus());
        presenter.login(username, password, f, f2, captcha, acl, x, y, HeaderUtils.getUserHeader("login"));
    }

    /**
     * 加载验证码，目前似乎是非必须，不填也是可以登录的
     */
    private void loadCaptcha() {
        String url = AddressHelper.getInstance().getVideo91PornAddress() + "captcha.php";

        Logger.t(TAG).d("验证码链接：" + url);
        Uri uri = Uri.parse(url);
        GlideApp.with(this).load(uri).placeholder(R.drawable.placeholder).transition(new DrawableTransitionOptions().crossFade(300)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(simpleDraweeView);
    }

    @NonNull
    @Override
    public UserPresenter createPresenter() {
        getActivityComponent().inject(this);
        NoLimit91PornServiceApi noLimit91PornServiceApi = null;
        if (!AddressHelper.getInstance().isEmpty(Keys.KEY_SP_CUSTOM_ADDRESS)) {
            noLimit91PornServiceApi = apiManager.getNoLimit91PornService();
        } else {
            showNeedSetAddressFirstDialog();
        }
        return new UserPresenter(noLimit91PornServiceApi, provider);
    }

    private void showNeedSetAddressFirstDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("温馨提示");
        builder.setMessage("还未设置91porn视频地址,无法登录或注册，现在去设置？");
        builder.setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, SettingActivity.class);
                startActivityWithAnimotion(intent);
                finish();
            }
        });
        builder.setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void loginSuccess(User user) {
        user.copyProperties(this.user);
        saveUserInfoPrf(username, password);
        showMessage("登录成功", TastyToast.SUCCESS);
        switch (loginForAction) {
            case LOGIN_ACTION_FOR_LOOK_MY_FAVORITE:
                Intent intent = new Intent(this, FavoriteActivity.class);
                startActivityWithAnimotion(intent);
                finish();
                break;
            case LOGIN_ACTION_FOR_SEARCH_91PRON_VIDEO:
                Intent intentSearch = new Intent(this, SearchActivity.class);
                startActivityWithAnimotion(intentSearch);
                finish();
                break;
            default:
                setResult(RESULT_OK);
                onBackPressed();
        }

    }

    private void saveUserInfoPrf(String username, String password) {
        SPUtils.put(this, Keys.KEY_SP_USER_LOGIN_USERNAME, username);
        //记住密码
        if (cbRemenberPassword.isChecked()) {
            SPUtils.put(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, Base64.encodeToString(password.getBytes(), Base64.DEFAULT));
        } else {
            SPUtils.put(this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
        }
        //自动登录
        if (cbAutoLogin.isChecked()) {
            SPUtils.put(this, Keys.KEY_SP_USER_AUTO_LOGIN, true);
        } else {
            SPUtils.put(this, Keys.KEY_SP_USER_AUTO_LOGIN, false);
        }
    }

    @Override
    public void loginError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void registerSuccess(User user) {
        user.copyProperties(this.user);
    }

    @Override
    public void registerFailure(String message) {

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_user_register) {
            Intent intent = new Intent(this, UserRegisterActivity.class);
            startActivityWithAnimotion(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
