package com.example.unnamedapp.new_subscription;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.choose_instagram.ChooseInstagramActivity;
import com.example.unnamedapp.choose_twitter.ChooseTwitterActivity;
import com.example.unnamedapp.model.AvatarTransformation;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewSubscriptionActivity extends AppCompatActivity implements BaseContract.BaseView {

    @BindView(R.id.imageViewAvatar)
    ImageView imageViewAvatar;

    @OnClick(R.id.buttonInstagram)
    void onClickInstagram(){
        Intent intent = new Intent(this, ChooseInstagramActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.buttonTiwtter)
    void onClickTwitter(){
        Intent intent = new Intent(this, ChooseTwitterActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription);
        initViews();
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_pudge2)
                .transform(new AvatarTransformation())
                .into(imageViewAvatar);
    }

    @Override
    public void initViews() {
        ButterKnife.bind(this);
    }
}
