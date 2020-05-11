package com.path.mypath.data_parser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataReply implements Parcelable {
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("photo_url")
    private String photoUrl;
    @SerializedName("message")
    private String message;

    public static Creator<DataReply> getCREATOR() {
        return CREATOR;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public DataReply createFromParcel(Parcel in) {
            DataReply data = new DataReply();
            data.setNickname(in.readString());
            data.setPhoto(in.readString());
            data.setMessage(in.readString());

            return data;
        }

        @Override
        public DataReply[] newArray(int size) {
            return new DataReply[size];
        }
    };

    public DataReply() {

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhoto() {
        return photoUrl;
    }

    public void setPhoto(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickname);
        dest.writeString(photoUrl);
        dest.writeString(message);
    }
}
