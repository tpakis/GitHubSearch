package com.github.aithanasakis.githubsearch.di;

import android.content.Context;

import com.github.aithanasakis.githubsearch.application.GitHubApplication;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by 3piCerberus on 24/04/2018.
 */

@Module
public class AppModule {
    GitHubApplication mApplication;

    public AppModule(GitHubApplication application) {
        mApplication = application;
    }

    @Provides
    public Context getAppContext(){
        return mApplication.getApplicationContext();
    }

    @Provides
    @Singleton
    GitHubApplication providesApplication() {
        return mApplication;
    }
}
