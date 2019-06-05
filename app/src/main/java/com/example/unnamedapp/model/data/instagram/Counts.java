package com.example.unnamedapp.model.data.instagram;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Counts {

    @SerializedName("media")
    @Expose
    public Integer media;
    @SerializedName("follows")
    @Expose
    public Integer follows;
    @SerializedName("followed_by")
    @Expose
    public Integer followedBy;

}
