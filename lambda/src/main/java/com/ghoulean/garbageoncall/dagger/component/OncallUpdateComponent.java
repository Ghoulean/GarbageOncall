package com.ghoulean.garbageoncall.dagger.component;

import javax.inject.Singleton;

import com.ghoulean.garbageoncall.dagger.ClientModule;
import com.ghoulean.garbageoncall.dagger.EnvironmentModule;
import com.ghoulean.garbageoncall.dagger.RotationConfig;
import com.ghoulean.garbageoncall.handler.OncallUpdateHandler;

import dagger.Component;

@Singleton
@Component(modules = {ClientModule.class, EnvironmentModule.class, RotationConfig.class})
public interface OncallUpdateComponent {
    OncallUpdateHandler oncallUpdateHandler();
}
