package com.path.mypath.data_parser;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class FCMObject implements Serializable {
    @SerializedName("to")
    private String to;
    @SerializedName("notification")
    private FCMNotification notification;
    @SerializedName("data")
    private FCMData data;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public FCMNotification getNotification() {
        return notification;
    }

    public void setNotification(FCMNotification notification) {
        this.notification = notification;
    }

    public FCMData getData() {
        return data;
    }

    public void setData(FCMData data) {
        this.data = data;
    }
}
