package com.u91porn.di.module;

import android.support.v7.app.AppCompatActivity;

import dagger.Module;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@Module
public class ActivityModule {
    private AppCompatActivity mAppCompatActivity;

    public ActivityModule(AppCompatActivity mAppCompatActivity) {
        this.mAppCompatActivity = mAppCompatActivity;
    }
}
