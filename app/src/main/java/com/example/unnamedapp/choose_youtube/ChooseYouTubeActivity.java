package com.example.unnamedapp.choose_youtube;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseYouTubeActivity extends AppCompatActivity implements BaseContract.BaseView {

    @BindView(R.id.webView)
    WebView webViewYouTube;

    @OnClick(R.id.buttonRemember)
    void onClickRemember(){
        try {
            Log.d("SPLIT", webViewYouTube.getOriginalUrl());
            String name = webViewYouTube.getOriginalUrl().split("https://m.youtube.com/")[1];
            Log.d("SPLIT", name);
            Intent intent = new Intent();
            intent.putExtra("user", name);
            setResult(RESULT_OK, intent);
            finish();
        }catch (Exception c){
            c.printStackTrace();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_youtube);
        initViews();
        webViewYouTube.loadUrl("https://www.youtube.com/");
        webViewYouTube.getSettings().setJavaScriptEnabled(true);
        webViewYouTube.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webViewYouTube.canGoBack()) {
            this.webViewYouTube.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void initViews() {
        ButterKnife.bind(this);
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }
}
