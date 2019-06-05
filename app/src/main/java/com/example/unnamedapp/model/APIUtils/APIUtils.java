package com.example.unnamedapp.model.APIUtils;

import android.util.Log;

import com.example.unnamedapp.model.APIService;
import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.RegistrationData;
import com.example.unnamedapp.model.data.ResponseFromApi;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class APIUtils {
    private static final String BASE_URL = "http://192.168.1.169:8000/api/";

    public Completable registerNewUser(RegistrationData data){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        return apiService.registerNewUser(data);
    }

    public Single<ResponseFromApi> authorization(LoginData data){
        Retrofit retrofit = getClient(BASE_URL);
        APIService apiService = retrofit.create(APIService.class);
        return apiService.login(data);
    }

    public Single<ArrayList<Long>> fetchTwitterPostsIds(final String link){
        return Single.create(new SingleOnSubscribe<ArrayList<Long>>() {
            @Override
            public void subscribe(SingleEmitter<ArrayList<Long>> e) throws Exception {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey("hlTZzOf9Ww9UwfFW4nObQQmTU");
                builder.setOAuthConsumerSecret("PTTXNYMYbLkRv8On2YEOKSiqUtyRvt2fDZlzfpW2NLa7uYGZDA");
                builder.setOAuthAccessToken("1127907113504788480-g5fUUlqcx4X77PLMcLxaDr41R471yJ");
                builder.setOAuthAccessTokenSecret("RU18Uh4R5OnniImY806u2MGPdmB08mRD3LGw8n7HnIK8h");
                Configuration configuration = builder.build();
                TwitterFactory factory = new TwitterFactory(configuration);
                twitter4j.Twitter twitter = factory.getInstance();
                Paging page = new Paging();
                //page.setPage(2);

                ArrayList<Long> longs = new ArrayList<Long>();
                List<Status> statuses = new ArrayList<Status>();
                try {
                    statuses = twitter.getUserTimeline(link, page);
                    for(int i = 0; i < statuses.size(); i++) {
                        Log.d("Twitter", "title is : " + statuses.get(i).getId());
                        longs.add(statuses.get(i).getId());
                    }
                } catch (TwitterException ex) {
                    ex.printStackTrace();
                }
                e.onSuccess(longs);
            }
        });
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
