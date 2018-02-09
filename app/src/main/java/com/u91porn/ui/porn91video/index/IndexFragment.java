package com.u91porn.ui.porn91video.index;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.orhanobut.logger.Logger;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.UnLimit91Adapter;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.eventbus.ProxySetEvent;
import com.u91porn.ui.MvpFragment;
import com.u91porn.utils.AppUtils;
import com.u91porn.utils.LoadHelperUtils;

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
public class IndexFragment extends MvpFragment<IndexView, IndexPresenter> implements IndexView, SwipeRefreshLayout.OnRefreshListener {


    private static final String TAG = IndexFragment.class.getSimpleName();
    @BindView(R.id.recyclerView_index)
    RecyclerView recyclerView;
    Unbinder unbinder;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    private UnLimit91Adapter mUnLimit91Adapter;
    private List<UnLimit91PornItem> mUnLimit91PornItemList;
    private LoadViewHelper helper;

    public IndexFragment() {
        // Required empty public constructor
    }

    public static IndexFragment getInstance() {
        return new IndexFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new UnLimit91Adapter(R.layout.item_unlimit_91porn, mUnLimit91PornItemList);
    }

    @NonNull
    @Override
    public IndexPresenter createPresenter() {
        getActivityComponent().inject(this);
        Logger.t(TAG).d(apiManager.toString());
        NoLimit91PornServiceApi noLimit91PornServiceApi = apiManager.getNoLimit91PornService();

        return new IndexPresenter(noLimit91PornServiceApi, cacheProviders, provider);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        // Setup contentView == SwipeRefreshView

        contentView.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mUnLimit91Adapter);

        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItems = mUnLimit91PornItemList.get(position);
                goToPlayVideo(unLimit91PornItems);
            }
        });

        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                loadData(false, true);
            }
        });
        AppUtils.setColorSchemeColors(context,contentView);
    }

    @Override
    public void onProxySetEvent(ProxySetEvent proxySetEvent) {
        super.onProxySetEvent(proxySetEvent);
        presenter.setNoLimit91PornServiceApi(apiManager.getNoLimit91PornService());
    }

    @Override
    protected void onLazyLoadOnce() {
        loadData(false, false);
    }

    @Override
    public void onRefresh() {
        loadData(true, true);
    }

    @Override
    public void setData(List<UnLimit91PornItem> data) {
        mUnLimit91PornItemList.clear();
        mUnLimit91PornItemList.addAll(data);
        mUnLimit91Adapter.notifyDataSetChanged();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "拼命加载中...");
        contentView.setEnabled(false);
    }

    @Override
    public void loadData(boolean pullToRefresh, boolean cleanCache) {
        presenter.loadIndexData(pullToRefresh, cleanCache);
    }

    @Override
    public void showContent() {
        helper.showContent();
        contentView.setEnabled(true);
        contentView.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }


    @Override
    public void showError(String message) {
        contentView.setRefreshing(false);
        helper.showError();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public String getTitle() {
        return category.getCategoryName();
    }
}
