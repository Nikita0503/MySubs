package com.example.unnamedapp.new_subscription;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.choose_instagram.ChooseInstagramActivity;
import com.example.unnamedapp.choose_twitter.ChooseTwitterActivity;
import com.example.unnamedapp.choose_youtube.ChooseYouTubeActivity;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NewSubscriptionActivity extends AppCompatActivity implements BaseContract.BaseView {

    private int mId;
    private File mPhoto;
    private NewSubscriptionPresenter mPresenter;

    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.buttonInstagram)
    Button buttonInstagram;
    @BindView(R.id.buttonTwitter)
    Button buttonTwitter;
    @BindView(R.id.buttonYouTube)
    Button buttonYouTube;
    @BindView(R.id.buttonCreate)
    Button buttonCreate;
    @BindView(R.id.imageViewAvatar)
    ImageView imageViewAvatar;
    @BindView(R.id.spin_kit)
    ProgressBar progressBar;

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

    @OnClick(R.id.imageViewAvatar)
    void onClickChooseAvatar(){
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, Constants.GALLERY_REQUEST);
    }

    @OnClick(R.id.buttonCreate)
    void onClickNewSubscription(){
        if(mPresenter.editor) {
            String name = editTextName.getText().toString();
            mPresenter.editSubscription(mId, name, mPhoto);
        }else{
            String name = editTextName.getText().toString();
            mPresenter.sendNewSubscription(name, mPhoto);
        }
        showLoading();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_subscription);
        initViews();
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        boolean isEditor = intent.getBooleanExtra("editor", false);
        mPresenter = new NewSubscriptionPresenter(this, token);
        mPresenter.onStart();
        if(isEditor){
            int id = intent.getIntExtra("id", -1);
            String instagram_id = intent.getStringExtra("instagram_id");
            String twitter_id = intent.getStringExtra("twitter_id");
            String youtube_id = intent.getStringExtra("youtube_id");
            String name = intent.getStringExtra("name");
            String image = intent.getStringExtra("image");
            if(!name.equals("")){
                if(youtube_id.split("/")[0].equals("channel")){
                    mPresenter.fetchChannelNameById(youtube_id.split("/")[1]);
                }else{
                    buttonYouTube.setText(youtube_id.split("/")[1]);
                }
                buttonCreate.setText(getResources().getString(R.string.edit_subscription));
                buttonInstagram.setText(instagram_id);
                buttonTwitter.setText(twitter_id);
                editTextName.setText(name);
                Picasso.with(getApplicationContext())
                        .load(image)
                        .transform(new AvatarTransformation())
                        .into(imageViewAvatar);
                mPresenter.setInstagramUser(instagram_id);
                mPresenter.setTwitterUser(twitter_id);
                mPresenter.setYouTubeUser(youtube_id);
            }
            mPresenter.editor = true;
            mId = id;
        }else {
            Picasso.with(getApplicationContext())
                    .load(R.drawable.ic_pudge2)
                    .transform(new AvatarTransformation())
                    .into(imageViewAvatar);
        }
    }



    @Override
    public void initViews() {
        ButterKnife.bind(this);
    }

    public void setYouTubeChannelName(String name){
        buttonYouTube.setText(name);
    }

    public void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
        buttonCreate.setEnabled(false);
    }

    public void hideLoading(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if(requestCode == Constants.GALLERY_REQUEST){
            Uri selectedImage = data.getData();
            mPhoto = new File(getRealPathFromUri(getApplicationContext(), selectedImage));
            Picasso.with(getApplicationContext())
                    .load(selectedImage)
                    .transform(new AvatarTransformation())
                    .into(imageViewAvatar);
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
            if(user.split("/")[0].equals("channel")){
                mPresenter.fetchChannelNameById(user.split("/")[1]);
            }else{
                buttonYouTube.setText(user.split("/")[1]);
            }
            Toast.makeText(getApplicationContext(), user, Toast.LENGTH_LONG).show();
        }
    }

    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }
}
