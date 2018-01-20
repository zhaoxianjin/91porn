package com.u91porn.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.devbrackets.android.exomedia.util.ResourceUtil;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.GitHubServiceApi;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.data.model.User;
import com.u91porn.service.UpdateDownloadService;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.forum.ForumFragment;
import com.u91porn.ui.main91porn.Main91PronContainerFragment;
import com.u91porn.ui.meizitu.MeiZiTuFragment;
import com.u91porn.ui.mine.MineFragment;
import com.u91porn.ui.music.MusicFragment;
import com.u91porn.ui.search.SearchActivity;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.ApkVersionUtils;
import com.u91porn.utils.AppManager;
import com.u91porn.utils.Constants;
import com.u91porn.utils.Keys;
import com.u91porn.utils.PermissionConstants;
import com.u91porn.utils.SDCardUtils;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.bottom_navigation_bar)
    BottomNavigationBar bottomNavigationBar;
    @BindView(R.id.fab_search)
    FloatingActionButton fabSearch;

    private Fragment mCurrentFragment;
    private int permisionCode = 300;
    private int permisionReqCode = 400;
    private String[] permission = PermissionConstants.getPermissions(PermissionConstants.STORAGE);
    private Main91PronContainerFragment mMain91PronContainerFragment;
    private MeiZiTuFragment mMeiZiTuFragment;
    private ForumFragment mForumFragment;
    private MusicFragment mMusicFragment;
    private MineFragment mMineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCurrentFragment = new Fragment();
        int selectIndex = getIntent().getIntExtra(Keys.KEY_SELECT_INDEX, 0);

        initBottomNavigationBar(selectIndex);
        checkUpdate();

        //testVersionUpdate();
        makeDirAndCheckPermision();
        mMain91PronContainerFragment = Main91PronContainerFragment.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.content, mMain91PronContainerFragment).commit();
        mCurrentFragment = mMain91PronContainerFragment;

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToSearchVideo();
            }
        });
        if (selectIndex != 0) {
            doOnTabSelected(selectIndex);
        }
    }

    private void initBottomNavigationBar(@IntRange(from = 0, to = 4) int position) {
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), R.string.title_video));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), R.string.title_photo));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), R.string.title_forum));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_music_black_24dp), R.string.title_music));
        bottomNavigationBar.addItem(new BottomNavigationItem(ResourceUtil.getDrawable(this, R.drawable.ic_menu_black_24dp), R.string.title_me));

        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar.setActiveColor(R.color.bottom_navigation_bar_active);
        bottomNavigationBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC);

        bottomNavigationBar.setFirstSelectedPosition(position);
        bottomNavigationBar.setTabSelectedListener(new BottomNavigationBar.SimpleOnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                doOnTabSelected(position);
            }
        });

        bottomNavigationBar.setBarBackgroundColor(R.color.bottom_navigation_bar_background);
        bottomNavigationBar.setFab(fabSearch);
        bottomNavigationBar.initialise();
    }

    private void doOnTabSelected(@IntRange(from = 0, to = 4) int position) {
        switch (position) {
            case 0:
                if (mMain91PronContainerFragment == null) {
                    mMain91PronContainerFragment = Main91PronContainerFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMain91PronContainerFragment);
                showFloatingActionButton(fabSearch);
                break;
            case 1:
                if (mMeiZiTuFragment == null) {
                    mMeiZiTuFragment = MeiZiTuFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMeiZiTuFragment);
                hideFloatingActionButton(fabSearch);
                break;
            case 2:
                if (mForumFragment == null) {
                    mForumFragment = ForumFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mForumFragment);
                hideFloatingActionButton(fabSearch);
                break;
            case 3:
                if (mMusicFragment == null) {
                    mMusicFragment = MusicFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMusicFragment);
                hideFloatingActionButton(fabSearch);
                break;
            case 4:
                if (mMineFragment == null) {
                    mMineFragment = MineFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMineFragment);
                hideFloatingActionButton(fabSearch);
                break;
            default:
        }
    }

    private void hideFloatingActionButton(FloatingActionButton fabSearch) {
        ViewGroup.LayoutParams layoutParams = fabSearch.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams coLayoutParams = (CoordinatorLayout.LayoutParams) layoutParams;
            FloatingActionButton.Behavior behavior = new FloatingActionButton.Behavior();
            coLayoutParams.setBehavior(behavior);
        }
        fabSearch.hide();
    }

    private void showFloatingActionButton(final FloatingActionButton fabSearch) {
        fabSearch.show(new FloatingActionButton.OnVisibilityChangedListener() {
            @Override
            public void onShown(FloatingActionButton fab) {
                fabSearch.requestLayout();
                bottomNavigationBar.setFab(fab);
            }
        });
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
        File file = new File(SDCardUtils.DOWNLOAD_PATH);

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

    private void checkUpdate() {
        int versionCode = ApkVersionUtils.getVersionCode(this);
        if (versionCode == 0) {
            Logger.t(TAG).d("获取应用本版失败");
            return;
        }
        presenter.checkUpdate(versionCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == permisionReqCode) {
            if (!AndPermission.hasPermission(MainActivity.this, permission)) {
                showMessage("你拒绝了读写存储卡权限，这将影响下载视频等功能！", TastyToast.WARNING);
            }
        }
        if (mCurrentFragment != null) {
            mCurrentFragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public static final int MIN_CLICK_DELAY_TIME = 2000;
    private long lastClickTime = 0;

    @Override
    public void onBackPressed() {
        if (mMain91PronContainerFragment != null && mMain91PronContainerFragment.onBackPressed()) {
            return;
        }
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

    private void goToSearchVideo() {
        User user = MyApplication.getInstace().getUser();
        if (user == null) {
            showMessage("请先登录", TastyToast.INFO);
            Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
            intent.putExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION,UserLoginActivity.LOGIN_ACTION_FOR_SEARCH_91PRON_VIDEO);
            startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
            return;
        }
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityWithAnimotion(intent);
    }


    //切换类型
    public Fragment switchContent(Fragment currentFragment, Fragment toShowFragment) {
        if (currentFragment != toShowFragment) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            // 先判断是否被add过
            if (!toShowFragment.isAdded()) {
                // 隐藏当前的fragment，add下一个到Activity中
                transaction.hide(currentFragment).add(R.id.content, toShowFragment).commit();
            } else {
                // 隐藏当前的fragment，显示下一个
                transaction.hide(currentFragment).show(toShowFragment).commit();
            }
        }
        return toShowFragment;
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
        GitHubServiceApi gitHubServiceApi = MyApplication.getInstace().getGitHubServiceApi();
        return new MainPresenter(new UpdatePresenter(gitHubServiceApi, new Gson(), provider));
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
}
