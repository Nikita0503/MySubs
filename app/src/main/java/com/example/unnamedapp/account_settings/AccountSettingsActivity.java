package com.example.unnamedapp.account_settings;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.authorization.AuthorizationActivity;
import com.example.unnamedapp.main.MainActivity;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.UserData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class AccountSettingsActivity extends AppCompatActivity implements BaseContract.BaseView, EasyPermissions.PermissionCallbacks {

    GoogleAccountCredential mCredential;


    private AccountSettingsPresenter mPresenter;

    @BindView(R.id.imageViewAvatar)
    ImageView imageViewAvatar;
    @BindView(R.id.imageViewTwitterUserAvatar)
    ImageView imageViewTwitterUserAvatar;
    @BindView(R.id.imageViewInstagramUserAvatar)
    ImageView imageViewInstagramUserAvatar;
    @BindView(R.id.imageViewYouTubeUserAvatar)
    ImageView imageViewYouTubeUserAvatar;
    @BindView(R.id.textViewUsername)
    TextView textViewUsername;
    @BindView(R.id.textViewIsAuthTwitter)
    TextView textViewIsAuthTwitter;
    @BindView(R.id.textViewIsAuthInstagram)
    TextView textViewIsAuthInstagram;
    @BindView(R.id.textViewIsAuthYouTube)
    TextView textViewIsAuthYouTube;
    @OnClick(R.id.imageViewTwitter)
    void onClickTwitter(){
        mPresenter.twitterLogin();
    }
    @OnClick(R.id.imageViewInstagram)
    void onClickInstagram(){
        mPresenter.instagramLogin();
    }
    @OnClick(R.id.imageViewYouTube)
    void onClickYouTube(){
        mPresenter.youTubeLogin();
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

    @Override
    public void showMessage(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showYouTubeUser(UserData userData){
        textViewIsAuthYouTube.setVisibility(View.VISIBLE);
        if(userData != null) {
            textViewIsAuthYouTube.setText(userData.name);
            imageViewYouTubeUserAvatar.setVisibility(View.VISIBLE);
            Picasso.with(getApplicationContext())
                    .load(userData.avatar)
                    .transform(new AvatarTransformation())
                    .into(imageViewYouTubeUserAvatar);
        }else{
            textViewIsAuthYouTube.setText(getResources().getString(R.string.not_authorized));
        }
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
        if (requestCode == 1000) {
            mPresenter.fetchYouTubeUserData(data);
        }
    }

    public void dos(GoogleAccountCredential credential){
        new MakeRequestTask(credential).execute();
    }
    public class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.youtube.YouTube mService = null;
        private Exception mLastError = null;

        MakeRequestTask(GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("MySubs")
                    .build();
        }

        /**
         * Background task to call YouTube Data API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                return getDataFromApi();
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }



        /**
         * Fetch information about the "GoogleDevelopers" YouTube channel.
         * @return List of Strings containing information about the channel.
         * @throws IOException
         */
        private List<String> getDataFromApi() throws IOException {
            // Get a list of up to 10 files.
            List<String> channelInfo = new ArrayList<String>();
            ChannelListResponse result = mService.channels().list("snippet,contentDetails,statistics")
                    .setForUsername("GoogleDevelopers")
                    .execute();
            List<Channel> channels = result.getItems();
            if (channels != null) {
                Channel channel = channels.get(0);
                channelInfo.add("This channel's ID is " + channel.getId() + ". " +
                        "Its title is '" + channel.getSnippet().getTitle() + ", " +
                        "and it has " + channel.getStatistics().getViewCount() + " views.");
            }
            Log.d("GOOD", channelInfo.get(0));
            return channelInfo;
        }


        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            //mProgress.show();
        }

        @Override
        protected void onPostExecute(List<String> output) {
            //mProgress.hide();
            if (output == null || output.size() == 0) {
                //mOutputText.setText("No results returned.");
            } else {
                output.add(0, "Data retrieved using the YouTube Data API:");
                //mOutputText.setText(TextUtils.join("\n", output));
            }
        }

        @Override
        protected void onCancelled() {
            //mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            Constants.REQUEST_AUTHORIZATION);
                } else {
                    //mOutputText.setText("The following error occurred:\n"
                            //+ mLastError.getMessage());
                }
            } else {
                //mOutputText.setText("Request cancelled.");
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // Do nothing.
    }

    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                AccountSettingsActivity.this,
                connectionStatusCode,
                Constants.REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

}
