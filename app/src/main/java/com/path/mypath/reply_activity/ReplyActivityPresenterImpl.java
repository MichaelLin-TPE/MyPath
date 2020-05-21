package com.path.mypath.reply_activity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataReply;
import com.path.mypath.data_parser.FCMData;
import com.path.mypath.data_parser.FCMNotification;
import com.path.mypath.data_parser.FCMObject;
import com.path.mypath.tools.HttpConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReplyActivityPresenterImpl implements ReplyActivityPresenter {

    private ReplyActivityVu mView;

    private ArrayList<DataArray> homeDataArray;

    private DataArray data;

    private Gson gson;
    private String token;

    private static final int REPLY = 4;

    private ArrayList<ArticleLikeNotification> likeArray;

    public ReplyActivityPresenterImpl(ReplyActivityVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onCatchCurrentData(DataArray data) {
        this.data = data;
        mView.setTitleView(data);
        mView.setRecyclerView(data);
    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        if(json != null){
            homeDataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onCatchReplyMessageListener(String message) {
        for (DataArray object : homeDataArray){
            if (object.getArticleTitle().equals(data.getArticleTitle()) && object.getCurrentTime() == data.getCurrentTime()){
                if (object.getReplyArray() != null && object.getReplyArray().size() != 0){
                    DataReply reply = new DataReply();
                    reply.setMessage(message);
                    reply.setPhoto(mView.getUserPhoto());
                    reply.setNickname(mView.getUserNickname());
                    object.getReplyArray().add(reply);
                    object.setReplyCount(object.getReplyArray().size());
                    mView.setRecyclerView(object);
                    break;
                }else {
                    DataReply reply = new DataReply();
                    reply.setMessage(message);
                    reply.setPhoto(mView.getUserPhoto());
                    reply.setNickname(mView.getUserNickname());
                    ArrayList<DataReply> replyArray = new ArrayList<>();
                    replyArray.add(reply);
                    object.setReplyCount(replyArray.size());
                    object.setReplyArray(replyArray);
                    mView.setRecyclerView(object);
                    break;
                }
            }
        }
        String json = gson.toJson(homeDataArray);
        mView.updateHomeData(json);



        //發送推播
        Log.i("Michael","留言者名字 : "+mView.getNickname()+" , 文章創作者 : "+data.getUserNickName());
        if (!mView.getNickname().equals(data.getUserNickName()) && token != null){
            FCMObject data = new FCMObject();
            FCMNotification notification = new FCMNotification();
            notification.setBody(String.format(Locale.getDefault(),"%s 在你的\"%s\"底下留了言 : %s",mView.getNickname(),this.data.getArticleTitle(),message));
            notification.setTitle("留言訊息");
            FCMData fcmData = new FCMData();
            fcmData.setDataContent(String.format(Locale.getDefault(),"%s 在你的\"%s\"底下留了言 : %s",mView.getNickname(),this.data.getArticleTitle(),message));
            fcmData.setDataTitle("留言訊息");
            data.setTo(token);
            data.setData(fcmData);
            data.setNotification(notification);
            String notificationJson = gson.toJson(data);
            Log.i("Michael","發送的JSON : "+notificationJson);

            HttpConnection connection = new HttpConnection();
            connection.startConnection("https://fcm.googleapis.com/fcm/send", notificationJson, new HttpConnection.OnPostNotificationListener() {
                @Override
                public void onSuccessful(String result) {
                    Log.i("Michael",result);
                }

                @Override
                public void onFail(String exception) {
                    Log.i("Michael",exception);
                }
            });

            ArticleLikeNotification likeData = new ArticleLikeNotification();
            likeData.setArticleCreatorName(this.data.getUserNickName());
            likeData.setUserNickname(mView.getUserNickname());
            likeData.setUserEmail(mView.getEmail());
            likeData.setUserPhoto(mView.getUserPhoto());
            likeData.setinviteStatusCode(REPLY);
            likeData.setPressedCurrentTime(System.currentTimeMillis());
            likeData.setInvite(false);
            likeData.setArticleTitle(this.data.getArticleTitle());
            likeData.setArticlePostTime(this.data.getCurrentTime());
            likeData.setReplyMessage(message);
            if (likeArray != null){
                likeArray.add(likeData);
                String likeJson = gson.toJson(likeArray);
                mView.updateLikeData(likeJson);
            }else {
                ArrayList<ArticleLikeNotification> likeArrayList = new ArrayList<>();
                likeArrayList.add(likeData);
                String likeJson = gson.toJson(likeArrayList);
                mView.updateLikeData(likeJson);
            }
        }

    }

    @Override
    public void onReplyMessageSuccessful() {
        String message = "留言成功";
        mView.showToast(message);

    }

    @Override
    public void onCatchUserTokenSuccessful(String token) {
        this.token = token;
    }

    @Override
    public void onCatchLikeDataSuccessful(String json) {
        if (json != null){
            likeArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());
        }
    }
}
