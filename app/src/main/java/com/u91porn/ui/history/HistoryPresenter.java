package com.u91porn.ui.history;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.orhanobut.logger.Logger;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;

import java.util.List;

import io.objectbox.Box;

/**
 * 浏览历史，只有观看视频，并解析出视频地址保存之后才会被记录
 *
 * @author flymegoc
 * @date 2017/12/22
 */

public class HistoryPresenter extends MvpBasePresenter<HistoryView> implements IHistory {

    private static final String TAG = HistoryPresenter.class.getSimpleName();
    private Box<UnLimit91PornItem> unLimit91PornItemBox;
    private int page = 1;
    private int pageSize = 10;

    public HistoryPresenter(Box<UnLimit91PornItem> unLimit91PornItemBox) {
        this.unLimit91PornItemBox = unLimit91PornItemBox;
    }

    @Override
    public void loadHistoryData(boolean pullToRefresh) {
        //如果刷新则重置页数
        if (pullToRefresh) {
            page = 1;
        }
        List<UnLimit91PornItem> unLimit91PornItemList = unLimit91PornItemBox.query().notNull(UnLimit91PornItem_.viewHistoryDate).orderDesc(UnLimit91PornItem_.viewHistoryDate).build().find((page - 1) * pageSize, pageSize);
        if (isViewAttached()) {

            if (page == 1) {
                Logger.t(TAG).d("加载首页");
                getView().setData(unLimit91PornItemList);
            } else {
                Logger.t(TAG).d("加载更多");
                getView().setMoreData(unLimit91PornItemList);
                getView().loadMoreDataComplete();
            }
            page++;
            if (unLimit91PornItemList.size() == 0 || unLimit91PornItemList.size() < pageSize) {
                Logger.t(TAG).d("没有更多");
                getView().noMoreData();
            }
        }
    }
}
