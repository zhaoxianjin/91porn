package com.u91porn.ui.download;


import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitsuki.swipe.SwipeItemLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
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

import java.io.File;
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
public class FinishedFragment extends MvpFragment<DownloadView, DownloadPresenter> implements DownloadManager.DownloadStatusUpdater, DownloadView {


    @BindView(R.id.recyclerView_download_finish)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private UnLimit91Adapter mUnLimit91Adapter;
    private Box<UnLimit91PornItem> unLimit91PornItemBox = MyApplication.getInstace().getBoxStore().boxFor(UnLimit91PornItem.class);

    public FinishedFragment() {
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
        mUnLimit91Adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItem = (UnLimit91PornItem) adapter.getItem(position);
                openMp4File(unLimit91PornItem);
            }
        });
        mUnLimit91Adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItem = (UnLimit91PornItem) adapter.getItem(position);
                if (view.getId() == R.id.right_menu_delete) {
                    SwipeItemLayout swipeItemLayout = (SwipeItemLayout) view.getParent();
                    swipeItemLayout.close();
                    File file = new File(unLimit91PornItem.getDownLoadPath());
                    if (file.exists()) {
                        showDeleteFileDialog(unLimit91PornItem);
                    } else {
                        presenter.deleteDownloadedTask(unLimit91PornItem, false);
                        presenter.loadFinishedData();
                    }
                }
            }
        });
        presenter.loadFinishedData();
    }

    private void showDeleteFileDialog(final UnLimit91PornItem unLimit91PornItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("是否连同删除本地文件？");
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.deleteDownloadedTask(unLimit91PornItem, false);
                presenter.loadFinishedData();
            }
        });
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.deleteDownloadedTask(unLimit91PornItem, true);
                presenter.loadFinishedData();
            }
        });
        builder.show();
    }

    private void openMp4File(UnLimit91PornItem unLimit91PornItem) {
        File file = new File(unLimit91PornItem.getDownLoadPath());
        if (file.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(getActivity().getApplicationContext(), "com.u91porn.fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mpeg");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } else {
            showReDownloadFileDialog(unLimit91PornItem);
        }
    }


    private void showReDownloadFileDialog(final UnLimit91PornItem unLimit91PornItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("提示");
        builder.setMessage("文件不存在，可能已经被删除，要重新下载？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                presenter.downloadVideo(unLimit91PornItem);
            }
        });
        builder.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_finish, container, false);
    }

    @Override
    public String getTitle() {
        return "下载完成";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DownloadManager.getImpl().removeUpdater(this);
    }

    @Override
    public void blockComplete(BaseDownloadTask task) {

    }

    @Override
    public void update(BaseDownloadTask task) {
        if (task.getStatus() == FileDownloadStatus.completed) {
            presenter.loadFinishedData();
        }
    }

    @Override
    public void setDownloadingData(List<UnLimit91PornItem> unLimit91PornItems) {

    }

    @Override
    public void setFinishedData(List<UnLimit91PornItem> unLimit91PornItems) {
        mUnLimit91Adapter.setNewData(unLimit91PornItems);
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

    }
}
