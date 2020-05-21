package com.path.mypath.share_page;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;
import java.util.List;

public class ShareActivityPresenterImpl implements ShareActivityPresenter {

    private ShareActivityVu mView;

    private String message;

    private String articleContent;

    private ArrayList<LatLng> latLngArrayList;

    private Gson gson;

    private boolean isRecording;

    private double distance;

    private DataObject userData;

    private ArrayList<DataArray> homeDataArray,searchDataArray;

    public ShareActivityPresenterImpl(ShareActivityVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onStopToRecordButtonClickListener(String articleContent) {

        if (articleContent != null && !articleContent.isEmpty()){
            mView.showFinishDialog(articleContent);
        }else {
            message = "下方輸入一點你去的地方吧~*";
            mView.showToast(message);
        }



    }

    @Override
    public void onStartToRecordButtonClickListener() {
        mView.showNoticeDialog();

    }

    @Override
    public void onLocationPermissionGranted() {
        mView.catchCurrentLocation();
    }

    @Override
    public void onBackIconClickListener() {
        if (isRecording){
            mView.showIsRecordingDialog();
        }else {
            mView.closePage();
        }
    }

    @Override
    public void onRecordConfirmClickListener() {
        isRecording = true;
        mView.setBtnStartEnable(false);
        mView.startToRecordMyPath();
        mView.showNotification();
    }

    @Override
    public void onRecordCancelClickListener() {
        //不用做任何事
        mView.setBtnStartEnable(true);
    }

    @Override
    public void onFinishRecordConfimClickListener(String articleContent) {
        isRecording = false;
        mView.setBtnStopEnable();
        mView.stopToRecordMyPath(articleContent);
    }

    @Override
    public void onCatchCurrentUserData(String articleContent, ArrayList<LatLng> latLngArrayList, double distance) {
        this.articleContent = articleContent;
        this.latLngArrayList = latLngArrayList;
        this.distance = distance;
        mView.setBtnStartEnable(true);
        mView.showDataModeDialog();
    }



    @Override
    public void onUpdateUserDataSuccessful() {
        message = "新增成功";
        mView.showToast(message);
        mView.closePage();
    }

    @Override
    public void onUpdateUserDataFailure() {
        message = "資料新增失敗,請確認網路狀態再重新試一次.";
        mView.showToast(message);
    }

    @Override
    public void onCatchHomeData(String json, DataArray dataArray) {
        if (json != null){
            ArrayList<DataArray> dataArrayList = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
            dataArrayList.add(dataArray);
            String homeJson = gson.toJson(dataArrayList);
            mView.updateHomeData(homeJson);
        }else {
            ArrayList<DataArray> dataArrayList = new ArrayList<>();
            dataArrayList.add(dataArray);
            String homeJson = gson.toJson(dataArrayList);
            mView.updateHomeData(homeJson);
        }
    }

    @Override
    public void onUpdateHomeDataSuccessful() {
        message = "此路徑已設成公開";
        mView.showToast(message);
    }

    @Override
    public void onBackConfirmClickListener() {
        mView.stopRecord();
        mView.closePage();

    }

    @Override
    public void onPublicButtonClickListener() {
        //公開
        DataArray dataArray = new DataArray();
        dataArray.setArticleTitle(articleContent);
        dataArray.setLocationArray(latLngArrayList);
        dataArray.setCurrentTime(System.currentTimeMillis());
        dataArray.setUserNickName(mView.getUserNickname());
        dataArray.setUserPhoto(mView.getUserPhotoUrl());
        dataArray.setUserEmail(mView.getUserEmail());
        dataArray.setHeartCount(0);
        dataArray.setReplyCount(0);
        dataArray.setDistance(distance);
        dataArray.setPhotoArray(new ArrayList<>());
        dataArray.setHeartPressUsers(new ArrayList<>());
        dataArray.setReplyArray(new ArrayList<>());

        //主資料更新
        if (homeDataArray != null && homeDataArray.size() != 0){
            homeDataArray.add(dataArray);
        }else {
            homeDataArray = new ArrayList<>();
            homeDataArray.add(dataArray);
        }
        String json = gson.toJson(homeDataArray);
        mView.updateHomeData(json);


        //搜尋資料更新
        if (searchDataArray != null && searchDataArray.size() != 0){
            int index = 0;
            boolean isDataFound = false;
            for (DataArray data : searchDataArray){
                if (data.getUserNickName().equals(mView.getUserNickname())){
                    searchDataArray.set(index,dataArray);
                    isDataFound = true;
                }
                index++;
            }
            if (!isDataFound){
                searchDataArray.add(dataArray);
            }
        }else {
            searchDataArray = new ArrayList<>();
            searchDataArray.add(dataArray);
        }
        String searchJson = gson.toJson(searchDataArray);
        mView.updatePublicJson(searchJson);

        //個人資料更新
        if (userData != null && userData.getDataArray() != null && userData.getDataArray().size() != 0){
            userData.getDataArray().add(dataArray);
        }else {
            ArrayList<DataArray> dataArrays = new ArrayList<>();
            dataArrays.add(dataArray);
            userData.setDataArray(dataArrays);
        }

        int articleCount = userData.getArticleCount() + 1;
        userData.setArticleCount(articleCount);

        String userJson = gson.toJson(userData);
        mView.updateUserData(userJson);
    }

    @Override
    public void onPrivateButtonClickListener() {
        //私人
        DataArray dataArray = new DataArray();
        dataArray.setArticleTitle(articleContent);
        dataArray.setLocationArray(latLngArrayList);
        dataArray.setCurrentTime(System.currentTimeMillis());
        dataArray.setUserNickName(mView.getUserNickname());
        dataArray.setUserPhoto(mView.getUserPhotoUrl());
        dataArray.setUserEmail(mView.getUserEmail());
        dataArray.setHeartCount(0);
        dataArray.setReplyCount(0);
        dataArray.setDistance(distance);
        dataArray.setPhotoArray(new ArrayList<>());
        dataArray.setHeartPressUsers(new ArrayList<>());
        dataArray.setReplyArray(new ArrayList<>());
        if (userData != null && userData.getDataArray() != null && userData.getDataArray().size() != 0){
            userData.getDataArray().add(dataArray);
        }else {
            ArrayList<DataArray> dataArrays = new ArrayList<>();
            dataArrays.add(dataArray);
            userData.setDataArray(dataArrays);
        }

        int articleCount = userData.getArticleCount() + 1;
        userData.setArticleCount(articleCount);

        String json = gson.toJson(userData);
        mView.updateUserData(json);
    }

    @Override
    public void onCatchUserDataSuccessful(String json) {
        if (json != null){
            userData = gson.fromJson(json,DataObject.class);
        }
    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        if (json != null){
            homeDataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onCatchSearchDataSuccessful(String json) {
        if (json != null){
            searchDataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }
}
