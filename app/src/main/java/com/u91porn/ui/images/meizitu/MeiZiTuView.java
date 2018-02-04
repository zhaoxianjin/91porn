package com.u91porn.ui.images.meizitu;

import com.u91porn.data.model.MeiZiTu;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/25
 */

public interface MeiZiTuView extends BaseView {
    void loadMoreFailed();

    void noMoreData();

    void setMoreData(List<MeiZiTu> meiZiTuList);

    void loadData(boolean pullToRefresh, boolean cleanCache);

    void setData(List<MeiZiTu> meiZiTuList);
}
