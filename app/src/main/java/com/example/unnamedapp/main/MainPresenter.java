package com.example.unnamedapp.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.model.APIUtils.APIInstagramUtils;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MainPresenter implements BaseContract.BasePresenter {

    private String mInstagramToken;
    private String mTwitterToken;
    private APIUtils mApiUtils;
    private APIInstagramUtils mApiInstagramUtils;
    private CompositeDisposable mDisposable;
    private MainActivity mActivity;

    public MainPresenter(MainActivity activity) {
        mActivity = activity;
        mApiUtils = new APIUtils();
        mApiInstagramUtils = new APIInstagramUtils();
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
        if(!mInstagramToken.equals("")){
            mActivity.showInstagramIcon();
            Log.d("INSTAGRAM", mInstagramToken);
            fetchInstagramPosts("232192182");
        }
        if(!mTwitterToken.equals("")){
            mActivity.showTwitterIcon();
        }
    }

    public void fetchTwitterPostsIds(String link){
        Disposable twitterPostsIds = mApiUtils.fetchTwitterPostsIds(link)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ArrayList<Long>>() {
                    @Override
                    public void onSuccess(ArrayList<Long> value) {
                        mActivity.addTwitterIdList(value);
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
                            ArrayList<String> shortCodes = new ArrayList<String>();
                            JSONObject object = new JSONObject(value.string());
                            JSONObject objectData = object.getJSONObject("data");

                            JSONObject objectUser = objectData.getJSONObject("user");
                            JSONObject objectMedia = objectUser.getJSONObject("edge_owner_to_timeline_media");
                            JSONArray arrayEdges = objectMedia.getJSONArray("edges");

                            for(int i = 0; i < arrayEdges.length(); i++){
                                JSONObject itemNode = arrayEdges.getJSONObject(i);
                                JSONObject objectNode = itemNode.getJSONObject("node");
                                String shortcode = objectNode.getString("shortcode");
                                Log.d("INSTAGRAM", shortcode);
                                shortCodes.add(shortcode);
                                //Log.d("INSTAGRAM_CODE", objectNode.getString("shortcode"));
                            }
                            mActivity.addInstagramList(shortCodes);
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
        //Disposable instagramPosts = mApiInstagramUtils.getInstagramPosts2()
        //        .subscribeOn(Schedulers.io())
        //        .observeOn(AndroidSchedulers.mainThread())
        //        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
        //            @Override
        //            public void onSuccess(ResponseBody value) {
        //                try {
        //                    JSONObject object = new JSONObject(value.string());
        //                    Log.i("INSTAGRAM", object.toString(4));
        //                } catch (Exception e) {
        //                    e.printStackTrace();
        //                }
        //            }
        //
        //            @Override
        //            public void onError(Throwable e) {
        //                e.printStackTrace();
        //            }
        //        });
        //mDisposable.add(instagramPosts);
    }


    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
