package com.path.mypath.chat_room_activity;

public interface ChatRoomPresenter {
    void onBackButtonClickListener();

    void onCatchChatDataSuccessful(String json);

    void onSendMessageClickListener(String toString);
}
