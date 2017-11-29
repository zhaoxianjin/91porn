package com.u91porn.ui.favorite;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.aitsuki.swipe.SwipeItemLayout;
import com.aitsuki.swipe.SwipeMenuRecyclerView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.adapter.FavoriteAdapter;
import com.u91porn.adapter.UnLimit91Adapter;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;
import com.u91porn.ui.BaseAppCompatActivity;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.play.PlayVideoActivity;
import com.u91porn.utils.BoxQureyHelper;
import com.u91porn.utils.Keys;

import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 */
public class FavoriteActivity extends MvpActivity<FavoriteView, FavoritePresenter> implements FavoriteView {

    @BindView(R.id.recyclerView)
    SwipeMenuRecyclerView recyclerView;

    private FavoriteAdapter mUnLimit91Adapter;
    private List<UnLimit91PornItem> mUnLimit91PornItemList;

    private int pageSize = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        ButterKnife.bind(this);
        setTitle(R.string.my_collect);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                    presenter.deleteFavorite(position, (UnLimit91PornItem) adapter.getItem(position));
                }
            }
        });

        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.loadFavoriteData(mUnLimit91PornItemList.size(), pageSize);
            }
        }, recyclerView);
        presenter.loadFavoriteData(mUnLimit91PornItemList.size(), pageSize);
    }

    @NonNull
    @Override
    public FavoritePresenter createPresenter() {
        return new FavoritePresenter();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
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
        mUnLimit91Adapter.loadMoreComplete();
        mUnLimit91Adapter.addData(unLimit91PornItemList);
        mUnLimit91Adapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    @Override
    public void deleteFavoriteSucc(int position) {
        mUnLimit91Adapter.remove(position);
    }

    @Override
    public void noLoadMoreData() {
        mUnLimit91Adapter.loadMoreEnd(true);
        showMessage("没有更多数据了");
    }

    @Override
    public String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg) {
        super.showMessage(msg);
    }

    @Override
    public LifecycleTransformer<Reply<String>> bindView() {
        return bindToLifecycle();
    }
}
