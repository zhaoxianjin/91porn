package com.u91porn.ui.play;

import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public interface IPlay extends IBasePlay {
    void loadVideoUrl(String viewKey);

    void loadVideoComment(String videoId, boolean pullToRefresh);

    void commentVideo(String comment, String uid, String vid);

    void replyComment(String comment, String username, String vid, String commentId);

    void saveVideoUrl(VideoResult videoResult, UnLimit91PornItem unLimit91PornItem);
}
