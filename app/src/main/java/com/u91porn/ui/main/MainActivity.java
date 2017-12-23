package com.u91porn.ui.main;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.barlibrary.ImmersionBar;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.data.model.User;
import com.u91porn.service.DownloadService;
import com.u91porn.ui.BaseAppCompatActivity;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.about.AboutActivity;
import com.u91porn.ui.common.CommonFragment;
import com.u91porn.ui.download.DownloadActivity;
import com.u91porn.ui.favorite.FavoriteActivity;
import com.u91porn.ui.history.HistoryActivity;
import com.u91porn.ui.index.IndexFragment;
import com.u91porn.ui.recentupdates.RecentUpdatesFragment;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.ApkVersionUtils;
import com.u91porn.utils.AppManager;
import com.u91porn.utils.CallBackWrapper;
import com.u91porn.utils.Constants;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SPUtils;
import com.yanzhenjie.permission.AndPermission;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 */
public class MainActivity extends MvpActivity<MainView, MainPresenter> implements NavigationView.OnNavigationItemSelectedListener, MainView {

    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navView;
    private ImageView userHeadImageView;
    private Fragment mCurrentFragment;
    private IndexFragment indexFragment;
    private CommonFragment commonFragment;
    private CommonFragment rpFragment;
    private CommonFragment tenMinutesFragment;
    private CommonFragment thisMonthFragment;
    private CommonFragment thisMonthCollectFragment;
    private CommonFragment mostCollectFragment;
    private CommonFragment nearScoreFragment;
    private CommonFragment thisMonthHotFragment;
    private CommonFragment lastMonthHotFragment;
    private CommonFragment hdVideoFragment;
    private RecentUpdatesFragment recentUpdatesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImmersionBar.with(this).init();
        ButterKnife.bind(this);
        File file = new File(Constants.DOWNLOAD_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        AndPermission.with(this)
                .requestCode(300)
                .permission(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .start();
        setSupportActionBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);
        userHeadImageView = navView.getHeaderView(0).findViewById(R.id.imageView);
        userHeadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = MyApplication.getInstace().getUser();
                if (user != null) {
                    showExitDialog();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
            }
        });
        mCurrentFragment = new Fragment();
        indexFragment = IndexFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content, indexFragment).commit();
        mCurrentFragment = indexFragment;

        setUpUserInfo(MyApplication.getInstace().getUser());

        checkUpdate();
    }

    private void checkUpdate() {
        int versionCode = ApkVersionUtils.getVersionCode(this);
        if (versionCode == 0) {
            Logger.t(TAG).d("获取应用本版失败");
            return;
        }
        presenter.checkUpdate(versionCode);
    }

    private void testVersionUpdate() {
        UpdateVersion updateVersion = new UpdateVersion();
        updateVersion.setVersionCode(4);
        updateVersion.setVersionName("1.0.4");
        updateVersion.setApkDownloadUrl("https://raw.githubusercontent.com/techGay/91porn/master/apk/app-beta_v1.0.2.apk");
        updateVersion.setUpdateMessage("新增用户注册功能");

        Logger.t(TAG).d(new Gson().toJson(updateVersion));

        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("updateVersion", updateVersion);
        startService(intent);
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("切换帐号");
        builder.setMessage("切换帐号登录还是退出当前帐号？");
        builder.setPositiveButton("退出当前帐号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApplication.getInstace().cleanCookies();
                MyApplication.getInstace().setUser(null);
                setUpUserInfo(null);
                SPUtils.put(MainActivity.this, Keys.KEY_SP_USER_LOGIN_USERNAME, "");
                SPUtils.put(MainActivity.this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
                SPUtils.put(MainActivity.this, Keys.KEY_SP_USER_AUTO_LOGIN, false);
            }
        });
        builder.setNegativeButton("切换帐号", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.USER_LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            setUpUserInfo(MyApplication.getInstace().getUser());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    private void setUpUserInfo(User user) {

        View headerView = navView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.tv_nav_username);
        TextView lastLoginTime = headerView.findViewById(R.id.tv_nav_last_login_time);
        TextView lastLoginIP = headerView.findViewById(R.id.tv_nav_last_login_ip);

        if (user == null) {
            userNameTextView.setText("请登录");
            lastLoginTime.setText("---");
            lastLoginIP.setText("---");
            return;
        }

        String status = user.getStatus().contains("正常") ? "正常" : "异常";
        userNameTextView.setText(user.getUserName() + "(" + status + ")");
        lastLoginTime.setText(user.getLastLoginTime().replace("(如果你觉得时间不对,可能帐号被盗)", ""));
        lastLoginIP.setText(user.getLastLoginIP());
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            Intent setIntent = new Intent(Intent.ACTION_MAIN);
            setIntent.addCategory(Intent.CATEGORY_HOME);
            setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(setIntent);
            // super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showSettingDialog();
            return true;
        } else if (id == R.id.action_exit_app) {
            Intent homeIntent = new Intent(Intent.ACTION_MAIN);
            homeIntent.addCategory(Intent.CATEGORY_HOME);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getAppManager().AppExit();
                }
            }, 100);

        } else if (id == R.id.action_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivityWithAnimotion(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("访问地址设置");
        View view = LayoutInflater.from(this).inflate(R.layout.setting_dialog_layout, null);
        final RadioGroup radioGroup = view.findViewById(R.id.rg_address);
        RadioButton naverRadioButton = view.findViewById(R.id.rb_never_go_address);
        RadioButton willGoRadioButton = view.findViewById(R.id.rb_will_go_someday);
        RadioButton customRadioButton = view.findViewById(R.id.rb_now_custom_adress);
        final EditText editText = view.findViewById(R.id.et_custom_ip_address);
        final String customAddress = (String) SPUtils.get(MainActivity.this, Keys.KEY_SP_CUSTOM_ADDRESS, "");
        String nowAddress = (String) SPUtils.get(this, Keys.KEY_SP_NOW_ADDRESS, "");
        if (TextUtils.isEmpty(customAddress)) {
            customRadioButton.setVisibility(View.GONE);
        } else {
            customRadioButton.setText(customAddress + "(当前自定义地址)");
        }
        if (!TextUtils.isEmpty(nowAddress)) {
            if (nowAddress.equals(Constants.NEVER_GO_ADDRESS)) {
                naverRadioButton.setChecked(true);
            } else if (nowAddress.equals(Constants.BASE_URL)) {
                willGoRadioButton.setChecked(true);
            } else if (nowAddress.equals(customAddress)) {
                customRadioButton.setVisibility(View.VISIBLE);
                customRadioButton.setText(customAddress + "(当前自定义地址)");
                customRadioButton.setChecked(true);
            }
        }
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String customAddress = editText.getText().toString().trim();
                //优先填入的自定义地址
                if (!TextUtils.isEmpty(customAddress)) {
                    //简单验证地址是否合法
                    if (customAddress.contains("http://") && customAddress.endsWith("/")) {
                        MyApplication.getInstace().setHost(customAddress);
                        SPUtils.put(MainActivity.this, Keys.KEY_SP_CUSTOM_ADDRESS, customAddress);
                    } else {
                        showMessage("设置失败，输入地址格式不正确");
                    }

                } else {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.rb_never_go_address:
                            MyApplication.getInstace().setHost(Constants.NEVER_GO_ADDRESS);
                            break;
                        case R.id.rb_will_go_someday:
                            MyApplication.getInstace().setHost(Constants.BASE_URL);
                            break;
                        case R.id.rb_now_custom_adress:
                            MyApplication.getInstace().setHost(customAddress);
                            break;
                        default:
                    }
                }


            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        boolean needCloseMenu = true;
        if (id == R.id.nav_index) {
            // Handle the camera action
            if (mCurrentFragment != indexFragment) {
                if (indexFragment == null) {
                    indexFragment = IndexFragment.getInstance();
                }
                switchContent(mCurrentFragment, indexFragment);
                setTitle(R.string.index);
            }
        } else if (id == R.id.nav_hot) {
            if (mCurrentFragment != commonFragment) {
                if (commonFragment == null) {
                    commonFragment = CommonFragment.getInstance("hot", null);
                }
                switchContent(mCurrentFragment, commonFragment);
                setTitle(R.string.hot);
            }
        } else if (id == R.id.nav_rp) {
            if (mCurrentFragment != rpFragment) {
                if (rpFragment == null) {
                    rpFragment = CommonFragment.getInstance("rp", null);
                }
                switchContent(mCurrentFragment, rpFragment);
                setTitle(R.string.near_rp);
            }
        } else if (id == R.id.nav_ten_minutes) {
            if (mCurrentFragment != tenMinutesFragment) {
                if (tenMinutesFragment == null) {
                    tenMinutesFragment = CommonFragment.getInstance("long", null);
                }
                switchContent(mCurrentFragment, tenMinutesFragment);
                setTitle(R.string.ten_minutes);
            }
        } else if (id == R.id.nav_this_months) {
            if (mCurrentFragment != thisMonthFragment) {
                if (thisMonthFragment == null) {
                    thisMonthFragment = CommonFragment.getInstance("md", null);
                }
                switchContent(mCurrentFragment, thisMonthFragment);
                setTitle(R.string.this_month);
            }

        } else if (id == R.id.nav_this_months_collect) {
            if (mCurrentFragment != thisMonthCollectFragment) {
                if (thisMonthCollectFragment == null) {
                    thisMonthCollectFragment = CommonFragment.getInstance("tf", null);
                }
                switchContent(mCurrentFragment, thisMonthCollectFragment);
                setTitle(R.string.this_month_collect);
            }
        } else if (id == R.id.nav_most_collect) {
            if (mCurrentFragment != mostCollectFragment) {
                if (mostCollectFragment == null) {
                    mostCollectFragment = CommonFragment.getInstance("mf", null);
                }
                switchContent(mCurrentFragment, mostCollectFragment);
                setTitle(R.string.most_collect);
            }
        } else if (id == R.id.nav_near_score) {
            if (mCurrentFragment != nearScoreFragment) {
                if (nearScoreFragment == null) {
                    nearScoreFragment = CommonFragment.getInstance("rf", null);
                }
                switchContent(mCurrentFragment, nearScoreFragment);
                setTitle(R.string.near_score);
            }
        } else if (id == R.id.nav_this_months_hot) {
            if (mCurrentFragment != thisMonthHotFragment) {
                if (thisMonthHotFragment == null) {
                    thisMonthHotFragment = CommonFragment.getInstance("top", null);
                }
                switchContent(mCurrentFragment, thisMonthHotFragment);
                setTitle(R.string.this_month_hot);
            }
        } else if (id == R.id.nav_recent_updates) {
            if (mCurrentFragment != recentUpdatesFragment) {
                if (recentUpdatesFragment == null) {
                    recentUpdatesFragment = RecentUpdatesFragment.newInstance("watch");
                }
                switchContent(mCurrentFragment, recentUpdatesFragment);
                setTitle(R.string.recent_updates);
            }
        } else if (id == R.id.nav_last_months_hot) {
            if (mCurrentFragment != lastMonthHotFragment) {
                if (lastMonthHotFragment == null) {
                    lastMonthHotFragment = CommonFragment.getInstance("top", "-1");
                }
                switchContent(mCurrentFragment, lastMonthHotFragment);
                setTitle(R.string.last_month_hot);
            }
        } else if (id == R.id.nav_hd_video) {
            if (mCurrentFragment != hdVideoFragment) {
                if (hdVideoFragment == null) {
                    hdVideoFragment = CommonFragment.getInstance("hd", null);
                }
                switchContent(mCurrentFragment, hdVideoFragment);
                setTitle(R.string.hd_video);
            }
        } else if (id == R.id.nav_my_download) {
            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
            startActivityWithAnimotion(intent);
            needCloseMenu = false;
        } else if (id == R.id.nav_my_collect) {
            User user = MyApplication.getInstace().getUser();
            if (user == null) {
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
                return true;
            }
            Intent intent = new Intent(MainActivity.this, FavoriteActivity.class);
            startActivityWithAnimotion(intent);
            needCloseMenu = false;
        } else if (id == R.id.nav_about) {
            needCloseMenu = false;
            Intent intent = new Intent(this, AboutActivity.class);
            startActivityWithAnimotion(intent);
        }
        if (needCloseMenu) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    //切换类型
    public void switchContent(Fragment toHide, Fragment toShow) {
        if (mCurrentFragment != toShow) {
            mCurrentFragment = toShow;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            // 先判断是否被add过
            if (!toShow.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                transaction.hide(toHide).add(R.id.content, toShow).commit();
            } else {
                // 隐藏当前的fragment，显示下一个
                transaction.hide(toHide).show(toShow).commit();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImmersionBar.with(this).destroy();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        return new MainPresenter(new UpdatePresenter(noLimit91PornServiceApi, new Gson()));
    }

    @Override
    public void needUpdate(UpdateVersion updateVersion) {

    }

    @Override
    public void noNeedUpdate() {

    }

    @Override
    public void checkUpdateError(String message) {

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
        super.showMessage(msg);
    }

    @Override
    public LifecycleTransformer<Reply<String>> bindView() {
        return bindToLifecycle();
    }
}
