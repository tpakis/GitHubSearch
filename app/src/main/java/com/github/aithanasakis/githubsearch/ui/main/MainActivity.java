package com.github.aithanasakis.githubsearch.ui.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;

import com.github.aithanasakis.githubsearch.R;
import com.github.aithanasakis.githubsearch.application.Constants;
import com.github.aithanasakis.githubsearch.model.GitRepository;
import com.github.aithanasakis.githubsearch.model.basic.Resource;
import com.github.aithanasakis.githubsearch.model.basic.Status;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MainRvAdapter.MainRvAdapterOnClickHandler{

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.recycler_view_repositories_list)
    RecyclerView recyclerViewRepositoriesList;
    @BindView(R.id.pull_to_refresh)
    SwipeRefreshLayout pullToRefresh;
    @BindString(R.string.search)
    String search;
    @BindString(R.string.error_fetching)
    String errorFetching;
    @BindString(R.string.error_no_results)
    String errorNoResults;
    @BindView(R.id.empty_container)
    ConstraintLayout emptyContainer;
    @BindView(R.id.main_content)
    ConstraintLayout mainContent;
    private MainActivityViewModel viewModel;
    private MainRvAdapter mMainAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //set the toolbar as action bar
        setSupportActionBar(mToolbar);

        //setup recyclerview
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        recyclerViewRepositoriesList.setLayoutManager(mLinearLayoutManager);
        mMainAdapter = new MainRvAdapter(this,MainActivity.this);
        recyclerViewRepositoriesList.setAdapter(mMainAdapter);
        pullToRefresh.setOnRefreshListener(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewRepositoriesList.getContext(),
                mLinearLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.line_divider));
        recyclerViewRepositoriesList.addItemDecoration(dividerItemDecoration);

        //setup viewmodel
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.getRepositoriesListObservable().observe(MainActivity.this, new Observer<Resource<List<GitRepository>>>() {
            @Override
            public void onChanged(@Nullable Resource<List<GitRepository>> repositories) {
                //check status
                if (repositories.status != Status.LOADING) {
                    pullToRefresh.setRefreshing(false);
                    if (repositories.status == Status.ERROR){
                        showError(repositories.message);
                    }
                }
                //check data to show in rv
                if ((repositories.data != null) && (repositories.data.size() > 0)
                        && !compareLists(mMainAdapter.getRecipesListShown(), repositories.data)) {
                    mMainAdapter.setRepositoriesListToShow(repositories.data);
                    runLayoutAnimation(recyclerViewRepositoriesList);
                    emptyContainer.setVisibility(View.GONE);
                }
            }
        });
    }
    //compare the shown list with the downloaded list to check if the recycler needs to refresh
    public boolean compareLists(List<GitRepository> baseList, List<GitRepository> newList) {
        boolean areSame = true;
        if (baseList == null) {
            areSame = false;
        } else {
            for (GitRepository item : newList) {
                if (!baseList.contains(item)) {
                    areSame = false;
                }
            }
        }
        return areSame;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        MenuItem mSearch = menu.findItem(R.id.action_search);
        SearchView mSearchView = (SearchView) mSearch.getActionView();
        mSearchView.setQueryHint(search);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Timber.d(query);
                viewModel.setLastQuery(query);
                viewModel.getData(true);
                mSearchView.clearFocus();
                pullToRefresh.setRefreshing(true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(GitRepository selectedRepository) {
        viewModel.setSelectedRepository(selectedRepository);
        Timber.d(selectedRepository.toString());
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.getData(false);
    }

    // Animation RecyclerView
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.rv_layout_animation);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    // swipe to refresh listener
    @Override
    public void onRefresh() {
        // Fetching data from server
        viewModel.getData(true);
    }

    private void showError(String message){
        message = (message.equals(Constants.SUCCESSCODE)) ? errorNoResults : errorFetching;
        Snackbar.make(mainContent,message,Snackbar.LENGTH_LONG).show();
    }
}
