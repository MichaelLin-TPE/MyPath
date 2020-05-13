package com.path.mypath.data_parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ArticleLikeNotification implements Serializable {
    @SerializedName("user_nickname")
    private String userNickname;
    @SerializedName("user_photo")
    private String userPhoto;
    @SerializedName("user_email")
    private String userEmail;
    @SerializedName("article_title")
    private String articleTitle;
    @SerializedName("article_creator_name")
    private String articleCreatorName;
    @SerializedName("article_post_time")
    private long articlePostTime;
    @SerializedName("pressed_current_time")
    private long pressedCurrentTime;

    public long getPressedCurrentTime() {
        return pressedCurrentTime;
    }

    public void setPressedCurrentTime(long pressedCurrentTime) {
        this.pressedCurrentTime = pressedCurrentTime;
    }

    public long getArticlePostTime() {
        return articlePostTime;
    }

    public void setArticlePostTime(long articlePostTime) {
        this.articlePostTime = articlePostTime;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleCreatorName() {
        return articleCreatorName;
    }

    public void setArticleCreatorName(String articleCreatorName) {
        this.articleCreatorName = articleCreatorName;
    }
}
