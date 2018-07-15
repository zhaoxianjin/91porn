package com.u91porn.di.component;

import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;
import com.u91porn.MyApplication;
import com.u91porn.data.ApiManager;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.model.User;
import com.u91porn.di.ApplicationContext;
import com.u91porn.di.module.ApplicationModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {
    void inject(MyApplication myApplication);

    @ApplicationContext
    Context getContext();

    ApiManager getApiManager();

    HttpProxyCacheServer getHttpProxyCacheServer();

    CacheProviders getCacheProviders();

    User getUser();
}
