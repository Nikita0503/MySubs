package com.example.unnamedapp.model.data.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InstagramUserdata {

    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("full_name")
    @Expose
    public String fullName;
    @SerializedName("profile_picture")
    @Expose
    public String profilePicture;
    @SerializedName("bio")
    @Expose
    public String bio;
    @SerializedName("website")
    @Expose
    public String website;
    @SerializedName("is_business")
    @Expose
    public Boolean isBusiness;
    @SerializedName("counts")
    @Expose
    public Counts counts;

}
