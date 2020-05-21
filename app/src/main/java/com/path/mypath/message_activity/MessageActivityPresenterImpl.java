package com.path.mypath.message_activity;

import com.google.gson.Gson;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;

public class MessageActivityPresenterImpl implements MessageActivityPresenter {

    private MessageActivityVu mView;


    public MessageActivityPresenterImpl(MessageActivityVu mView) {
        this.mView = mView;
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onCatchPersonChatData(ArrayList<MessageListDTO> msgArray, ArrayList<String> roomIdArray) {
        mView.setRecyclerView(msgArray,roomIdArray);
    }

    @Override
    public void onMessageItemClickListener(String roomId) {
        mView.intentToChatRoomActivity(roomId);
    }
}
