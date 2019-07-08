package com.example.unnamedapp.choose_instagram;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseInstagramActivity extends AppCompatActivity implements BaseContract.BaseView {

    @BindView(R.id.webView)
    WebView webViewInstagram;

    @OnClick(R.id.buttonRemember)
    void onClickRemember(){
        try {
            String name = webViewInstagram.getOriginalUrl().split("https://www.instagram.com/")[1];
            if(name.equals("explore/") || name.equals("accounts/") || name.equals("accounts/activity/")
                    || name.substring(0, 2).equals("p/")){
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_a_twitter), Toast.LENGTH_SHORT).show();
            }else {
                Log.d("SPLIT", webViewInstagram.getOriginalUrl());
                name = name.substring(0, name.length() - 1);
                Log.d("SPLIT", name);
                Intent intent = new Intent();
                intent.putExtra("user", name);
                setResult(RESULT_OK, intent);
                finish();
            }
        }catch (Exception c){
            c.printStackTrace();
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.choose_a_twitter), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_instagram);
        initViews();
        webViewInstagram.loadUrl("https://www.instagram.com/");
        webViewInstagram.getSettings().setJavaScriptEnabled(true);
        webViewInstagram.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webViewInstagram.canGoBack()) {
            this.webViewInstagram.goBack();
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
