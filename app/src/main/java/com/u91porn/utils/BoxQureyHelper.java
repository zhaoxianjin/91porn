package com.u91porn.utils;

import com.u91porn.MyApplication;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;

import io.objectbox.Box;

/**
 * @author flymegoc
 * @date 2017/11/22
 * @describe
 */

public class BoxQureyHelper {

    public static UnLimit91PornItem findByViewKey(String viewKey) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        return unLimit91PornItemBox.query().equal(UnLimit91PornItem_.viewKey, viewKey).build().findFirst();
    }

    public static String getVideoUrlByViewKey(String viewKey) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        UnLimit91PornItem tmp = unLimit91PornItemBox.query().equal(UnLimit91PornItem_.viewKey, viewKey).build().findFirst();
        if (tmp == null) {
            return "";
        } else {
            return tmp.getVideoUrl();
        }
    }

    public static UnLimit91PornItem findByVideoUrl(String videoUrl) {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        return unLimit91PornItemBox.query().equal(UnLimit91PornItem_.videoUrl, videoUrl).build().findFirst();
    }
}
