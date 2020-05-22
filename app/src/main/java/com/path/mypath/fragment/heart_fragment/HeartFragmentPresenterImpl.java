package com.path.mypath.fragment.heart_fragment;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.FansData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HeartFragmentPresenterImpl implements HeartFragmentPresenter {

    private HeartFragmentVu mView;

    private Gson gson;

    private ArrayList<DataArray> dataArray;

    private ArrayList<ArticleLikeNotification> likeArray;

    private DataObject object,fansObject;

    private static final int SEND = 3;

    private static final int REJECTED = 2;

    private static final int ACCEPT = 1;

    public HeartFragmentPresenterImpl(HeartFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchLikeDataSuccessful(String json) {
        if (json != null){
            likeArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());
            if (likeArray != null){
                mView.setView(true);
                Collections.sort(likeArray, new Comparator<ArticleLikeNotification>() {
                    @Override
                    public int compare(ArticleLikeNotification o1, ArticleLikeNotification o2) {
                        return (int) (o2.getPressedCurrentTime() - o1.getPressedCurrentTime());
                    }
                });
                mView.setRecyclerView(likeArray);
            }
        }else {
            Log.i("Michael","likeJson = null");
            mView.setView(false);
        }
    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        if (json != null){
            dataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onHeartLikeItemClickListener(ArticleLikeNotification data) {
        for (DataArray homeData : dataArray){
            if (homeData.getArticleTitle().equals(data.getArticleTitle())&& homeData.getCurrentTime() == data.getArticlePostTime()){
                mView.intentToSingleViewPage(homeData);
                break;
            }
        }
    }

    @Override
    public void onCancelButtonClickListener(ArticleLikeNotification data) {

        data.setinviteStatusCode(REJECTED);
        int index = 0;
        for (ArticleLikeNotification notification : likeArray){
            if (notification.getUserNickname().equals(data.getUserNickname()) && notification.getArticleCreatorName().equals(data.getArticleCreatorName()) && notification.isInvite() && notification.getPressedCurrentTime() == data.getPressedCurrentTime()){
                likeArray.set(index,data);
                break;
            }
            index ++;
        }
        String json = gson.toJson(likeArray);
        mView.updateLikeData(json);
    }

    @Override
    public void onAcceptButtonClickListener(ArticleLikeNotification data) {
        data.setinviteStatusCode(ACCEPT);
        int index = 0;
        for (ArticleLikeNotification notification : likeArray){
            if (notification.getUserNickname().equals(data.getUserNickname()) && notification.getArticleCreatorName().equals(data.getArticleCreatorName()) && notification.isInvite() && notification.getPressedCurrentTime() == data.getPressedCurrentTime()){
                likeArray.set(index,data);
                if (object.getFansArray() != null && object.getFansArray().size() != 0){
                    FansData fansData = new FansData();
                    fansData.setEmail(notification.getUserEmail());
                    fansData.setNickname(notification.getUserNickname());
                    fansData.setPhoto(notification.getUserPhoto());
                    object.getFansArray().add(fansData);
                    object.setFriendCount(object.getFansArray().size());
                }else {
                    FansData fansData = new FansData();
                    fansData.setEmail(notification.getUserEmail());
                    fansData.setNickname(notification.getUserNickname());
                    fansData.setPhoto(notification.getUserPhoto());
                    ArrayList<FansData> fansDataArrayList = new ArrayList<>();
                    fansDataArrayList.add(fansData);
                    object.setFansArray(fansDataArrayList);
                    object.setFriendCount(object.getFansArray().size());
                }
                mView.searchFansData(data);
                break;
            }
            index ++;
        }


        String userJson =gson.toJson(object);
        mView.updateUserData(userJson);
        String json = gson.toJson(likeArray);
        mView.updateLikeData(json);
    }

    @Override
    public void onCatchUserDataSuccessful(String json) {
        object = gson.fromJson(json,DataObject.class);
    }

    @Override
    public void onCatchFansData(String json, ArticleLikeNotification data) {
        FansData fansData = new FansData();
        fansData.setEmail(mView.getEmail());
        fansData.setNickname(mView.getNickname());
        fansData.setPhoto(mView.getPhoto());
        fansObject = gson.fromJson(json,DataObject.class);
        if (fansObject != null){
            if (fansObject.getChaseArray() != null && fansObject.getChaseArray().size() != 0){
                fansObject.getChaseArray().add(fansData);
                fansObject.setChasingCount(fansObject.getChaseArray().size());
            }else {
                ArrayList<FansData> fansDataArrayList = new ArrayList<>();
                fansDataArrayList.add(fansData);
                fansObject.setChaseArray(fansDataArrayList);
                fansObject.setChasingCount(fansObject.getChaseArray().size());
            }
            String fansJson = gson.toJson(fansObject);
            mView.updateFansData(data.getUserEmail(),fansJson);
        }

    }
}
