package com.example.unnamedapp.new_subscription;

import android.widget.Toast;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.example.unnamedapp.model.data.SubscriptionData;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.schedulers.Schedulers;

public class NewSubscriptionPresenter implements BaseContract.BasePresenter {

    private String mInstagramUser;
    private String mTwitterUser;
    private String mYouTubeUser;
    private APIUtils mApiUtils;
    private CompositeDisposable mDisposable;
    private NewSubscriptionActivity mActivity;

    public NewSubscriptionPresenter(NewSubscriptionActivity activity, String token) {
        mActivity = activity;
        mApiUtils = new APIUtils();
        mApiUtils.setToken(token);
    }

    @Override
    public void onStart() {
        mDisposable = new CompositeDisposable();
    }

    public void setInstagramUser(String instagramUser){
        mInstagramUser = instagramUser;
    }

    public void setTwitterUser(String twitterUser){
        mTwitterUser = twitterUser;
    }

    public void setYouTubeUser(String youTubeUser){
        mYouTubeUser = youTubeUser;
    }

    public void sendNewSubscription(final String name){
        SubscriptionData subscriptionData = new SubscriptionData(name, null);
        subscriptionData.instagram_id = mInstagramUser;
        subscriptionData.twitter_id = mTwitterUser;
        subscriptionData.youtube_id = mYouTubeUser;
        Disposable newSubscription = mApiUtils.sendNewSubscription2(subscriptionData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mActivity.showMessage(name + " " + mActivity.getResources().getString(R.string.has_been_added));
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }
                });
        mDisposable.add(newSubscription);
    }

    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
