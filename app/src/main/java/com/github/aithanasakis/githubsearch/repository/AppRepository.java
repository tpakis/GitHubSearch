package com.github.aithanasakis.githubsearch.repository;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.github.aithanasakis.githubsearch.localdata.GitHubDAO;
import com.github.aithanasakis.githubsearch.model.GitOwner;
import com.github.aithanasakis.githubsearch.model.GitRepository;
import com.github.aithanasakis.githubsearch.model.basic.Resource;
import com.github.aithanasakis.githubsearch.model.basic.Status;
import com.github.aithanasakis.githubsearch.network.GitHubApi;

import java.security.acl.Owner;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

public class AppRepository implements IRepository {
    private GitHubDAO gitHubDAO;
    private GitHubApi gitHubApi;
    private MutableLiveData<Resource<List<GitRepository>>> repositoriesListObservable = new MutableLiveData<Resource<List<GitRepository>>>();
    private MutableLiveData<List<GitOwner>> subscribersListObservable = new MutableLiveData<List<GitOwner>>();
    private Status pendingStatus;

    @Inject
    public AppRepository(GitHubDAO gitHubDAO, GitHubApi gitHubApi) {
        this.gitHubDAO = gitHubDAO;
        this.gitHubApi = gitHubApi;
    }

    public void fetchData(String query, boolean online) {
        pendingStatus = Status.LOADING;
        //check if we want online data
        if (online) {
            getRecipesFromWeb(query);
        } else {
            loadAllRecipesFromDB();
        }
    }

    public void fetchSubscribers(String url) {
        getSubcribersFromWeb(url);
    }

    private void loadAllRecipesFromDB() {
        Timber.d("loadAllRecipesFromDB");
        new AsyncTask<Void, Void, List<GitRepository>>() {
            @Override
            protected List<GitRepository> doInBackground(Void... a) {
                return gitHubDAO.getAllEntries();
            }

            @Override
            protected void onPostExecute(List<GitRepository> results) {
                //if no data is stored in db then the pendingStatus will be loading
                setRecipesListObservableData(results, null);
            }
        }.execute();
    }


    private void getRecipesFromWeb(String query) {
        Timber.d("getRecipesFromWeb");
        gitHubApi.getRepositories(query).enqueue(new Callback<List<GitRepository>>() {
            @Override
            public void onResponse(Call<List<GitRepository>> call, Response<List<GitRepository>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                    pendingStatus = Status.SUCCESS;
                    addRecipesToDB(response.body());
                } else {
                    // error case
                    setRecipesListObservableStatus(Status.ERROR, String.valueOf(response.code()));
                    switch (response.code()) {
                        case 404:
                            Timber.d("not found");
                            break;
                        case 200:
                            Timber.d("empty list");
                            break;
                        case 500:
                            Timber.d("not logged in or server broken");
                            break;
                        default:
                            Timber.d("unknown error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GitRepository>> call, Throwable t) {
                setRecipesListObservableStatus(Status.ERROR, t.getMessage());
            }
        });
    }

    private void getSubcribersFromWeb(String url) {
        Timber.d("getRecipesFromWeb");
        gitHubApi.getUsers(url).enqueue(new Callback<List<GitOwner>>() {
            @Override
            public void onResponse(Call<List<GitOwner>> call, Response<List<GitOwner>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                    subscribersListObservable.setValue(response.body());
                } else {
                    // error case
                    subscribersListObservable.setValue(null);
                    switch (response.code()) {
                        case 404:
                            Timber.d("not found");
                            break;
                        case 200:
                            Timber.d("empty list");
                            break;
                        case 500:
                            Timber.d("not logged in or server broken");
                            break;
                        default:
                            Timber.d("unknown error");
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<List<GitOwner>> call, Throwable t) {
                subscribersListObservable.setValue(null);
            }
        });
    }

    private void addRecipesToDB(List<GitRepository> items) {
        Timber.d("addRecipesToDB");
        new AsyncTask<List<GitRepository>, Void, Void>() {

            @Override
            protected Void doInBackground(List<GitRepository>... params) {
                gitHubDAO.deleteAllEntries();

                for (GitRepository item : params[0]) {
                    //start to insert item in db
                    gitHubDAO.insertEntry(item); //-1 if not inserted
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                loadAllRecipesFromDB();
            }
        }.execute(items);
    }


    public MutableLiveData<Resource<List<GitRepository>>> getRepositoriesListObservable() {
        return repositoriesListObservable;
    }

    public MutableLiveData<List<GitOwner>> getSubscribersListObservable() {
        return subscribersListObservable;
    }

    /**
     * This method changes the observable's LiveData data without changing the status
     *
     * @param mRepositoriesList the data that need to be updated
     * @param message           optional message for error
     */
    private void setRecipesListObservableData(List<GitRepository> mRepositoriesList, String message) {
        Status loadingStatus = pendingStatus;
       /* if (repositoriesListObservable.getValue()!= null){
            loadingStatus=repositoriesListObservable.getValue().status;
        }*/
        switch (loadingStatus) {
            case LOADING:
                repositoriesListObservable.setValue(Resource.loading(mRepositoriesList));
                break;
            case ERROR:
                repositoriesListObservable.setValue(Resource.error(message, mRepositoriesList));
                break;
            case SUCCESS:
                repositoriesListObservable.setValue(Resource.success(mRepositoriesList));
                break;
        }
    }

    /**
     * This method changes the observable's LiveData status without changing the data
     *
     * @param status  The new status of LiveData
     * @param message optional message for error
     */
    private void setRecipesListObservableStatus(Status status, String message) {
        List<GitRepository> loadingList = null;
        if (repositoriesListObservable.getValue() != null) {
            loadingList = repositoriesListObservable.getValue().data;
        }
        switch (status) {
            case ERROR:
                repositoriesListObservable.setValue(Resource.error(message, loadingList));
                break;
            case LOADING:
                repositoriesListObservable.setValue(Resource.loading(loadingList));
                break;
            case SUCCESS:
                //extra carefull not to be null, could implement a check but not needed now
                repositoriesListObservable.setValue(Resource.success(loadingList));
                break;
        }
    }
}
