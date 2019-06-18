package com.example.unnamedapp.model.APIUtils;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.example.unnamedapp.model.Constants;
import com.example.unnamedapp.model.data.PostData;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.squareup.picasso.Picasso;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;

public class APIYouTubeUtils {

    private String mChannelId;
    private GoogleAccountCredential mCredential;

    public void setChannelId(String channelId){
        mChannelId = channelId;
    }

    public void setCredential(GoogleAccountCredential credential){
        mCredential = credential;
    }

    public Single<ArrayList<PostData>> getPopularVideos = Single.create(new SingleOnSubscribe<ArrayList<PostData>>() {
        @Override
        public void subscribe(SingleEmitter<ArrayList<PostData>> e) throws Exception {
            com.google.api.services.youtube.YouTube mService = null;
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            Log.d("YOUTUBE_C", mCredential.getSelectedAccountName());
            mService = new com.google.api.services.youtube.YouTube.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("MySubs")
                    .build();
            String uploadId = null;
            ChannelListResponse result = mService.channels().list("contentDetails")
                    .setId("UCnbxcA3kZ_uUYIBHNvxpDQw")
                    .setFields("items/contentDetails/relatedPlaylists/uploads")
                    .execute();
            List<Channel> channels = result.getItems();
            if (channels != null) {
                Channel channel = channels.get(0);
                uploadId = channel.getContentDetails().getRelatedPlaylists().getUploads();
            }
            Log.d("YOUTUBE_DATA", uploadId);
            ArrayList<PostData> postList = new ArrayList<PostData>();
            PlaylistItemListResponse response = mService.playlistItems()
                    .list("snippet,contentDetails")
                    .setPlaylistId(uploadId)
                    .setMaxResults(1L)
                    .execute();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            List<PlaylistItem> allVideos = response.getItems();
            for(int i = 0; i < allVideos.size(); i++){
                PlaylistItem item = allVideos.get(i);
                Log.d("YOUTUBE_DATA",  item.getSnippet().getPublishedAt().toString());
                Date date = dateFormat.parse(item.getSnippet().getPublishedAt().toString());
                postList.add(new PostData(Constants.YOUTUBE_ID, item.getSnippet().getResourceId().getVideoId(), date));
            }
            e.onSuccess(postList);
        }
    });


}
