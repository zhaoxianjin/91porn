package com.u91porn.ui.images.mm99;

import com.u91porn.data.model.Mm99;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public interface Mm99View extends BaseView {
    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<Mm99> unLimit91PornItemList);

    void loadData(boolean pullToRefresh, boolean cleanCache);

    void setData(List<Mm99> data);
}
