package com.example.android.popularmovie.dagger;

import android.app.Application;

import com.example.android.popularmovie.dagger.module.ActivityModule;
import com.example.android.popularmovie.dagger.module.ApplicationModule;
import com.example.android.popularmovie.dagger.module.FragmentModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {AndroidSupportInjectionModule.class, ApplicationModule.class, ActivityModule.class, FragmentModule.class})
public interface ApplicationComponent extends AndroidInjector<MyApplication> {
    @Component.Builder
    interface Builder {
        @BindsInstance
        ApplicationComponent.Builder application(Application application);

        ApplicationComponent build();
    }

    @Override
    void inject(MyApplication myApplication);
}