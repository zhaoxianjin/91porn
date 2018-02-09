package com.u91porn.ui.porn91video.play;

import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.u91porn.R;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * @author flymegoc
 */
public class JiaoZiVideoPlayerActivity extends BasePlayVideo {

    JZVideoPlayerStandard jzVideoPlayerStandard;

    @Override
    public void initPlayerView() {
        View view = LayoutInflater.from(this).inflate(R.layout.playback_engine_jiao_zi, videoplayerContainer, true);
        jzVideoPlayerStandard = view.findViewById(R.id.videoplayer);
    }

    @Override
    public void playVideo(String title, String videoUrl, String name, String thumImgUrl) {
        jzVideoPlayerStandard.setVisibility(View.VISIBLE);
        String proxyUrl = httpProxyCacheServer.getProxyUrl(videoUrl);
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
}
