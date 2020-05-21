package com.path.mypath.data_parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * invite_status_code = 0;
 * 0 = 正常
 * 1 = 接受
 * 2 = 拒絕
 * 3 = 發送中
 * 4 = 留言
 */


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
    @SerializedName("is_invite")
    private boolean isInvite;
    @SerializedName("invite_status_code")
    private int inviteStatusCode;
    @SerializedName("reply_message")
    private String replyMessage;

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getReplyMessage() {
        return replyMessage;
    }

    public int getinviteStatusCode() {
        return inviteStatusCode;
    }

    public void setinviteStatusCode(int inviteStatusCode) {
        this.inviteStatusCode = inviteStatusCode;
    }

    public boolean isInvite() {
        return isInvite;
    }

    public void setInvite(boolean invite) {
        isInvite = invite;
    }

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
