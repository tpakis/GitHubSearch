package com.github.aithanasakis.githubsearch.di;

import android.arch.persistence.room.Room;

import com.github.aithanasakis.githubsearch.application.GitHubApplication;
import com.github.aithanasakis.githubsearch.localdata.GitHubDAO;
import com.github.aithanasakis.githubsearch.localdata.GitHubDB;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by 3piCerberus on 24/04/2018.
 */
@Module(includes = AppModule.class)
public class RoomDbModule {
    private GitHubDB gitHubDB;

    public RoomDbModule(GitHubApplication mApplication) {
        gitHubDB = Room.databaseBuilder(mApplication, GitHubDB.class, GitHubDB.DB_NAME).build();
    }

    @Singleton
    @Provides
    GitHubDB providesRoomDatabase() {
        return gitHubDB;
    }

    @Singleton
    @Provides
    GitHubDAO providesGitHubDao(GitHubDB gitHubDB) {
        return gitHubDB.getGitHubDao();
    }
}
