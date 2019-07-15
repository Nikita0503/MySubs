package com.example.unnamedapp.account_settings;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIInstagramUtils;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.UserData;
import com.example.unnamedapp.model.data.instagram.InstagramData;
import com.example.unnamedapp.model.data.instagram.InstagramUserdata;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
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
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

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
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String token = sp.getString("TwitterToken", "");
        if(!token.equals("")){
            fetchTwitterUserData();
        }else{
            mActivity.showTwitterUser(null);
        }
    }

    public void twitterLogin(){
        AuthenticationDialogTwitter dialog = new AuthenticationDialogTwitter(mActivity, this);
        dialog.setCancelable(true);
        dialog.show();
    }


    public void fetchTwitterUserData(){
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String name = sp.getString("TwitterName", "");
        String image = sp.getString("TwitterImage", "");
        mActivity.showTwitterUser(new UserData(name, image));
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
        AuthenticationDialogInstagram dialog = new AuthenticationDialogInstagram(mActivity, this);
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
