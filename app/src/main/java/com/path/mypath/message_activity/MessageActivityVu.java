package com.path.mypath.message_activity;

import java.util.ArrayList;

public interface MessageActivityVu {
    void closePage();

    void setRecyclerView(ArrayList<MessageListDTO> msgArray, ArrayList<String> roomIdArray);

    void intentToChatRoomActivity(String roomId);
}
