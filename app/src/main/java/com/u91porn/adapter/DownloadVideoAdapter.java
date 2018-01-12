package com.u91porn.adapter;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.format.Formatter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.model.FileDownloadStatus;
import com.u91porn.R;
import com.u91porn.data.model.UnLimit91PornItem;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/9
 */

public class DownloadVideoAdapter extends BaseQuickAdapter<UnLimit91PornItem, BaseViewHolder> {

    public DownloadVideoAdapter(int layoutResId, @Nullable List<UnLimit91PornItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UnLimit91PornItem item) {
        helper.setText(R.id.tv_91porn_item_title, item.getTitleWithDuration());
        SimpleDraweeView simpleDraweeView = helper.getView(R.id.iv_91porn_item_img);
        Uri uri = Uri.parse(item.getImgUrl());
        simpleDraweeView.setImageURI(uri);
        helper.setProgress(R.id.progressBar_download, item.getProgress());
        helper.setText(R.id.tv_download_progress, String.valueOf(item.getProgress()) + "%");
        helper.setText(R.id.tv_download_filesize, Formatter.formatFileSize(helper.itemView.getContext(), item.getSoFarBytes()).replace("MB", "") + "/ " + Formatter.formatFileSize(helper.itemView.getContext(), item.getTotalFarBytes()));
        if (item.getStatus() == FileDownloadStatus.completed) {
            helper.setText(R.id.tv_download_speed, "已完成");
            helper.setVisible(R.id.iv_download_control, false);
        } else {
            //未下载完成，显示控制
            helper.setVisible(R.id.iv_download_control, true);
            if (FileDownloader.getImpl().isServiceConnected()) {

                if (item.getStatus() == FileDownloadStatus.progress) {
                    helper.setImageResource(R.id.iv_download_control, R.drawable.pause_download);
                    helper.setText(R.id.tv_download_speed, item.getSpeed() + " KB/s");
                } else {
                    helper.setImageResource(R.id.iv_download_control, R.drawable.start_download);
                    if (item.getStatus() == FileDownloadStatus.paused) {
                        helper.setText(R.id.tv_download_speed, "暂停中");
                    } else if (item.getStatus() == FileDownloadStatus.pending) {
                        helper.setText(R.id.tv_download_speed, "准备中");
                    } else if (item.getStatus() == FileDownloadStatus.started) {
                        helper.setText(R.id.tv_download_speed, "开始下载");
                    } else if (item.getStatus() == FileDownloadStatus.connected) {
                        helper.setText(R.id.tv_download_speed, "连接中");
                    }
                }
            } else {
                helper.setText(R.id.tv_download_speed, "暂停中");
                helper.setImageResource(R.id.iv_download_control, R.drawable.start_download);
            }
        }
        helper.addOnClickListener(R.id.right_menu_delete);
    }
}
