package com.github.aithanasakis.githubsearch.network;

import com.github.aithanasakis.githubsearch.model.GitOwner;
import com.github.aithanasakis.githubsearch.model.GitRepository;


import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GitHubApi {

    //search for repositories with string parameter
    @GET("search/repositories?sort=stars")
    Call<List<GitRepository>> getRepositories(@Query("q") String query);

    //search for subscribers
    @GET
    Call<List<GitOwner>> getUsers(@Url String url);
}
