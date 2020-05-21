package com.path.mypath.fragment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class MessageObject implements Serializable {

    @SerializedName("message_array")
    private ArrayList<MessageArray> messageArray;
    @SerializedName("user1")
    private String user1;
    @SerializedName("user1Nickname")
    private String user1Nickname;
    @SerializedName("user1PhotoUrl")
    private String user1PhotoUrl;
    @SerializedName("user2")
    private String user2;
    @SerializedName("user2Nickname")
    private String user2Nickname;
    @SerializedName("user2PhotoUrl")
    private String user2PhotoUrl;

    public String getUser1Nickname() {
        return user1Nickname;
    }

    public void setUser1Nickname(String user1Nickname) {
        this.user1Nickname = user1Nickname;
    }

    public String getUser1PhotoUrl() {
        return user1PhotoUrl;
    }

    public void setUser1PhotoUrl(String user1PhotoUrl) {
        this.user1PhotoUrl = user1PhotoUrl;
    }

    public String getUser2Nickname() {
        return user2Nickname;
    }

    public void setUser2Nickname(String user2Nickname) {
        this.user2Nickname = user2Nickname;
    }

    public String getUser2PhotoUrl() {
        return user2PhotoUrl;
    }

    public void setUser2PhotoUrl(String user2PhotoUrl) {
        this.user2PhotoUrl = user2PhotoUrl;
    }

    public ArrayList<MessageArray> getMessageArray() {
        return messageArray;
    }

    public void setMessageArray(ArrayList<MessageArray> messageArray) {
        this.messageArray = messageArray;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }
}
