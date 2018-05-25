package com.github.aithanasakis.githubsearch.application;

import android.app.Application;
import android.content.Context;

import com.facebook.stetho.Stetho;
import com.github.aithanasakis.githubsearch.BuildConfig;
import com.github.aithanasakis.githubsearch.di.AppComponent;
import com.github.aithanasakis.githubsearch.di.AppModule;
import com.github.aithanasakis.githubsearch.di.DaggerAppComponent;
import com.github.aithanasakis.githubsearch.di.RetrofitModule;
import com.github.aithanasakis.githubsearch.di.RoomDbModule;


import timber.log.Timber;

public class GitHubApplication extends Application{

    private AppComponent mAppComponent;
    private static Context context;
    private static GitHubApplication mApplication;

    public void onCreate() {
        super.onCreate();

        mApplication=this;
        GitHubApplication.context = getApplicationContext();
        if (BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
            initializeStetho();
        }
        initDagger();
    }

    public void  initializeStetho() {
        Stetho.initializeWithDefaults(this);
    }

    private void initDagger(){

        mAppComponent = DaggerAppComponent.builder()
                .retrofitModule(new RetrofitModule())
                .appModule(new AppModule(this))
                .roomDbModule(new RoomDbModule(this))
                .build();
    }

    public AppComponent getMainActivityViewModelComponent() {
        return mAppComponent;
    }

    public static GitHubApplication getMyApplication() {
        return mApplication;
    }

}
