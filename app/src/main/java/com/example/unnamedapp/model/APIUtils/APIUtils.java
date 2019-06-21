package com.example.unnamedapp.model.APIUtils;

import android.util.Log;

import com.example.unnamedapp.model.APIService;
import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.RegistrationData;
import com.example.unnamedapp.model.data.AuthData;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIUtils {
    private static final String BASE_URL = "http://192.168.1.169:8000/api/";
    private String mToken;

    public void setToken(String token){
        mToken = token;
    }

    public Completable registerNewUser(RegistrationData data){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        return apiService.registerNewUser(data);
    }

    public Single<AuthData> authorization(LoginData data){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        return apiService.login(data);
    }

    public Single<ArrayList<SubscriptionData>> getSubscriptions(){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getSubscriptions("token " + mToken);
    }

    public Completable sendNewSubscription(SubscriptionData subscriptionData){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        return apiService.sendNewSubscription("token " + mToken, subscriptionData);
    }

    public Completable sendNewSubscription2(SubscriptionData subscriptionData, MultipartBody.Part photo){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), subscriptionData.name);
        RequestBody instagramId = RequestBody.create(MediaType.parse("text/plain"), subscriptionData.instagram_id);
        RequestBody twitterId = RequestBody.create(MediaType.parse("text/plain"), subscriptionData.twitter_id);
        RequestBody youtubeId = RequestBody.create(MediaType.parse("text/plain"), subscriptionData.youtube_id);
        Log.d("token", mToken);
        return apiService.sendNewSubscription2("token " + mToken, name, instagramId, twitterId, youtubeId, photo);
    }

    public static Retrofit getClient(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }
}
