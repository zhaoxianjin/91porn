package com.u91porn.ui.porn91video.play;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.flymegoc.exolibrary.widget.ExoVideoControlsMobile;
import com.flymegoc.exolibrary.widget.ExoVideoView;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u91porn.R;
import com.u91porn.utils.GlideApp;

import java.io.File;

/**
 * @author flymegoc
 */
public class ExoMediaPlayerActivity extends BasePlayVideo implements OnPreparedListener {

    private static final String TAG = ExoMediaPlayerActivity.class.getSimpleName();
    private ExoVideoView videoPlayer;
    private ExoVideoControlsMobile videoControlsMobile;
    private boolean isPauseByActivityEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initPlayerView() {
        View view = LayoutInflater.from(this).inflate(R.layout.playback_engine_exo_media, videoplayerContainer, true);
        videoPlayer = view.findViewById(R.id.video_view);
        videoControlsMobile = (ExoVideoControlsMobile) videoPlayer.getVideoControls();
        videoPlayer.setOnPreparedListener(this);
    }

    @Override
    public void playVideo(String title, String videoUrl, String name, String thumImgUrl) {

        if (isPauseByActivityEvent) {
            isPauseByActivityEvent = false;
            videoPlayer.reset();
        }
        videoControlsMobile.setOnBackButtonClickListener(new ExoVideoControlsMobile.OnBackButtonClickListener() {
            @Override
            public void onBackClick(View view) {
                onBackPressed();
            }
        });
        if (!TextUtils.isEmpty(thumImgUrl)) {
            GlideApp.with(this).load(Uri.parse(thumImgUrl)).transition(new DrawableTransitionOptions().crossFade(300)).into(videoPlayer.getPreviewImageView());
        }
        //加载本地下载好的视频
        if (unLimit91PornItem.getStatus() == FileDownloadStatus.completed) {
            File downloadFile = new File(unLimit91PornItem.getDownLoadPath());
            if (downloadFile.exists()) {
                videoPlayer.setVideoPath(downloadFile.getAbsolutePath());
            } else {
                String proxyUrl = httpProxyCacheServer.getProxyUrl(videoUrl);
                videoPlayer.setVideoURI(Uri.parse(proxyUrl));
            }
        } else {
            String proxyUrl = httpProxyCacheServer.getProxyUrl(videoUrl);
            videoPlayer.setVideoURI(Uri.parse(proxyUrl));
        }
        videoControlsMobile.setTitle(title);
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
        videoplayerContainer.removeView(videoPlayer);
        videoPlayer.release();
        super.onDestroy();
    }
}
