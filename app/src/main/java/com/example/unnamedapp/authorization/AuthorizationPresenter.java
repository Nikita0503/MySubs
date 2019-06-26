package com.example.unnamedapp.authorization;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.LoginData;
import com.example.unnamedapp.model.data.AuthData;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;

import java.util.Arrays;

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

    public void isSignedIn(){
        SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                Context.MODE_PRIVATE);
        String token = sp.getString("appToken", "");
        String email = sp.getString("email", "");
        if(!token.equals("")){
            mActivity.openMainActivity(token, email);
        }
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
                .subscribeWith(new DisposableSingleObserver<AuthData>() {
                    @Override
                    public void onSuccess(AuthData authData) {
                        //Log.d("VALUE", token);
                        //mActivity.openMainActivity(token);
                        mActivity.showMessage("success");
                        try {
                            Log.d("VALUE", authData.name);
                            Log.d("VALUE", authData.token);
                            Log.d("VALUE", authData.email);
                            SharedPreferences sp = mActivity.getSharedPreferences("UnnamedApplication",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("appToken", authData.token);
                            editor.putString("email", authData.email);
                            editor.commit();
                        } catch (Exception c){
                            c.printStackTrace();
                        }
                        mActivity.openMainActivity(authData.token, authData.email);
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
