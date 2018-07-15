package com.u91porn.adapter;

import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.UnLimit91PornItem;
import com.u91porn.utils.GlideApp;

import java.util.List;

/**
 * @author flymegoc
 * @date 2017/11/14
 */

public class UnLimit91Adapter extends BaseQuickAdapter<UnLimit91PornItem, BaseViewHolder> {

    public UnLimit91Adapter(int layoutResId, @Nullable List<UnLimit91PornItem> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UnLimit91PornItem item) {
        helper.setText(R.id.tv_91porn_item_title, item.getTitleWithDuration());
        helper.setText(R.id.tv_91porn_item_info, item.getInfo());
        ImageView simpleDraweeView = helper.getView(R.id.iv_91porn_item_img);
        Uri uri = Uri.parse(item.getImgUrl());
        GlideApp.with(helper.itemView).load(uri).placeholder(R.drawable.placeholder).transition(new DrawableTransitionOptions().crossFade(300)).into(simpleDraweeView);
    }
}
