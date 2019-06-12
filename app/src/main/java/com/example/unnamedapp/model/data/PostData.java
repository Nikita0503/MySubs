package com.example.unnamedapp.model.data;

import java.util.Date;

public class PostData {
    public int socialWebId;
    public String postId;
    public Date date;

    public PostData(int socialWebId, String postId, Date date) {
        this.socialWebId = socialWebId;
        this.postId = postId;
        this.date = date;
    }
}
