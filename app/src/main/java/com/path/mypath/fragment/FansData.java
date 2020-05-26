package com.path.mypath.fragment;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FansData implements Parcelable {
    @SerializedName("photo")
    private String photo;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("email")
    private String email;

    public FansData() {

    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public FansData(Parcel in) {
        photo = in.readString();
        nickname = in.readString();
        email = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(photo);
        dest.writeString(nickname);
        dest.writeString(email);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<FansData> CREATOR = new Parcelable.Creator<FansData>() {
        @Override
        public FansData createFromParcel(Parcel in) {
            return new FansData(in);
        }

        @Override
        public FansData[] newArray(int size) {
            return new FansData[size];
        }
    };
}