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
    private Context mContext;
    private ArrayList<Long> mList;
    private ArrayList<String> mInstagramList;

    public WallListAdapter(MainActivity activity){
        mActivity = activity;
        mContext = activity.getApplicationContext();
        mList = new ArrayList<Long>();
        mInstagramList = new ArrayList<String>();
    }

    public void addList(ArrayList<Long> ids){
        mList.addAll(ids);
        notifyDataSetChanged();
    }

    public void addInstagramList(ArrayList<String> list){
        mInstagramList.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //View view = inflater.inflate(R.layout.twitter_item, viewGroup, false);
        //return new WallListAdapter.TwitterViewHolder(view);
        View view = inflater.inflate(R.layout.instagram_item, viewGroup, false);
        return new WallListAdapter.InstagramViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int i) {
        final InstagramViewHolder holder = (InstagramViewHolder) viewHolder;
        final WebView webView = new WebView(mActivity);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://www.instagram.com/p/"+mInstagramList.get(i)+"/");
        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!url.startsWith("https://www.instagram.com/p/")){
                    view.goBack();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.setPackage("com.instagram.android");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(intent);

                }
                webView.setVisibility(View.VISIBLE);
                webView.loadUrl ("javascript: (function () {" +
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
        //final TwitterViewHolder holder = (TwitterViewHolder) viewHolder;
        //TweetUtils.loadTweet(mList.get(i), new Callback<Tweet>() {
        //    @Override
        //    public void success(Result<Tweet> result) {
        //        holder.layout.addView(new TweetView(mActivity, result.data));
        //    }
        //
        //    @Override
        //    public void failure(TwitterException exception) {
        //        exception.printStackTrace();
        //    }
        //});
    }

    @Override
    public int getItemCount() {
        //return mList.size();
        return mInstagramList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int pos){
        return pos;
    }



    public static class TwitterViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layout;
        public TwitterViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (ConstraintLayout) itemView.findViewById(R.id.item_twitter);
        }
    }

    public static class InstagramViewHolder extends RecyclerView.ViewHolder{
        ConstraintLayout layout;
        public InstagramViewHolder(@NonNull View itemView) {
            super(itemView);
            layout = (ConstraintLayout) itemView.findViewById(R.id.layout);
            itemView.setVisibility(View.GONE);
        }
    }
}
