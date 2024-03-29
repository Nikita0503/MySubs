package com.example.unnamedapp.model;

import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.RegistrationData;
import com.example.unnamedapp.model.data.AuthData;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.example.unnamedapp.model.data.instagram.InstagramData;


import java.util.ArrayList;

import io.reactivex.Completable;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
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

    @DELETE("subs/{id}/")
    Completable deleteSubscription(@Header("Authorization") String token, @Path("id") int id);

    @Multipart
    @POST("subs/")
    Completable sendNewSubscription(@Header("Authorization") String token,  @Part("name") RequestBody name,
                                    @Part("instagram_id") RequestBody instagram_id,
                                    @Part("twitter_id") RequestBody twitter_id,
                                    @Part("youtube_id") RequestBody youtube_id);

    @Multipart
    @POST("subs/")
    Completable sendNewSubscriptionWithPhoto(@Header("Authorization") String token, @Part("name") RequestBody name,
                                     @Part("instagram_id") RequestBody instagram_id,
                                     @Part("twitter_id") RequestBody twitter_id,
                                     @Part("youtube_id") RequestBody youtube_id,
                                     @Part MultipartBody.Part data);

    @Multipart
    @PUT("subs/{id}/")
    Completable editSubscription(@Header("Authorization") String token, @Path("id") int id,
                                    @Part("name") RequestBody name,
                                    @Part("instagram_id") RequestBody instagram_id,
                                    @Part("twitter_id") RequestBody twitter_id,
                                    @Part("youtube_id") RequestBody youtube_id);

    @Multipart
    @PUT("subs/{id}/")
    Completable editSubscriptionWithPhoto(@Header("Authorization") String token, @Path("id") int id,
                                             @Part("name") RequestBody name,
                                             @Part("instagram_id") RequestBody instagram_id,
                                             @Part("twitter_id") RequestBody twitter_id,
                                             @Part("youtube_id") RequestBody youtube_id,
                                             @Part MultipartBody.Part data);

    @GET("{username}/?__a=1")
    Single<ResponseBody> getIdByUsername(@Path("username") String username);

    @GET("graphql/query/?query_id=17888483320059182&first=8")
    Single<ResponseBody> getInstagramPosts(@Query("id") String id); //если нет токена если есть еще нужно

    @GET("graphql/query/?query_id=17888483320059182&first=8")
    Single<ResponseBody> getInstagramPosts(@Query("id") String id, @Query("after") String after);
}
