package com.example.unnamedapp.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.model.APIUtils.APIInstagramUtils;
import com.example.unnamedapp.model.APIUtils.APITwitterUtils;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.PostData;
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
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;
import twitter4j.Status;
import twitter4j.TwitterException;

public class MainPresenter implements BaseContract.BasePresenter {

    private boolean mDowloadedInstagram;
    private boolean mDowloadedTwitter;
    private String mYouTubeToken;
    private String mInstagramToken;
    private String mTwitterToken;
    private ArrayList<PostData> mPosts;
    private APIUtils mApiUtils;
    private APIInstagramUtils mApiInstagramUtils;
    private APITwitterUtils mApiTwitterUtils;
    private CompositeDisposable mDisposable;
    private MainActivity mActivity;

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
        mApiUtils = new APIUtils();
        mApiInstagramUtils = new APIInstagramUtils();
        mApiTwitterUtils = new APITwitterUtils();
        mPosts = new ArrayList<PostData>();
        mDowloadedInstagram = false;
        mDowloadedTwitter = false;
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
        }
    }

    public void fetchTwitterPostsIds(String link){
        Disposable twitterPostsIds = mApiTwitterUtils.fetchTwitterPostsIds(link)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<List<Status>>() {
                    @Override
                    public void onSuccess(List<Status> statuses) {
                        for(int i = 0; i < statuses.size(); i++) {
                            Log.d("Twitter", "title is : " + statuses.get(i).getId());
                            mPosts.add(new PostData(Constants.TWITTER_ID, String.valueOf(statuses.get(i).getId()), statuses.get(i).getCreatedAt()));
                            mDowloadedTwitter = true;
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
                                mDowloadedInstagram = true;
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
        if(mDowloadedTwitter && mDowloadedInstagram){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            selectionSort();
            for(int i = 0; i < mPosts.size(); i++) {
                Log.d("SORT", mPosts.get(i).socialWebId + " " + dateFormat.format(mPosts.get(i).date));
            }
            mActivity.addPosts(mPosts);
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
    }

    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
