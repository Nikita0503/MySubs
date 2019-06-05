package com.example.unnamedapp.main;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.unnamedapp.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;

public class WallListAdapter extends RecyclerView.Adapter {

    private MainActivity mActivity;
    private ArrayList<Long> mList;

    public WallListAdapter(MainActivity activity){
        mActivity = activity;
        mList = new ArrayList<Long>();
    }

    public void addList(ArrayList<Long> ids){
        mList.addAll(ids);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.twitter_item, viewGroup, false);
        return new WallListAdapter.TwitterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final TwitterViewHolder holder = (TwitterViewHolder) viewHolder;
        //Twi
        TweetUtils.loadTweet(mList.get(i), new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {
                holder.layout.addView(new TweetView(mActivity, result.data));
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class TwitterViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layout;
        public TwitterViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (ConstraintLayout) itemView.findViewById(R.id.item_twitter);
        }
    }
}
