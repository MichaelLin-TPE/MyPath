package com.path.mypath.share_page;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;

public class ShareActivityPresenterImpl implements ShareActivityPresenter {

    private ShareActivityVu mView;

    private String message;

    private String articleContent;

    private ArrayList<LatLng> latLngArrayList;

    private Gson gson;

    public ShareActivityPresenterImpl(ShareActivityVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onStopToRecordButtonClickListener(String articleContent) {

        if (articleContent != null){
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
        mView.closePage();
    }

    @Override
    public void onRecordConfirmClickListener() {
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
        mView.setBtnStopEnable();
        mView.stopToRecordMyPath(articleContent);
    }

    @Override
    public void onCatchCurrentUserData(String articleContent, ArrayList<LatLng> latLngArrayList) {
        this.articleContent = articleContent;
        this.latLngArrayList = latLngArrayList;
        mView.searchCurrentUserData();
    }

    @Override
    public void onCatchCurrentUserDataSuccessful(String json) {
        DataObject data = gson.fromJson(json,DataObject.class);
        if (data != null){
            DataArray dataArray = new DataArray();
            dataArray.setArticleTitle(articleContent);
            dataArray.setLocationArray(latLngArrayList);
            dataArray.setCurrentTime(System.currentTimeMillis());
            dataArray.setUserNickName(mView.getUserNickname());
            dataArray.setUserPhoto(mView.getUserPhotoUrl());
            dataArray.setHeartCount(0);
            dataArray.setReplyCount(0);
            dataArray.setHeartPressUsers(new ArrayList<>());
            dataArray.setReplyArray(new ArrayList<>());
            ArrayList<DataArray> dataArrays = new ArrayList<>();
            dataArrays.add(dataArray);
            if (data.getDataArray() == null || data.getDataArray().size() == 0){
                data.setDataArray(dataArrays);
            }else {
                data.getDataArray().add(dataArray);
            }
            int articleCount = data.getDataArray().size();
            data.setArticleCount(articleCount);
            String newJson = gson.toJson(data);
            if (newJson != null){
                mView.updateUserData(newJson);
            }
        }
    }

    @Override
    public void onUpdateUserDataSuccessful() {
        message = "新增成功";
        mView.showToast(message);
    }

    @Override
    public void onUpdateUserDataFailure() {
        message = "資料新增失敗,請確認網路狀態再重新試一次.";
        mView.showToast(message);
    }
}
