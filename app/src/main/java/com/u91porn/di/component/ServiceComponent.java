package com.u91porn.di.component;

import com.u91porn.di.PerService;
import com.u91porn.di.module.ServiceModule;

import dagger.Component;

/**
 * @author flymegoc
 * @date 2018/2/4
 */
@PerService
@Component(dependencies = ApplicationComponent.class, modules = ServiceModule.class)
public interface ServiceComponent {

}
