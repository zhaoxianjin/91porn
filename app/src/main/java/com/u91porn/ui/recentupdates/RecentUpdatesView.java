package com.u91porn.ui.recentupdates;

import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * Created by flymegoc on 2017/12/19.
 */

public interface RecentUpdatesView extends BaseView {
    void loadMoreDataComplete();

    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList);

    void loadData(boolean pullToRefresh);

    void setData(List<UnLimit91PornItem> data);
}
