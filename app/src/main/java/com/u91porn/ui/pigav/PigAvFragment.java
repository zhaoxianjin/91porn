package com.u91porn.ui.pigav;


import android.content.Intent;
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
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.PigAvAdapter;
import com.u91porn.data.model.PigAv;
import com.u91porn.eventbus.BaseUrlChangeEvent;
import com.u91porn.ui.MvpFragment;
import com.u91porn.ui.pigav.playpigav.PlayPigAvActivity;
import com.u91porn.utils.AppUtils;
import com.u91porn.utils.constants.Keys;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class PigAvFragment extends MvpFragment<PigAvView, PigAvPresenter> implements PigAvView, SwipeRefreshLayout.OnRefreshListener {


    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.swipe_layout)
    SwipeRefreshLayout swipeLayout;
    Unbinder unbinder;
    private PigAvAdapter piaAvAdapter;

    public PigAvFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        piaAvAdapter = new PigAvAdapter(R.layout.item_pig_av);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_pig_av, container, false);
    }

    @NonNull
    @Override
    public PigAvPresenter createPresenter() {
        getActivityComponent().inject(this);

        return new PigAvPresenter(cacheProviders, provider, apiManager.getPigAvServiceApi());
    }

    @Override
    public void onBaseUrlChangeEvent(BaseUrlChangeEvent baseUrlChangeEvent) {
        super.onBaseUrlChangeEvent(baseUrlChangeEvent);
        presenter.setPigAvServiceApi(apiManager.getPigAvServiceApi());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        swipeLayout.setOnRefreshListener(this);
        AppUtils.setColorSchemeColors(context, swipeLayout);
        piaAvAdapter.setWidth(QMUIDisplayHelper.getScreenWidth(context));
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(piaAvAdapter);
        piaAvAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PigAv pigAv = (PigAv) adapter.getItem(position);
                if (pigAv == null) {
                    return;
                }
                Intent intent = new Intent(context, PlayPigAvActivity.class);
                intent.putExtra(Keys.KEY_INTENT_PIG_AV_ITEM, pigAv);
                startActivityWithAnimotion(intent);
            }
        });
        piaAvAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.moreVideoList(category.getCategoryValue(), false);
            }
        });
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
        presenter.videoList(category.getCategoryValue(), false);
    }

    public static PigAvFragment getInstance() {
        return new PigAvFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setData(List<PigAv> pigAvList) {
        piaAvAdapter.setNewData(pigAvList);
    }

    @Override
    public void loadMoreFailed() {
        piaAvAdapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        piaAvAdapter.loadMoreEnd(true);
    }

    @Override
    public void setMoreData(List<PigAv> pigAvList) {
        piaAvAdapter.loadMoreComplete();
        piaAvAdapter.addData(pigAvList);
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
    public void onRefresh() {
        presenter.videoList(category.getCategoryValue(), true);
    }
}
