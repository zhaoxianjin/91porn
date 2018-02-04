package com.u91porn.ui.porn91video.recentupdates;

/**
 * 最近更新
 *
 * @author flymegoc
 * @date 2017/12/19
 */

public interface IRencentUpdate extends IBaseRecentUpdate {
    void loadRecentUpdatesData(final boolean pullToRefresh, boolean cleanCache, String next);
}
