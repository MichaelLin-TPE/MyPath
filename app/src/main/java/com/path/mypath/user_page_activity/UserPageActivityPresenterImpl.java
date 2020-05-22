package com.path.mypath.user_page_activity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.FCMData;
import com.path.mypath.data_parser.FCMNotification;
import com.path.mypath.data_parser.FCMObject;
import com.path.mypath.tools.FCMSender;
import com.path.mypath.tools.HttpConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserPageActivityPresenterImpl implements UserPageActivityPresenter {

    private UserPageActivityVu mView;

    private Gson gson;

    private DataObject object;

    private String token;

    private ArrayList<ArticleLikeNotification> notificationDataArray;

    private ArrayList<DataArray> dataArray;

    public UserPageActivityPresenterImpl(UserPageActivityVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onCatchPersonalDataSuccessful(String json) {
        object = gson.fromJson(json,DataObject.class);
        mView.setData(object);
    }

    @Override
    public void onSendButtonClickListener(String creatorEmail) {
        //發送推播
        FCMObject data = new FCMObject();
        FCMNotification notification = new FCMNotification();
        notification.setBody(String.format(Locale.getDefault(),"%s 發送了追蹤邀請給你",mView.getNickname()));
        notification.setTitle("發送邀請");
        FCMData fcmData = new FCMData();
        fcmData.setDataContent(String.format(Locale.getDefault(),"%s 按你的讚唷",mView.getNickname()));
        fcmData.setDataTitle("發送邀請");
        data.setTo(token);
        data.setData(fcmData);
        data.setNotification(notification);
        String json = gson.toJson(data);

        String url = "https://fcm.googleapis.com/fcm/send";
        HttpConnection connection = new HttpConnection();
        connection.startConnection(url, json, new HttpConnection.OnPostNotificationListener() {
            @Override
            public void onSuccessful(String result) {
                Log.i("Michael","推送成功 : "+result);
            }

            @Override
            public void onFail(String exception) {
                Log.i("Michael","推送失敗 : "+exception);
            }
        });

        ArticleLikeNotification notifo = new ArticleLikeNotification();
        notifo.setPressedCurrentTime(System.currentTimeMillis());
        notifo.setUserPhoto(mView.getUserPhoto());
        notifo.setUserNickname(mView.getNickname());
        notifo.setUserEmail(mView.getUserEmail());
        notifo.setArticleTitle("");
        notifo.setArticlePostTime(0);
        notifo.setArticleCreatorName(creatorEmail);
        notifo.setInvite(true);
        notifo.setinviteStatusCode(3);
        if (notificationDataArray != null && notificationDataArray.size() != 0){
            boolean isDataRepeat = false;
            for (ArticleLikeNotification dataNoti : notificationDataArray){
                if (dataNoti.getArticleCreatorName().equals(notifo.getArticleCreatorName()) && dataNoti.getUserNickname().equals(notifo.getUserNickname()) && dataNoti.getArticleTitle().isEmpty()){
                    dataNoti.setinviteStatusCode(3);
                    dataNoti.setPressedCurrentTime(System.currentTimeMillis());
                    isDataRepeat = true;
                }
            }
            if (!isDataRepeat){
                notificationDataArray.add(notifo);
            }

        }else {
            notificationDataArray = new ArrayList<>();
            notificationDataArray.add(notifo);
        }
        String likeJson = gson.toJson(notificationDataArray);

        mView.sendChasePermission(likeJson);
    }

    @Override
    public void onCatchUserLikeDataSuccessful(String json) {
        notificationDataArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());
    }

    @Override
    public void onCatchUserDataSuccessful(String token) {
        this.token = token;
    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        if (json != null){
            dataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onMapItemClickListener(DataArray locationArray) {
        boolean isDataFound = false;
        if (dataArray != null){
            for (DataArray data : dataArray){
                if (data.getArticleTitle().equals(locationArray.getArticleTitle()) && data.getCurrentTime() == locationArray.getCurrentTime()){
                    mView.intentToSingleViewActivity(data);
                    isDataFound = true;
                    break;
                }
            }
            if (!isDataFound){
                mView.intentToSingleViewActivity(locationArray);
            }

        }
    }
}
