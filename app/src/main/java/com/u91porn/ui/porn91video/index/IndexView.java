package com.u91porn.ui.porn91video.index;

import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.BaseView;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/15
 * @describe
 */

public interface IndexView extends BaseView {

    void loadData(boolean pullToRefresh,boolean cleanCache);

    void setData(List<UnLimit91PornItem> data);
}
