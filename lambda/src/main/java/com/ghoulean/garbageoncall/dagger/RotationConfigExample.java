package com.ghoulean.garbageoncall.dagger;

import java.util.List;

import javax.inject.Singleton;

import com.ghoulean.garbageoncall.model.OncallRotation;
import com.ghoulean.garbageoncall.model.Person;
import com.ghoulean.garbageoncall.model.Schedule;

import dagger.Module;
import dagger.Provides;

// This is hardcoded because I'm bad at programming + lazy
@Module
public final class RotationConfigExample {
    @Singleton
    @Provides
    public OncallRotation provideOncallRotation() {
        return new OncallRotation(
            new Schedule<Person>(List.of(
                new Person("123456789012345678", "Person1", true),
                new Person("222222222222222222", "Person2", false),
                new Person("333333333333333333", "Person3", false)
            )),
            new Schedule<Boolean>(List.of(false, true))
        );
    }
}
