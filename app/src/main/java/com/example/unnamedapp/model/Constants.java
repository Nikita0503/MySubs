package com.example.unnamedapp.model;

import com.google.api.services.youtube.YouTubeScopes;

public interface Constants {

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    static final String BUTTON_TEXT = "Call YouTube Data API";
    static final String PREF_ACCOUNT_NAME = "accountName";
    static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY };

    static final int GALLERY_REQUEST = 0;
    static final int INSTAGRAM_ID = 1;
    static final int TWITTER_ID = 2;
    static final int YOUTUBE_ID = 3;
}
