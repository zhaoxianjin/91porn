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
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.Forum91PornAdapter;
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
public class ForumFragment extends MvpFragment<ForumView, ForumPresenter> implements ForumView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    @BindView(R.id.tv_forum_91_porn_tip)
    TextView tipTextView;
    Unbinder unbinder;
    private Forum91PornAdapter forun91PornAdapter;

    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment getInstance() {
        return new ForumFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Forum91PronItem> forum91PronItemList = new ArrayList<>();
        forun91PornAdapter = new Forum91PornAdapter(context, R.layout.item_forum_91_porn, forum91PronItemList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_forum, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        swipeLayout.setOnRefreshListener(this);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(forun91PornAdapter);
        forun91PornAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData(false);
            }
        });
        if ("17".equals(category.getCategoryValue()) || "4".equals(category.getCategoryValue())) {
            tipTextView.setVisibility(View.VISIBLE);
            swipeLayout.setEnabled(false);
        }
        forun91PornAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Forum91PronItem forum91PronItem = (Forum91PronItem) adapter.getItem(position);
                Intent intent = new Intent(context, Browse91PornActivity.class);
                intent.putExtra(Keys.KEY_INTENT_BROWSE_FORUM_91_PORN_ITEM, forum91PronItem);
                startActivityWithAnimotion(intent);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        loadData(true);
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        if ("index".equals(category.getCategoryValue())) {
            presenter.loadForumIndexListData(true);
        } else if ("17".equals(category.getCategoryValue()) || "4".equals(category.getCategoryValue())) {
            swipeLayout.setEnabled(false);
        } else {
            presenter.loadForumListData(pullToRefresh, category.getCategoryValue());
        }
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
        forun91PornAdapter.setNewData(forum91PronItemList);
    }

    @Override
    public void setForumIndexListData(List<PinnedHeaderEntity<Forum91PronItem>> pinnedHeaderEntityList) {

    }

    @Override
    public void loadMoreFailed() {
        forun91PornAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        forun91PornAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<Forum91PronItem> forum91PronItemList) {
        forun91PornAdapter.loadMoreComplete();
        forun91PornAdapter.addData(forum91PronItemList);
    }

    @Override
    public void onRefresh() {
        loadData(true);
    }
}
