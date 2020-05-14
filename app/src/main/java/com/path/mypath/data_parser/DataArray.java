package com.path.mypath.data_parser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    @SerializedName("user_email")
    private String userEmail;
    @SerializedName("distance")
    private double distance;
    @SerializedName("photo_array")
    private ArrayList<String> photoArray;

    public DataArray(){

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

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public ArrayList<DataUserPresHeart> getHeartPressUsers() {
        return heartPressUsers;
    }

    public void setHeartPressUsers(ArrayList<DataUserPresHeart> heartPressUsers) {
        this.heartPressUsers = heartPressUsers;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public ArrayList<String> getPhotoArray() {
        return photoArray;
    }

    public void setPhotoArray(ArrayList<String> photoArray) {
        this.photoArray = photoArray;
    }

    public static Creator<DataArray> getCREATOR() {
        return CREATOR;
    }

    protected DataArray(Parcel in) {
        if (in.readByte() == 0x01) {
            locationArray = new ArrayList<LatLng>();
            in.readList(locationArray, LatLng.class.getClassLoader());
        } else {
            locationArray = null;
        }
        currentTime = in.readLong();
        userNickName = in.readString();
        userPhoto = in.readString();
        heartCount = in.readInt();
        replyCount = in.readInt();
        if (in.readByte() == 0x01) {
            replyArray = new ArrayList<DataReply>();
            in.readList(replyArray, DataReply.class.getClassLoader());
        } else {
            replyArray = null;
        }
        articleTitle = in.readString();
        if (in.readByte() == 0x01) {
            heartPressUsers = new ArrayList<DataUserPresHeart>();
            in.readList(heartPressUsers, DataUserPresHeart.class.getClassLoader());
        } else {
            heartPressUsers = null;
        }
        userEmail = in.readString();
        distance = in.readDouble();
        if (in.readByte() == 0x01) {
            photoArray = new ArrayList<String>();
            in.readList(photoArray, String.class.getClassLoader());
        } else {
            photoArray = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (locationArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(locationArray);
        }
        dest.writeLong(currentTime);
        dest.writeString(userNickName);
        dest.writeString(userPhoto);
        dest.writeInt(heartCount);
        dest.writeInt(replyCount);
        if (replyArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(replyArray);
        }
        dest.writeString(articleTitle);
        if (heartPressUsers == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(heartPressUsers);
        }
        dest.writeString(userEmail);
        dest.writeDouble(distance);
        if (photoArray == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(photoArray);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<DataArray> CREATOR = new Parcelable.Creator<DataArray>() {
        @Override
        public DataArray createFromParcel(Parcel in) {
            return new DataArray(in);
        }

        @Override
        public DataArray[] newArray(int size) {
            return new DataArray[size];
        }
    };
}