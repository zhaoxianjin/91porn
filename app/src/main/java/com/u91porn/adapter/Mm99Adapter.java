package com.u91porn.adapter;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.Mm99;
import com.u91porn.utils.GlideApp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author flymegoc
 * @date 2018/2/1
 */

public class Mm99Adapter extends BaseQuickAdapter<Mm99, BaseViewHolder> {
    private Map<String, Integer> heightMap = new HashMap<>();
    private int width;

    public Mm99Adapter(int layoutResId) {
        super(layoutResId);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final Mm99 item) {
        final ImageView imageView = helper.getView(R.id.iv_item_99_mm);
        GlideApp.with(helper.itemView.getContext()).asBitmap().load(item.getImgUrl()).transition(new BitmapTransitionOptions().crossFade(300)).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                imageView.setImageBitmap(resource);
                int height;
                if (!heightMap.containsKey(item.getImgUrl())) {
                    height = resource.getHeight() * width / item.getImgWidth();
                    heightMap.put(item.getImgUrl(), height);
                } else {
                    height = heightMap.get(item.getImgUrl());
                }
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) helper.itemView.getLayoutParams();
                layoutParams.height = height;
                helper.itemView.setLayoutParams(layoutParams);
            }
        });

    }
}
