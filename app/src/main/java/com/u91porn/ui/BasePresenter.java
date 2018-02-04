package com.u91porn.ui;

import android.arch.lifecycle.Lifecycle;

import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.cache.CacheProviders;
import com.u91porn.data.dao.DataBaseManager;

/**
 * @author flymegoc
 * @date 2018/2/4
 */

public class BasePresenter {
    protected CacheProviders cacheProviders;
    protected LifecycleProvider<Lifecycle.Event> provider;
    protected DataBaseManager dataBaseManager;

    public BasePresenter(DataBaseManager dataBaseManager) {
        this.dataBaseManager = dataBaseManager;
    }

    public BasePresenter(CacheProviders cacheProviders) {
        this.cacheProviders = cacheProviders;
    }

    public BasePresenter(LifecycleProvider<Lifecycle.Event> provider) {
        this.provider = provider;
    }


    public BasePresenter(CacheProviders cacheProviders, LifecycleProvider<Lifecycle.Event> provider) {
        this.cacheProviders = cacheProviders;
        this.provider = provider;
    }

    public BasePresenter(LifecycleProvider<Lifecycle.Event> provider, DataBaseManager dataBaseManager) {
        this.provider = provider;
        this.dataBaseManager = dataBaseManager;
    }

    public BasePresenter(CacheProviders cacheProviders, LifecycleProvider<Lifecycle.Event> provider, DataBaseManager dataBaseManager) {
        this.cacheProviders = cacheProviders;
        this.provider = provider;
        this.dataBaseManager = dataBaseManager;
    }
}
