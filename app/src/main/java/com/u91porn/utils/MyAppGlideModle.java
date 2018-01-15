package com.u91porn.utils;

import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;

/**
 * @author flymegoc
 * @date 2018/1/14
 */

@GlideModule
public final class MyAppGlideModle extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 250;
        builder.setDiskCache(new DiskLruCacheFactory(AppCacheUtils.getGlideDiskCacheDir(context).getAbsolutePath(), diskCacheSizeBytes));
    }
}
