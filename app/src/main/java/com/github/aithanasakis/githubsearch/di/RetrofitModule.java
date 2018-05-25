package com.github.aithanasakis.githubsearch.di;

import com.github.aithanasakis.githubsearch.network.GitHubApi;
import com.github.aithanasakis.githubsearch.network.ItemTypeAdapterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by 3piCerberus on 24/04/2018.
 */

@Module(includes = OkHttpClientModule.class)
public class RetrofitModule {
    final static String BASE_URL =
            "https://api.github.com/";

    @Provides
    @Singleton
    public GitHubApi getRecipesService(Retrofit getClient){
        return getClient.create(GitHubApi.class);
    }

    @Provides
    public Retrofit getClient(OkHttpClient okHttpClient,
                              GsonConverterFactory gsonConverterFactory, Gson gson){
        return new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    @Provides
    public Gson gson(){
        GsonBuilder gsonBuilder = new GsonBuilder();
        return gsonBuilder.registerTypeAdapterFactory(new ItemTypeAdapterFactory()).create();
    }

    @Provides
    public GsonConverterFactory gsonConverterFactory(Gson gson){
        return GsonConverterFactory.create(gson);
    }
}

