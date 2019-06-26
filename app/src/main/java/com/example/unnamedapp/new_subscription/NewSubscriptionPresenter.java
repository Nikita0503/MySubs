package com.example.unnamedapp.new_subscription;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.example.unnamedapp.model.APIUtils.APIYouTubeUtils;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.io.File;
import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class NewSubscriptionPresenter implements BaseContract.BasePresenter {

    public boolean editor;
    private String mInstagramUser;
    private String mTwitterUser;
    private String mYouTubeUser;
    private APIUtils mApiUtils;
    private CompositeDisposable mDisposable;
    private NewSubscriptionActivity mActivity;

    public NewSubscriptionPresenter(NewSubscriptionActivity activity, String token) {
        mActivity = activity;
        mApiUtils = new APIUtils();
        mApiUtils.setToken(token);
        mInstagramUser = "";
        mTwitterUser = "";
        mYouTubeUser = "";
    }

    @Override
    public void onStart() {
        mDisposable = new CompositeDisposable();
    }

    public void setInstagramUser(String instagramUser){
        mInstagramUser = instagramUser;
    }

    public void setTwitterUser(String twitterUser){
        mTwitterUser = twitterUser;
    }

    public void setYouTubeUser(String youTubeUser){
        mYouTubeUser = youTubeUser;
    }

    public void sendNewSubscription(final String name, File filePhoto){
        SubscriptionData subscriptionData = new SubscriptionData(name, null);
        subscriptionData.instagram_id = mInstagramUser;
        subscriptionData.twitter_id = mTwitterUser;
        subscriptionData.youtube_id = mYouTubeUser;
        Disposable newSubscription;
        Log.d("User", "YouTube " + mYouTubeUser);
        Log.d("User", "Twitter" + mTwitterUser);
        Log.d("User", "Instagram" + mInstagramUser);
        if(filePhoto != null) {
            Log.d("FILE", "not null");
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePhoto);
            MultipartBody.Part photo = MultipartBody.Part.createFormData("image", filePhoto.getName(), requestFile);
            newSubscription = mApiUtils.sendNewSubscription(subscriptionData, photo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            mActivity.showMessage(name + " " + mActivity.getResources().getString(R.string.has_been_added));
                            mActivity.hideLoading();
                            mActivity.finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mActivity.hideLoading();
                        }
                    });
        }else{
            Log.d("FILE", "null");
            newSubscription = mApiUtils.sendNewSubscription(subscriptionData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            mActivity.showMessage(name + " " + mActivity.getResources().getString(R.string.has_been_added));
                            mActivity.finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
        mDisposable.add(newSubscription);
    }

    public void editSubscription(int id, final String name, File filePhoto){
        SubscriptionData subscriptionData = new SubscriptionData(name, null);
        subscriptionData.instagram_id = mInstagramUser;
        subscriptionData.twitter_id = mTwitterUser;
        subscriptionData.youtube_id = mYouTubeUser;
        Disposable editSubscription;
        if(filePhoto != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), filePhoto);
            MultipartBody.Part photo = MultipartBody.Part.createFormData("image", filePhoto.getName(), requestFile);
            editSubscription = mApiUtils.editSubscription(id, subscriptionData, photo)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            mActivity.showMessage(name + " " + mActivity.getResources().getString(R.string.has_been_added));
                            mActivity.hideLoading();
                            mActivity.finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            mActivity.hideLoading();
                        }
                    });
        }else{
            editSubscription = mApiUtils.editSubscription(id, subscriptionData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableCompletableObserver() {
                        @Override
                        public void onComplete() {
                            mActivity.showMessage(name + " " + mActivity.getResources().getString(R.string.has_been_added));
                            mActivity.finish();
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    });
        }
        mDisposable.add(editSubscription);
    }

    public void fetchChannelNameById(String id){
        APIYouTubeUtils apiYouTubeUtils = new APIYouTubeUtils();
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String youtubeToken = sp.getString("YouTubeToken", "");
        GoogleAccountCredential mCredential = GoogleAccountCredential.usingOAuth2(
                mActivity.getApplicationContext(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());
        if(!youtubeToken.equals("")){
            mCredential.setSelectedAccountName(youtubeToken);
        }
        apiYouTubeUtils.setCredential(mCredential);
        apiYouTubeUtils.setChannel(id, APIYouTubeUtils.CHANNEL_ID);
        Disposable channelName =  apiYouTubeUtils.getChannelNameById
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<String>() {
                    @Override
                    public void onSuccess(String name) {
                        mActivity.setYouTubeChannelName(name);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(channelName);
    }

    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
