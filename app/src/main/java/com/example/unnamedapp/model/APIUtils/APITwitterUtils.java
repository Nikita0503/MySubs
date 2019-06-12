package com.example.unnamedapp.model.APIUtils;

import android.util.Log;

import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.PostData;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class APITwitterUtils {
    public Single<List<Status>> fetchTwitterPostsIds(final String link){
        return Single.create(new SingleOnSubscribe<List<Status>>() {
            @Override
            public void subscribe(SingleEmitter<List<Status>> e) throws Exception {
                ConfigurationBuilder builder = new ConfigurationBuilder();
                builder.setOAuthConsumerKey("hlTZzOf9Ww9UwfFW4nObQQmTU");
                builder.setOAuthConsumerSecret("PTTXNYMYbLkRv8On2YEOKSiqUtyRvt2fDZlzfpW2NLa7uYGZDA");
                builder.setOAuthAccessToken("1127907113504788480-g5fUUlqcx4X77PLMcLxaDr41R471yJ");
                builder.setOAuthAccessTokenSecret("RU18Uh4R5OnniImY806u2MGPdmB08mRD3LGw8n7HnIK8h");
                Configuration configuration = builder.build();
                TwitterFactory factory = new TwitterFactory(configuration);
                twitter4j.Twitter twitter = factory.getInstance();
                Paging page = new Paging();
                page.setCount(8);
                //page.setPage(2);

                List<Status> statuses = new ArrayList<Status>();
                try {
                    statuses = twitter.getUserTimeline(link, page);
                } catch (TwitterException ex) {
                    ex.printStackTrace();
                }
                e.onSuccess(statuses);
            }
        });
    }
}
