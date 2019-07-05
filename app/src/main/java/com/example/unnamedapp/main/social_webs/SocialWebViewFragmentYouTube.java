package com.example.unnamedapp.main.social_webs;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.example.unnamedapp.R;
import com.example.unnamedapp.new_subscription.NewSubscriptionActivity;

public class SocialWebViewFragmentYouTube extends Fragment{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_choose_youtube, container, false);
        final WebView webViewYouTube = (WebView) v.findViewById(R.id.webView);
        webViewYouTube.loadUrl("https://www.youtube.com");
        webViewYouTube.getSettings().setJavaScriptEnabled(true);
        webViewYouTube.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewYouTube.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webViewYouTube.canGoBack())
                {
                    webViewYouTube.goBack();
                    return true;
                }
                return false;
            }

        });
        Button rememberButton = (Button) v.findViewById(R.id.buttonRemember);
        rememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Log.d("SPLIT", webViewYouTube.getOriginalUrl());
                    String name = webViewYouTube.getOriginalUrl().split("https://m.youtube.com/")[1];
                    Log.d("SPLIT", name);
                    if(name.split("/")[0].equals("feed")){
                        Toast.makeText(getContext(), getResources().getString(R.string.choose_a_channel), Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(getContext(), NewSubscriptionActivity.class);
                        intent.putExtra("fromWall", true);
                        intent.putExtra("youtube_id", name);
                        startActivity(intent);
                    }

                }catch (Exception c){
                    c.printStackTrace();
                }
            }
        });
        return v;
    }


}
