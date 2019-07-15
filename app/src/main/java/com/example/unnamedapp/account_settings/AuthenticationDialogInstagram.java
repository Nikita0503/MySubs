package com.example.unnamedapp.account_settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.unnamedapp.R;

public class AuthenticationDialogInstagram extends Dialog {
    private Context mContext;
    private WebView mWebView;
    private AccountSettingsPresenter mPresenter;

    private String mUrl;


    public AuthenticationDialogInstagram(@NonNull Context context, AccountSettingsPresenter presenter) {
        super(context);
        mContext = context;
        mUrl = "https://api.instagram.com/oauth/authorize/?client_id=f49bcb3f92a34eb784f6aef6c3e5790c&redirect_uri=http://localhost/&response_type=token";
        mPresenter = presenter;
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
        mWebView.loadUrl(mUrl);
        mWebView.setWebViewClient(new WebViewClient(){

            boolean authComplete;
            String accessToken;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(url.contains("#access_token=") && !authComplete){
                    Uri uri = Uri.parse(url);
                    accessToken = uri.getEncodedFragment();
                    accessToken = accessToken.substring(accessToken.lastIndexOf("=")+1);
                    mPresenter.fetchInstagramUserData(accessToken);
                    SharedPreferences activityPreferences = mContext.getSharedPreferences("UnnamedApplication", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = activityPreferences.edit();
                    editor.putString("InstagramToken", accessToken);
                    editor.commit();
                    dismiss();
                }
            }
        });
    }
}
