package com.u91porn.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.VideoComment;

import java.util.List;

/**
 * 视频评论适配器
 *
 * @author flymegoc
 * @date 2017/12/26
 */

public class VideoCommentAdapter extends BaseQuickAdapter<VideoComment, BaseViewHolder> {
    private int clickPosition = -1;
    private StringBuilder stringBuilder;
    private Context context;

    public VideoCommentAdapter(Context context, int layoutResId, @Nullable List<VideoComment> data) {
        super(layoutResId, data);
        this.context = context;
        stringBuilder = new StringBuilder();
    }

    @Override
    protected void convert(BaseViewHolder helper, VideoComment item) {
        helper.setText(R.id.tv_item_video_comment_title_info, item.getTitleInfo());
        stringBuilder.delete(0, stringBuilder.length());
        int size = item.getCommentQuoteList().size();
        for (int i = 0; i < size; i++) {
            stringBuilder.append(item.getCommentQuoteList().get(i));
            if (i < size - 1) {
                stringBuilder.append("\n");
            }
        }
        helper.setText(R.id.tv_item_video_comment_content, stringBuilder.toString());
        helper.setText(R.id.tv_item_video_comment_replytime, item.getuName() + " ----" + item.getReplyTime());
        if (helper.getLayoutPosition() == clickPosition) {
            helper.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            helper.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.common_background));
        }
    }

    public int getClickPosition() {
        return clickPosition;
    }

    public void setClickPosition(int clickPosition) {
        this.clickPosition = clickPosition;
    }
}
