package com.u91porn.ui.play;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.u91porn.R;
import com.u91porn.widget.VideoControlsMobile;

/**
 * @author flymegoc
 */
public class ExoMediaPlayerActivity extends BasePlayVideoActivity implements OnPreparedListener {

    private VideoView videoplayer;
    private VideoControlsMobile videoControlsMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View getPlayerView() {
        View view = LayoutInflater.from(this).inflate(R.layout.playback_engine_exo_media, videoplayerContainer, false);
        videoplayer = view.findViewById(R.id.video_view);
        videoplayer.setReleaseOnDetachFromWindow(false);
        videoplayer.setOnPreparedListener(this);
        videoControlsMobile = new VideoControlsMobile(this);
        videoplayer.setControls(videoControlsMobile);
        return view;
    }

    @Override
    public void playVideo(String title, String videoUrl, String name, String thumImgUrl) {
        String proxyUrl = proxy.getProxyUrl(videoUrl);
        videoplayer.setVideoURI(Uri.parse(proxyUrl));
    }

    @Override
    public void onPrepared() {
        videoplayer.start();
    }

    @Override
    public void onBackPressed() {
        if (videoControlsMobile.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoplayer.release();
    }
}
