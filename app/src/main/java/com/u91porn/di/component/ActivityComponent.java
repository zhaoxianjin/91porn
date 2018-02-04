package com.u91porn.di.component;

import com.u91porn.di.PerActivity;
import com.u91porn.di.module.ActivityModule;

import dagger.Component;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {

}
