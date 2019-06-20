package com.example.unnamedapp.model.data;

public class SubscriptionData {
    public int id;
    public String instagram_id;
    public String twitter_id;
    public String youtube_id;
    public String name;
    public String image;

    public SubscriptionData(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public SubscriptionData(int id, String instagram_id, String twitter_id, String youtube_id, String name, String image) {
        this.id = id;
        this.instagram_id = instagram_id;
        this.twitter_id = twitter_id;
        this.youtube_id = youtube_id;
        this.name = name;
        this.image = image;
    }
}
