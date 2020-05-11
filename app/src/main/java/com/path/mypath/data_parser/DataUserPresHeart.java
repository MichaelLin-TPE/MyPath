package com.path.mypath.data_parser;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class DataUserPresHeart implements Parcelable {
    @SerializedName("name")
    private String name;
    @SerializedName("photo_url")
    private String photoUrl;

    public static Creator<DataArray> getCREATOR() {
        return CREATOR;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        @Override
        public DataUserPresHeart createFromParcel(Parcel in) {

            DataUserPresHeart data = new DataUserPresHeart();
            data.setName(in.readString());
            data.setPhotoUrl(in.readString());
            return data;
        }

        @Override
        public DataUserPresHeart[] newArray(int size) {
            return new DataUserPresHeart[size];
        }
    };

    public DataUserPresHeart() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(photoUrl);
    }
}
