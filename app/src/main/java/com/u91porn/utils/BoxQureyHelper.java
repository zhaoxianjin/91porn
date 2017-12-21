package com.u91porn.utils;

import com.orhanobut.logger.Logger;
import com.u91porn.MyApplication;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;
import com.u91porn.data.model.VideoResult;
import com.u91porn.data.model.VideoResult_;

import io.objectbox.Box;

/**
 * @author flymegoc
 * @date 2017/11/22
 * @describe
 */

public class BoxQureyHelper {

    private static final String TAG = BoxQureyHelper.class.getSimpleName();

    public static UnLimit91PornItem findByViewKey(String viewKey) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        return unLimit91PornItemBox.query().equal(UnLimit91PornItem_.viewKey, viewKey).build().findFirst();
    }

    public static String getVideoUrlByViewKey(String viewKey) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);

        UnLimit91PornItem tmp = unLimit91PornItemBox.query().equal(UnLimit91PornItem_.viewKey, viewKey).build().findFirst();
        Logger.t(TAG).d(tmp);
        if (tmp == null || tmp.getVideoResult().getTarget() == null) {
            return "";
        } else {
            return tmp.getVideoResult().getTarget().getVideoUrl();
        }
    }

    public static UnLimit91PornItem findByVideoUrl(String videoUrl) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        Box<VideoResult> videoResultBox = MyApplication.getInstace().getBoxStore().boxFor(VideoResult.class);
        VideoResult videoResult = videoResultBox.query().equal(VideoResult_.videoUrl, videoUrl).build().findFirst();
        return unLimit91PornItemBox.get(videoResult.getId());
    }
}
