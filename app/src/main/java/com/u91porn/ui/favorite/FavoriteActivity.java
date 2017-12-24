package com.u91porn.ui.favorite;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aitsuki.swipe.SwipeItemLayout;
import com.aitsuki.swipe.SwipeMenuRecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.adapter.FavoriteAdapter;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.User;
import com.u91porn.ui.MvpActivity;
import com.u91porn.utils.Keys;
import com.u91porn.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 */
public class FavoriteActivity extends MvpActivity<FavoriteView, FavoritePresenter> implements FavoriteView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.contentView)
    SwipeRefreshLayout contentView;

    private FavoriteAdapter mUnLimit91Adapter;
    private List<UnLimit91PornItem> mUnLimit91PornItemList;
    private CacheProviders cacheProviders = MyApplication.getInstace().getCacheProviders();

    private LoadViewHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        setTitle(R.string.my_collect);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setContentInsetStartWithNavigation(0);

        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);

        mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new FavoriteAdapter(R.layout.item_right_menu_delete, mUnLimit91PornItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(mUnLimit91Adapter);
        mUnLimit91Adapter.setEmptyView(R.layout.empty_view, recyclerView);

        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                goToPlayVideo((UnLimit91PornItem) adapter.getItem(position));
            }
        });

        mUnLimit91Adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                SwipeItemLayout swipeItemLayout = (SwipeItemLayout) view.getParent();
                swipeItemLayout.close();
                if (view.getId() == R.id.right_menu_delete) {
                    showMessage("暂不支持删除");
                    //presenter.deleteFavorite(position, (UnLimit91PornItem) adapter.getItem(position));
                }
            }
        });

        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                //presenter.loadFavoriteData(mUnLimit91PornItemList.size(), pageSize);
                presenter.loadRemoteFavoriteData(false);
            }
        }, recyclerView);
        //presenter.loadFavoriteData(mUnLimit91PornItemList.size(), pageSize);

        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                presenter.loadRemoteFavoriteData(false);
            }
        });
        boolean needRefresh = (boolean) SPUtils.get(this, Keys.KEY_SP_USER_FAVORITE_NEED_REFRESH, false);
        presenter.loadRemoteFavoriteData(needRefresh);
    }

    @NonNull
    @Override
    public FavoritePresenter createPresenter() {
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        User user = MyApplication.getInstace().getUser();
        return new FavoritePresenter(unLimit91PornItemBox, noLimit91PornServiceApi, cacheProviders, user,provider);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favorite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_favorite_export) {
            showExportDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showExportDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("导出选择");
        builder.setMessage("导出视频链接和标题还是仅导出视频链接？");
        builder.setNegativeButton("包括标题", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.exportData(false);
            }
        });
        builder.setPositiveButton("仅链接", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.exportData(true);
            }
        });
        builder.show();

    }

    @Override
    public void setFavoriteData(List<UnLimit91PornItem> unLimit91PornItemList) {
        SPUtils.put(this, Keys.KEY_SP_USER_FAVORITE_NEED_REFRESH, false);
        mUnLimit91Adapter.loadMoreComplete();
        mUnLimit91Adapter.setNewData(unLimit91PornItemList);
        mUnLimit91Adapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    @Override
    public void deleteFavoriteSucc(int position) {
        mUnLimit91Adapter.remove(position);
    }

    @Override
    public void loadMoreDataComplete() {
        mUnLimit91Adapter.loadMoreComplete();
    }

    @Override
    public void loadMoreFailed() {
        showMessage("加载更多失败");
        mUnLimit91Adapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        mUnLimit91Adapter.loadMoreEnd(true);
        showMessage("没有更多数据了");
    }

    @Override
    public void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList) {
        mUnLimit91Adapter.addData(unLimit91PornItemList);
    }

    @Override
    public String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {
        contentView.setRefreshing(false);
        helper.showError();
        showMessage(e.getMessage());
        e.printStackTrace();
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        contentView.setEnabled(false);
    }

    @Override
    public void showContent() {
        helper.showContent();
        contentView.setEnabled(true);
        contentView.setRefreshing(false);
    }

    @Override
    public void showMessage(String msg) {
        super.showMessage(msg);
    }

    @Override
    public void onRefresh() {
        presenter.loadRemoteFavoriteData(true);
    }
}
