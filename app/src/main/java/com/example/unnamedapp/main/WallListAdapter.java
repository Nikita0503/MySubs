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
import android.widget.Button;
import android.widget.TextView;

import com.example.unnamedapp.R;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.PostData;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
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
import java.util.Date;

public class WallListAdapter extends RecyclerView.Adapter {
    YouTubePlayer player;
    private MainActivity mActivity;
    private Context mContext;
    //private ArrayList<Long> mList;
    //private ArrayList<String> mInstagramList;
    private ArrayList<PostData> mPosts;

    public WallListAdapter(MainActivity activity){
        mActivity = activity;
        mContext = activity.getApplicationContext();
        //mList = new ArrayList<Long>();
        //mInstagramList = new ArrayList<String>();
        mPosts = new ArrayList<PostData>();
    }

    public void addPosts(ArrayList<PostData> ids){
        mPosts.addAll(ids);
        notifyDataSetChanged();
        Log.d("SORT", "count = " + mPosts.size());
        //for(int i = 0; i < mPosts.size(); i++){
        //    Log.d("SORT", mPosts.get(i).postId);
        //}
    }

   //public void addInstagramList(ArrayList<String> list){
   //    mInstagramList.addAll(list);
   //    notifyDataSetChanged();
   //}

   //public void addPosts(ArrayList<PostData> postData){
   //    mPosts.addAll(postData);
   //    notifyDataSetChanged();
   //}

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.post_item, viewGroup, false);
        return new WallListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ViewHolder holder = (ViewHolder) viewHolder;
        if(mPosts.get(i).socialWebId == Constants.INSTAGRAM_ID) {
            final WebView webView = new WebView(mActivity);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl("https://www.instagram.com/p/" + mPosts.get(i).postId + "/");
            webView.setWebViewClient(new WebViewClient() {
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (!url.startsWith("https://www.instagram.com/p/")) {
                        view.goBack();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        intent.setPackage("com.instagram.android");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mActivity.startActivity(intent);

                    }
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl("javascript: (function () {" +
                            "var dots = document. getElementsByClassName ('MEAGs') [0] .style.display = 'none';" +
                            "var head = document. getElementsByClassName ('gW4DF') [0] .style.display = 'none';" +
                            "var head = document. getElementsByClassName ('KGiwt') [0] .style.display = 'none';" +
                            "}) ()");
                    holder.itemView.setVisibility(View.VISIBLE);
                    Log.d("INSTAGRAM", view.getUrl());
                    Log.d("INSTAGRAM", "height1 = " + view.getContentHeight());
                    // Set the height of the webview to view.getContentHeight() here?
                    //webView.setLayoutParams(new ConstraintLayout.LayoutParams(mActivity.getResources().getDisplayMetrics().widthPixels, oW_lN _0mzm- sqdOP yWX7d    _8A5w5
                    //        (int) (view.getContentHeight() * mActivity.getResources().getDisplayMetrics().density)));
                    //webView.getSettings().setJavaScriptEnabled(false);
                }

            });
            holder.layout.addView(webView);
        }
        if(mPosts.get(i).socialWebId == Constants.TWITTER_ID) {
            TweetUtils.loadTweet(Long.parseLong(mPosts.get(i).postId), new Callback<Tweet>() {
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
        if(mPosts.get(i).socialWebId == Constants.YOUTUBE_ID){
            //final YouTubePlayerView videoView = new YouTubePlayerView(mActivity);
            //
            //videoView.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        videoView.initialize("1012252393464-62h0hpktcc77hvn0rk90pqsr046joesk.apps.googleusercontent.com", new YouTubePlayer.OnInitializedListener() {
            //                    @Override
            //                    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
            //                        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
            //                        youTubePlayer.loadVideo(mPosts.get(i).postId);
            //                        youTubePlayer.play();
            //                        //videoplayer=youTubePlayer;
            //                    }
            //
            //                    @Override
            //                    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
            //                        Log.d("ERROR", "123");
            //                    }
            //
            //                }
            //        );
            //    }
            //});
            //holder.layout.addView(videoView);
            YouTubePlayerView youTubePlayerView =  new YouTubePlayerView(mActivity);
            youTubePlayerView.initialize(new YouTubePlayerInitListener() {
                @Override
                public void onInitSuccess(final com.pierfrancescosoffritti.youtubeplayer.player.YouTubePlayer initializedYouTubePlayer) {
                    initializedYouTubePlayer.addListener(new AbstractYouTubePlayerListener() {
                        @Override
                        public void onReady() {
                            String videoId = mPosts.get(i).postId;
                            initializedYouTubePlayer.cueVideo(videoId, 0);
                        }
                    });
                }
            }, true);
            holder.layout.addView(youTubePlayerView);
        }

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int pos){
        return pos;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (ConstraintLayout) itemView.findViewById(R.id.item_post);
        }
    }


}
