package com.github.aithanasakis.githubsearch.ui.details;

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
import com.github.aithanasakis.githubsearch.model.GitOwner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsRvAdapter extends RecyclerView.Adapter<DetailsRvAdapter.ResultsHolder> {

    private List<GitOwner> subscribersList;
    private Context context;

    public DetailsRvAdapter(Context context) {
        this.context = context;
    }

    public void setSubscribersListToShow(List<GitOwner> subscribersList) {
        this.subscribersList = subscribersList;
        notifyDataSetChanged();
    }

    @Override
    public ResultsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subscriber_item, parent, false);

        return new ResultsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ResultsHolder holder, int position) {
        GitOwner subscriber = subscribersList.get(position);
        holder.textViewSubscriberName.setText(subscriber.getLogin());
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .circleCrop()
                .placeholder(R.drawable.fork_symbol)
                .error(R.drawable.fork_symbol);
        Glide.with(holder.imageViewSubscriberPhoto.getContext()).load(subscriber.getAvatarUrl()).apply(options).into(holder.imageViewSubscriberPhoto);

    }

    @Override
    public int getItemCount() {
        if (subscribersList == null) return 0;
        return subscribersList.size();
    }


    public class ResultsHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.imageView_subscriber_photo)
        ImageView imageViewSubscriberPhoto;
        @BindView(R.id.textView_subscriber_name)
        TextView textViewSubscriberName;

        public ResultsHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
