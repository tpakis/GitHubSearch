package com.github.aithanasakis.githubsearch.di;

import com.github.aithanasakis.githubsearch.localdata.GitHubDAO;
import com.github.aithanasakis.githubsearch.network.GitHubApi;
import com.github.aithanasakis.githubsearch.repository.AppRepository;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by 3piCerberus on 24/04/2018.
 */

@Module(includes = {RoomDbModule.class, RetrofitModule.class})
public class AppRepositoryModule {

    @Provides
    @Singleton
    public AppRepository getRecipesRepository(GitHubDAO gitHubDAO, GitHubApi gitHubApi){
        return new AppRepository(gitHubDAO,gitHubApi);
    }
}
