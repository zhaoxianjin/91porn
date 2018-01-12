package com.u91porn.ui.play;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.flymegoc.exolibrary.widget.ExoVideoControlsMobile;
import com.flymegoc.exolibrary.widget.ExoVideoView;
import com.u91porn.R;

/**
 * @author flymegoc
 */
public class ExoMediaPlayerActivity extends BasePlayVideo implements OnPreparedListener {

    private ExoVideoView videoplayer;
    private ExoVideoControlsMobile videoControlsMobile;
    private boolean isPauseByActivityEvent = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void initPlayerView() {
        View view = LayoutInflater.from(this).inflate(R.layout.playback_engine_exo_media, videoplayerContainer, true);
        videoplayer = view.findViewById(R.id.video_view);
        videoControlsMobile = (ExoVideoControlsMobile) videoplayer.getVideoControls();
        videoplayer.setOnPreparedListener(this);
    }

    @Override
    public void playVideo(String title, String videoUrl, String name, String thumImgUrl) {
        if (isPauseByActivityEvent) {
            isPauseByActivityEvent = false;
            videoplayer.reset();
        }
        videoControlsMobile.setOnBackButtonClickListener(new ExoVideoControlsMobile.OnBackButtonClickListener() {
            @Override
            public void onBackClick(View view) {
                onBackPressed();
            }
        });
        videoplayer.setPreviewImage(Uri.parse(thumImgUrl));
        String proxyUrl = proxy.getProxyUrl(videoUrl);
        videoplayer.setVideoURI(Uri.parse(proxyUrl));
        videoControlsMobile.setTitle(title);
    }

    @Override
    public void onPrepared() {
        videoplayer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!videoplayer.isPlaying() && isPauseByActivityEvent) {
            isPauseByActivityEvent = false;
            videoplayer.start();
        }
    }

    @Override
    protected void onPause() {
        videoplayer.pause();
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
        videoplayer.release();
        super.onDestroy();
    }
}
