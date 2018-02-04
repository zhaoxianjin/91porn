package com.u91porn.ui;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/2/4
 */

public interface BaseLoadMoreView<T> extends BaseView {
    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<T> moreData);

    void loadData(boolean pullToRefresh, boolean cleanCache);

    void setData(List<T> data);
}
