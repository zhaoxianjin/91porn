package com.u91porn.ui.download;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.aitsuki.swipe.SwipeItemLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.orhanobut.logger.Logger;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.adapter.UnLimit91Adapter;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.data.model.UnLimit91PornItem_;
import com.u91porn.ui.BaseFragment;
import com.u91porn.ui.MvpFragment;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.utils.DownloadManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.objectbox.Box;
import io.rx_cache2.Reply;

/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadingFragment extends MvpFragment<DownloadView, DownloadPresenter> implements DownloadManager.DownloadStatusUpdater, DownloadView {

    private static final String TAG = DownloadingFragment.class.getSimpleName();
    @BindView(R.id.recyclerView_download)
    RecyclerView recyclerView;
    Unbinder unbinder;
    private UnLimit91Adapter mUnLimit91Adapter;


    public DownloadingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DownloadManager.getImpl().addUpdater(this);
    }

    @NonNull
    @Override
    public DownloadPresenter createPresenter() {
        DownloadActivity downloadActivity = (DownloadActivity) getActivity();
        Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);
        return new DownloadPresenter(unLimit91PornItemBox, downloadActivity.provider);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        List<UnLimit91PornItem> mUnLimit91PornItemList = new ArrayList<>();
        mUnLimit91Adapter = new UnLimit91Adapter(R.layout.item_right_menu_delete, mUnLimit91PornItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mUnLimit91Adapter);
        mUnLimit91Adapter.setEmptyView(R.layout.empty_view, recyclerView);

        mUnLimit91Adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItem = (UnLimit91PornItem) adapter.getItem(position);
                Logger.t(TAG).d("当前状态：" + unLimit91PornItem.getStatus());
                if (view.getId() == R.id.right_menu_delete) {
                    SwipeItemLayout swipeItemLayout = (SwipeItemLayout) view.getParent();
                    swipeItemLayout.close();
                    boolean isSuccess = FileDownloader.getImpl().clear(unLimit91PornItem.getDownloadId(), unLimit91PornItem.getDownLoadPath());
                    if (isSuccess) {
                        presenter.deleteDownloadingTask(unLimit91PornItem);
                        presenter.loadDownloadingData();
                    } else {
                        showMessage("删除失败，请重试");
                    }
                } else if (view.getId() == R.id.iv_download_control) {
                    if (unLimit91PornItem.getStatus() == FileDownloadStatus.progress && FileDownloader.getImpl().isServiceConnected()) {
                        FileDownloader.getImpl().pause(unLimit91PornItem.getDownloadId());
                        ((ImageView) view).setImageResource(R.drawable.start_download);
                    } else {
                        DownloadManager.getImpl().startDownload(unLimit91PornItem.getVideoResult().getTarget().getVideoUrl(), unLimit91PornItem.getDownLoadPath());
                        ((ImageView) view).setImageResource(R.drawable.pause_download);
                    }
                }
            }
        });
        presenter.loadDownloadingData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_download, container, false);
    }

    @Override
    public void blockComplete(BaseDownloadTask task) {
        Logger.t(TAG).d("已经下载完成了");
        presenter.loadDownloadingData();
    }

    @Override
    public void update(BaseDownloadTask task) {
        presenter.loadDownloadingData();
    }

    @Override
    public String getTitle() {
        return "正在下载";
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().removeUpdater(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void setDownloadingData(List<UnLimit91PornItem> unLimit91PornItems) {
        mUnLimit91Adapter.setNewData(unLimit91PornItems);
    }

    @Override
    public void setFinishedData(List<UnLimit91PornItem> unLimit91PornItems) {

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

}
