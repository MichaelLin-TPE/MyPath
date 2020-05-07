package com.path.mypath.data_parser;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.ArrayList;

public class DataArray implements Serializable {
    @SerializedName("location_array")
    private ArrayList<LatLng> locationArray;
    @SerializedName("current_time")
    private long currentTime;
    @SerializedName("user_nickname")
    private String userNickName;
    @SerializedName("user_photo")
    private String userPhoto;
    @SerializedName("heart_count")
    private int heartCount;
    @SerializedName("reply_count")
    private int replyCount;
    @SerializedName("reply_array")
    private ArrayList<DataReply> replyArray;
    @SerializedName("article_title")
    private String articleTitle;
    @SerializedName("heart_press_user")
    private ArrayList<DataUserPresHeart> heartPressUsers;

    public ArrayList<DataUserPresHeart> getHeartPressUsers() {
        return heartPressUsers;
    }

    public void setHeartPressUsers(ArrayList<DataUserPresHeart> heartPressUsers) {
        this.heartPressUsers = heartPressUsers;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public ArrayList<LatLng> getLocationArray() {
        return locationArray;
    }

    public void setLocationArray(ArrayList<LatLng> locationArray) {
        this.locationArray = locationArray;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public int getHeartCount() {
        return heartCount;
    }

    public void setHeartCount(int heartCount) {
        this.heartCount = heartCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public ArrayList<DataReply> getReplyArray() {
        return replyArray;
    }

    public void setReplyArray(ArrayList<DataReply> replyArray) {
        this.replyArray = replyArray;
    }
}
