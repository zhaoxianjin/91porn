package com.u91porn.ui.porn91video.author;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.UnLimit91Adapter;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.MvpActivity;
import com.u91porn.utils.LoadHelperUtils;
import com.u91porn.utils.constants.Keys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class AuthorActivity extends MvpActivity<AuthorView, AuthorPresenter> implements AuthorView {
    public static final int AUTHORACTIVITY_RESULT_CODE = 1;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    private UnLimit91Adapter mUnLimit91Adapter;
    private LoadViewHelper helper;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_author);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        uid = getIntent().getStringExtra(Keys.KEY_INTENT_UID);
        if (TextUtils.isEmpty(uid)) {
            showMessage("用户信息错误，无法获取数据", TastyToast.ERROR);
            return;
        }
        init();
    }

    private void init() {
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.authorVideos(uid, true);
            }
        });
        swipeLayout.setEnabled(false);
        List<UnLimit91PornItem> mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new UnLimit91Adapter(R.layout.item_unlimit_91porn, mUnLimit91PornItemList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mUnLimit91Adapter);

        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItems = (UnLimit91PornItem) adapter.getData().get(position);
                Intent intent = new Intent();
                intent.putExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM, unLimit91PornItems);
                setResult(AUTHORACTIVITY_RESULT_CODE, intent);
                onBackPressed();
            }
        });
        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {

                presenter.authorVideos(uid, false);
            }
        }, recyclerView);

        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                swipeLayout.setEnabled(false);
                presenter.authorVideos(uid, true);
            }
        });
        presenter.authorVideos(uid, false);
    }

    @NonNull
    @Override
    public AuthorPresenter createPresenter() {
        getActivityComponent().inject(this);
        NoLimit91PornServiceApi noLimit91PornServiceApi = apiManager.getNoLimit91PornService();
        return new AuthorPresenter(noLimit91PornServiceApi, provider, cacheProviders);
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
    public void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList) {
        mUnLimit91Adapter.addData(unLimit91PornItemList);
    }

    @Override
    public void setData(List<UnLimit91PornItem> data) {
        mUnLimit91Adapter.setNewData(data);
        recyclerView.smoothScrollToPosition(0);
        swipeLayout.setEnabled(true);
        swipeLayout.setRefreshing(false);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "加载中，请稍候...");
    }

    @Override
    public void showContent() {
        helper.showContent();
        if (mUnLimit91Adapter.getData().size() == 0) {
            helper.showEmpty();
            LoadHelperUtils.setEmptyText(helper.getLoadEmpty(), R.id.tv_empty_info, "暂无数据");
        }
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
        helper.showError();
        LoadHelperUtils.setErrorText(helper.getLoadError(), R.id.tv_error_text, "加载数据失败了，点击重试");
    }
}
