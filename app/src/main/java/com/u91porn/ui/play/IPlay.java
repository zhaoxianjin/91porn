package com.u91porn.ui.play;

import com.u91porn.data.model.UnLimit91PornItem;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public interface IPlay extends IBasePlay{
    void loadVideoUrl(String viewKey);

    void saveVideoUrl(String videoUrl,UnLimit91PornItem unLimit91PornItem);
}
