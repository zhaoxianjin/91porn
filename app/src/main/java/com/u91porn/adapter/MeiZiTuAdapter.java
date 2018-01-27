package com.u91porn.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.u91porn.R;
import com.u91porn.data.model.MeiZiTu;
import com.u91porn.utils.GlideApp;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author flymegoc
 * @date 2018/1/25
 */

public class MeiZiTuAdapter extends BaseQuickAdapter<MeiZiTu, BaseViewHolder> {
    private Map<String, Integer> heightMap = new HashMap<>();
    private int width;

    public MeiZiTuAdapter(int layoutResId, @Nullable List<MeiZiTu> data) {
        super(layoutResId, data);
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    protected void convert(BaseViewHolder helper, MeiZiTu item) {
        ImageView imageView = helper.getView(R.id.iv_item_mei_zi_tu);
        GlideApp.with(helper.itemView.getContext()).load(buildGlideUrl(item.getThumbUrl())).transition(new DrawableTransitionOptions().crossFade(300)).into(imageView);
        int height;
        if (!heightMap.containsKey(item.getThumbUrl())) {
            height = item.getHeight() * width / item.getWidth();
            heightMap.put(item.getThumbUrl(), height);
        } else {
            height = heightMap.get(item.getThumbUrl());
        }
        StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) helper.itemView.getLayoutParams();
        layoutParams.height = height;
        helper.itemView.setLayoutParams(layoutParams);
    }

    private GlideUrl buildGlideUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        } else {
            return new GlideUrl(url, new LazyHeaders.Builder()
                    .addHeader("Accept-Language", "zh-CN,zh;q=0.9,zh-TW;q=0.8")
                    .addHeader("Host", "i.meizitu.net")
                    .addHeader("Referer", "http://www.mzitu.com/")
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36")
                    .build());
        }
    }
}
