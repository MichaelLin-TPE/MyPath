package com.path.mypath.data_parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class DataObject implements Serializable {
    @SerializedName("user_photo")
    private String userPhoto;
    @SerializedName("user_nickname")
    private String userNickname;
    @SerializedName("article_count")
    private int articleCount;
    @SerializedName("friend_count")
    private int friendCount;
    @SerializedName("chasing_count")
    private int chasingCount;
    @SerializedName("data_array")
    private ArrayList<DataArray> dataArray;

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public int getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(int articleCount) {
        this.articleCount = articleCount;
    }

    public int getFriendCount() {
        return friendCount;
    }

    public void setFriendCount(int friendCount) {
        this.friendCount = friendCount;
    }

    public int getChasingCount() {
        return chasingCount;
    }

    public void setChasingCount(int chasingCount) {
        this.chasingCount = chasingCount;
    }

    public ArrayList<DataArray> getDataArray() {
        return dataArray;
    }

    public void setDataArray(ArrayList<DataArray> dataArray) {
        this.dataArray = dataArray;
    }
}
