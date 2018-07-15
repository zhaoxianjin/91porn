package com.u91porn.di.module;

import android.app.Application;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.u91porn.data.ApiManager;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.User;
import com.u91porn.di.ApplicationContext;
import com.u91porn.utils.AppCacheUtils;
import com.u91porn.utils.VideoCacheFileNameGenerator;

import java.io.File;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.rx_cache2.internal.RxCache;
import io.victoralbertos.jolyglot.GsonSpeaker;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application mApplication) {
        this.mApplication = mApplication;
    }

    @Provides
    public Application providesApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context providesContext() {
        return mApplication;
    }

    @Singleton
    @Provides
    ApiManager providesApiManager(@ApplicationContext Context context) {
        return new ApiManager(context);
    }

    @Singleton
    @Provides
    HttpProxyCacheServer providesHttpProxyCacheServer(@ApplicationContext Context context) {
        return new HttpProxyCacheServer.Builder(context)
                // 1 Gb for cache
                .maxCacheSize(AppCacheUtils.MAX_VIDEO_CACHE_SIZE)
                .cacheDirectory(AppCacheUtils.getVideoCacheDir(context))
                .fileNameGenerator(new VideoCacheFileNameGenerator())
                .build();
    }

    @Singleton
    @Provides
    CacheProviders ProvidesCacheProviders(@ApplicationContext Context context) {
        File cacheDir = AppCacheUtils.getRxCacheDir(context);
        return new RxCache.Builder()
                .persistence(cacheDir, new GsonSpeaker())
                .using(CacheProviders.class);
    }

    @Singleton
    @Provides
    User providesUser() {
        return new User();
    }
}
