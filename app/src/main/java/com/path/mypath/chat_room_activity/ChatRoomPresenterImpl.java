package com.path.mypath.chat_room_activity;

import com.google.gson.Gson;
import com.path.mypath.fragment.MessageArray;
import com.path.mypath.fragment.MessageObject;

import java.util.ArrayList;

public class ChatRoomPresenterImpl implements ChatRoomPresenter {

    private ChatRoomVu mView;

    private Gson gson ;

    private MessageObject msgData;

    private ArrayList<MessageArray> msgArray;

    public ChatRoomPresenterImpl(ChatRoomVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onCatchChatDataSuccessful(String json) {
        if (json != null){
            msgData = gson.fromJson(json,MessageObject.class);
            if (msgData != null && msgData.getMessageArray()!= null && msgData.getMessageArray().size() != 0){
                msgArray = msgData.getMessageArray();
                String titleName = "";
                if (msgData.getUser1Nickname().equals(mView.getUserNickname())){
                    titleName = msgData.getUser2Nickname();
                }else if (msgData.getUser2Nickname().equals(mView.getUserNickname())){
                    titleName = msgData.getUser1Nickname();
                }
                mView.setTitle(titleName);
                mView.setRecyclerView(msgArray);
            }
        }
    }

    @Override
    public void onSendMessageClickListener(String message) {
        if (message != null && !message.isEmpty()){
            MessageArray data = new MessageArray();
            data.setMessage(message);
            data.setUserPhotoUrl(mView.getUserPhoto());
            data.setUserEmail(mView.getUserEmail());
            data.setTime(System.currentTimeMillis());
            data.setUserNickname(mView.getUserNickname());
            data.setPhotoUrl("");
            msgArray.add(data);
            String json = gson.toJson(msgData);
            mView.updateChatData(json);
        }else {
            String content = "說點甚麼吧~~";
            mView.showToast(content);
        }
    }
}
