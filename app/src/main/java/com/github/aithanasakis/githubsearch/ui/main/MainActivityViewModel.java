package com.github.aithanasakis.githubsearch.ui.main;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.Nullable;

import com.github.aithanasakis.githubsearch.application.GitHubApplication;
import com.github.aithanasakis.githubsearch.model.GitOwner;
import com.github.aithanasakis.githubsearch.model.GitRepository;
import com.github.aithanasakis.githubsearch.model.basic.Resource;
import com.github.aithanasakis.githubsearch.repository.AppRepository;


import java.util.List;

import javax.inject.Inject;

public class MainActivityViewModel extends ViewModel{

    @Inject
    AppRepository mRepository;
    private MediatorLiveData<Resource<List<GitRepository>>> repositoriesListObservable = new MediatorLiveData<Resource<List<GitRepository>>>();
    private MediatorLiveData<List<GitOwner>> subscribersListObservable = new MediatorLiveData<List<GitOwner>>();
    private String lastQuery = "";
    private GitRepository selectedRepository;

    @Inject
    public MainActivityViewModel() {
        super();

        GitHubApplication.getMyApplication().getMainActivityViewModelComponent().inject(this);
        //subscribe to Livedata of the repository and pass it along to the view (activity)
        repositoriesListObservable.addSource(mRepository.getRepositoriesListObservable(), new Observer<Resource<List<GitRepository>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<GitRepository>> repositories) {
                repositoriesListObservable.setValue(repositories);
            }
        });
        subscribersListObservable.addSource(mRepository.getSubscribersListObservable(), new Observer<List<GitOwner>>() {
            @Override
            public void onChanged(@Nullable List<GitOwner> subsrcibers) {
                subscribersListObservable.setValue(subsrcibers);
            }
        });
    }

    public GitRepository getSelectedRepository() {
        return selectedRepository;
    }

    public void setSelectedRepository(GitRepository selectedRepository) {
        this.selectedRepository = selectedRepository;
        mRepository.fetchSubscribers(selectedRepository.getSubscribersUrl());
    }

    public String getLastQuery() {
        return lastQuery;
    }

    public void setLastQuery(String lastQuery) {
        this.lastQuery = lastQuery;
    }

    public void getData(boolean online){
        mRepository.fetchData(lastQuery,online);
    }

    public LiveData<Resource<List<GitRepository>>> getRepositoriesListObservable() {
        return repositoriesListObservable;
    }

    public MediatorLiveData<List<GitOwner>> getSubscribersListObservable() {
        return subscribersListObservable;
    }
}
