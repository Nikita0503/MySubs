package com.example.unnamedapp.main;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.account_settings.AccountSettingsActivity;
import com.example.unnamedapp.model.AvatarTransformation;
import com.example.unnamedapp.model.data.PostData;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.example.unnamedapp.model.data.UserData;
import com.example.unnamedapp.new_subscription.NewSubscriptionActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.FoldingCube;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class MainActivity extends YouTubeBaseActivity implements BaseContract.BaseView {

    private MainPresenter mPresenter;
    private SubscriptionsListAdapter mAdapter;
    private WallListAdapter mWallAdapter;

    @BindView(R.id.spin_kit)
    ProgressBar progressBar;
    @BindView(R.id.recyclerViewWall)
    RecyclerView recyclerViewWall;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private RecyclerView mRecyclerViewSideMenu;
    private ImageView mImageViewUser;
    private ImageView mImageViewTwitterIcon;
    private ImageView mImageViewInstagramIcon;
    private ImageView mImageViewYouTubeIcon;
    private Button mButtonNewSubscription;
    private TextView mTextViewUserName;
    @OnClick(R.id.header)
    void onClickHeader(){
        Intent intent = new Intent(MainActivity.this, AccountSettingsActivity.class);
        intent.putExtra("userdata", new UserData("Pudge", "https://res.cloudinary.com/teepublic/image/private/s--6liaugi7--/t_Preview/b_rgb:6e2229,c_limit,f_jpg,h_630,q_90,w_630/v1513129027/production/designs/2171617_1.jpg"));
        startActivity(intent);
    }

    @Override
    public void onStart(){
        super.onStart();
        mPresenter.onStart();
        mPresenter.checkIsSignedIn();
        mPresenter.fetchSubscriptions();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .twitterAuthConfig(new TwitterAuthConfig("hlTZzOf9Ww9UwfFW4nObQQmTU", "PTTXNYMYbLkRv8On2YEOKSiqUtyRvt2fDZlzfpW2NLa7uYGZDA"))
                .debug(true)
                .build();
        Twitter.initialize(config);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String token = intent.getStringExtra("token");
        mPresenter = new MainPresenter(this, token);
        initViews();
    }


    public void addPosts(ArrayList<PostData> posts){
        mWallAdapter = new WallListAdapter(this);
        recyclerViewWall.setAdapter(mWallAdapter);
        recyclerViewWall.invalidate();
        mWallAdapter.addPosts(posts);
    }


    @Override
    public void initViews() {
        ButterKnife.bind(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "In developing", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                mAdapter.resetSelectedIndex();
            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        mButtonNewSubscription = navigationView.findViewById(R.id.buttonAddSubscription);
        mButtonNewSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "In developing...", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
                mAdapter.resetSelectedIndex();
                hideDrawerLayout();
                Intent intent = new Intent(MainActivity.this, NewSubscriptionActivity.class);
                intent.putExtra("token", mPresenter.token);
                startActivity(intent);
            }
        });//mWallAdapter = new WallListAdapter(this);
        //recyclerViewWall.swapAdapter(mWallAdapter, true);
        mImageViewUser = navigationView.findViewById(R.id.imageViewAvatar);
        mTextViewUserName = navigationView.findViewById(R.id.textViewName);
        setUser(new UserData("Pudge", "https://gamepedia.cursecdn.com/dota2_gamepedia/c/c0/Pudge_icon.png"));
        mAdapter = new SubscriptionsListAdapter(getApplicationContext(), this);
        mWallAdapter = new WallListAdapter(this);
        mRecyclerViewSideMenu = navigationView.findViewById(R.id.recyclerViewSubscriptions);
        mRecyclerViewSideMenu.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerViewSideMenu.setAdapter(mAdapter);
        mRecyclerViewSideMenu.addItemDecoration(new SpacesItemDecoration(10));
        recyclerViewWall.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewWall.setAdapter(mWallAdapter);
        mImageViewTwitterIcon = navigationView.findViewById(R.id.imageViewTwitterIcon);
        mImageViewInstagramIcon = navigationView.findViewById(R.id.imageViewInstagramIcon);
        mImageViewYouTubeIcon = navigationView.findViewById(R.id.imageViewYouTubeIcon);
        Sprite doubleBounce = new WanderingCubes();
        progressBar.setIndeterminateDrawable(doubleBounce);
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void showYouTubeIcon(){
        mImageViewYouTubeIcon.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_youtube)
                .into(mImageViewYouTubeIcon);
    }

    public void showTwitterIcon(){
        mImageViewTwitterIcon.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_twitter)
                .into(mImageViewTwitterIcon);
    }

    public void showInstagramIcon(){
        mImageViewInstagramIcon.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_instagram)
                .into(mImageViewInstagramIcon);
    }

    public void hideDrawerLayout(){
        drawer.closeDrawer(Gravity.START, true);
    }

    public void setUser(UserData user){
        mTextViewUserName.setText(user.name);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_pudge2)
                .transform(new AvatarTransformation())
                .into(mImageViewUser);
    }


    public void fetchPosts(SubscriptionData subscriptionData){
        mPresenter.fetchYouTubePostsIds(subscriptionData.youtube_id);
        mPresenter.fetchTwitterPostsIds(subscriptionData.twitter_id);
        mPresenter.fetchIdByUsername(subscriptionData.instagram_id);
    }

    public void deleteSubscription(int id){
        mPresenter.deleteSubscription(id);
    }

    public void addSubscriptions(ArrayList<SubscriptionData> subs){
        mAdapter.addSubscriptions(subs);
    }

    public void showLoading(){
        progressBar.setVisibility(View.VISIBLE);
    }

    public void hideLoading(){
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration
    {
        private int space;

        public SpacesItemDecoration(int space)
        {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state)
        {
            //добавить переданное кол-во пикселей отступа снизу
            outRect.bottom = space;
        }
    }
}
