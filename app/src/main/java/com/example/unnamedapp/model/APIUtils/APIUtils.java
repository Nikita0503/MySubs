package com.example.unnamedapp.model.APIUtils;

import com.example.unnamedapp.model.APIService;
import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.RegistrationData;
import com.example.unnamedapp.model.data.AuthData;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
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

    public static Retrofit getClient(String baseUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;
    }
}
