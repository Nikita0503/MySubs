package com.example.unnamedapp.model.APIUtils;

import com.example.unnamedapp.model.APIService;
import com.example.unnamedapp.model.data.instagram.InstagramData;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONObject;

import io.reactivex.Single;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIInstagramUtils {
    private static final String BASE_URL = "https://api.instagram.com/v1/";
    private String mNextPageToken;

    public void setNextPageToken(String nextPageToken) {
        mNextPageToken = nextPageToken;
    }

    public void resetToStart(){
        mNextPageToken = null;
    }

    public Single<InstagramData> getInstagramUserinfo(String accessToken){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getInstagramUserdata(accessToken);
    }

    public Single<ResponseBody> getIdByUsername(String username){
        Retrofit retrofit = getClient("https://www.instagram.com/");
        APIService apiService = retrofit.create(APIService.class);
        return apiService.getIdByUsername(username);
    }

    public Single<ResponseBody> getInstagramPosts(String userId){
        Retrofit retrofit = getClient("https://www.instagram.com/");
        APIService apiService = retrofit.create(APIService.class);
        if(mNextPageToken==null) {
            return apiService.getInstagramPosts(userId);
        }else{
            return apiService.getInstagramPosts(userId, mNextPageToken);
        }
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
