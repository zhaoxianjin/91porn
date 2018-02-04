package com.u91porn.adapter;

import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.PigAv;
import com.u91porn.utils.GlideApp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author flymegoc
 * @date 2018/1/30
 */

public class PigAvAdapter extends BaseQuickAdapter<PigAv, BaseViewHolder> {
    private Map<String, Integer> heightMap = new HashMap<>();
    private int width;

    public PigAvAdapter(int layoutResId) {
        super(layoutResId);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    protected void convert(BaseViewHolder helper, PigAv item) {
        helper.setText(R.id.tv_item_pig_av_title, item.getTitle());
        int height;
        if (!heightMap.containsKey(item.getImgUrl())) {
            height = item.getImgHeight() * width / item.getImgWidth()+15;
            heightMap.put(item.getImgUrl(), height);
        } else {
            height = heightMap.get(item.getImgUrl());
        }
        AppCompatImageView imageView = helper.getView(R.id.iv_item_pig_av_img);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParams.height = height;
        imageView.setLayoutParams(layoutParams);
        GlideApp.with(helper.itemView).load(item.getImgUrl()).placeholder(R.drawable.placeholder).transition(new DrawableTransitionOptions().crossFade(300)).into(imageView);
    }
}
