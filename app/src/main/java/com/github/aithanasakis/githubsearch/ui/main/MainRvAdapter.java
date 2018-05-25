package com.github.aithanasakis.githubsearch.ui.main;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.aithanasakis.githubsearch.R;
import com.github.aithanasakis.githubsearch.model.GitRepository;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainRvAdapter extends RecyclerView.Adapter<MainRvAdapter.ResultsHolder> {
    private final MainRvAdapterOnClickHandler mClickHandler;

    private List<GitRepository> repositoriesItemsList;
    private Context context;

    public MainRvAdapter(MainRvAdapterOnClickHandler handler, Context context) {
        mClickHandler = handler;
        this.context = context;
    }

    public interface MainRvAdapterOnClickHandler {
        void onClick(GitRepository selectedRepository);
    }

    public void setRepositoriesListToShow(List<GitRepository> repositoriesItemsList) {
        this.repositoriesItemsList = repositoriesItemsList;
        notifyDataSetChanged();
    }

    public List<GitRepository> getRecipesListShown() {
        return repositoriesItemsList;
    }

    @Override
    public ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.repository_item, parent, false);

        return new ResultsHolder(itemView);
    }


    @Override
    public void onBindViewHolder(ResultsHolder holder, int position) {
        GitRepository repositoryItem = repositoriesItemsList.get(position);
        holder.textViewTitle.setText(repositoryItem.getName());
        holder.textViewDescription.setText(repositoryItem.getDescription());
        holder.textViewForks.setText(String.valueOf(repositoryItem.getForksCount()));
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop()
                .placeholder(R.drawable.fork_symbol)
                .error(R.drawable.fork_symbol);
        Glide.with(holder.imageViewOwner.getContext()).load(repositoryItem.getOwner().getAvatarUrl()).apply(options).into(holder.imageViewOwner);

    }

    @Override
    public int getItemCount() {
        if (repositoriesItemsList == null) return 0;
        return repositoriesItemsList.size();
    }

    public class ResultsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageView_owner)
        ImageView imageViewOwner;
        @BindView(R.id.textView_title)
        TextView textViewTitle;
        @BindView(R.id.textView_description)
        TextView textViewDescription;
        @BindView(R.id.textView_forks)
        TextView textViewForks;

        public ResultsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int positionClicked = getAdapterPosition();
            GitRepository selectedItem = repositoriesItemsList.get(positionClicked);
            mClickHandler.onClick(selectedItem);
        }
    }
}
