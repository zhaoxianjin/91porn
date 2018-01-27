package com.u91porn.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.qmuiteam.qmui.span.QMUIAlignMiddleImageSpan;
import com.qmuiteam.qmui.util.QMUIDisplayHelper;
import com.u91porn.R;
import com.u91porn.data.Api;
import com.u91porn.data.model.Forum91PronItem;
import com.u91porn.utils.GlideApp;

import java.util.List;

/**
 * @author flymegoc
 * @date 2018/1/23
 */

public class Forum91PornAdapter extends BaseQuickAdapter<Forum91PronItem, Forum91PornAdapter.ViewHolder> {
    private Context context;

    public Forum91PornAdapter(Context context, int layoutResId, @Nullable List<Forum91PronItem> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(final ViewHolder helper, final Forum91PronItem item) {
        helper.spannableString = SpannableString.valueOf("  " + item.getTitle() + "    " + (TextUtils.isEmpty(item.getAgreeCount()) ? "" : item.getAgreeCount()));
        GlideApp.with(context).asDrawable().load(Uri.parse(Api.APP_91PRON_FROUM_DOMAIN + item.getFolder())).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                resource.setBounds(0, 0, QMUIDisplayHelper.px2dp(context, 150), QMUIDisplayHelper.px2dp(context, 150));
                QMUIAlignMiddleImageSpan qmuiAlignMiddleImageSpan = new QMUIAlignMiddleImageSpan(resource, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE);
                helper.spannableString.setSpan(qmuiAlignMiddleImageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                helper.setText(R.id.tv_item_forum_91_porn_title, helper.spannableString);
            }
        });
        if (!TextUtils.isEmpty(item.getIcon())) {
            GlideApp.with(context).asDrawable().load(Uri.parse(Api.APP_91PRON_FROUM_DOMAIN + item.getIcon())).into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    resource.setBounds(0, 0, QMUIDisplayHelper.px2dp(context, 150), QMUIDisplayHelper.px2dp(context, 150));
                    QMUIAlignMiddleImageSpan qmuiAlignMiddleImageSpan = new QMUIAlignMiddleImageSpan(resource, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE);
                    helper.spannableString.setSpan(qmuiAlignMiddleImageSpan, 1, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    helper.setText(R.id.tv_item_forum_91_porn_title, helper.spannableString);
                }
            });
        }
        if (item.getImageList() != null) {
            for (int i = 0; i < item.getImageList().size(); i++) {
                final int j = i;
                String url = item.getImageList().get(i);
                GlideApp.with(context).asDrawable().load(Uri.parse(Api.APP_91PRON_FROUM_DOMAIN + url)).into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        resource.setBounds(0, 0, QMUIDisplayHelper.px2dp(context, 150), QMUIDisplayHelper.px2dp(context, 150));
                        QMUIAlignMiddleImageSpan qmuiAlignMiddleImageSpan = new QMUIAlignMiddleImageSpan(resource, QMUIAlignMiddleImageSpan.ALIGN_MIDDLE);
                        helper.spannableString.setSpan(qmuiAlignMiddleImageSpan, item.getTitle().length() + j+2, item.getTitle().length() + j + 3, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                        helper.setText(R.id.tv_item_forum_91_porn_title, helper.spannableString);
                    }
                });
            }
            if (item.getImageList().contains("images/default/digest_1.gif")) {
                helper.setTextColor(R.id.tv_item_forum_91_porn_title, ContextCompat.getColor(context, R.color.forum_91_porn_essence));
            } else {
                helper.setTextColor(R.id.tv_item_forum_91_porn_title, ContextCompat.getColor(context, R.color.item_91pron_title_text_color));
            }
        }
        helper.setText(R.id.tv_item_forum_91_porn_author_publish_time, item.getAuthor() + "\n" + item.getAuthorPublishTime());
        helper.setText(R.id.tv_item_forum_91_porn_reply_view, item.getReplyCount() + "/" + item.getViewCount());
        helper.setText(R.id.tv_item_forum_91_porn_last_post_author_time, item.getLastPostAuthor() + "\n" + item.getLastPostTime());
    }

    class ViewHolder extends BaseViewHolder {
        SpannableString spannableString;

        public ViewHolder(View view) {
            super(view);
        }
    }
}
