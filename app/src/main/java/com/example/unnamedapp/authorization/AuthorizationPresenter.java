package com.example.unnamedapp.authorization;

import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.ResponseFromApi;

import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class AuthorizationPresenter implements BaseContract.BasePresenter {

    private APIUtils mApiUtils;
    private CompositeDisposable mDisposable;
    private AuthorizationActivity mActivity;


    public AuthorizationPresenter(AuthorizationActivity activity) {
        mActivity = activity;
        mApiUtils = new APIUtils();
    }

    @Override
    public void onStart() {
        mDisposable = new CompositeDisposable();
    }

    public void fetchAuthorizationToken(String email, String password) {
        if (email.equals("")) {
            mActivity.showMessage(mActivity.getResources().getString(R.string.incorrect_email));
            return;
        }
        if (password.equals("")) {
            mActivity.showMessage(mActivity.getResources().getString(R.string.incorrect_password));
            return;
        }
        LoginData data = new LoginData(email, password);
        Log.d("VALUE", email);
        Log.d("VALUE", password);
        Disposable token = mApiUtils.authorization(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ResponseFromApi>() {
                    @Override
                    public void onSuccess(ResponseFromApi token) {
                        //Log.d("VALUE", token);
                        //mActivity.openMainActivity(token);
                        mActivity.showMessage("success");
                        try {
                            Log.d("VALUE", token.name);
                            Log.d("VALUE", token.token);
                            Log.d("VALUE", token.email);
                        } catch (Exception c){
                            c.printStackTrace();
                        }
                        mActivity.openMainActivity("token");
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mActivity.showMessage("fail");
                    }
                });
        mDisposable.add(token);
    }

    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
