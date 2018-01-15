package com.u91porn.ui.download;

import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.favorite.IBaseFavorite;

/**
 * @author flymegoc
 * @date 2017/11/26
 * @describe
 */

public interface IBaseDownload extends IBaseFavorite{
    void downloadVideo(UnLimit91PornItem unLimit91PornItem,boolean isDownloadNeedWifi,boolean isForceReDownload);
}
