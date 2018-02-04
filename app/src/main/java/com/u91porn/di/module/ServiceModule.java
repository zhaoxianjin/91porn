package com.u91porn.di.module;

import android.app.Service;

import dagger.Module;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@Module
public class ServiceModule {
    private final Service mService;

    public ServiceModule(Service mService) {
        this.mService = mService;
    }
}
