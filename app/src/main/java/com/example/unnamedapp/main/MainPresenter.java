package com.example.unnamedapp.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIInstagramUtils;
import com.example.unnamedapp.model.APIUtils.APITwitterUtils;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.example.unnamedapp.model.APIUtils.APIYouTubeUtils;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.PostData;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.example.unnamedapp.model.data.UserData;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import twitter4j.Status;
import twitter4j.TwitterException;

public class  MainPresenter implements BaseContract.BasePresenter {

    public String token;
    private boolean mDownloadedYouTube;
    private boolean mDownloadedInstagram;
    private boolean mDownloadedTwitter;
    private String mYouTubeToken;
    private String mInstagramToken;
    private String mTwitterToken;
    private ArrayList<PostData> mPosts;
    private APIUtils mApiUtils;
    private APIInstagramUtils mApiInstagramUtils;
    private APITwitterUtils mApiTwitterUtils;
    private APIYouTubeUtils mApiYouTubeUtils;
    private CompositeDisposable mDisposable;
    private MainActivity mActivity;

    public MainPresenter(MainActivity activity, String token) {
        this.token = token;
        mActivity = activity;
        mApiUtils = new APIUtils();
        mApiUtils.setToken(token);
        mApiInstagramUtils = new APIInstagramUtils();
        mApiTwitterUtils = new APITwitterUtils();
        mApiYouTubeUtils = new APIYouTubeUtils();
        mPosts = new ArrayList<PostData>();
        mDownloadedYouTube = false;
        mDownloadedInstagram = false;
        mDownloadedTwitter = false;
    }

    @Override
    public void onStart() {
        mDisposable = new CompositeDisposable();
    }

    public void checkIsSignedIn(){
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        mInstagramToken = sp.getString("InstagramToken", "");
        mTwitterToken = sp.getString("TwitterToken", "");
        mYouTubeToken = sp.getString("YouTubeToken", "");
        if(!mInstagramToken.equals("")){
            mActivity.showInstagramIcon();
            Log.d("INSTAGRAM", mInstagramToken);
            //fetchInstagramPosts("232192182");
        }
        if(!mTwitterToken.equals("")){
            mActivity.showTwitterIcon();
        }
        if(!mYouTubeToken.equals("")){
            Log.d("YOUTUBE_TOKEN", mYouTubeToken);
            mActivity.showYouTubeIcon();
            //fetchYouTubePostsIds();
        }
    }

    public void fetchSubscriptions(){
        Disposable subs = mApiUtils.getSubscriptions()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ArrayList<SubscriptionData>>() {
                    @Override
                    public void onSuccess(ArrayList<SubscriptionData> value) {
                        mActivity.addSubscriptions(value);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(subs);
    }

    public void deleteSubscription(int id){
        Disposable deleteSub = mApiUtils.deleteSubscription(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mActivity.showMessage(mActivity.getResources().getString(R.string.has_been_deleted));
                        fetchSubscriptions();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(deleteSub);
    }

    public void fetchYouTubePostsIds(String channelData){
        if(channelData.equals("")){
            mDownloadedYouTube = true;
            sortPosts();
            return;
        }
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String youtubeToken = sp.getString("YouTubeToken", "");
        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                mActivity.getApplicationContext(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());
        if(!youtubeToken.equals("")){
            mCredential.setSelectedAccountName(youtubeToken);
        }
        String channel = channelData.split("/")[1];
        mApiYouTubeUtils.setCredential(mCredential);
        if(channelData.split("/")[0].equals("user")) {
            mApiYouTubeUtils.setChannel(channel, APIYouTubeUtils.CHANNEL_NAME);
        }
        if(channelData.split("/")[0].equals("channel")){
            mApiYouTubeUtils.setChannel(channel, APIYouTubeUtils.CHANNEL_ID);
        }
        Log.d("YOUTUBE_DATA", mCredential.getSelectedAccountName());
        Disposable youTubePostsIds = mApiYouTubeUtils.getPopularVideos
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ArrayList<PostData>>() {
                    @Override
                    public void onSuccess(ArrayList<PostData> videos) {
                        for(int i = 0; i < videos.size(); i++){
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Log.d("YOUTUBE_DATA", videos.get(i).postId);
                        }
                        mDownloadedYouTube = true;
                        mPosts.addAll(videos);
                        sortPosts();
                    }
                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(youTubePostsIds);
    }

    public void fetchTwitterPostsIds(String link){
        if(link.equals("")){
            mDownloadedTwitter = true;
            sortPosts();
            return;
        }
        Disposable twitterPostsIds = mApiTwitterUtils.fetchTwitterPostsIds(link)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Status>>() {
                    @Override
                    public void onSuccess(List<Status> statuses) {
                        for(int i = 0; i < statuses.size(); i++) {
                            Log.d("Twitter", "title is : " + statuses.get(i).getId());
                            mPosts.add(new PostData(Constants.TWITTER_ID, String.valueOf(statuses.get(i).getId()), statuses.get(i).getCreatedAt()));
                            mDownloadedTwitter = true;
                        }
                        sortPosts();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(twitterPostsIds);
    }

    public void fetchIdByUsername(String username){
        if(username.equals("")){
            mDownloadedInstagram = true;
            sortPosts();
            return;
        }
        Disposable instagramId = mApiInstagramUtils.getIdByUsername(username)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody value) {
                        try {
                            JSONObject object = new JSONObject(value.string());
                            String id = object.getString("logging_page_id").split("_")[1];
                            fetchInstagramPosts(id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(instagramId);
    }

    public void fetchInstagramPosts(String id){
        Disposable instagramPosts = mApiInstagramUtils.getInstagramPosts(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody value) {
                        try {
                            JSONObject object = new JSONObject(value.string());
                            JSONObject objectData = object.getJSONObject("data");
                            JSONObject objectUser = objectData.getJSONObject("user");
                            JSONObject objectMedia = objectUser.getJSONObject("edge_owner_to_timeline_media");
                            JSONArray arrayEdges = objectMedia.getJSONArray("edges");
                            for(int i = 0; i < arrayEdges.length(); i++){
                                JSONObject itemNode = arrayEdges.getJSONObject(i);
                                JSONObject objectNode = itemNode.getJSONObject("node");
                                String shortcode = objectNode.getString("shortcode");
                                long takeAnTimestamp = objectNode.getLong("taken_at_timestamp");
                                Log.d("INSTAGRAM", shortcode);
                                mPosts.add(new PostData(Constants.INSTAGRAM_ID, shortcode, new Date(takeAnTimestamp*1000)));
                                mDownloadedInstagram = true;
                                //Log.d("INSTAGRAM_CODE", objectNode.getString("shortcode"));
                            }
                            sortPosts();
                            //mActivity.addInstagramList(shortCodes);
                            //Log.d("INSTAGRAM", object.toString(4));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(instagramPosts);
    }

    private void sortPosts(){
        if(mDownloadedTwitter && mDownloadedInstagram && mDownloadedYouTube){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            selectionSort();
            for(int i = 0; i < mPosts.size(); i++) {
                Log.d("SORT", mPosts.get(i).socialWebId + " " + dateFormat.format(mPosts.get(i).date));
            }
            mActivity.addPosts(mPosts);
            mPosts.clear();
        }

    }

    public void selectionSort(){
    /*По очереди будем просматривать все подмножества
      элементов массива (0 - последний, 1-последний,
      2-последний,...)*/
        for (int i = 0; i < mPosts.size(); i++) {
        /*Предполагаем, что первый элемент (в каждом
           подмножестве элементов) является минимальным */
            long min = mPosts.get(i).date.getTime();
            int min_i = i;
        /*В оставшейся части подмножества ищем элемент,
           который меньше предположенного минимума*/
            for (int j = i+1; j < mPosts.size(); j++) {
                //Если находим, запоминаем его индекс
                if (mPosts.get(j).date.getTime() < min) {
                    min = mPosts.get(j).date.getTime();
                    min_i = j;
                }
            }
        /*Если нашелся элемент, меньший, чем на текущей позиции,
          меняем их местами*/
            if (i != min_i) {
                PostData tmp = mPosts.get(i);
                mPosts.set(i, mPosts.get(min_i));
                mPosts.set(min_i, tmp);
            }
        }
        Collections.reverse(mPosts);
    }

    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
