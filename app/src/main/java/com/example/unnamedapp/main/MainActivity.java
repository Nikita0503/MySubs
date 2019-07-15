package com.example.unnamedapp.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.unnamedapp.BaseContract;
import com.example.unnamedapp.R;
import com.example.unnamedapp.SpacesItemDecoration;
import com.example.unnamedapp.account_settings.AccountSettingsActivity;
import com.example.unnamedapp.main.social_webs.SocialPagerAdapter;
import com.example.unnamedapp.main.social_webs.SocialWebViewFragmentInstagram;
import com.example.unnamedapp.main.social_webs.SocialWebViewFragmentTwitter;
import com.example.unnamedapp.main.social_webs.SocialWebViewFragmentYouTube;
import com.example.unnamedapp.model.data.PostData;
import com.example.unnamedapp.model.data.SubscriptionData;
import com.example.unnamedapp.model.data.UserData;
import com.example.unnamedapp.new_subscription.NewSubscriptionActivity;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements BaseContract.BaseView {

    private MainPresenter mPresenter;
    private SubscriptionsListAdapter mAdapter;
    private WallListAdapter mWallAdapter;
    private SocialPagerAdapter mFragmentAdapter;
    private SubscriptionData mSubscriptionData;

    private SocialWebViewFragmentInstagram mInstagramWebFragment;
    private SocialWebViewFragmentTwitter mTwitterWebFragment;
    private SocialWebViewFragmentYouTube mYouTubeWebFragment;

    @BindView(R.id.textViewAppName)
    TextView textViewTitle;
    @BindView(R.id.spin_kit)
    ProgressBar progressBar;
    @BindView(R.id.recyclerViewWall)
    RecyclerView recyclerViewWall;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.social_web_pager)
    ViewPager mViewPager;
    @BindView(R.id.social_web_tablayout)
    TabLayout mTabLayout;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private RecyclerView mRecyclerViewSideMenu;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.GET_ACCOUNTS},
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
        String email = intent.getStringExtra("email");
        mTextViewUserName.setText(email.split("@")[0]);
        if(!isOnline()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.no_internet))
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mPresenter.onStart();
        mPresenter.checkIsSignedIn();
        mPresenter.fetchSubscriptions();
    }

    public void createWallAdapter(){
        mWallAdapter = new WallListAdapter(this);
        mPresenter.resetToStart();
        recyclerViewWall.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewWall.setAdapter(mWallAdapter);
        recyclerViewWall.invalidate();
    }

    public void addPosts(ArrayList<PostData> posts){
        mWallAdapter.addPosts(posts);
    }

    @Override
    public void initViews() {
        ButterKnife.bind(this);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "In developing", Snackbar.LENGTH_SHORT)
                //        .setAction("Action", null).show();
                    textViewTitle.setText(getResources().getString(R.string.app_name));
                    showSocialWebBrowsers();
                    hideLoading();

            }
        });
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        //toggle.setDrawerIndicatorEnabled(false);
        toggle.syncState();
        mButtonNewSubscription = navigationView.findViewById(R.id.buttonAddSubscription);
        mButtonNewSubscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Snackbar.make(v, "In developing...", Snackbar.LENGTH_SHORT)
                //        .setAction("Action", null).show();

                hideDrawerLayout();
                Intent intent = new Intent(MainActivity.this, NewSubscriptionActivity.class);
                intent.putExtra("token", mPresenter.token);
                startActivity(intent);
            }
        });//mWallAdapter = new WallListAdapter(this);
        //recyclerViewWall.swapAdapter(mWallAdapter, true);
        mTextViewUserName = navigationView.findViewById(R.id.textViewName);
        mAdapter = new SubscriptionsListAdapter(getApplicationContext(), this);
        mFragmentAdapter = new SocialPagerAdapter(getSupportFragmentManager());
        mInstagramWebFragment = new SocialWebViewFragmentInstagram();
        mInstagramWebFragment.setToken(mPresenter.token);
        mFragmentAdapter.addFragment(mInstagramWebFragment, "Instagram");
        mTwitterWebFragment = new SocialWebViewFragmentTwitter();
        mTwitterWebFragment.setToken(mPresenter.token);
        mFragmentAdapter.addFragment(mTwitterWebFragment, "Twitter");
        mYouTubeWebFragment = new SocialWebViewFragmentYouTube();
        mYouTubeWebFragment.setToken(mPresenter.token);
        mFragmentAdapter.addFragment(mYouTubeWebFragment, "YouTube");
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mFragmentAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
        mWallAdapter = new WallListAdapter(this);
        mRecyclerViewSideMenu = navigationView.findViewById(R.id.recyclerViewSubscriptions);
        mRecyclerViewSideMenu.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecyclerViewSideMenu.setAdapter(mAdapter);
        mRecyclerViewSideMenu.addItemDecoration(new SpacesItemDecoration(10));
        //recyclerViewWall.setAdapter(mWallAdapter);
        mImageViewTwitterIcon = navigationView.findViewById(R.id.imageViewTwitterIcon);
        mImageViewInstagramIcon = navigationView.findViewById(R.id.imageViewInstagramIcon);
        mImageViewYouTubeIcon = navigationView.findViewById(R.id.imageViewYouTubeIcon);
        Sprite doubleBounce = new WanderingCubes();
        progressBar.setIndeterminateDrawable(doubleBounce);
        showSocialWebBrowsers();
    }

    @Override
    public void showMessage(String message){
        Snackbar.make(getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    public void showSocialWebBrowsers(){
        mTabLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        recyclerViewWall.setVisibility(View.GONE);
    }

    public void showWall(){
        mTabLayout.setVisibility(View.GONE);
        mViewPager.setVisibility(View.GONE);
        recyclerViewWall.setVisibility(View.VISIBLE);
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

    public void showUnauthorizedYouTubeIcon(){
        mImageViewYouTubeIcon.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_youtube_unauthorized)
                .into(mImageViewYouTubeIcon);
    }

    public void showUnauthorizedTwitterIcon(){
        mImageViewTwitterIcon.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_twitter_unauthorized)
                .into(mImageViewTwitterIcon);
    }

    public void showUnauthorizedInstagramIcon(){
        mImageViewInstagramIcon.setVisibility(View.VISIBLE);
        Picasso.with(getApplicationContext())
                .load(R.drawable.ic_instagram_unauthorized)
                .into(mImageViewInstagramIcon);
    }

    public void hideDrawerLayout(){
        drawer.closeDrawer(Gravity.START, true);
    }

    public void fetchPosts(SubscriptionData subscriptionData){
        textViewTitle.setText(subscriptionData.name);
        mPresenter.fetchYouTubePostsIds(subscriptionData.youtube_id);
        mPresenter.fetchTwitterPostsIds(subscriptionData.twitter_id);
        mPresenter.fetchIdByUsername(subscriptionData.instagram_id);
        mSubscriptionData = subscriptionData;
    }

    public void fetchNextPagePosts(){
        mPresenter.fetchYouTubePostsIds(mSubscriptionData.youtube_id);
        mPresenter.fetchTwitterPostsIds(mSubscriptionData.twitter_id);
        mPresenter.fetchIdByUsername(mSubscriptionData.instagram_id);
    }

    public void openEditActivity(SubscriptionData subscription){
        Intent intent = new Intent(this, NewSubscriptionActivity.class);
        intent.putExtra("token", mPresenter.token);
        intent.putExtra("editor", true);
        intent.putExtra("id", subscription.id);
        intent.putExtra("name", subscription.name);
        intent.putExtra("image", subscription.image);
        intent.putExtra("instagram_id", subscription.instagram_id);
        intent.putExtra("twitter_id", subscription.twitter_id);
        intent.putExtra("youtube_id", subscription.youtube_id);
        startActivity(intent);
    }

    public void deleteSubscription(int id){
        mPresenter.deleteSubscription(id);
    }

    public void addSubscriptions(ArrayList<SubscriptionData> subs){
        mAdapter.addSubscriptions(subs);
        mInstagramWebFragment.setSubscriptionList(subs);
        mTwitterWebFragment.setSubscriptionList(subs);
        mYouTubeWebFragment.setSubscriptionList(subs);
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
            //super.onBackPressed();
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        mPresenter.onStop();
    }
}
