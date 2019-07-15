package com.example.unnamedapp.account_settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.unnamedapp.R;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class AuthenticationDialogTwitter extends Dialog {

    private Twitter twitter;
    private RequestToken requestToken = null;
    private AccessToken accessToken;
    private String oauth_url,oauth_verifier;
    private WebView mWebView;
    private AccountSettingsPresenter mPresenter;
    private Context mContext;


    public AuthenticationDialogTwitter(@NonNull Context context, AccountSettingsPresenter presenter) {
        super(context);
        mPresenter = presenter;
        mContext = context;
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(getContext().getResources().getString(R.string.twitter_consumer_key), getContext().getString(R.string.twitter_consumer_secret));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.auth_dialog);
        initializeWebView();

    }

    private void initializeWebView(){
        mWebView = (WebView) findViewById(R.id.webView);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        new TokenGet().execute();
    }


    private class TokenGet extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            try {
                requestToken = twitter.getOAuthRequestToken();
                oauth_url = requestToken.getAuthorizationURL();
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return oauth_url;
        }
        @Override
        protected void onPostExecute(String oauth_url) {
            // запускаем диалог с вебвью
            if(oauth_url != null){
                mWebView.loadUrl(oauth_url);
                mWebView.setWebViewClient(new WebViewClient() {
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon){
                        super.onPageStarted(view, url, favicon);
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        if (url.contains("oauth_verifier") && authComplete == false){
                            dismiss();
                            authComplete = true;
                            Uri uri = Uri.parse(url);
                            oauth_verifier = uri.getQueryParameter("oauth_verifier");
                            String access_token = uri.getQueryParameter("oauth_token");
                            Log.d("TWITTER_WEB", access_token);
                            SharedPreferences activityPreferences = mContext.getSharedPreferences("UnnamedApplication", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = activityPreferences.edit();
                            editor.putString("TwitterToken", access_token);
                            editor.commit();

                            new AccessTokenGet().execute();

                        }
                    }
                });

            }
        }
    }


    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
        @Override
        protected Boolean doInBackground(String... args) {
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);
                User user = twitter.showUser(accessToken.getUserId());
                SharedPreferences activityPreferences = mContext.getSharedPreferences("UnnamedApplication", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = activityPreferences.edit();
                editor.putString("TwitterName", user.getName());
                editor.putString("TwitterImage", user.getOriginalProfileImageURL());
                editor.commit();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean response) {
            mPresenter.fetchTwitterUserData();

        }
    }
}
