package com.path.mypath.fragment;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.data_parser.FCMData;
import com.path.mypath.data_parser.FCMNotification;
import com.path.mypath.data_parser.FCMObject;
import com.path.mypath.tools.HttpConnection;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragmentPresenterImpl implements HomeFragmentPresenter {
    private HomeFragmentVu mView;

    private Gson gson;

    private DataArray articleData, pressedData;

    private ArrayList<DataArray> dataArrayList, realTimeDataArray, publicDataArray;

    private ArrayList<RoomIdObject> roomIdArray;

    private String userEmail, creatorEmail, roomId;

    private DataObject userData, creatorData, realTimeUserData;

    private ArrayList<MessageArray> msgArray;

    public HomeFragmentPresenterImpl(HomeFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchUserDataSuccessful(String json) {
        if (json != null) {
            dataArrayList = gson.fromJson(json, new TypeToken<List<DataArray>>() {
            }.getType());
            Collections.sort(dataArrayList, new Comparator<DataArray>() {
                @Override
                public int compare(DataArray o1, DataArray o2) {

                    Date d1,d2;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss",Locale.TAIWAN);

                    try{
                        String currentTime1 = sdf.format(new Date(o1.getCurrentTime()));
                        String currentTime2 = sdf.format(new Date(o2.getCurrentTime()));
                        d1 = sdf.parse(currentTime1);
                        d2 = sdf.parse(currentTime2);

                    }catch (Exception e){
                        e.printStackTrace();
                        return 0;
                    }
                    if (d1 != null && d2 != null){
                        if (d1.before(d2)){
                            return 1;
                        }else {
                            return -1;
                        }
                    }else {
                        return 0;
                    }

                }
            });
            if (dataArrayList != null) {
                mView.setRecyclerView(dataArrayList);
            }
        }
    }

    @Override
    public void onHeartClickListener(DataArray articleData, int position, boolean isCheck, int selectIndex) {
        this.articleData = articleData;
        if (isCheck) {
            int currentHeartCount = articleData.getHeartCount() - 1;
            articleData.setHeartCount(currentHeartCount);
            dataArrayList.get(position).getHeartPressUsers().remove(selectIndex);
        } else {
            int currentHeartCount = articleData.getHeartCount() + 1;
            DataUserPresHeart data = new DataUserPresHeart();
            data.setPhotoUrl(mView.getPhotoUrl());
            data.setName(mView.getNickname());
            articleData.setHeartCount(currentHeartCount);
            articleData.getHeartPressUsers().add(data);
            dataArrayList.set(position, articleData);
        }

        String json = gson.toJson(dataArrayList);

        Map<String, Object> map = new HashMap<>();
        map.put("json", json);

        mView.updateUserData(map);

        //發送推播
        if (!isCheck) {
            Log.i("Michael", "準備發送推播");
            if (!articleData.getUserNickName().equals(mView.getNickname())) {
                mView.searchUserPersonalData(articleData.getUserEmail());

                //發送點讚訊息給發文者
                mView.searchCreatorLikeData(articleData.getUserEmail());

            }
        }


    }

    @Override
    public void onCatchRealTimeData(String json) {
        if (json != null) {
            realTimeDataArray = gson.fromJson(json, new TypeToken<List<DataArray>>() {
            }.getType());
        }
    }

    @Override
    public void onCatchFCMTokenSuccessful(String token) {
        FCMObject data = new FCMObject();
        FCMNotification notification = new FCMNotification();
        notification.setBody(String.format(Locale.getDefault(), "%s 按你的讚唷", mView.getNickname()));
        notification.setTitle("點讚訊息");
        FCMData fcmData = new FCMData();
        fcmData.setDataContent(String.format(Locale.getDefault(), "%s 按你的讚唷", mView.getNickname()));
        fcmData.setDataTitle("點讚訊息");
        data.setTo(token);
        data.setData(fcmData);
        data.setNotification(notification);
        String json = gson.toJson(data);
        Log.i("Michael", "發送的JSON : " + json);

        HttpConnection connection = new HttpConnection();
        connection.startConnection("https://fcm.googleapis.com/fcm/send", json, new HttpConnection.OnPostNotificationListener() {
            @Override
            public void onSuccessful(String result) {
                Log.i("Michael", result);
            }

            @Override
            public void onFail(String exception) {
                Log.i("Michael", exception);
            }
        });
    }

    @Override
    public void onCatchCreatorLikeData(String json) {

        ArticleLikeNotification notification = new ArticleLikeNotification();
        notification.setArticleCreatorName(articleData.getUserNickName());
        notification.setArticlePostTime(articleData.getCurrentTime());
        notification.setArticleTitle(articleData.getArticleTitle());
        notification.setUserEmail(mView.getUserEmail());
        notification.setUserNickname(mView.getNickname());
        notification.setUserPhoto(mView.getPhotoUrl());
        notification.setinviteStatusCode(0);
        notification.setInvite(false);
        notification.setPressedCurrentTime(System.currentTimeMillis());
        notification.setReplyMessage("");
        if (json != null) {
            ArrayList<ArticleLikeNotification> dataArray = gson.fromJson(json, new TypeToken<List<ArticleLikeNotification>>() {
            }.getType());
            if (dataArray != null) {
                boolean isDataRepeat = false;
                for (ArticleLikeNotification data : dataArray) {
                    if (data.getArticleTitle().equals(notification.getArticleTitle()) && data.getArticlePostTime() == notification.getArticlePostTime()) {
                        isDataRepeat = true;
                        break;
                    }
                }
                if (isDataRepeat) {
                    return;
                }
                dataArray.add(notification);
                String likeJson = gson.toJson(dataArray);
                mView.saveUserLikeData(likeJson, articleData.getUserEmail());
            }
        } else {

            ArrayList<ArticleLikeNotification> notificationArray = new ArrayList<>();
            notificationArray.add(notification);
            String likeJson = gson.toJson(notificationArray);

            mView.saveUserLikeData(likeJson, articleData.getUserEmail());
        }
    }

    @Override
    public void onReplyButtonClickListener(DataArray data) {
        mView.intentToReplyActivity(data);
    }

    @Override
    public void onReplyCountClickListener(DataArray data) {
        mView.intentToReplyActivity(data);
    }

    @Override
    public void onUserPhotoClickListener(DataArray data) {
        if (data.getUserNickName().equals(mView.getNickname())) {
            mView.intentToHomeActivity();
        } else {
            mView.intentToUserPageReviewActivity(data.getUserEmail());
        }
    }

    @Override
    public void onSendButtonClickListener(DataArray data) {
        this.pressedData = data;
        mView.showSendMessageDialog(data.getUserNickName(), data.getUserEmail());
    }

    @Override
    public void onEditTextSendTypeListener(String message, String userEmail, String articleCreator) {

        if (message != null && !message.isEmpty()) {
            if (roomIdArray == null) {
                mView.createChatRoom(userEmail, articleCreator, message);
            } else {
                roomId = "";

                for (RoomIdObject object : roomIdArray) {
                    if (object.getUser1().equals(userEmail) && object.getUser2().equals(articleCreator)) {
                        roomId = object.getRoomId();
                        break;
                    }
                    if (object.getUser1().equals(articleCreator) && object.getUser2().equals(userEmail)) {
                        roomId = object.getRoomId();
                        break;
                    }
                }
                if (roomId != null && !roomId.isEmpty()) {
                    mView.searchPersonChatRoomData(userEmail, articleCreator, message, roomId);
                } else {
                    mView.createChatRoom(userEmail, articleCreator, message);
                }
            }

            mView.searchCreatorUserData(articleCreator, message);

        } else {
            String content = "說點話吧!!!";
            mView.showToast(content);
        }


    }

    @Override
    public void onCatchRoomIdList(ArrayList<RoomIdObject> roomArray) {
        if (roomArray.size() != 0) {
            roomIdArray = roomArray;
        }
    }

    @Override
    public void onCreateRoomSuccessful(String message, String userEmail, String articleCreator) {
        mView.searchForRoomId(userEmail, articleCreator, message);
    }

    @Override
    public void onCatchRoomIdSuccessful(String roomId, String userEmail, String articleCreator, String message) {
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

        mView.createPersonalChatRoom(roomId, msgJson);
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
        if (json != null) {
            userData = gson.fromJson(json, DataObject.class);
            if (userData.getRoomIdArray() != null && userData.getRoomIdArray().size() != 0) {

                boolean isRoomRepeat = false;

                for (String room : userData.getRoomIdArray()) {
                    if (room.equals(roomId)) {
                        isRoomRepeat = true;
                        break;
                    }
                }
                if (isRoomRepeat) {
                    return;
                }
                userData.getRoomIdArray().add(roomId);


            } else {
                ArrayList<String> roomIdArray = new ArrayList<>();
                roomIdArray.add(roomId);
                userData.setRoomIdArray(roomIdArray);
            }
            String userJson = gson.toJson(userData);
            mView.updatePersonalUserData(userJson, userEmail);
            mView.searchCreatorData(creatorEmail);
        }
    }

    @Override
    public void onCatchPersonalCreatorDataSuccessful(String json) {
        if (json != null) {
            creatorData = gson.fromJson(json, DataObject.class);

            if (creatorData.getRoomIdArray() != null && creatorData.getRoomIdArray().size() != 0) {

                boolean isRoomRepeat = false;

                for (String room : creatorData.getRoomIdArray()) {
                    if (room.equals(roomId)) {
                        isRoomRepeat = true;
                        break;
                    }
                }
                if (isRoomRepeat) {
                    return;
                }
                creatorData.getRoomIdArray().add(roomId);


            } else {
                ArrayList<String> roomIdArray = new ArrayList<>();
                roomIdArray.add(roomId);
                creatorData.setRoomIdArray(roomIdArray);
            }
            String userJson = gson.toJson(creatorData);
            mView.updatePersonalUserData(userJson, creatorEmail);
        }
    }

    @Override
    public void onCatchPersonalChatData(String json, String userEmail, String articleCreator, String message) {
        if (json != null) {

            MessageObject object = gson.fromJson(json, MessageObject.class);
            MessageArray data = new MessageArray();
            data.setUserPhotoUrl(mView.getPhotoUrl());
            data.setUserNickname(mView.getNickname());
            data.setUserEmail(mView.getUserEmail());
            data.setPhotoUrl("");
            data.setMessage(message);
            data.setTime(System.currentTimeMillis());
            if (object.getMessageArray() != null && object.getMessageArray().size() != 0) {
                object.getMessageArray().add(data);
                String msgJson = gson.toJson(object);
                mView.updatePersonalChatData(msgJson, roomId);
            } else {
                msgArray = new ArrayList<>();
                msgArray.add(data);
                object.setMessageArray(msgArray);
                String msgJson = gson.toJson(object);
                mView.updatePersonalChatData(msgJson, roomId);
            }
        }
    }

    @Override
    public void onHeartCountClickListener(DataArray data) {
        mView.intentToHeartActivity(data);
    }

    @Override
    public void onCatchCreatorUserToken(String token, String message) {
        FCMObject data = new FCMObject();
        FCMNotification notification = new FCMNotification();
        notification.setBody(String.format(Locale.getDefault(), "%s : %s", mView.getNickname(), message));
        notification.setTitle("訊息");
        FCMData fcmData = new FCMData();
        fcmData.setDataContent(String.format(Locale.getDefault(), "%s : %s", mView.getNickname(), message));
        fcmData.setDataTitle("訊息");
        data.setTo(token);
        data.setData(fcmData);
        data.setNotification(notification);
        String json = gson.toJson(data);
        Log.i("Michael", "發送的JSON : " + json);

        HttpConnection connection = new HttpConnection();
        connection.startConnection("https://fcm.googleapis.com/fcm/send", json, new HttpConnection.OnPostNotificationListener() {
            @Override
            public void onSuccessful(String result) {
                Log.i("Michael", result);
            }

            @Override
            public void onFail(String exception) {
                Log.i("Michael", exception);
            }
        });
    }

    @Override
    public void onSortClickListener(DataArray data) {
        if (data.getUserNickName().equals(mView.getNickname())) {
            mView.showDeleteDialog(data);
        }else {
            mView.showReportDialog(data);
        }
    }

    @Override
    public void onDeleteItemClickListener(DataArray data) {

        for (DataArray homeData : realTimeDataArray) {
            String title = homeData.getArticleTitle();
            long currentTime = homeData.getCurrentTime();
            if (title.equals(data.getArticleTitle()) && currentTime == data.getCurrentTime()) {
                realTimeDataArray.remove(homeData);
                break;
            }
        }
        String json = gson.toJson(realTimeDataArray);
        mView.setRecyclerView(realTimeDataArray);
        mView.updateHomeData(json);
        if (realTimeUserData == null) {
            return;
        }
        for (DataArray userData : realTimeUserData.getDataArray()) {
            if (userData.getArticleTitle().equals(data.getArticleTitle()) && userData.getCurrentTime() == data.getCurrentTime()) {
                realTimeUserData.getDataArray().remove(userData);
                break;
            }
        }
        realTimeUserData.setArticleCount(realTimeUserData.getDataArray().size());
        String userJson = gson.toJson(realTimeUserData);
        mView.updatePersonalUserData(userJson, mView.getUserEmail());
        if (publicDataArray == null) {
            return;
        }
        for (DataArray publicData : publicDataArray) {
            if (publicData.getArticleTitle().equals(data.getArticleTitle()) && publicData.getCurrentTime() == data.getCurrentTime()) {
                publicDataArray.remove(publicData);
                if (realTimeUserData.getDataArray().size() != 0) {
                    int randomIndex = (int) Math.floor(Math.random() * realTimeUserData.getDataArray().size());
                    publicDataArray.add(realTimeUserData.getDataArray().get(randomIndex));
                }
                String pubJson = gson.toJson(publicDataArray);
                mView.updatePublicData(pubJson);
                break;
            }
        }


    }

    @Override
    public void onCatchRealTimeUserData(String json) {
        if (json != null) {
            realTimeUserData = gson.fromJson(json, DataObject.class);
        }
    }

    @Override
    public void onCatchPublicData(String json) {
        if (json != null) {
            publicDataArray = gson.fromJson(json, new TypeToken<List<DataArray>>() {
            }.getType());
        }
    }

    @Override
    public void onReportItemClickListener(DataArray data) {
        String emailBody = String.format(Locale.getDefault(),"文章 : %s\n還有其他想說的可以打在下方(檢舉文若屬實,我會立即處理請放心,處理完會在回信給您. 請耐心等候:\n",data.getArticleTitle());
        mView.sendEmailToCreator(emailBody);
    }
}
