package com.u91porn.ui.common;

import retrofit2.http.Header;

/**
 * @author flymegoc
 * @date 2017/11/27
 * @describe
 */

public interface ICommon extends IBaseCommon {
    void loadHotData(final boolean pullToRefresh, String category, String m,@Header("Referer") String referer);
}
