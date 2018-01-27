package com.u91porn.ui.main;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.devbrackets.android.exomedia.util.ResourceUtil;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.ApiManager;
import com.u91porn.data.GitHubServiceApi;
import com.u91porn.data.model.Notice;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.data.model.User;
import com.u91porn.service.UpdateDownloadService;
import com.u91porn.ui.BaseFragment;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.basemain.BaseMainFragment;
import com.u91porn.ui.mine.MineFragment;
import com.u91porn.ui.music.MusicFragment;
import com.u91porn.ui.notice.NoticePresenter;
import com.u91porn.ui.search.SearchActivity;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.ApkVersionUtils;
import com.u91porn.utils.AppManager;
import com.u91porn.utils.Constants;
import com.u91porn.utils.FragmentUtils;
import com.u91porn.utils.Keys;
import com.u91porn.utils.PermissionConstants;
import com.u91porn.utils.SDCardUtils;
import com.u91porn.utils.SPUtils;
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
    @BindView(R.id.content)
    FrameLayout content;

    private Fragment mCurrentFragment;
    private int permisionCode = 300;
    private int permisionReqCode = 400;
    private String[] permission = PermissionConstants.getPermissions(PermissionConstants.STORAGE);
    private Main91PronVideoFragment mMain91PronVideoFragment;
    private MainMeiZiTuFragment mMaiMeiZiTuFragment;
    private Main91ForumFragment mMain91ForumFragment;
    private MusicFragment mMusicFragment;
    private MineFragment mMineFragment;
    private FragmentManager fragmentManager;
    private int selectIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mCurrentFragment = new Fragment();
        selectIndex = getIntent().getIntExtra(Keys.KEY_SELECT_INDEX, 0);
        if (savedInstanceState != null) {
            selectIndex = savedInstanceState.getInt(Keys.KEY_SELECT_INDEX);
        }
        initBottomNavigationBar(selectIndex);
        checkUpdate();
        checkNewNotice();
        makeDirAndCheckPermision();

        fabSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doOnFloatingActionButtonClick(selectIndex);

            }
        });
        doOnTabSelected(selectIndex);
    }

    private void doOnFloatingActionButtonClick(@IntRange(from = 0, to = 4) int position) {
        switch (position) {
            case 0:
                showVideoBottomSheet();
                break;
            case 1:
                showPictureBottomSheet();
                break;
            case 2:
                showForumBottomSheet();
                break;
            case 3:

                break;
            case 4:
                break;
            default:
        }
    }

    private void showVideoBottomSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(this)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_search_black_24dp), "搜索91视频")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), "91视频")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), "朱古力视频")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                goToSearchVideo();
                                break;
                            case 1:
                                break;
                            case 2:
                                break;
                            default:
                        }
                    }
                })
                .build()
                .show();
    }

    private void showPictureBottomSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(this)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), "妹子图")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), "花瓣网")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                break;
                            default:
                        }
                    }
                })
                .build()
                .show();
    }

    private void showForumBottomSheet() {
        new QMUIBottomSheet.BottomListSheetBuilder(this)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), "91论坛")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), "草榴社区")
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                break;
                            case 1:
                                break;
                            default:
                        }
                    }
                })
                .build()
                .show();
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
                if (mMain91PronVideoFragment == null) {
                    mMain91PronVideoFragment = Main91PronVideoFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMain91PronVideoFragment, content.getId(), 0);
                showFloatingActionButton(fabSearch);
                break;
            case 1:
                if (mMaiMeiZiTuFragment == null) {
                    mMaiMeiZiTuFragment = MainMeiZiTuFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMaiMeiZiTuFragment, content.getId(), 1);
                showFloatingActionButton(fabSearch);
                break;
            case 2:
                if (mMain91ForumFragment == null) {
                    mMain91ForumFragment = Main91ForumFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMain91ForumFragment, content.getId(), 2);
                showFloatingActionButton(fabSearch);
                break;
            case 3:
                if (mMusicFragment == null) {
                    mMusicFragment = MusicFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMusicFragment, content.getId(), 3);
                hideFloatingActionButton(fabSearch);
                break;
            case 4:
                if (mMineFragment == null) {
                    mMineFragment = MineFragment.getInstance();
                }
                mCurrentFragment = switchContent(mCurrentFragment, mMineFragment, content.getId(), 4);
                hideFloatingActionButton(fabSearch);
                break;
            default:
        }
        selectIndex = position;
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.t(TAG).d("----------onSaveInstanceState()");
        outState.putInt(Keys.KEY_SELECT_INDEX, selectIndex);
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
        File file = new File(SDCardUtils.DOWNLOAD_VIDEO_PATH);

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

    private void checkNewNotice() {
        int versionCode = (int) SPUtils.get(this, Keys.KEY_SP_NOTICE_VERSION_CODE, 1);
        presenter.checkNewNotice(versionCode);
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
        if (mCurrentFragment != null && mCurrentFragment instanceof BaseMainFragment && ((BaseMainFragment) mCurrentFragment).onBackPressed()) {
            return;
        }
        showMessage("再次点击退出程序", TastyToast.INFO);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
        } else {
            FileDownloader.getImpl().pauseAll();
            //没啥意义
            if (!existActivityWithAnimation) {
                super.onBackPressed();
            }
            finish();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppManager.getAppManager().AppExit();
                }
            }, 500);
        }
    }

    private void goToSearchVideo() {
        User user = MyApplication.getInstace().getUser();
        if (user == null) {
            showMessage("请先登录", TastyToast.INFO);
            Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
            intent.putExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, UserLoginActivity.LOGIN_ACTION_FOR_SEARCH_91PRON_VIDEO);
            startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
            return;
        }
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityWithAnimotion(intent);
    }


    //切换类型
    public Fragment switchContent(Fragment currentFragment, Fragment toShowFragment, int viewId, long itemId) {
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        Fragment fragment = null;
        if (currentFragment != toShowFragment) {
            FragmentTransaction transaction = fragmentManager.beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            // 先判断是否被add过
            String name = FragmentUtils.makeFragmentName(viewId, itemId);
            fragment = fragmentManager.findFragmentByTag(name);
            if (fragment == null) {
                // 隐藏当前的fragment，add下一个到Activity中
                transaction.hide(currentFragment).add(viewId, toShowFragment, FragmentUtils.makeFragmentName(viewId, itemId)).commit();
                fragment = toShowFragment;
            } else {
                // 隐藏当前的fragment，显示下一个
                transaction.hide(currentFragment).show(fragment).commit();
            }
        }
        return fragment;
    }

    private void showUpdateDialog(final UpdateVersion updateVersion) {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("发现新版本--v" + updateVersion.getVersionName());
        builder.setMessage(updateVersion.getUpdateMessage());
        builder.addAction("立即更新", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                showMessage("开始下载", TastyToast.INFO);
                Intent intent = new Intent(MainActivity.this, UpdateDownloadService.class);
                intent.putExtra("updateVersion", updateVersion);
                startService(intent);
            }
        });
        builder.addAction("稍后更新", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.setLeftAction("该版本不再提示", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                //保存版本号，用户对于此版本选择了不在提示
                SPUtils.put(MainActivity.this, Keys.KEY_SP_IGNORE_THIS_VERSION_UPDATE_TIP, updateVersion.getVersionCode());
                dialog.dismiss();
            }
        });
        builder.show();
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        GitHubServiceApi gitHubServiceApi = ApiManager.getInstance().getGitHubServiceApi();
        Gson gson = new Gson();
        return new MainPresenter(new UpdatePresenter(gitHubServiceApi, gson, provider), new NoticePresenter(gitHubServiceApi, gson, provider));
    }

    @Override
    public void needUpdate(UpdateVersion updateVersion) {
        int versionCode = (int) SPUtils.get(this, Keys.KEY_SP_IGNORE_THIS_VERSION_UPDATE_TIP, 0);
        //如果保存的版本号等于当前要升级的版本号，表示用户已经选择不在提示，不显示提示对话框了
        if (versionCode == updateVersion.getVersionCode()) {
            return;
        }
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
    public void haveNewNotice(Notice notice) {
        showNewNoticeDialog(notice);
    }

    private void showNewNoticeDialog(final Notice notice) {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("新公告");
        builder.setMessage(notice.getNoticeMessage());
        builder.addAction("我知道了", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                SPUtils.put(MainActivity.this, Keys.KEY_SP_NOTICE_VERSION_CODE, notice.getVersionCode());
            }
        });
        builder.show();
    }

    @Override
    public void noNewNotice() {
        Logger.t(TAG).d("没有新公告");
    }

    @Override
    public void checkNewNoticeError(String message) {
        Logger.t(TAG).d("检查新公告：" + message);
    }
}
