package com.example.unnamedapp.authorization;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.registration.RegistrationActivity;
import com.example.unnamedapp.main.MainActivity;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.internal.TwitterApi;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AuthorizationActivity extends AppCompatActivity implements BaseContract.BaseView {

    AuthorizationPresenter mPresenter;

    @BindView(R.id.editTextEmail)
    EditText editTextEmail;
    @BindView(R.id.editTextPassword)
    EditText editTextPassword;
    @OnClick(R.id.buttonSignIn)
    void onClickAuthorization(){
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        mPresenter.fetchAuthorizationToken(email, password);
    }

    @OnClick(R.id.buttonSignUp)
    void onClickSignUp(){
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        initViews();
        mPresenter = new AuthorizationPresenter(this);
        openMainActivity("12");

    }

    public void onStart(){
        super.onStart();
        mPresenter.onStart();
    }

    public void openMainActivity(String token){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void showMessage(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void initViews() {
        ButterKnife.bind(this);
    }

    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }
}
