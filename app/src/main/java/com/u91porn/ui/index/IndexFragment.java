package com.u91porn.ui.index;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitsuki.swipe.SwipeItemLayout;
import com.aitsuki.swipe.SwipeMenuRecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.adapter.UnLimit91Adapter;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.User;
import com.u91porn.ui.MvpFragment;
import com.u91porn.ui.favorite.FavoritePresenter;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.play.PlayVideoActivity;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.Keys;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.objectbox.Box;
import io.rx_cache2.Reply;


/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class IndexFragment extends MvpFragment<IndexView, IndexPresenter> implements IndexView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView_index)
    SwipeMenuRecyclerView recyclerView;
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
    public IndexPresenter createPresenter() {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        CacheProviders cacheProviders = MyApplication.getInstace().getCacheProviders();
        User user = MyApplication.getInstace().getUser();
        return new IndexPresenter(new FavoritePresenter(unLimit91PornItemBox, noLimit91PornServiceApi, cacheProviders, user));
    }

    @Override
    public String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return getString(R.string.load_failed);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);

        mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new UnLimit91Adapter(R.layout.item_unlimit_91porn, mUnLimit91PornItemList);
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
                loadData(false);
            }
        });
        loadData(false);
    }

    private void goToPlayVideo(UnLimit91PornItem unLimit91PornItem) {
        Intent intent = new Intent(getContext(), PlayVideoActivity.class);
        intent.putExtra(Keys.KEY_INTENT_UNLIMIT91PORNITEM, unLimit91PornItem);
        ((MainActivity) getActivity()).startActivityWithAnimotion(intent);
    }

    @Override
    public void onRefresh() {
        loadData(true);
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
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadIndexData(pullToRefresh);
    }

    @Override
    public void showContent() {
        helper.showContent();
        contentView.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg) {
        super.showMessage(msg);
    }

    @Override
    public LifecycleTransformer<Reply<String>> bindView() {
        return bindToLifecycle();
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        contentView.setRefreshing(false);
        helper.showError();
        showMessage(e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
