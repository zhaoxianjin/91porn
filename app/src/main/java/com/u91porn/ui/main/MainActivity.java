package com.u91porn.ui.main;

import android.content.Intent;
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
import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Severity;
import com.devbrackets.android.exomedia.util.ResourceUtil;
import com.google.gson.Gson;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIBottomSheet;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.BuildConfig;
import com.u91porn.R;
import com.u91porn.data.GitHubServiceApi;
import com.u91porn.data.model.Notice;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.eventbus.LowMemoryEvent;
import com.u91porn.service.UpdateDownloadService;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.basemain.BaseMainFragment;
import com.u91porn.ui.images.Main99MmFragment;
import com.u91porn.ui.images.MainMeiZiTuFragment;
import com.u91porn.ui.mine.MineFragment;
import com.u91porn.ui.music.MusicFragment;
import com.u91porn.ui.notice.NoticePresenter;
import com.u91porn.ui.pigav.MainPigAvFragment;
import com.u91porn.ui.porn91forum.Main91ForumFragment;
import com.u91porn.ui.porn91video.Main91PronVideoFragment;
import com.u91porn.ui.porn91video.search.SearchActivity;
import com.u91porn.ui.setting.SettingActivity;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.ApkVersionUtils;
import com.u91porn.utils.FragmentUtils;
import com.u91porn.utils.SDCardUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.UserHelper;
import com.u91porn.utils.constants.Constants;
import com.u91porn.utils.constants.Keys;
import com.u91porn.utils.constants.PermissionConstants;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class MainActivity extends MvpActivity<MainView, MainPresenter> implements MainView {
    final int PORN91 = 1;
    final int PIG_AV = 2;
    final int MEI_ZI_TU = 0;
    final int MM_99 = 1;
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
    private Main99MmFragment mMain99MmFragment;
    private MainPigAvFragment mMainPigAvFragment;
    private MusicFragment mMusicFragment;
    private MineFragment mMineFragment;
    private FragmentManager fragmentManager;
    private int selectIndex;
    private int firstTabShow;
    private int secondTabShow;
    private boolean isBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
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
        firstTabShow = (int) SPUtils.get(this, Keys.KEY_SP_FIRST_TAB_SHOW, PORN91);
        secondTabShow = (int) SPUtils.get(this, Keys.KEY_SP_SECOND_TAB_SHOW, MEI_ZI_TU);
        doOnTabSelected(selectIndex);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Logger.t(TAG).d("onNewIntent");
    }

    private void doOnFloatingActionButtonClick(@IntRange(from = 0, to = 4) int position) {
        switch (position) {
            case 0:
                showVideoBottomSheet(firstTabShow);
                break;
            case 1:
                showPictureBottomSheet(secondTabShow);
                break;
            case 2:
                showForumBottomSheet(0);
                break;
            case 3:

                break;
            case 4:
                break;
            default:
        }
    }

    private void showVideoBottomSheet(int checkIndex) {
        new QMUIBottomSheet.BottomListSheetBuilder(this, true)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_search_black_24dp), "搜索91视频")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), "91视频")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_video_library_black_24dp), "朱古力视频")
                .setCheckedIndex(checkIndex)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        switch (position) {
                            case 0:
                                goToSearchVideo();
                                break;
                            default:
                                handlerFirstTabClickToShow(position, selectIndex, true);
                        }
                    }
                })
                .build()
                .show();
    }

    private void showPictureBottomSheet(int checkIndex) {
        new QMUIBottomSheet.BottomListSheetBuilder(this, true)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), "妹子图")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), "九妹图社")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_photo_library_black_24dp), "花瓣网")
                .setCheckedIndex(checkIndex)
                .setOnSheetItemClickListener(new QMUIBottomSheet.BottomListSheetBuilder.OnSheetItemClickListener() {
                    @Override
                    public void onClick(QMUIBottomSheet dialog, View itemView, int position, String tag) {
                        dialog.dismiss();
                        handlerSecondTabClickToShow(position, selectIndex, true);
                    }
                })
                .build()
                .show();
    }

    private void showForumBottomSheet(int selectIndex) {
        new QMUIBottomSheet.BottomListSheetBuilder(this, true)
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), "91论坛")
                .addItem(ResourceUtil.getDrawable(this, R.drawable.ic_library_books_black_24dp), "草榴社区")
                .setCheckedIndex(selectIndex)
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
                handlerFirstTabClickToShow(firstTabShow, position, false);
                showFloatingActionButton(fabSearch);
                break;
            case 1:
                handlerSecondTabClickToShow(secondTabShow, position, false);
                showFloatingActionButton(fabSearch);
                break;
            case 2:
                if (AddressHelper.getInstance().isEmpty(Keys.KEY_SP_FORUM_91_PORN_ADDRESS)) {
                    showNeedSetAddressDialog();
                    return;
                }
                if (mMain91ForumFragment == null) {
                    mMain91ForumFragment = Main91ForumFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMain91ForumFragment, content.getId(), position, false);
                showFloatingActionButton(fabSearch);
                break;
            case 3:
                if (mMusicFragment == null) {
                    mMusicFragment = MusicFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMusicFragment, content.getId(), position, false);
                hideFloatingActionButton(fabSearch);
                break;
            case 4:
                if (mMineFragment == null) {
                    mMineFragment = MineFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMineFragment, content.getId(), position, false);
                hideFloatingActionButton(fabSearch);
                break;
            default:
        }
        selectIndex = position;
    }

    private void handlerFirstTabClickToShow(int position, int itemId, boolean isInnerReplace) {
        switch (position) {
            case PORN91:
                if (AddressHelper.getInstance().isEmpty(Keys.KEY_SP_CUSTOM_ADDRESS)) {
                    showNeedSetAddressDialog();
                    return;
                }
                if (mMain91PronVideoFragment == null) {
                    mMain91PronVideoFragment = Main91PronVideoFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMain91PronVideoFragment, content.getId(), itemId, isInnerReplace);
                firstTabShow = PORN91;
                SPUtils.put(this, Keys.KEY_SP_FIRST_TAB_SHOW, PORN91);
                mMainPigAvFragment = null;
                break;
            case PIG_AV:
                if (AddressHelper.getInstance().isEmpty(Keys.KEY_SP_PIG_AV_ADDRESS)) {
                    showNeedSetAddressDialog();
                    return;
                }
                if (mMainPigAvFragment == null) {
                    mMainPigAvFragment = MainPigAvFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMainPigAvFragment, content.getId(), itemId, isInnerReplace);
                firstTabShow = PIG_AV;
                SPUtils.put(this, Keys.KEY_SP_FIRST_TAB_SHOW, PIG_AV);
                mMain91PronVideoFragment = null;
                break;
            default:
        }
    }

    private void showNeedSetAddressDialog() {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(context);
        builder.setTitle("温馨提示");
        builder.setMessage("还未设置对应地址，现在去设置？");
        builder.addAction("去设置", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
                Intent intent = new Intent(context, SettingActivity.class);
                startActivityWithAnimotion(intent);
            }
        });
        builder.addAction("返回", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void handlerSecondTabClickToShow(int position, int itemId, boolean isInnerReplace) {
        switch (position) {
            case MEI_ZI_TU:
                if (mMaiMeiZiTuFragment == null) {
                    mMaiMeiZiTuFragment = MainMeiZiTuFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMaiMeiZiTuFragment, content.getId(), itemId, isInnerReplace);
                secondTabShow = MEI_ZI_TU;
                SPUtils.put(this, Keys.KEY_SP_SECOND_TAB_SHOW, MEI_ZI_TU);
                mMain99MmFragment = null;
                break;
            case MM_99:
                if (mMain99MmFragment == null) {
                    mMain99MmFragment = Main99MmFragment.getInstance();
                }
                mCurrentFragment = FragmentUtils.switchContent(fragmentManager, mCurrentFragment, mMain99MmFragment, content.getId(), itemId, isInnerReplace);
                secondTabShow = MM_99;
                SPUtils.put(this, Keys.KEY_SP_SECOND_TAB_SHOW, MM_99);
                mMaiMeiZiTuFragment = null;
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
            finishAffinity();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    int pid = android.os.Process.myPid();
                    android.os.Process.killProcess(pid);
                }
            }, 500);
        }
    }

    private void goToSearchVideo() {

        if (!UserHelper.isUserInfoComplete(user)) {
            showMessage("请先登录", TastyToast.INFO);
            Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
            intent.putExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, UserLoginActivity.LOGIN_ACTION_FOR_SEARCH_91PRON_VIDEO);
            startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
            return;
        }
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityWithAnimotion(intent);
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
        getActivityComponent().inject(this);
        GitHubServiceApi gitHubServiceApi = apiManager.getGitHubServiceApi();
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


    @Override
    protected void onResume() {
        super.onResume();
        isBackground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isBackground = true;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTryToReleaseMemory(LowMemoryEvent lowMemoryEvent) {
        if (content == null || fragmentManager == null || !isBackground) {
            return;
        }
        if (!BuildConfig.DEBUG) {
            Bugsnag.notify(new Throwable(TAG + ":LowMemory,try to release some memory now!"), Severity.INFO);
        }
        try {
            Logger.t(TAG).d("start try to release memory ....");
            FragmentTransaction bt = fragmentManager.beginTransaction();
            for (int i = 0; i < 5; i++) {
                //只移除当前未选中的
                if (i != selectIndex) {
                    String name = FragmentUtils.makeFragmentName(content.getId(), i);
                    Fragment fragment = fragmentManager.findFragmentByTag(name);
                    if (fragment != null) {
                        bt.remove(fragment);
                        setNull(i);
                    }
                }
            }
            bt.commitAllowingStateLoss();
            //通知系统尝试释放内存
            System.gc();
            System.runFinalization();
            Logger.t(TAG).d("try to release memory success !!!");
        } catch (Exception e) {
            e.printStackTrace();
            if (!BuildConfig.DEBUG) {
                Bugsnag.notify(new Throwable(TAG + " tryToReleaseMemory error::", e), Severity.WARNING);
            }
        }
    }

    private void setNull(int position) {
        switch (position) {
            case 0:
                mMainPigAvFragment = null;
                mMain91PronVideoFragment = null;
                break;
            case 1:
                mMaiMeiZiTuFragment = null;
                mMain99MmFragment = null;
                break;
            case 2:
                mMain91ForumFragment = null;
                break;
            case 3:
                mMusicFragment = null;
                break;
            case 4:
                mMineFragment = null;
                break;
            default:
        }
    }
}
