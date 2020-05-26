package com.path.mypath.single_view_activity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.fragment.MessageArray;
import com.path.mypath.fragment.MessageObject;
import com.path.mypath.fragment.RoomIdObject;

import java.util.ArrayList;
import java.util.List;

public class SingleViewPresenterImpl implements SingleViewPresenter {

    private SingleViewVu mView;
    private Gson gson;
    
    private ArrayList<DataArray> homeDataArray;
    
    private DataArray homeData,pressedData;

    private DataObject userData,creatorData;

    private ArrayList<RoomIdObject> roomIdArray;

    private String userEmail,creatorEmail,roomId;

    private ArrayList<MessageArray> msgArray;

    public SingleViewPresenterImpl(SingleViewVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchData(DataArray data) {
        mView.setView(data);
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        if (json != null){
            homeDataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onHeartButtonClickListener(DataArray data) {
        boolean isPressedHeart = false;
        boolean isDataFound = false;
        for (DataArray object : homeDataArray){
            if (object.getArticleTitle().equals(data.getArticleTitle()) && object.getCurrentTime() == data.getCurrentTime()){
                isDataFound = true;
                for (DataUserPresHeart heart : object.getHeartPressUsers()){
                    if (heart.getName().equals(mView.getUserNickname())){
                        object.getHeartPressUsers().remove(heart);
                        if (object.getHeartPressUsers().size() == 0){
                            object.setHeartCount(0);
                        }else {
                            object.setHeartCount(object.getHeartPressUsers().size());
                        }

                        isPressedHeart = true;
                        break;
                    }
                }
            }
        }
        if (isDataFound){
            if (isPressedHeart){
                mView.setHeartIcon(true);
                mView.setHeartCountLess();

                String json = gson.toJson(homeDataArray);

                mView.update(json);

                Log.i("Michael","變成空白");
            }else {
                Log.i("Michael","變成紅心");
                mView.setHeartIcon(false);
                mView.setHeartCountMore();
                for (DataArray object : homeDataArray){
                    if (object.getArticleTitle().equals(data.getArticleTitle()) && object.getCurrentTime() == data.getCurrentTime()){
                        object.setHeartCount(object.getHeartCount()+1);
                        DataUserPresHeart heart = new DataUserPresHeart();
                        heart.setName(mView.getUserNickname());
                        heart.setPhotoUrl(mView.getUserPhoto());
                        if (object.getHeartPressUsers() != null && object.getHeartPressUsers().size() != 0){
                            object.getHeartPressUsers().add(heart);
                        }else {
                            ArrayList<DataUserPresHeart> heartArray = new ArrayList<>();
                            heartArray.add(heart);
                            object.setHeartPressUsers(heartArray);
                        }
                        break;
                    }
                }
                String json = gson.toJson(homeDataArray);
                mView.update(json);
            }
        }

    }

    @Override
    public void onReplyClickListener(DataArray data) {
        mView.intentToReplyActivity(data);
    }

    @Override
    public void onHeartCountClickListener(DataArray data) {
        mView.intentToHeartActivity(data);
    }

    @Override
    public void onSendMessageClickListener(DataArray data) {
        this.pressedData = data;
        mView.showSendMessageDialog(data.getUserNickName(),data.getUserEmail());
    }

    @Override
    public void onEditTextSendTypeListener(String message, String userEmail, String articleCreator) {
        if (message != null && !message.isEmpty()){
            if (roomIdArray == null){
                mView.createChatRoom(userEmail,articleCreator,message);
            }else {
                roomId = "";

                for (RoomIdObject object : roomIdArray){
                    if (object.getUser1().equals(userEmail) && object.getUser2().equals(articleCreator)){
                        roomId = object.getRoomId();
                        break;
                    }
                    if (object.getUser1().equals(articleCreator) && object.getUser2().equals(userEmail)){
                        roomId = object.getRoomId();
                        break;
                    }
                }
                if (roomId != null && !roomId.isEmpty()){
                    mView.searchPersonChatRoomData(userEmail,articleCreator,message,roomId);
                }else {
                    mView.createChatRoom(userEmail,articleCreator,message);
                }
            }
        }else {
            String content = "說點話吧!!!";
            mView.showToast(content);
        }
    }

    @Override
    public void onCatchRoomIdList(ArrayList<RoomIdObject> roomArray) {
        if (roomArray.size() != 0){
            roomIdArray = roomArray;
        }
    }

    @Override
    public void onCreateRoomSuccessful(String message, String userEmail, String articleCreator) {
        mView.searchForRoomId(userEmail,articleCreator,message);
    }

    @Override
    public void onCatchRoomIdSuccessful(String id, String userEmail, String articleCreator, String message) {
        this.userEmail = userEmail;
        this.creatorEmail = articleCreator;

        MessageObject object = new MessageObject();
        object.setUser1(userEmail);
        object.setUser2(articleCreator);
        object.setUser1Nickname(mView.getNickname());
        object.setUser1PhotoUrl(mView.getPhotoUrl());
        object.setUser2Nickname(pressedData.getUserNickName());
        object.setUser2PhotoUrl(pressedData.getUserPhoto());
        object.setRoomId(roomId);
        ArrayList<MessageArray> msgArray = new ArrayList<>();
        MessageArray data = new MessageArray();
        data.setMessage(message);
        data.setPhotoUrl("");
        data.setUserEmail(mView.getUserEmail());
        data.setUserNickname(mView.getNickname());
        data.setUserPhotoUrl(mView.getPhotoUrl());
        data.setTime(System.currentTimeMillis());
        msgArray.add(data);
        object.setMessageArray(msgArray);
        String msgJson = gson.toJson(object);

        mView.createPersonalChatRoom(roomId,msgJson);
    }

    @Override
    public void onCreatePersonalChatRoomSuccessful(String roomId) {
        this.roomId = roomId;
        String message = "發送訊息成功";
        mView.showToast(message);
        mView.reSearchRoomIdList();
        mView.searchUserData(userEmail);
    }

    @Override
    public void onCatchPersonalUserDataSuccessful(String json, String userEmail) {
        if (json != null){
            userData = gson.fromJson(json, DataObject.class);
            if (userData.getRoomIdArray() != null && userData.getRoomIdArray().size() != 0){

                boolean isRoomRepeat = false;

                for (String room : userData.getRoomIdArray()){
                    if (room.equals(roomId)){
                        isRoomRepeat = true;
                        break;
                    }
                }
                if (isRoomRepeat){
                    return;
                }
                userData.getRoomIdArray().add(roomId);


            }else {
                ArrayList<String > roomIdArray = new ArrayList<>();
                roomIdArray.add(roomId);
                userData.setRoomIdArray(roomIdArray);
            }
            String userJson = gson.toJson(userData);
            mView.updatePersonalUserData(userJson,userEmail);
            mView.searchCreatorData(creatorEmail);
        }
    }

    @Override
    public void onCatchPersonalData(String json) {
        //這裡先不做
    }

    @Override
    public void onCatchPersonalCreatorDataSuccessful(String json) {
        if (json != null){
            creatorData = gson.fromJson(json,DataObject.class);

            if (creatorData.getRoomIdArray() != null && creatorData.getRoomIdArray().size() != 0){

                boolean isRoomRepeat = false;

                for (String room : creatorData.getRoomIdArray()){
                    if (room.equals(roomId)){
                        isRoomRepeat = true;
                        break;
                    }
                }
                if (isRoomRepeat){
                    return;
                }
                creatorData.getRoomIdArray().add(roomId);


            }else {
                ArrayList<String > roomIdArray = new ArrayList<>();
                roomIdArray.add(roomId);
                creatorData.setRoomIdArray(roomIdArray);
            }
            String userJson = gson.toJson(creatorData);
            mView.updatePersonalUserData(userJson,creatorEmail);
        }
    }

    @Override
    public void onCatchPersonalChatData(String json, String userEmail, String articleCreator, String message) {
        if (json != null){

            MessageObject object = gson.fromJson(json,MessageObject.class);
            MessageArray data = new MessageArray();
            data.setUserPhotoUrl(mView.getPhotoUrl());
            data.setUserNickname(mView.getNickname());
            data.setUserEmail(mView.getUserEmail());
            data.setPhotoUrl("");
            data.setMessage(message);
            data.setTime(System.currentTimeMillis());
            if (object.getMessageArray() != null && object.getMessageArray().size() != 0){
                object.getMessageArray().add(data);
                String msgJson = gson.toJson(object);
                mView.updatePersonalChatData(msgJson,roomId);
            }else {
                msgArray = new ArrayList<>();
                msgArray.add(data);
                object.setMessageArray(msgArray);
                String msgJson = gson.toJson(object);
                mView.updatePersonalChatData(msgJson,roomId);
            }
        }
    }

    @Override
    public void onPhotoClickListener(DataArray data) {
        if (data.getUserNickName().equals(mView.getNickname())){

        }else {
            mView.intentToUserPageReviewActivity(data.getUserEmail());
        }
    }
}
