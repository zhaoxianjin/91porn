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
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.FavoriteAdapter;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.MvpActivity;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.HeaderUtils;
import com.u91porn.utils.LoadHelperUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.constants.Keys;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    private LoadViewHelper helper;
    private AlertDialog deleteAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        deleteAlertDialog = DialogUtils.initLodingDialog(this, "删除中，请稍后...");
        initToolBar(toolbar);
        toolbar.setContentInsetStartWithNavigation(0);

        // Setup contentView == SwipeRefreshView
        contentView.setOnRefreshListener(this);

        List<UnLimit91PornItem> mUnLimit91PornItemList = new ArrayList<>();
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
                    UnLimit91PornItem unLimit91PornItem = (UnLimit91PornItem) adapter.getItem(position);
                    if (unLimit91PornItem == null || unLimit91PornItem.getVideoResult() == null) {
                        showMessage("信息错误，无法删除", TastyToast.WARNING);
                        return;
                    }
                    presenter.deleteFavorite(unLimit91PornItem.getVideoResult().getVideoId());
                }
            }
        });

        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                //presenter.loadFavoriteData(mUnLimit91PornItemList.size(), pageSize);
                presenter.loadRemoteFavoriteData(false, HeaderUtils.getIndexHeader());
            }
        }, recyclerView);
        //presenter.loadFavoriteData(mUnLimit91PornItemList.size(), pageSize);

        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                presenter.loadRemoteFavoriteData(false, HeaderUtils.getIndexHeader());
            }
        });
        boolean needRefresh = (boolean) SPUtils.get(this, Keys.KEY_SP_USER_FAVORITE_NEED_REFRESH, false);
        presenter.loadRemoteFavoriteData(needRefresh, HeaderUtils.getIndexHeader());
    }

    @NonNull
    @Override
    public FavoritePresenter createPresenter() {
        getActivityComponent().inject(this);
        DataBaseManager dataBaseManager = DataBaseManager.getInstance();
        NoLimit91PornServiceApi noLimit91PornServiceApi = apiManager.getNoLimit91PornService();
        return new FavoritePresenter(dataBaseManager, noLimit91PornServiceApi, cacheProviders, user, provider);
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
            showMessage("暂不支持导出", TastyToast.WARNING);
            //showExportDialog();
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
        mUnLimit91Adapter.setNewData(unLimit91PornItemList);
    }

    @Override
    public void loadMoreDataComplete() {
        mUnLimit91Adapter.loadMoreComplete();
    }

    @Override
    public void loadMoreFailed() {
        showMessage("加载更多失败", TastyToast.ERROR);
        mUnLimit91Adapter.loadMoreFail();
    }

    @Override
    public void noMoreData() {
        mUnLimit91Adapter.loadMoreEnd(true);
        showMessage("没有更多数据了", TastyToast.INFO);
    }

    @Override
    public void setMoreData(List<UnLimit91PornItem> unLimit91PornItemList) {
        mUnLimit91Adapter.addData(unLimit91PornItemList);
    }

    @Override
    public void deleteFavoriteSucc(String message) {
        //标志删除失败，下次加载服务器数据，清空缓存
        SPUtils.put(this, Keys.KEY_SP_USER_FAVORITE_NEED_REFRESH, true);
        dismissDialog();
        showMessage(message, TastyToast.SUCCESS);
    }

    @Override
    public void deleteFavoriteError(String message) {
        dismissDialog();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void showDeleteDialog() {
        deleteAlertDialog.show();
    }

    private void dismissDialog() {
        if (deleteAlertDialog != null && deleteAlertDialog.isShowing() && !isFinishing()) {
            deleteAlertDialog.dismiss();
        }
    }

    @Override
    public void showError(String message) {
        contentView.setRefreshing(false);
        helper.showError();
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "拼命加载中...");
        contentView.setEnabled(false);
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
    public void onRefresh() {
        presenter.loadRemoteFavoriteData(true, HeaderUtils.getIndexHeader());
    }
}
