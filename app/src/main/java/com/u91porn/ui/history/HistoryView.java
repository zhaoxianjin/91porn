package com.u91porn.ui.history;

import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/12/22
 */

public interface HistoryView extends BaseView {
    void loadMoreDataComplete();

    void loadMoreFailed();

    void noMoreData();

    void setData(List<UnLimit91PornItem> unLimit91PornItemList);

    void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList);
}
