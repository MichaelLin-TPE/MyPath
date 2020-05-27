package com.path.mypath.chat_room_activity;

import android.util.Log;

import com.google.gson.Gson;
import com.path.mypath.data_parser.FCMData;
import com.path.mypath.data_parser.FCMNotification;
import com.path.mypath.data_parser.FCMObject;
import com.path.mypath.fragment.MessageArray;
import com.path.mypath.fragment.MessageObject;
import com.path.mypath.tools.HttpConnection;

import java.util.ArrayList;
import java.util.Locale;

public class ChatRoomPresenterImpl implements ChatRoomPresenter {

    private ChatRoomVu mView;

    private Gson gson ;

    private MessageObject msgData;

    private ArrayList<MessageArray> msgArray;

    private String friendEmail;

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
                    friendEmail = msgData.getUser2();
                }else if (msgData.getUser2Nickname().equals(mView.getUserNickname())){
                    titleName = msgData.getUser1Nickname();
                    friendEmail = msgData.getUser1();
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

            mView.searchFriendData(friendEmail,message);

        }else {
            String content = "說點甚麼吧~~";
            mView.showToast(content);
        }
    }

    @Override
    public void onCatchFriendToken(String token, String message) {
        FCMObject data = new FCMObject();
        FCMNotification notification = new FCMNotification();
        notification.setBody(String.format(Locale.getDefault(),"%s : %s",mView.getNickname(),message));
        notification.setTitle("訊息");
        FCMData fcmData = new FCMData();
        fcmData.setDataContent(String.format(Locale.getDefault(),"%s : %s",mView.getNickname(),message));
        fcmData.setDataTitle("訊息");
        data.setTo(token);
        data.setData(fcmData);
        data.setNotification(notification);
        String json = gson.toJson(data);
        Log.i("Michael","發送的JSON : "+json);

        HttpConnection connection = new HttpConnection();
        connection.startConnection("https://fcm.googleapis.com/fcm/send", json, new HttpConnection.OnPostNotificationListener() {
            @Override
            public void onSuccessful(String result) {
                Log.i("Michael",result);
            }

            @Override
            public void onFail(String exception) {
                Log.i("Michael",exception);
            }
        });
    }
}
