package com.u91porn.ui.porn91video.play;

import com.u91porn.data.model.UnLimit91PornItem;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public interface IPlay extends IBasePlay {
    void loadVideoUrl(UnLimit91PornItem unLimit91PornItem);

    void loadVideoComment(String videoId, boolean pullToRefresh,String referer);

    void commentVideo(String comment, String uid, String vid,String referer);

    void replyComment(String comment, String username, String vid, String commentId,String referer);
}
