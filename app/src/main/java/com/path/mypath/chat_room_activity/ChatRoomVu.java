package com.path.mypath.chat_room_activity;

import com.path.mypath.fragment.MessageArray;

import java.util.ArrayList;

public interface ChatRoomVu {
    void closePage();

    void setRecyclerView(ArrayList<MessageArray> msgArray);

    String getUserNickname();

    void setTitle(String titleName);

    void showToast(String content);

    String getUserPhoto();

    String getUserEmail();

    void updateChatData(String json);

    void searchFriendData(String friendEmail, String message);

    String getNickname();
}
