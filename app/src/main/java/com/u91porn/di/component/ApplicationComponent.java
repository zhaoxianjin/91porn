package com.u91porn.di.component;

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
}
