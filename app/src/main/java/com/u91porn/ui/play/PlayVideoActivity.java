package com.u91porn.ui.play;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.danikula.videocache.HttpProxyCacheServer;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.cookie.SetCookieCache;
import com.u91porn.cookie.SharedPrefsCookiePersistor;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.User;
import com.u91porn.data.model.VideoResult;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.download.DownloadPresenter;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SPUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;
import io.objectbox.Box;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 */
public class PlayVideoActivity extends MvpActivity<PlayVideoView, PlayVideoPresenter> implements PlayVideoView {

    private final String TAG = PlayVideoActivity.class.getSimpleName();

    @BindView(R.id.videoplayer)
    JZVideoPlayerStandard jzVideoPlayerStandard;
    @BindView(R.id.fl_comment)
    FrameLayout flComment;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private AlertDialog mAlertDialog;
    private AlertDialog favoriteDialog;
    HttpProxyCacheServer proxy = MyApplication.getInstace().getProxy();
    private LoadViewHelper helper;

    private UnLimit91PornItem unLimit91PornItem;
    private NoLimit91PornServiceApi mNoLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
    private Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
    private CacheProviders cacheProviders = MyApplication.getInstace().getCacheProviders();
    private FavoritePresenter favoritePresenter = new FavoritePresenter(unLimit91PornItemBox, mNoLimit91PornServiceApi, cacheProviders, MyApplication.getInstace().getUser());
    private DownloadPresenter downloadPresenter = new DownloadPresenter();
    private SharedPrefsCookiePersistor sharedPrefsCookiePersistor = MyApplication.getInstace().getSharedPrefsCookiePersistor();
    private SetCookieCache setCookieCache = MyApplication.getInstace().getSetCookieCache();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        unLimit91PornItem = (UnLimit91PornItem) getIntent().getSerializableExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM);

        if (unLimit91PornItem == null) {
            showMessage("参数错误，无法解析");
            return;
        }
        setTitle("91Porn - 正在播放");
        toolbar.setSubtitle(unLimit91PornItem.getTitle());

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setContentInsetStartWithNavigation(0);
        mAlertDialog = DialogUtils.initLodingDialog(this, "视频地址解析中...");

        helper = new LoadViewHelper(flComment);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                presenter.loadVideoUrl(unLimit91PornItem.getViewKey());
            }
        });

        UnLimit91PornItem tmp = BoxQureyHelper.findByViewKey(unLimit91PornItem.getViewKey());
        if (tmp == null || tmp.getVideoResult().getTarget() == null) {
            presenter.loadVideoUrl(unLimit91PornItem.getViewKey());
        } else {
            Logger.t(TAG).d("使用已有播放地址");
            showMessage("使用已有播放地址");
            //浏览历史
            if (tmp.getViewHistoryDate() == null) {
                tmp.setViewHistoryDate(new Date());
                unLimit91PornItemBox.put(tmp);
            }
            unLimit91PornItem.setVideoResult(tmp.getVideoResult());
            playVideo(unLimit91PornItem.getTitle(), unLimit91PornItem.getVideoResult().getTarget().getVideoUrl(), "", "");
        }

        favoriteDialog = DialogUtils.initLodingDialog(this, "收藏中,请稍后...");
    }


    private void playVideo(String title, String videoUrl, String name, String thumImgUrl) {
        String proxyUrl = proxy.getProxyUrl(videoUrl);
        jzVideoPlayerStandard.setUp(proxyUrl, JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, title);
        //自动播放
        jzVideoPlayerStandard.startButton.performClick();
        if (!TextUtils.isEmpty(thumImgUrl)) {
            jzVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse(thumImgUrl));
        }
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    @NonNull
    @Override
    public PlayVideoPresenter createPresenter() {
        return new PlayVideoPresenter(mNoLimit91PornServiceApi, favoritePresenter, downloadPresenter, sharedPrefsCookiePersistor, setCookieCache, cacheProviders);
    }

    @Override
    public void showParsingDialog() {
        if (mAlertDialog == null) {
            return;
        }
        mAlertDialog.show();
    }

    @Override
    public void playVideo(VideoResult videoResult) {
        dismissDialog();
        showMessage("解析成功，开始播放");
        presenter.saveVideoUrl(videoResult, unLimit91PornItem);
        helper.showContent();
        unLimit91PornItem.videoResult.setTarget(videoResult);
        playVideo(unLimit91PornItem.getTitle(), videoResult.getVideoUrl(), "", videoResult.getThumbImgUrl());
    }

    @Override
    public void errorParseVideoUrl(String errorMessage) {
        dismissDialog();
        helper.showError();
        showMessage(errorMessage);
    }

    @Override
    public void favoriteSuccess() {
        SPUtils.put(this, Keys.KEY_SP_USER_FAVORITE_NEED_REFRESH, true);
        showMessage("收藏成功");
    }

    @Override
    public String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        showMessage(e.getMessage());
        dismissDialog();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {
        dismissDialog();
    }

    @Override
    public LifecycleTransformer<Reply<String>> bindView() {
        return bindToLifecycle();
    }

    @Override
    public void showMessage(String msg) {
        super.showMessage(msg);
        dismissDialog();
    }

    /**
     * 是否是用户主动取消
     */
    private void dismissDialog() {
        if (mAlertDialog != null && mAlertDialog.isShowing() && !isFinishing()) {
            mAlertDialog.dismiss();
        }
        if (favoriteDialog != null && favoriteDialog.isShowing() && !isFinishing()) {
            favoriteDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.playvideo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_play_collect) {
            //presenter.favorite(unLimit91PornItem);
            VideoResult videoResult = unLimit91PornItem.getVideoResult().getTarget();
            if (videoResult == null) {
                showMessage("还未成功解析视频链接，不能收藏！");
                return true;
            }
            User user = MyApplication.getInstace().getUser();
            if (user == null) {
                goToLogin();
                showMessage("请先登录");
                return true;
            }
            if (Integer.parseInt(videoResult.getOwnnerId()) == user.getUserId()) {
                showMessage("不能收藏自己的视频");
                return true;
            }
            favoriteDialog.show();
            presenter.favorite("addToFavorites", String.valueOf(user.getUserId()), videoResult.getVideoId(), videoResult.getOwnnerId(), "json");
            return true;
        } else if (id == R.id.menu_play_download) {
            presenter.downloadVideo(unLimit91PornItem);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, UserLoginActivity.class);
        startActivity(intent);
    }
}
