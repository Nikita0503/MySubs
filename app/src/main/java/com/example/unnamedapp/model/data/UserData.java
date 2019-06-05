package com.example.unnamedapp.model.data;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.squareup.picasso.Transformation;

import java.io.Serializable;

public class UserData implements Serializable {
    public String name;
    public String avatar;

    public UserData(String name, String avatar) {
        this.name = name;
        this.avatar = avatar;
    }


}
