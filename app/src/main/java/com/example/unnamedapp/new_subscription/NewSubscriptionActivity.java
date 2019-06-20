package com.example.unnamedapp.new_subscription;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.choose_instagram.ChooseInstagramActivity;
import com.example.unnamedapp.choose_twitter.ChooseTwitterActivity;
import com.example.unnamedapp.choose_youtube.ChooseYouTubeActivity;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.Constants;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewSubscriptionActivity extends AppCompatActivity implements BaseContract.BaseView {

    private NewSubscriptionPresenter mPresenter;

    @BindView(R.id.editTextName)
    EditText editTextName;

    @BindView(R.id.buttonInstagram)
    Button buttonInstagram;

    @BindView(R.id.buttonTwitter)
    Button buttonTwitter;

    @BindView(R.id.buttonYouTube)
    Button buttonYouTube;

    @BindView(R.id.imageViewAvatar)
    ImageView imageViewAvatar;

    @OnClick(R.id.buttonInstagram)
    void onClickInstagram(){
        Intent intent = new Intent(this, ChooseInstagramActivity.class);
        startActivityForResult(intent, Constants.INSTAGRAM_ID);
    }

    @OnClick(R.id.buttonTwitter)
    void onClickTwitter(){
        Intent intent = new Intent(this, ChooseTwitterActivity.class);
        startActivityForResult(intent, Constants.TWITTER_ID);
    }

    @OnClick(R.id.buttonYouTube)
    void onClickYouTube(){
        Intent intent = new Intent(this, ChooseYouTubeActivity.class);
        startActivityForResult(intent, Constants.YOUTUBE_ID);
    }

    @OnClick(R.id.buttonCreate)
    void onClickNewSubscription(){
        String name = editTextName.getText().toString();
        mPresenter.sendNewSubscription(name);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription);
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        mPresenter = new NewSubscriptionPresenter(this, token);
        initViews();
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_pudge2)
                .transform(new AvatarTransformation())
                .into(imageViewAvatar);
    }

    @Override
    public void onStart(){
        super.onStart();
        mPresenter.onStart();
    }

    @Override
    public void initViews() {
        ButterKnife.bind(this);
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if(requestCode == Constants.INSTAGRAM_ID) {
            String user = data.getStringExtra("user");
            buttonInstagram.setText(user);
            mPresenter.setInstagramUser(user);
            Toast.makeText(getApplicationContext(), user, Toast.LENGTH_LONG).show();
        }
        if(requestCode == Constants.TWITTER_ID) {
            String user = data.getStringExtra("user");
            buttonTwitter.setText(user);
            mPresenter.setTwitterUser(user);
            Toast.makeText(getApplicationContext(), user, Toast.LENGTH_LONG).show();
        }
        if(requestCode == Constants.YOUTUBE_ID) {
            String user = data.getStringExtra("user");
            mPresenter.setYouTubeUser(user);
            buttonYouTube.setText(user.split("/")[1]);
            Toast.makeText(getApplicationContext(), user, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }
}
