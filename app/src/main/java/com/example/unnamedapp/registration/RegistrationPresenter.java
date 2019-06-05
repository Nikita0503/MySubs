package com.example.unnamedapp.registration;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.APIUtils.APIUtils;
import com.example.unnamedapp.model.data.RegistrationData;
import com.example.unnamedapp.model.data.instagram.InstagramData;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class RegistrationPresenter implements BaseContract.BasePresenter {

    private APIUtils mApiUtils;
    private CompositeDisposable mDisposable;
    private RegistrationActivity mActivity;


    public RegistrationPresenter(RegistrationActivity activity) {
        mActivity = activity;
        mApiUtils = new APIUtils();
    }

    @Override
    public void onStart() {
        mDisposable = new CompositeDisposable();
    }

    public void registerNewUser(String email, String name, String password, String repeatPassword){
        if(email.equals("")){
            mActivity.showMessage(mActivity.getResources().getString(R.string.incorrect_email));
            return;
        }
        if(name.equals("")){
            mActivity.showMessage(mActivity.getResources().getString(R.string.incorrect_name));
            return;
        }
        if(password.equals("")){
            mActivity.showMessage(mActivity.getResources().getString(R.string.incorrect_password));
            return;
        }
        if(repeatPassword.equals("")){
            mActivity.showMessage(mActivity.getResources().getString(R.string.incorrect_repeated_password));
            return;
        }
        if(!password.equals(repeatPassword)){
            mActivity.showMessage(mActivity.getResources().getString(R.string.not_the_same_passwords));
            return;
        }
        RegistrationData data = new RegistrationData(email, name, password);
        Disposable register = mApiUtils.registerNewUser(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        mActivity.showMessage("success");
                        mActivity.finish();
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        mActivity.showMessage("fail");
                    }
                });
        mDisposable.add(register);
    }

    @Override
    public void onStop() {
        mDisposable.clear();
    }
}
