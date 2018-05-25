package com.github.aithanasakis.githubsearch.ui.details;

import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.aithanasakis.githubsearch.R;
import com.github.aithanasakis.githubsearch.model.GitOwner;
import com.github.aithanasakis.githubsearch.model.GitRepository;
import com.github.aithanasakis.githubsearch.ui.main.MainActivityViewModel;

import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DetailsFragment extends DialogFragment {
    Unbinder unbinder;
    GitRepository selectedRepository;
    @BindView(R.id.imageView_header)
    ImageView imageViewHeader;
    @BindView(R.id.textView_details_title)
    TextView textViewDetailsTitle;
    @BindView(R.id.textView_details_no)
    TextView textViewDetailsNo;
    @BindView(R.id.subscribers_recycler)
    RecyclerView subscribersRecycler;
    @BindView(R.id.textView_no_subscribers)
    TextView textViewNoSubscribers;
    @BindInt(R.integer.grid_columns)
    int columnsCount;
    private MainActivityViewModel viewModel;
    private GridLayoutManager mGridLayoutManager;
    private DetailsRvAdapter detailsRvAdapter;

    public DetailsFragment() {
        // Empty constructor is required for DialogFragment
    }

    public static DetailsFragment newInstance() {
        DetailsFragment frag = new DetailsFragment();
        Bundle args = new Bundle();
        // args.putParcelable("wine", mWine);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        viewModel = ViewModelProviders.of(getActivity()).get(MainActivityViewModel.class);
        selectedRepository = viewModel.getSelectedRepository();
        textViewDetailsTitle.setText(selectedRepository.getName());
        textViewDetailsNo.setText(getActivity().getResources().getString(R.string.subscibers_number, 0));
        mGridLayoutManager = new GridLayoutManager(getActivity(), columnsCount);
        subscribersRecycler.setLayoutManager(mGridLayoutManager);
        detailsRvAdapter = new DetailsRvAdapter(getActivity());
        subscribersRecycler.setAdapter(detailsRvAdapter);
        //setup viewmodel
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);
        viewModel.getSubscribersListObservable().observe(this, new Observer<List<GitOwner>>() {
            @Override
            public void onChanged(@Nullable List<GitOwner> gitSubscribers) {
                if (gitSubscribers != null && gitSubscribers.size() > 0) {
                    textViewDetailsNo.setText(getActivity().getResources().getString(R.string.subscibers_number, gitSubscribers.size()));
                    detailsRvAdapter.setSubscribersListToShow(gitSubscribers);
                    textViewNoSubscribers.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
