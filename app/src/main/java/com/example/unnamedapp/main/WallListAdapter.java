package com.example.unnamedapp.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.example.unnamedapp.R;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.PostData;
import com.pierfrancescosoffritti.youtubeplayer.player.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerInitListener;
import com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayerView;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.util.ArrayList;

public class WallListAdapter extends RecyclerView.Adapter {

    private MainActivity mActivity;
    private ArrayList<PostData> mPosts;

    public WallListAdapter(MainActivity activity) {
        mActivity = activity;
        mPosts = new ArrayList<PostData>();
    }

    public void addPosts(ArrayList<PostData> ids) {
        mPosts.clear();
        mPosts.addAll(ids);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view;
        switch (mPosts.get(i).socialWebId){
            case Constants.INSTAGRAM_ID:
                view = inflater.inflate(R.layout.instagram_item, viewGroup, false);
                Log.d("ADAPTER", mPosts.get(i).socialWebId+": "+mPosts.get(i).postId);
                return new WallListAdapter.InstagramViewHolder(view);
            case Constants.TWITTER_ID:
                view = inflater.inflate(R.layout.post_item, viewGroup, false);
                Log.d("ADAPTER", mPosts.get(i).socialWebId+": "+mPosts.get(i).postId);
                return new WallListAdapter.TwitterViewHolder(view);
            case Constants.YOUTUBE_ID:
                view = inflater.inflate(R.layout.youtube_item, viewGroup, false);
                Log.d("ADAPTER", mPosts.get(i).socialWebId+": "+mPosts.get(i).postId);
                return new WallListAdapter.YouTubeViewHolder(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
        //final ViewHolder holder = (ViewHolder) viewHolder;
        if (viewHolder instanceof InstagramViewHolder) {
            ((InstagramViewHolder)viewHolder).webView.getSettings().setJavaScriptEnabled(true);
            ((InstagramViewHolder)viewHolder).webView.loadUrl("https://www.instagram.com/p/" + mPosts.get(i).postId + "/");
            ((InstagramViewHolder)viewHolder).webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (!url.startsWith("https://www.instagram.com/p/")) {
                        view.goBack();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setPackage("com.instagram.android");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(intent);

                    }
                    view.loadUrl("javascript: (function () {" +
                            "var dots = document. getElementsByClassName ('MEAGs') [0] .style.display = 'none';" +
                            "var head = document. getElementsByClassName ('gW4DF') [0] .style.display = 'none';" +
                            "var head = document. getElementsByClassName ('KGiwt') [0] .style.display = 'none';" +
                            "var head = document. getElementsByClassName ('ltpMr Slqrh') [0] .style.display = 'none';" +
                            "var head = document. getElementsByClassName ('bY2yH') [0] .style.display = 'none';" +
                            "}) ()");

                }

            });
        }
        else if (viewHolder instanceof TwitterViewHolder) {
            TweetUtils.loadTweet(Long.parseLong(mPosts.get(i).postId), new Callback<Tweet>() {
                @Override
                public void success(Result<Tweet> result) {
                    ((TwitterViewHolder) viewHolder).layout.addView(new TweetView(mActivity, result.data));
                }

                @Override
                public void failure(TwitterException exception) {
                    exception.printStackTrace();
                }
            });
        }
        else if (viewHolder instanceof YouTubeViewHolder) {
            final String videoId = mPosts.get(i).postId;
            ((YouTubeViewHolder)viewHolder).youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                @Override
                public void onInitSuccess(final com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer initializedYouTubePlayer) {
                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {

                            initializedYouTubePlayer.cueVideo(videoId, 0);
                        }
                    });
                }
            }, true);
        }

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public int getItemViewType(int position) {
        //if(mPosts.get(position).socialWebId == Constants.INSTAGRAM_ID){
        //    return Constants.INSTAGRAM_ID;
        //}else if(mPosts.get(position).socialWebId == Constants.TWITTER_ID){
        //    return Constants.TWITTER_ID;
        //}else if(mPosts.get(position).socialWebId == Constants.YOUTUBE_ID){
        //    return Constants.YOUTUBE_ID;
        //}else{
        //    return 0;
        //}
        return position;
    }



    public static class TwitterViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layout;

        public TwitterViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (ConstraintLayout) itemView.findViewById(R.id.item_post);
        }
    }

    public static class InstagramViewHolder extends RecyclerView.ViewHolder {
        WebView webView;

        public InstagramViewHolder(@NonNull View itemView) {
            super(itemView);
            webView = (WebView) itemView.findViewById(R.id.webView);
        }
    }

    public static class YouTubeViewHolder extends RecyclerView.ViewHolder {
        YouTubePlayerView youTubePlayerView;

        public YouTubeViewHolder(@NonNull View itemView) {
            super(itemView);
            youTubePlayerView = (YouTubePlayerView) itemView.findViewById(R.id.youtube_view);
        }
    }

}
