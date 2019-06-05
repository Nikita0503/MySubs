package com.example.unnamedapp.choose_instagram;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
        Toast.makeText(getApplicationContext(), webViewInstagram.getOriginalUrl(), Toast.LENGTH_SHORT).show();
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
}
