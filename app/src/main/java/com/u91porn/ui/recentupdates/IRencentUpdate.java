package com.u91porn.ui.recentupdates;

/**
 * Created by flymegoc on 2017/12/19.
 */

public interface IRencentUpdate extends IBaseRecentUpdate {
    void loadRecentUpdatesData(final boolean pullToRefresh, String next);
}
