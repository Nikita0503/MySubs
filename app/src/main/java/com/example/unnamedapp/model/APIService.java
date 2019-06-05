package com.example.unnamedapp.model;

import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.RegistrationData;
import com.example.unnamedapp.model.data.ResponseFromApi;
import com.example.unnamedapp.model.data.instagram.InstagramData;


import org.json.JSONObject;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIService {

    @GET("users/self/?")
    Single<InstagramData> getInstagramUserdata(@Query("access_token") String accessToken);

    @POST("user/register/")
    Completable registerNewUser(@Body RegistrationData data);

    @POST("user/login/")
    Single<ResponseFromApi> login(@Body LoginData data);

    @GET("graphql/query/?query_id=17888483320059182&first=10")
    Single<ResponseBody> getInstagramPosts(@Query("id") String id);

    @GET("rockstar05031998/?__a=1")
    Single<ResponseBody> getInstagramPosts2();
}
