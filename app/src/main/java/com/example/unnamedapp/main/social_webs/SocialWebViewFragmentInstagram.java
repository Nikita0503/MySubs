package com.example.unnamedapp.main.social_webs;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.example.unnamedapp.R;
import com.example.unnamedapp.SpacesItemDecoration;
import com.example.unnamedapp.main.MainActivity;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.example.unnamedapp.new_subscription.NewSubscriptionActivity;

import java.util.ArrayList;

public class SocialWebViewFragmentInstagram extends Fragment{

    private String mToken;
    private ArrayList<SubscriptionData> mList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_choose_instagram, container, false);
        final WebView webViewInstagram = (WebView) v.findViewById(R.id.webView);
        webViewInstagram.loadUrl("https://www.instagram.com");
        webViewInstagram.getSettings().setJavaScriptEnabled(true);
        webViewInstagram.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webViewInstagram.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK
                        && event.getAction() == MotionEvent.ACTION_UP
                        && webViewInstagram.canGoBack())
                {
                    webViewInstagram.goBack();
                    return true;
                }
                return false;
            }

        });
        Button rememberButton = (Button) v.findViewById(R.id.buttonRemember);
        rememberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = webViewInstagram.getOriginalUrl().split("https://www.instagram.com/")[1];
                Log.d("SPLIT", webViewInstagram.getOriginalUrl());
                name = name.substring(0, name.length() - 1);
                Dialog dialog = onCreateDialog(name);
                dialog.show();
            }
        });
        return v;
    }

    public void setToken(String token){
        mToken = token;
    }

    public void setSubscriptionList(ArrayList<SubscriptionData> list){
        mList = list;
    }

    private Dialog onCreateDialog(final String name) {
        final String[] choiсe = new  String[]{getResources().getString(R.string.create_new_subscription),
                getResources().getString(R.string.edit)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(choiсe,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        switch (item){
                            case 0:
                                try {
                                    Intent intent = new Intent(getContext(), NewSubscriptionActivity.class);
                                    intent.putExtra("token", mToken);
                                    intent.putExtra("fromWall", true);
                                    intent.putExtra("instagram_id", name);
                                    startActivity(intent);
                                }catch (Exception c){
                                    c.printStackTrace();
                                }
                                break;
                            case 1:
                                Dialog chooseDialog = onCreateDialogChooseSub(name);
                                chooseDialog.show();
                                break;
                        }
                        dialog.cancel();
                    }
                });
        //builder.setCancelable(false);
        return builder.create();
    }

    private Dialog onCreateDialogChooseSub(String name){
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.choose_subscription_layout);
        dialog.setTitle(getResources().getString(R.string.choose_subsription));
        RecyclerView recyclerViewChoice = (RecyclerView) dialog.findViewById(R.id.recyclerView);
        ChooseSubscriptionAdapter chooseSubscriptionAdapter = new ChooseSubscriptionAdapter(getContext(), mToken, mList, Constants.INSTAGRAM_ID, name);
        recyclerViewChoice.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewChoice.setAdapter(chooseSubscriptionAdapter);
        recyclerViewChoice.addItemDecoration(new SpacesItemDecoration(10));
        return dialog;
    }
}
