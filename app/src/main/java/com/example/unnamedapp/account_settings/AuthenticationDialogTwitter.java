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
    private String mUrl;


    public AuthenticationDialogTwitter(@NonNull Context context, AccountSettingsPresenter presenter) {
        super(context);
        mUrl = "https://www.twitter.com";
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
        //mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.setVerticalScrollBarEnabled(true);
        mWebView.setHorizontalScrollBarEnabled(true);
        //mWebView.loadUrl(mUrl);
        new TokenGet().execute();
    }






    private class TokenGet extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... args) {
            //получаем токен и юрл для запросов
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

                    //если уже до этого логинились то показываем экран не с полями для ввода,
                    // а просто кнопку авторизироваться
                    boolean authComplete = false;

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon){
                        super.onPageStarted(view, url, favicon);
                    }

                    //закрываем диалог после получения ответа "ок"
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        //если все ок то авторизируем и закрываем диалог если все плохо
                        // и запрос не верный, или пароль или нет инета или что то еще
                        //тогда закрываем диалог и тихо истерим
                        if (url.contains("oauth_verifier") && authComplete == false){
                            authComplete = true;
                            Uri uri = Uri.parse(url);
                            oauth_verifier = uri.getQueryParameter("oauth_verifier");
                            String access_token = uri.getQueryParameter("oauth_token");
                            Log.d("TWITTER_WEB", access_token);
                            SharedPreferences activityPreferences = mContext.getSharedPreferences("UnnamedApplication", Activity.MODE_PRIVATE);
                            SharedPreferences.Editor editor = activityPreferences.edit();
                            editor.putString("TwitterToken", access_token);
                            editor.commit();
                            dismiss();
                            //new AccessTokenGet().execute();
                        } else if(url.contains("denied")){
                            //auth_dialog.dismiss();
                            Log.d("TWITTER_WEB", "Sorry !, Permission Denied");
                        }
                    }
                });

            }else{
                Log.d("TWITTER_WEB", "Sorry !, Network Error or Invalid Credentials");
            }
        }
    }
















    //а в этом потоке мы создаем запрос к апи на получение данных
    private class AccessTokenGet extends AsyncTask<String, String, Boolean> {

        // создаем и показываем загрузочный прогрес диалог
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        // по полученым токену в потоке выше иы получаем возможность
        // получить данные поюзеру, его ися его аватарку и т д.
        @Override
        protected Boolean doInBackground(String... args) {
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, oauth_verifier);

                //получаем данные по юзеру
                User user = twitter.showUser(accessToken.getUserId());
                // говнокод так не делайте потому что я это делал для примера и решил что мне можно)
                // но вообще лучше сохранить в преференсы или передать на другой экран или еще что то
                // для примера прокатит. А сделал я так потому что работа с UI в этом потоке запрещена
                //вся работа с ui должна быть в главном потоке
                //nameText = user.getName();

                //runOnUiThread(new Runnable(){
                //    @Override
                //    public void run(){
                //        //name.setText(nameText);
                //    }
                //});
            } catch (TwitterException e) {
                e.printStackTrace();
            }
            return true;
        }

        //прячем загрузочный диалог
        //здесь например можно сделать переход на следующий экран после окончания загрузки
        // данных из твиттера, или еще что то что вам хочется.
        @Override
        protected void onPostExecute(Boolean response) {
            if(response){
               // progress.hide();
            }
        }
    }
}
