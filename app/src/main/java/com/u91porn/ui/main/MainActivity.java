package com.u91porn.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jaeger.library.StatusBarUtil;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.BuildConfig;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.data.model.User;
import com.u91porn.service.UpdateDownloadService;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.about.AboutActivity;
import com.u91porn.ui.common.CommonFragment;
import com.u91porn.ui.download.DownloadActivity;
import com.u91porn.ui.favorite.FavoriteActivity;
import com.u91porn.ui.history.HistoryActivity;
import com.u91porn.ui.index.IndexFragment;
import com.u91porn.ui.recentupdates.RecentUpdatesFragment;
import com.u91porn.ui.search.SearchActivity;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.ApkVersionUtils;
import com.u91porn.utils.AppManager;
import com.u91porn.utils.Constants;
import com.u91porn.utils.Keys;
import com.u91porn.utils.PermissionConstants;
import com.u91porn.utils.PlaybackEngine;
import com.u91porn.utils.RegexUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.widget.IpInputEditText;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    @BindView(R.id.content)
    FrameLayout content;
    @BindView(R.id.status_bar)
    View statusBar;
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
    private int permisionCode = 300;
    private int permisionReqCode = 400;
    private String[] permission = PermissionConstants.getPermissions(PermissionConstants.STORAGE);
    private List<Fragment> fragments = new ArrayList<>();
    private String ipAddress;
    private String portStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
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
        initIndexFragment();

        checkUpdate();

        //testVersionUpdate();
        showDebugDBAddressLogToast(this);
        makeDirAndCheckPermision();
    }

    private void initIndexFragment() {
        mCurrentFragment = new Fragment();
        indexFragment = IndexFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content, indexFragment).commit();
        mCurrentFragment = indexFragment;
    }

    /**
     * 申请权限并创建下载目录
     */
    private void makeDirAndCheckPermision() {
        if (!AndPermission.hasPermission(MainActivity.this, permission)) {
            AndPermission.with(this)
                    .requestCode(permisionCode)
                    .permission(permission)
                    .rationale(new RationaleListener() {
                        @Override
                        public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                            // 此对话框可以自定义，调用rationale.resume()就可以继续申请。
                            AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                        }
                    })
                    .callback(listener)
                    .start();
        }
    }

    private PermissionListener listener = new PermissionListener() {
        File file = new File(Constants.DOWNLOAD_PATH);

        @Override
        public void onSucceed(int requestCode, @NonNull List<String> grantedPermissions) {
            // 权限申请成功回调。

            // 这里的requestCode就是申请时设置的requestCode。
            // 和onActivityResult()的requestCode一样，用来区分多个不同的请求。
            if (requestCode == permisionCode) {
                // TODO ...
                if (AndPermission.hasPermission(MainActivity.this, grantedPermissions)) {
                    if (!file.exists()) {
                        if (!file.mkdirs()) {
                            showMessage("创建下载目录失败了", TastyToast.ERROR);
                        }
                    }
                } else {
                    AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode).show();
                }
            }
        }

        @Override
        public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
            // 权限申请失败回调。
            if (requestCode == permisionCode) {
                // TODO ...
                if (!AndPermission.hasPermission(MainActivity.this, deniedPermissions)) {
                    // 是否有不再提示并拒绝的权限。
                    if (AndPermission.hasAlwaysDeniedPermission(MainActivity.this, deniedPermissions)) {
                        // 第一种：用AndPermission默认的提示语。
                        AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode).show();
                    } else {
                        AndPermission.defaultSettingDialog(MainActivity.this, permisionReqCode).show();
                    }
                }
            }
        }
    };

    public void showDebugDBAddressLogToast(Context context) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Method getAddressLog = debugDB.getMethod("getAddressLog");
                Object value = getAddressLog.invoke(null);
                showMessage((String) value, TastyToast.INFO);
            } catch (Exception ignore) {

            }
        }
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
        updateVersion.setUpdateMessage("1. 播放界面改版\n" +
                "2. 增加显示视频评论以及评论视频，回复评论\n" +
                "3. 集成APP崩溃日志收集\n" +
                "4. 修复安装包证书错误问题\n" +
                "5. 其他细小改进");
        showUpdateDialog(updateVersion);
//        Logger.t(TAG).d(new Gson().toJson(updateVersion));
//
//        Intent intent = new Intent(this, UpdateDownloadService.class);
//        intent.putExtra("updateVersion", updateVersion);
//        startService(intent);
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
        if (requestCode == permisionReqCode) {
            if (!AndPermission.hasPermission(MainActivity.this, permission)) {
                showMessage("你拒绝了读写存储卡权限，这将影响下载视频等功能！", TastyToast.WARNING);
            }
        } else if (requestCode == Constants.USER_LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            setUpUserInfo(MyApplication.getInstace().getUser());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpUserInfo(MyApplication.getInstace().getUser());
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

    public static final int MIN_CLICK_DELAY_TIME = 2000;
    private long lastClickTime = 0;

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showMessage("再次点击退出程序", TastyToast.INFO);
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
            } else {
                FileDownloader.getImpl().pauseAll();
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        existActivityWithAnimation = false;
                        MainActivity.super.onBackPressed();
                        AppManager.getAppManager().AppExit();
                    }
                }, 100);
            }
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
            showIPAddressSettingDialog();
            return true;
        } else if (id == R.id.action_exit_app) {
            FileDownloader.getImpl().pauseAll();
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
            return true;
        } else if (id == R.id.action_history) {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivityWithAnimotion(intent);
            return true;
        } else if (id == R.id.action_playback_engine) {
            showPlaybackEngineChoiceDialog();
            return true;
        } else if (id == R.id.action_search_video) {
            User user = MyApplication.getInstace().getUser();
            if (user == null) {
                showMessage("请先登录", TastyToast.INFO);
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
                return true;
            }
            Intent intent = new Intent(this, SearchActivity.class);
            startActivityWithAnimotion(intent);
            return true;
        } else if (id == R.id.action_proxy_setting) {
            showProxyAddressSettingDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showProxyAddressSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("HTTP代理设置（Beta）");
        View view = getLayoutInflater().inflate(R.layout.dialog_layout_proxy_setting, null);
        final IpInputEditText ipAddressEditText = view.findViewById(R.id.et_dialog_proxy_setting_ip_address);
        final AppCompatEditText portEditText = view.findViewById(R.id.et_dialog_proxy_setting_port);
        ipAddressEditText.setIpAddressStr(ipAddress);
        portEditText.setText(portStr);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ipAddress = ipAddressEditText.getIpAddressStr();
                Logger.t(TAG).d(ipAddress);
                portStr = portEditText.getText().toString().trim();
                if (TextUtils.isEmpty(portStr)) {
                    showProxyAddressSettingDialog();
                    showMessage("端口或地址不正确", TastyToast.INFO);
                    return;
                }
                int port = Integer.valueOf(portStr);
                if (RegexUtils.isIP(ipAddress) && port < Constants.PROXY_MAX_PORT) {
                    MyApplication.getInstace().initRetrofit(ipAddress, port);
                    getSupportFragmentManager().beginTransaction().remove(indexFragment).commit();
                    for (Fragment fragment : fragments) {
                        getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                    }
                    initIndexFragment();
                } else {
                    showProxyAddressSettingDialog();
                    showMessage("端口或地址不正确", TastyToast.INFO);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        builder.setNeutralButton("清空代理", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApplication.getInstace().initRetrofit("", 0);
                getSupportFragmentManager().beginTransaction().remove(indexFragment).commit();
                for (Fragment fragment : fragments) {
                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
                initIndexFragment();
            }
        });
        builder.show();
    }


    private void showPlaybackEngineChoiceDialog() {
        final String[] items = new String[]{"Google Exoplayer Engine", "JiaoZiPlayer Engine",};
        final int checkedIndex = (int) SPUtils.get(this, Keys.KEY_SP_PLAYBACK_ENGINE, PlaybackEngine.DEFAULT_PLAYER_ENGINE);
        new QMUIDialog.CheckableDialogBuilder(this)
                .setTitle("播放引擎选择")
                .setCheckedIndex(checkedIndex)
                .addItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtils.put(MainActivity.this, Keys.KEY_SP_PLAYBACK_ENGINE, which);
                        showMessage("设置成功", TastyToast.SUCCESS);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showIPAddressSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("访问地址设置");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_ip_address_setting, null);
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
        willGoRadioButton.setText(Constants.BASE_URL + "(不需翻墙，但会被封杀)");
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
                    if (RegexUtils.isURL(customAddress)) {
                        MyApplication.getInstace().setHost(customAddress);
                        SPUtils.put(MainActivity.this, Keys.KEY_SP_CUSTOM_ADDRESS, customAddress);
                    } else {
                        showIPAddressSettingDialog();
                        showMessage("设置失败，输入地址格式不正确", TastyToast.ERROR);
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
            fragments.add(mCurrentFragment);
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

    private void showUpdateDialog(final UpdateVersion updateVersion) {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("发现新版本--v" + updateVersion.getVersionName())
                .setMessage(updateVersion.getUpdateMessage())
                .addAction("立即更新", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                        showMessage("开始下载", TastyToast.INFO);
                        Intent intent = new Intent(MainActivity.this, UpdateDownloadService.class);
                        intent.putExtra("updateVersion", updateVersion);
                        startService(intent);
                    }
                })
                .addAction("稍后更新", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        return new MainPresenter(new UpdatePresenter(noLimit91PornServiceApi, new Gson(), provider));
    }

    @Override
    public void needUpdate(UpdateVersion updateVersion) {
        showUpdateDialog(updateVersion);
    }

    @Override
    public void noNeedUpdate() {
        Logger.t(TAG).d("当前已是最新版本");
    }

    @Override
    public void checkUpdateError(String message) {
        Logger.t(TAG).d("检查更新错误：" + message);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setStatusBarColor(@ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        int mStatusBarColor = ContextCompat.getColor(this, R.color.colorPrimary);
        StatusBarUtil.setColorForDrawerLayout(this, (DrawerLayout) findViewById(R.id.drawer_layout), mStatusBarColor, StatusBarUtil.DEFAULT_STATUS_BAR_ALPHA);
    }
}
