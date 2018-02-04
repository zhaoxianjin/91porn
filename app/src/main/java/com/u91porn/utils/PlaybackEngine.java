package com.u91porn.utils;

import android.content.Context;
import android.content.Intent;

import com.u91porn.ui.porn91video.play.ExoMediaPlayerActivity;
import com.u91porn.ui.porn91video.play.JiaoZiVideoPlayerActivity;
import com.u91porn.utils.constants.Keys;

/**
 * 播放引擎切换
 *
 * @author flymegoc
 * @date 2018/1/2
 */

public class PlaybackEngine {
    public static final String[] PLAY_ENGINE_ITEMS = new String[]{"Google Exoplayer Engine", "JiaoZiPlayer Engine",};
    public static final int EXOMEDIAPLAYER_ENGINE = 0;
    public static final int JIAOZIVIDEOPLAYER_ENGINE = 1;
    public static final int DEFAULT_PLAYER_ENGINE = EXOMEDIAPLAYER_ENGINE;

    /**
     * 获取播放引擎
     *
     * @param context 上下文
     * @return intent
     */
    public static Intent getPlaybackEngineIntent(Context context) {

        int engine = (int) SPUtils.get(context, Keys.KEY_SP_PLAYBACK_ENGINE, DEFAULT_PLAYER_ENGINE);

        Intent intent = new Intent();
        switch (engine) {
            case EXOMEDIAPLAYER_ENGINE:
                intent.setClass(context, ExoMediaPlayerActivity.class);
                break;
            case JIAOZIVIDEOPLAYER_ENGINE:
                intent.setClass(context, JiaoZiVideoPlayerActivity.class);
                break;
            default:
        }
        return intent;
    }
}
