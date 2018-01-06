package com.u91porn.ui.play;

import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.VideoResult;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public interface IPlay extends IBasePlay {
    void loadVideoUrl(String viewKey,String referer);

    void loadVideoComment(String videoId, boolean pullToRefresh,String referer);

    void commentVideo(String comment, String uid, String vid,String referer);

    void replyComment(String comment, String username, String vid, String commentId,String referer);

    void saveVideoUrl(VideoResult videoResult, UnLimit91PornItem unLimit91PornItem);
}
