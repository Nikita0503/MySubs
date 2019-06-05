package com.example.unnamedapp.account_settings;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIInstagramUtils;
import com.example.unnamedapp.model.data.UserData;
import com.example.unnamedapp.model.data.instagram.InstagramData;
import com.example.unnamedapp.model.data.instagram.InstagramUserdata;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AccountSettingsPresenter implements BaseContract.BasePresenter {

    private UserData mUserdata;
    public TwitterAuthClient twitterAuthClient;
    public APIInstagramUtils mAPIInstagramUtils;
    private CompositeDisposable mDisposable;
    private AccountSettingsActivity mActivity;

    public AccountSettingsPresenter(AccountSettingsActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onStart() {
        mDisposable = new CompositeDisposable();
        twitterInit();
        instagramInit();
    }

    public UserData getUserdata() {
        return mUserdata;
    }

    public void setUserdata(UserData userdata) {
        mUserdata = userdata;
    }

    private void twitterInit(){
        TwitterConfig config = new TwitterConfig.Builder(mActivity)
                .twitterAuthConfig(new TwitterAuthConfig(mActivity.getResources().getString(R.string.twitter_consumer_key), mActivity.getResources().getString(R.string.twitter_consumer_secret)))
                .debug(true)
                .build();
        Twitter.initialize(config);
        TwitterSession twitterSession = TwitterCore.getInstance().getSessionManager().getActiveSession();
        if(twitterSession != null){
            Log.d("twitter", twitterSession.getUserName());
            fetchTwitterUserData();
        }else{
            Log.d("twitter", "null");
            mActivity.showTwitterUser(null);
        }
    }

    public void twitterLogin(){
        twitterAuthClient = new TwitterAuthClient();
        twitterAuthClient.authorize(mActivity, new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                fetchTwitterUserData();
                SharedPreferences activityPreferences = mActivity.getSharedPreferences("UnnamedApplication", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = activityPreferences.edit();
                editor.putString("TwitterToken", "exist");
                editor.commit();
            }

            @Override
            public void failure(TwitterException exception) {
                exception.printStackTrace();
            }
        });
    }

    private void fetchTwitterUserData(){
        TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(true, false, true).enqueue(new Callback<User>() {
            @Override
            public void success(Result<User> userResult) {
                try {
                    User user = userResult.data;
                    mActivity.showTwitterUser(new UserData(user.name, user.profileImageUrl));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    public void instagramInit(){
        mAPIInstagramUtils = new APIInstagramUtils();
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String instagramToken = sp.getString("InstagramToken", "");
        if(!instagramToken.equals("")){
            fetchInstagramUserData(instagramToken);
        }else{
            mActivity.showInstagramUser(null);
        }
    }

    public void instagramLogin(){
        AuthenticationDialog dialog = new AuthenticationDialog(mActivity, this);
        dialog.setCancelable(true);
        dialog.show();
    }

    public void fetchInstagramUserData(String accessToken){
        Log.d("INSTAGRAM", accessToken);
        Disposable instagramUserdata = mAPIInstagramUtils.getInstagramUserinfo(accessToken)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<InstagramData>() {
                    @Override
                    public void onSuccess(InstagramData value) {
                        InstagramUserdata instagramUserdata = value.data;
                        mActivity.showInstagramUser(new UserData(instagramUserdata.fullName, instagramUserdata.profilePicture));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(instagramUserdata);
    }

    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
