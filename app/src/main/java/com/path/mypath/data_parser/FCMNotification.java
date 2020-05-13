package com.path.mypath.data_parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FCMNotification implements Serializable {
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
