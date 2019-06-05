package com.example.unnamedapp.account_settings;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.data.UserData;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AccountSettingsActivity extends AppCompatActivity implements BaseContract.BaseView {

    private AccountSettingsPresenter mPresenter;

    @BindView(R.id.imageViewAvatar)
    ImageView imageViewAvatar;
    @BindView(R.id.imageViewTwitterUserAvatar)
    ImageView imageViewTwitterUserAvatar;
    @BindView(R.id.imageViewInstagramUserAvatar)
    ImageView imageViewInstagramUserAvatar;
    @BindView(R.id.textViewUsername)
    TextView textViewUsername;
    @BindView(R.id.textViewIsAuthTwitter)
    TextView textViewIsAuthTwitter;
    @BindView(R.id.textViewIsAuthInstagram)
    TextView textViewIsAuthInstagram;
    @OnClick(R.id.imageViewTwitter)
    void onClickTwitter(){
        mPresenter.twitterLogin();
    }
    @OnClick(R.id.imageViewInstagram)
    void onClickInstagram(){
        mPresenter.instagramLogin();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);
        Intent intent = getIntent();
        UserData userData = (UserData) intent.getSerializableExtra("userdata");
        mPresenter = new AccountSettingsPresenter(this);
        mPresenter.setUserdata(userData);
        initViews();
    }

    @Override
    public void onStart(){
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void initViews() {
        ButterKnife.bind(this);
        UserData userData = mPresenter.getUserdata();
        textViewUsername.setText(userData.name);
        Picasso.with(getApplicationContext())
                .load(userData.avatar)
                .into(imageViewAvatar);
    }

    public void showTwitterUser(UserData userData){
        textViewIsAuthTwitter.setVisibility(View.VISIBLE);
        if(userData != null) {
            textViewIsAuthTwitter.setText(userData.name);
            imageViewTwitterUserAvatar.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(userData.avatar)
                    .transform(new AvatarTransformation())
                    .into(imageViewTwitterUserAvatar);
        }else{
            textViewIsAuthTwitter.setText(getResources().getString(R.string.not_authorized));
        }
    }

    public void showInstagramUser(UserData userData){
        textViewIsAuthInstagram.setVisibility(View.VISIBLE);
        if(userData != null) {
            textViewIsAuthInstagram.setText(userData.name);
            imageViewInstagramUserAvatar.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(userData.avatar)
                    .transform(new AvatarTransformation())
                    .into(imageViewInstagramUserAvatar);
        }else{
            textViewIsAuthInstagram.setText(getResources().getString(R.string.not_authorized));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 140) {
            mPresenter.twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }
}
