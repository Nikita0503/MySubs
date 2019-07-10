package com.example.unnamedapp.account_settings;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIInstagramUtils;
import com.example.unnamedapp.model.APIUtils.APIYouTubeUtils;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.UserData;
import com.example.unnamedapp.model.data.instagram.InstagramData;
import com.example.unnamedapp.model.data.instagram.InstagramUserdata;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
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

import java.util.Arrays;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import pub.devrel.easypermissions.EasyPermissions;

public class AccountSettingsPresenter implements BaseContract.BasePresenter {
    public GoogleAccountCredential mCredential;
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
        youTubeInit();
        twitterInit();
        instagramInit();
    }

    public void youTubeInit(){
        mCredential = GoogleAccountCredential.usingOAuth2(
                mActivity.getApplicationContext(), Arrays.asList(Constants.SCOPES))
                .setBackOff(new ExponentialBackOff());
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String youtubeToken = sp.getString("YouTubeToken", "");
        if(!youtubeToken.equals("")){
            mActivity.showYouTubeUser(new UserData(youtubeToken, null));
            mCredential.setSelectedAccountName(youtubeToken);
        }else{
            mActivity.showYouTubeUser(null);
        }
        mActivity.dos(mCredential);
    }

    public void youTubeLogin(){
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String accountName = sp.getString("YouTubeToken", "");
        if (!accountName.equals("")) {
            mCredential.setSelectedAccountName(accountName);
            mActivity.showYouTubeUser(new UserData(mCredential.getSelectedAccountName(), null));
        } else {
            mActivity.startActivityForResult(mCredential.newChooseAccountIntent(), Constants.REQUEST_ACCOUNT_PICKER);
        }
    }

    public void fetchYouTubeUserData(Intent data) {
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("YouTubeToken", accountName);
        editor.commit();
        mCredential.setSelectedAccountName(accountName);
        mActivity.showYouTubeUser(new UserData(mCredential.getSelectedAccountName(), null));
        mActivity.dos(mCredential);
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
