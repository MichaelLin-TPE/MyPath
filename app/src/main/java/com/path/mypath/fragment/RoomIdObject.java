package com.path.mypath.fragment;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RoomIdObject implements Serializable {
    @SerializedName("user1")
    private String user1;
    @SerializedName("user2")
    private String user2;

    @SerializedName("room_id")
    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
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
