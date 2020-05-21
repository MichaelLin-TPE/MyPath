package com.path.mypath.fragment.user_fragment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageObject implements Serializable {
    @SerializedName("user_email")
    private String userEmail;
    @SerializedName("user_nickname")
    private String userNickname;
    @SerializedName("message")
    private String message;
    @SerializedName("user_photo_url")
    private String userPhotoUrl;
    @SerializedName("photo_url")
    private String photoUrl;
    @SerializedName("time")
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserPhotoUrl() {
        return userPhotoUrl;
    }

    public void setUserPhotoUrl(String userPhotoUrl) {
        this.userPhotoUrl = userPhotoUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
