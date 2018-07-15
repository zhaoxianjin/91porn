package com.u91porn.ui.proxy;

import com.u91porn.data.model.ProxyModel;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/20
 */

public interface ProxyView extends BaseView {
    void testProxySuccess(String message);

    void testProxyError(String message);

    void parseGouBanJiaSuccess(List<ProxyModel> proxyModelList);

    void loadMoreDataComplete();

    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<ProxyModel> proxyModelList);

    void beginParseProxy();
}
