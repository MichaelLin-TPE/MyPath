package com.path.mypath.fragment;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.data_parser.FCMData;
import com.path.mypath.data_parser.FCMNotification;
import com.path.mypath.data_parser.FCMObject;
import com.path.mypath.tools.FCMSender;
import com.path.mypath.tools.HttpConnection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HomeFragmentPresenterImpl implements HomeFragmentPresenter {
    private HomeFragmentVu mView;

    private Gson gson;

    private DataArray articleData;

    private ArrayList<DataArray> dataArrayList , realTimeDataArray;

    public HomeFragmentPresenterImpl(HomeFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchUserDataSuccessful(String json) {
        if (json != null){
            dataArrayList = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
            Collections.sort(dataArrayList, new Comparator<DataArray>() {
                @Override
                public int compare(DataArray o1, DataArray o2) {
                    return (int) (o2.getCurrentTime() - o1.getCurrentTime());
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
        if (isCheck){
            int currentHeartCount = articleData.getHeartCount() - 1;
            articleData.setHeartCount(currentHeartCount);
            dataArrayList.get(position).getHeartPressUsers().remove(selectIndex);
        }else {
            int currentHeartCount = articleData.getHeartCount() + 1;
            DataUserPresHeart data = new DataUserPresHeart();
            data.setPhotoUrl(mView.getPhotoUrl());
            data.setName(mView.getNickname());
            articleData.setHeartCount(currentHeartCount);
            articleData.getHeartPressUsers().add(data);
            dataArrayList.set(position,articleData);
        }

        String json = gson.toJson(dataArrayList);

        Map<String,Object> map = new HashMap<>();
        map.put("json",json);

        mView.updateUserData(map);

        //發送推播
        if (!isCheck){
            Log.i("Michael","準備發送推播");
            if (!articleData.getUserNickName().equals(mView.getNickname())){
                mView.searchUserPersonalData(articleData.getUserEmail());

                //發送點讚訊息給發文者
                mView.searchCreatorLikeData(articleData.getUserEmail());

            }
        }


    }

    @Override
    public void onCatchRealTimeData(String json) {
        if (json != null){
            realTimeDataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onCatchFCMTokenSuccessful(String token) {
        FCMObject data = new FCMObject();
        FCMNotification notification = new FCMNotification();
        notification.setBody(String.format(Locale.getDefault(),"%s 按你的讚唷",mView.getNickname()));
        notification.setTitle("點讚訊息");
        FCMData fcmData = new FCMData();
        fcmData.setDataContent(String.format(Locale.getDefault(),"%s 按你的讚唷",mView.getNickname()));
        fcmData.setDataTitle("點讚訊息");
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

    @Override
    public void onCatchCreatorLikeData(String json) {

        ArticleLikeNotification notification = new ArticleLikeNotification();
        notification.setArticleCreatorName(articleData.getUserNickName());
        notification.setArticlePostTime(articleData.getCurrentTime());
        notification.setArticleTitle(articleData.getArticleTitle());
        notification.setUserEmail(mView.getUserEmail());
        notification.setUserNickname(mView.getNickname());
        notification.setUserPhoto(mView.getPhotoUrl());
        notification.setPressedCurrentTime(System.currentTimeMillis());

        if (json != null){
            ArrayList<ArticleLikeNotification> dataArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());
            if (dataArray != null){
                boolean isDataRepeat = false;
                for (ArticleLikeNotification data : dataArray){
                    if (data.getArticleTitle().equals(notification.getArticleTitle()) && data.getArticlePostTime() == notification.getArticlePostTime()){
                        isDataRepeat = true;
                        break;
                    }
                }
                if (isDataRepeat){
                    return;
                }
                dataArray.add(notification);
                String likeJson = gson.toJson(dataArray);
                mView.saveUserLikeData(likeJson,articleData.getUserEmail());
            }
        }else {

            ArrayList<ArticleLikeNotification> notificationArray = new ArrayList<>();
            notificationArray.add(notification);
            String likeJson = gson.toJson(notificationArray);

            mView.saveUserLikeData(likeJson,articleData.getUserEmail());
        }
    }
}
