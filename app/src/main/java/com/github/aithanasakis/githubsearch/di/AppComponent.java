package com.github.aithanasakis.githubsearch.di;



import com.github.aithanasakis.githubsearch.localdata.GitHubDAO;
import com.github.aithanasakis.githubsearch.network.GitHubApi;
import com.github.aithanasakis.githubsearch.ui.main.MainActivityViewModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by 3piCerberus on 24/04/2018.
 */

@Singleton
@Component(modules = {AppModule.class,RetrofitModule.class,AppRepositoryModule.class,RoomDbModule.class})
public interface AppComponent {
    void inject(MainActivityViewModel viewModel);
}