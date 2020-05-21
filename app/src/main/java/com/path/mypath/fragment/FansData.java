package com.path.mypath.fragment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FansData implements Serializable {
    @SerializedName("photo")
    private String photo;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("email")
    private String email;

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
}
