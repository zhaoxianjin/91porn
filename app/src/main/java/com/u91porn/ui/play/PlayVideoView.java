package com.u91porn.ui.play;

import com.hannesdorfmann.mosby3.mvp.MvpView;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.ui.BaseView;

import io.rx_cache2.Reply;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public interface PlayVideoView extends BaseView {
    void showParsingDialog();

    void playVideo(String videoUrl);

    void errorParseVideoUrl(String errorMessage);
}
