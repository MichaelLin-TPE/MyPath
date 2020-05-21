package com.path.mypath.message_activity;

import java.util.ArrayList;

public interface MessageActivityPresenter {

    void onBackButtonClickListener();

    void onCatchPersonChatData(ArrayList<MessageListDTO> msgArray, ArrayList<String> roomIdArray);

    void onMessageItemClickListener(String roomId);
}
