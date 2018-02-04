package com.u91porn.di.module;

import android.app.Application;

import dagger.Module;

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
}
