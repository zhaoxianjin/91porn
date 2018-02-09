package com.u91porn.ui.download;


import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aitsuki.swipe.SwipeItemLayout;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.adapter.DownloadVideoAdapter;
import com.u91porn.data.dao.DataBaseManager;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.service.DownloadVideoService;
import com.u91porn.ui.MvpFragment;
import com.u91porn.utils.AppCacheUtils;
import com.u91porn.utils.DownloadManager;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.constants.Keys;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class FinishedFragment extends MvpFragment<DownloadView, DownloadPresenter> implements DownloadManager.DownloadStatusUpdater, DownloadView {


    @BindView(R.id.recyclerView_download_finish)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private DownloadVideoAdapter mDownloadAdapter;
    private boolean isFoucesRefresh = false;

    @Inject
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
        getActivityComponent().inject(this);

        File videoCacheDir = AppCacheUtils.getVideoCacheDir(getContext());
        return new DownloadPresenter(DataBaseManager.getInstance(), provider, httpProxyCacheServer, videoCacheDir);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        List<UnLimit91PornItem> mUnLimit91PornItemList = new ArrayList<>();
        mDownloadAdapter = new DownloadVideoAdapter(R.layout.item_right_menu_delete_download, mUnLimit91PornItemList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mDownloadAdapter);
        mDownloadAdapter.setEmptyView(R.layout.empty_view, recyclerView);
        mDownloadAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItem = (UnLimit91PornItem) adapter.getItem(position);
                openMp4File(unLimit91PornItem);
            }
        });
        mDownloadAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                UnLimit91PornItem unLimit91PornItem = (UnLimit91PornItem) adapter.getItem(position);
                if (view.getId() == R.id.right_menu_delete && unLimit91PornItem != null) {
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    /**
     * 调用系统播放器播放本地视频
     *
     * @param unLimit91PornItem item
     */
    private void openMp4File(UnLimit91PornItem unLimit91PornItem) {
        File file = new File(unLimit91PornItem.getDownLoadPath());
        if (file.exists()) {
            Uri uri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                uri = FileProvider.getUriForFile(context.getApplicationContext(), "com.u91porn.fileprovider", file);
            } else {
                uri = Uri.fromFile(file);
            }
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/mp4");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            PackageManager pm = context.getPackageManager();
            ComponentName cn = intent.resolveActivity(pm);
            if (cn == null) {
                showMessage("你手机上未安装任何可以播放此视频的播放器！", TastyToast.INFO);
                return;
            }
            startActivity(intent);
        } else {
            showReDownloadFileDialog(unLimit91PornItem);
        }
    }


    private void showReDownloadFileDialog(final UnLimit91PornItem unLimit91PornItem) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("文件不存在，可能已经被删除，要重新下载？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean isDownloadNeedWifi = (boolean) SPUtils.get(getContext(), Keys.KEY_SP_DOWNLOAD_VIDEO_NEED_WIFI, false);
                unLimit91PornItem.setDownloadId(0);
                unLimit91PornItem.setSoFarBytes(0);
                DataBaseManager.getInstance().update(unLimit91PornItem);
                presenter.downloadVideo(unLimit91PornItem, isDownloadNeedWifi, true);
                isFoucesRefresh = true;
                Intent intent = new Intent(getContext(), DownloadVideoService.class);
                context.startService(intent);
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
    public void complete(BaseDownloadTask task) {
        presenter.loadFinishedData();
    }

    @Override
    public void update(BaseDownloadTask task) {
        if (isFoucesRefresh) {
            isFoucesRefresh = false;
            presenter.loadFinishedData();
        }
    }

    @Override
    public void setDownloadingData(List<UnLimit91PornItem> unLimit91PornItems) {

    }

    @Override
    public void setFinishedData(List<UnLimit91PornItem> unLimit91PornItems) {
        mDownloadAdapter.setNewData(unLimit91PornItems);
    }


    @Override
    public void showError(String message) {
        showMessage(message, TastyToast.ERROR);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }
}
