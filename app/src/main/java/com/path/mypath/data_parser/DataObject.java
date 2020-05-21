package com.path.mypath.data_parser;

import com.google.gson.annotations.SerializedName;
import com.path.mypath.fragment.FansData;

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
    @SerializedName("is_public_account")
    private boolean isPublicAccount;
    @SerializedName("sentence")
    private String sentence;
    @SerializedName("email")
    private String email;
    @SerializedName("fans_array")
    private ArrayList<FansData> fansArray;
    @SerializedName("chase_array")
    private ArrayList<FansData> chaseArray;
    @SerializedName("room_id_list")
    private ArrayList<String> roomIdArray;

    public ArrayList<String> getRoomIdArray() {
        return roomIdArray;
    }

    public void setRoomIdArray(ArrayList<String> roomIdArray) {
        this.roomIdArray = roomIdArray;
    }

    public ArrayList<FansData> getFansArray() {
        return fansArray;
    }

    public void setFansArray(ArrayList<FansData> fansArray) {
        this.fansArray = fansArray;
    }

    public ArrayList<FansData> getChaseArray() {
        return chaseArray;
    }

    public void setChaseArray(ArrayList<FansData> chaseArray) {
        this.chaseArray = chaseArray;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public boolean isPublicAccount() {
        return isPublicAccount;
    }

    public void setPublicAccount(boolean publicAccount) {
        isPublicAccount = publicAccount;
    }

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
