package com.u91porn.ui.pigav.playpigav;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.flymegoc.exolibrary.widget.ExoVideoControlsMobile;
import com.flymegoc.exolibrary.widget.ExoVideoView;
import com.jaeger.library.StatusBarUtil;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.PigAvAdapter;
import com.u91porn.data.PigAvServiceApi;
import com.u91porn.data.model.PigAv;
import com.u91porn.data.model.PigAvVideo;
import com.u91porn.ui.MvpActivity;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.GlideApp;
import com.u91porn.utils.constants.Keys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class PlayPigAvActivity extends MvpActivity<PlayPigAvView, PlayPigAvPresenter> implements PlayPigAvView, OnPreparedListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.video_view)
    ExoVideoView videoPlayer;
    @BindView(R.id.play_container)
    FrameLayout playContainer;
    private ExoVideoControlsMobile videoControlsMobile;
    private boolean isPauseByActivityEvent = false;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_pig_av);
        ButterKnife.bind(this);
        setVideoViewHeight(playContainer);
        initDialog();
        videoControlsMobile = (ExoVideoControlsMobile) videoPlayer.getVideoControls();
        videoPlayer.setOnPreparedListener(this);
        videoControlsMobile.setOnBackButtonClickListener(new ExoVideoControlsMobile.OnBackButtonClickListener() {
            @Override
            public void onBackClick(View view) {
                onBackPressed();
            }
        });
        PigAv pigAv = (PigAv) getIntent().getSerializableExtra(Keys.KEY_INTENT_PIG_AV_ITEM);
        if (pigAv != null) {
            parseVideoUrl(pigAv);
        } else {
            showMessage("参数错误，无法播放", TastyToast.WARNING);
        }
    }

    private void parseVideoUrl(PigAv pigAv) {
        videoControlsMobile.setTitle(pigAv.getTitle());
        presenter.parseVideoUrl(pigAv.getContentUrl(), pigAv.getpId(), false);
    }

    private void initDialog() {
        alertDialog = DialogUtils.initLodingDialog(this, "解析视频地址中，请稍后...");
    }

    /**
     * 根据屏幕宽度信息重设videoview宽高为16：9比例
     */
    protected void setVideoViewHeight(View playerView) {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        layoutParams.height = QMUIDisplayHelper.getScreenWidth(this) * 9 / 16;
        playerView.setLayoutParams(layoutParams);
    }

    @Override
    public void onPrepared() {
        videoPlayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoPlayer.isPlaying() && isPauseByActivityEvent) {
            isPauseByActivityEvent = false;
            videoPlayer.start();
        }
    }

    @NonNull
    @Override
    public PlayPigAvPresenter createPresenter() {
        getActivityComponent().inject(this);

        PigAvServiceApi pigAvServiceApi = apiManager.getPigAvServiceApi();
        return new PlayPigAvPresenter(cacheProviders, provider, pigAvServiceApi);
    }

    @Override
    protected void onPause() {
        videoPlayer.pause();
        isPauseByActivityEvent = true;
        super.onPause();

    }

    @Override
    public void onBackPressed() {
        if (videoControlsMobile.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        videoPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE || newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_USER) {
            //这里没必要，因为我们使用的是setColorForSwipeBack，并不会有这个虚拟的view，而是设置的padding
            StatusBarUtil.hideFakeStatusBarView(this);
        } else if (newConfig.orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
    }

    @Override
    public void playVideo(PigAvVideo pigAvVideo) {
        String url = pigAvVideo.getFile();
        GlideApp.with(context).load(pigAvVideo.getImage()).into(videoPlayer.getPreviewImageView());
        if (TextUtils.isEmpty(url) && pigAvVideo.getSources() != null && pigAvVideo.getSources().size() > 0) {
            url = pigAvVideo.getSources().get(0).getFile();
        }
        if (TextUtils.isEmpty(url)) {
            showMessage("播放地址无效", TastyToast.ERROR);
            return;
        }
        String proxyUrl = httpProxyCacheServer.getProxyUrl(url);
        videoPlayer.setVideoURI(Uri.parse(proxyUrl));
    }

    @Override
    public void listVideo(List<PigAv> pigAvList) {
        PigAvAdapter pigAvAdapter = new PigAvAdapter(R.layout.item_pig_av);
        pigAvAdapter.setWidth(QMUIDisplayHelper.getScreenWidth(context));
        pigAvAdapter.setNewData(pigAvList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(pigAvAdapter);
        pigAvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PigAv pigAv = (PigAv) adapter.getItem(position);
                if (pigAv == null) {
                    return;
                }
                videoPlayer.pause();
                videoPlayer.reset();
                parseVideoUrl(pigAv);
            }
        });
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        alertDialog.show();
    }

    @Override
    public void showContent() {
        dismissDialog();
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
        dismissDialog();
    }

    private void dismissDialog() {
        if (alertDialog != null && alertDialog.isShowing() && !isFinishing()) {
            alertDialog.dismiss();
        }
    }
}
