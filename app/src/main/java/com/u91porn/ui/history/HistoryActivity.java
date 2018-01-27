package com.u91porn.ui.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.aitsuki.swipe.SwipeMenuRecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.u91porn.R;
import com.u91porn.adapter.HistoryAdapter;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.MvpActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class HistoryActivity extends MvpActivity<HistoryView, HistoryPresenter> implements HistoryView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;

    private HistoryAdapter mUnLimit91Adapter;
    private List<UnLimit91PornItem> mUnLimit91PornItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new HistoryAdapter(R.layout.item_unlimit_91porn, mUnLimit91PornItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(mUnLimit91Adapter);
        mUnLimit91Adapter.setEmptyView(R.layout.empty_view, recyclerView);

        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                goToPlayVideo((UnLimit91PornItem) adapter.getItem(position));
            }
        });
        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.loadHistoryData(false);
            }
        }, recyclerView);
        presenter.loadHistoryData(false);
    }

    @NonNull
    @Override
    public HistoryPresenter createPresenter() {
        return new HistoryPresenter(DataBaseManager.getInstance());
    }

    @Override
    public void showError(String message) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }


    @Override
    public void loadMoreDataComplete() {
        mUnLimit91Adapter.loadMoreComplete();
    }

    @Override
    public void loadMoreFailed() {
        mUnLimit91Adapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        mUnLimit91Adapter.loadMoreEnd(true);
    }

    @Override
    public void setData(List<UnLimit91PornItem> unLimit91PornItemList) {
        mUnLimit91Adapter.setNewData(unLimit91PornItemList);
    }

    @Override
    public void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList) {
        mUnLimit91Adapter.addData(unLimit91PornItemList);
    }
}
