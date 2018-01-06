package com.u91porn.ui.recentupdates;

/**
 *最近更新
 * @author flymegoc
 * @date 2017/12/19
 */

public interface IRencentUpdate extends IBaseRecentUpdate {
    void loadRecentUpdatesData(final boolean pullToRefresh, String next,String referer);
}
