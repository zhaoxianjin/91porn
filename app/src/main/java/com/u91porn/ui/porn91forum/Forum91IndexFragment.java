package com.u91porn.ui.porn91forum;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.oushangfeng.pinnedsectionitemdecoration.PinnedHeaderItemDecoration;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.BaseHeaderAdapter;
import com.u91porn.adapter.Forum91PornIndexAdapter;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.data.model.PinnedHeaderEntity;
import com.u91porn.eventbus.BaseUrlChangeEvent;
import com.u91porn.ui.MvpFragment;
import com.u91porn.ui.porn91forum.browse91porn.Browse91PornActivity;
import com.u91porn.utils.AppUtils;
import com.u91porn.utils.constants.Keys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class Forum91IndexFragment extends MvpFragment<ForumView, ForumPresenter> implements ForumView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;
    private Forum91PornIndexAdapter forum91PornIndexAdapter;

    public Forum91IndexFragment() {
        // Required empty public constructor
        List<PinnedHeaderEntity<Forum91PronItem>> forum91PornItemSectionList = new ArrayList<>();
        forum91PornIndexAdapter = new Forum91PornIndexAdapter(forum91PornItemSectionList);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);

        return inflater.inflate(R.layout.fragment_forum91_index, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        swipeLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.addItemDecoration(new PinnedHeaderItemDecoration.Builder(BaseHeaderAdapter.TYPE_HEADER).setDividerId(R.drawable.divider).enableDivider(true).create());
        recyclerView.setAdapter(forum91PornIndexAdapter);
        forum91PornIndexAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                PinnedHeaderEntity<Forum91PronItem> forum91PronItemPinnedHeaderEntity = (PinnedHeaderEntity<Forum91PronItem>) adapter.getItem(position);
                if (forum91PronItemPinnedHeaderEntity == null || forum91PronItemPinnedHeaderEntity.getData() == null) {
                    return;
                }
                Intent intent = new Intent(context, Browse91PornActivity.class);
                intent.putExtra(Keys.KEY_INTENT_BROWSE_FORUM_91_PORN_ITEM, forum91PronItemPinnedHeaderEntity.getData());
                startActivityWithAnimotion(intent);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        loadData(true);
    }

    public static Forum91IndexFragment getInstance() {
        return new Forum91IndexFragment();
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        swipeLayout.setRefreshing(pullToRefresh);
    }

    @Override
    public void showContent() {
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        swipeLayout.setRefreshing(false);
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void setForumListData(List<Forum91PronItem> forum91PronItemList) {

    }

    @Override
    public void setForumIndexListData(List<PinnedHeaderEntity<Forum91PronItem>> pinnedHeaderEntityList) {
        forum91PornIndexAdapter.setNewData(pinnedHeaderEntityList);
    }

    @Override
    public void loadMoreFailed() {

    }

    @Override
    public void noMoreData() {

    }

    @Override
    public void setMoreData(List<Forum91PronItem> forum91PronItemList) {

    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadForumIndexListData(pullToRefresh);
    }

    @NonNull
    @Override
    public ForumPresenter createPresenter() {
        getActivityComponent().inject(this);
        return new ForumPresenter(apiManager.getForum91PronServiceApi(), provider);
    }


    @Override
    public void onBaseUrlChangeEvent(BaseUrlChangeEvent baseUrlChangeEvent) {
        presenter.setForum91PronServiceApi(apiManager.getForum91PronServiceApi());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
