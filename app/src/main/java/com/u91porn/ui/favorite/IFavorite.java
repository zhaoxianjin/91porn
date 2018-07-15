package com.u91porn.ui.favorite;

import com.u91porn.data.model.UnLimit91PornItem;

/**
 * @author flymegoc
 * @date 2017/11/28
 * @describe
 */

public interface IFavorite extends IBaseFavorite {

    void loadRemoteFavoriteData(boolean pullToRefresh,String referer);

    void deleteFavorite(String rvid);

    void exportData(boolean onlyUrl);
}
