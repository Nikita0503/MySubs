package com.example.unnamedapp.model;

import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.RegistrationData;
import com.example.unnamedapp.model.data.AuthData;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.example.unnamedapp.model.data.instagram.InstagramData;


import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface APIService {

    @GET("users/self/?")
    Single<InstagramData> getInstagramUserdata(@Query("access_token") String accessToken);

    @POST("user/register/")
    Completable registerNewUser(@Body RegistrationData data);

    @POST("user/login/")
    Single<AuthData> login(@Body LoginData data);

    @GET("subs/")
    Single<ArrayList<SubscriptionData>> getSubscriptions(@Header("Authorization") String token);

    @POST("subs/")
    Completable sendNewSubscription(@Header("Authorization") String token, @Body SubscriptionData subscriptionData);

    @Multipart
    @POST("subs/")
    Completable sendNewSubscription2(@Header("Authorization") String token, @Part("name") RequestBody name,
                                    @Part("instagram_id") RequestBody instagram_id,
                                    @Part("twitter_id") RequestBody twitter_id,
                                    @Part("youtube_id") RequestBody youtube_id);

    @GET("graphql/query/?query_id=17888483320059182&first=8")
    Single<ResponseBody> getInstagramPosts(@Query("id") String id);
}
