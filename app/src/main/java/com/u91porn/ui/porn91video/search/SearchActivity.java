package com.u91porn.ui.porn91video.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.helper.loadviewhelper.help.OnLoadViewListener;
import com.helper.loadviewhelper.load.LoadViewHelper;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.UnLimit91Adapter;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.ui.MvpActivity;
import com.u91porn.utils.LoadHelperUtils;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.constants.Keys;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.NiceSpinnerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class SearchActivity extends MvpActivity<SearchView, SearchPresenter> implements SearchView {

    private static final String TAG = SearchActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.search_view)
    android.support.v7.widget.SearchView searchView;
    @BindView(R.id.nice_spinner_search)
    NiceSpinner niceSpinnerSearch;
    @BindView(R.id.nice_spinner_sort_by)
    NiceSpinner niceSpinnerSortBy;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    private String searchId;
    private String sort = "addate";
    private UnLimit91Adapter mUnLimit91Adapter;
    private LoadViewHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        init();
        setListener();
        boolean isFirst = (boolean) SPUtils.get(this, Keys.KEY_SP_FIRST_IN_SEARCH_VIDEO, false);
        if (!isFirst) {
            SPUtils.put(this, Keys.KEY_SP_FIRST_IN_SEARCH_VIDEO, true);
            showTipDialog();
        }
    }

    private void init() {
        initToolBar(toolbar);
        searchView.setQueryHint("搜索视频");
        searchView.onActionViewExpanded();

        List<String> datasetSortBy = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.sort_by)));
        niceSpinnerSortBy.attachDataSource(datasetSortBy);
        List<String> datasetSearch = new LinkedList<>(Arrays.asList(getResources().getStringArray(R.array.search)));
        niceSpinnerSearch.attachDataSource(datasetSearch);

        List<UnLimit91PornItem> mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new UnLimit91Adapter(R.layout.item_unlimit_91porn, mUnLimit91PornItemList);
        mUnLimit91Adapter.openLoadAnimation();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mUnLimit91Adapter);

        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItems = (UnLimit91PornItem) adapter.getData().get(position);
                goToPlayVideo(unLimit91PornItems);
            }
        });
        mUnLimit91Adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                presenter.searchVideos(searchId, sort, false);
            }
        }, recyclerView);

        helper = new LoadViewHelper(recyclerView);
        helper.setListener(new OnLoadViewListener() {
            @Override
            public void onRetryClick() {
                presenter.searchVideos(searchId, sort, false);
            }
        });
    }

    private void showTipDialog() {
        new QMUIDialog.MessageDialogBuilder(this)
                .setTitle("提示")
                .setMessage("1 .普通用户搜索间隔时间10分钟,VIP搜索间隔时间10秒\n" +
                        "2 .搜索视频标题显示不全，属于正常现象（官网就是如此）\n" +
                        "3 .目前暂无办法破除搜索时间限制")
                .addAction("知道了", new QMUIDialogAction.ActionListener() {
                    @Override
                    public void onClick(QMUIDialog dialog, int index) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void setListener() {
        searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals(searchId)) {
                    return false;
                }
                searchId = query;
                presenter.searchVideos(searchId, sort, true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new android.support.v7.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return true;
            }
        });
        niceSpinnerSortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NiceSpinnerAdapter niceSpinnerAdapter = (NiceSpinnerAdapter) parent.getAdapter();
                String item = (String) niceSpinnerAdapter.getItemInDataset(position);
                if (sort.equals(item)) {
                    return;
                }
                switch (item) {
                    case "添加时间":
                        sort = "addate";
                        break;
                    case "标题":
                        sort = "title";
                        break;
                    case "观看次数":
                        sort = "viewnum";
                        break;
                    case "影片时长":
                        sort = "duration";
                        break;
                    default:
                }
                if (TextUtils.isEmpty(searchId)) {
                    return;
                }
                presenter.searchVideos(searchId, sort, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        niceSpinnerSearch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                NiceSpinnerAdapter niceSpinnerAdapter = (NiceSpinnerAdapter) parent.getAdapter();
                String item = (String) niceSpinnerAdapter.getItemInDataset(position);
                if ("视频".equals(item)) {
                    niceSpinnerSortBy.setVisibility(View.VISIBLE);
                } else {
                    showMessage("暂不支持其他搜索方式", TastyToast.INFO);
                    niceSpinnerSortBy.setVisibility(View.GONE);
                }
                searchView.setQueryHint("搜索" + item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @NonNull
    @Override
    public SearchPresenter createPresenter() {
        getActivityComponent().inject(this);
        NoLimit91PornServiceApi noLimit91PornServiceApi = apiManager.getNoLimit91PornService();
        return new SearchPresenter(noLimit91PornServiceApi, provider);
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
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        helper.showLoading();
        LoadHelperUtils.setLoadingText(helper.getLoadIng(), R.id.tv_loading_text, "搜索中，请稍候...");
    }

    @Override
    public void showContent() {
        helper.showContent();
        if (mUnLimit91Adapter.getData().size() == 0) {
            helper.showEmpty();
            LoadHelperUtils.setEmptyText(helper.getLoadEmpty(), R.id.tv_empty_info, "没有找到相关数据");
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
        LoadHelperUtils.setErrorText(helper.getLoadError(), R.id.tv_error_text, "搜索失败了，点击重试");
    }
}
