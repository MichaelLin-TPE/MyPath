package com.path.mypath.search_user_activity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.FCMData;
import com.path.mypath.data_parser.FCMNotification;
import com.path.mypath.data_parser.FCMObject;
import com.path.mypath.heart_activity.UserData;
import com.path.mypath.tools.HttpConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SearchUserPresenterImpl implements SearchUserPresenter{
    private SearchUserVu mView;

    private ArrayList<ArrayList<ArticleLikeNotification>> likeArray;

    private ArrayList<UserData> dataArray;

    private Gson gson;

    private static final int SEND = 3;

    public SearchUserPresenterImpl(SearchUserVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchUserDataArray(ArrayList<UserData> dataArray) {
        this.dataArray = dataArray;

    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onPhotoClickListener(UserData data) {
        mView.intentToUserPage(data.getEmail());
    }

    @Override
    public void onCatchLikeData(ArrayList<String> likJsonArray) {
        likeArray = new ArrayList<>();
        for (String json : likJsonArray){
            ArrayList<ArticleLikeNotification> dataArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());
            likeArray.add(dataArray);
        }
        Log.i("Michael","likeArray size : "+likeArray.size());
        mView.showSearchNoUserView(false);
        mView.setRecyclerView(dataArray,likeArray);

    }

    @Override
    public void onChaseButtonClickListener(UserData data) {

        boolean isUserFound = false;
        ArticleLikeNotification likeData = new ArticleLikeNotification();
        likeData.setPressedCurrentTime(System.currentTimeMillis());
        likeData.setUserPhoto(mView.getPhoto());
        likeData.setUserNickname(mView.getNickname());
        likeData.setUserEmail(mView.getEmail());
        likeData.setArticleTitle("");
        likeData.setArticlePostTime(0);
        likeData.setArticleCreatorName(data.getNickname());
        likeData.setInvite(true);
        likeData.setinviteStatusCode(SEND);
        likeData.setReplyMessage("");
        /**
         * 如果本來就有資料的化
         */
        for (ArrayList<ArticleLikeNotification> object : likeArray){
            for (ArticleLikeNotification notification : object){
                if (notification.getArticleCreatorName().equals(data.getNickname())){

                    object.add(likeData);
                    String json = gson.toJson(object);
                    mView.updateLikeUserData(json,data.getEmail());
                    isUserFound = true;
                    break;
                }
            }
        }
        /**
         * 如果沒資料的化
         */
        if (!isUserFound){
            ArrayList<ArticleLikeNotification> notificationArray = new ArrayList<>();
            notificationArray.add(likeData);
            String json = gson.toJson(notificationArray);
            mView.updateLikeUserData(json,data.getEmail());
        }

        mView.searchUserToken(data.getEmail());


    }

    @Override
    public void onCatchUserToken(String token) {
        //發送推播
        FCMObject data = new FCMObject();
        FCMNotification notification = new FCMNotification();
        notification.setBody(String.format(Locale.getDefault(),"%s 發送了追蹤邀請給你",mView.getNickname()));
        notification.setTitle("發送邀請");
        FCMData fcmData = new FCMData();
        fcmData.setDataContent(String.format(Locale.getDefault(),"%s 發送了追蹤邀請給你",mView.getNickname()));
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
    }

    @Override
    public void onEditActionSearchListener(String content) {
        if (dataArray == null || dataArray.size() == 0){
            return;
        }
        ArrayList<UserData> replaceDataArray = new ArrayList<>();
        for (UserData data : dataArray){
            if (data.getNickname().equals(content)){
                replaceDataArray.add(data);
                break;
            }
            if (data.getEmail().equals(content)){
                replaceDataArray.add(data);
                break;
            }
        }
        if (replaceDataArray.size() == 0){
            mView.showSearchNoUserView(true);
            mView.setRecyclerView(replaceDataArray,likeArray);
            return;
        }
        mView.showSearchNoUserView(false);
        mView.setRecyclerView(replaceDataArray,likeArray);

    }
}
