package com.path.mypath.data_parser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.ArrayList;

public class DataArray implements Parcelable {
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

    public static Creator<DataArray> getCREATOR() {
        return CREATOR;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public DataArray createFromParcel(Parcel in) {
            DataArray data = new DataArray();
            data.setLocationArray(in.readArrayList(LatLng.class.getClassLoader()));
            data.setCurrentTime(in.readLong());
            data.setUserNickName(in.readString());
            data.setUserPhoto(in.readString());
            data.setHeartCount(in.readInt());
            data.setReplyCount(in.readInt());
            data.setReplyArray(in.readArrayList(DataReply.class.getClassLoader()));
            data.setArticleTitle(in.readString());
            data.setHeartPressUsers(in.readArrayList(DataUserPresHeart.class.getClassLoader()));
            return data;
        }

        @Override
        public DataArray[] newArray(int size) {
            return new DataArray[size];
        }
    };

    public DataArray() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(locationArray);
        dest.writeLong(currentTime);
        dest.writeString(userNickName);
        dest.writeString(userPhoto);
        dest.writeInt(heartCount);
        dest.writeInt(replyCount);
        dest.writeList(replyArray);
        dest.writeString(articleTitle);
        dest.writeList(heartPressUsers);
    }

    public ArrayList getHeartPressUsers() {
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

    public ArrayList getLocationArray() {
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

    public ArrayList getReplyArray() {
        return replyArray;
    }

    public void setReplyArray(ArrayList<DataReply> replyArray) {
        this.replyArray = replyArray;
    }


}
