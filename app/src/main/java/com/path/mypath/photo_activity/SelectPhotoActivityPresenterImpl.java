package com.path.mypath.photo_activity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectPhotoActivityPresenterImpl implements SelectPhotoActivityPresenter {

    private SelectPhotoActivityVu mView;

    private boolean isUploading;

    private String message;

    private ArrayList<byte[]> byteArray;

    private DataObject userData;

    private ArrayList<DataArray> homeDataArray,searchDataArray;

    private String articleTitle;

    private Gson gson;

    public SelectPhotoActivityPresenterImpl(SelectPhotoActivityVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchUserPhotoByte(ArrayList<byte[]> byteArray) {
        mView.setViewPager(byteArray);
        this.byteArray = byteArray;
    }

    @Override
    public void onCatchUploadPhotoFailListener(String exception, int photoIndex) {
        message = String.format(Locale.getDefault(),"第 %d 張照片上傳失敗,請重新上傳一次\nErrorCode : %s",photoIndex,exception);
        mView.showToast(message);
    }

    @Override
    public void onShowFinishInformation() {
        isUploading = false;
        message = "照片上傳完成";
        mView.showToast(message);
        mView.showPublicConfirmDialog();
    }

    @Override
    public void onPublicConfirmClickListener(ArrayList<String> photoUrlArray, String articleTitle) {
        //公開資料
        DataArray articleData = new DataArray();
        articleData.setDistance(0);
        articleData.setUserPhoto(mView.getUserPhoto());
        articleData.setUserEmail(mView.getUserEmail());
        articleData.setUserNickName(mView.getUserNickname());
        articleData.setHeartPressUsers(new ArrayList<>());
        articleData.setReplyArray(new ArrayList<>());
        articleData.setHeartCount(0);
        articleData.setReplyCount(0);
        articleData.setArticleTitle(this.articleTitle);
        articleData.setCurrentTime(System.currentTimeMillis());
        articleData.setLocationArray(new ArrayList<>());
        articleData.setPhotoArray(photoUrlArray);

        //更新主資料
        if (homeDataArray != null){
            homeDataArray.add(articleData);
        }else {
            homeDataArray = new ArrayList<>();
            homeDataArray.add(articleData);
        }
        String homeJson = gson.toJson(homeDataArray);
        mView.updateHomeData(homeJson);

        //更新搜尋頁面資料
        if (searchDataArray != null){
            int index = 0;
            for (DataArray data : searchDataArray){
                if (data.getUserNickName().equals(articleData.getUserNickName())){
                    searchDataArray.set(index,articleData);
                }
                index ++;
            }
        }else {
            searchDataArray = new ArrayList<>();
            searchDataArray.add(articleData);
        }
        String searchJson = gson.toJson(searchDataArray);
        mView.updateSearchData(searchJson);

        //更新個人資料
        if (userData.getDataArray() != null){
            userData.getDataArray().add(articleData);
        }else {
            ArrayList<DataArray> userDataArray = new ArrayList<>();
            userDataArray.add(articleData);
            userData.setDataArray(userDataArray);
        }
        int articleCount = userData.getArticleCount() + 1;
        userData.setArticleCount(articleCount);
        String json = gson.toJson(userData);
        mView.updateUserData(json);
    }

    @Override
    public void onPrivateConfirmClickListener(ArrayList<String> photoUrlArray, String articleTitle) {
        //私人資料
        DataArray articleData = new DataArray();
        articleData.setDistance(0);
        articleData.setUserPhoto(mView.getUserPhoto());
        articleData.setUserEmail(mView.getUserEmail());
        articleData.setUserNickName(mView.getUserNickname());
        articleData.setHeartPressUsers(new ArrayList<>());
        articleData.setReplyArray(new ArrayList<>());
        articleData.setHeartCount(0);
        articleData.setReplyCount(0);
        articleData.setArticleTitle(this.articleTitle);
        articleData.setCurrentTime(System.currentTimeMillis());
        articleData.setLocationArray(new ArrayList<>());
        articleData.setPhotoArray(photoUrlArray);

        if (userData.getDataArray() != null){
            userData.getDataArray().add(articleData);
        }else {
            ArrayList<DataArray> userDataArray = new ArrayList<>();
            userDataArray.add(articleData);
            userData.setDataArray(userDataArray);
        }
        String json = gson.toJson(userData);
        mView.updateUserData(json);
    }

    @Override
    public void onAddButtonClickListener() {
        mView.showSelectPhotoPage();
    }

    @Override
    public void onBackButtonClickListener() {
        if (isUploading){
            message = "資料還在上傳中,確定要退出嗎?";
            mView.showWaitingDialog(message);
        }else {
            mView.closePage();
        }
    }

    @Override
    public void onBackConfirmClickListener() {
        mView.closePage();
    }

    @Override
    public void onFinishButtonClickListener(String articleTitle) {
        this.articleTitle = articleTitle;
        if (articleTitle != null && !articleTitle.isEmpty()){
            isUploading = true;
            message = "上傳照片中.....請稍後";
            mView.showToast(message);
            mView.uploadPhotoToFirebase(byteArray);
            mView.setFinishButtonEnable(false);
        }else {
            message = "請描述一下你去了甚麼地方...";
            mView.showToast(message);
        }

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
    public void onUpdateSuccessful() {
        message = "更新成功";
        mView.showToast(message);
        mView.closePage();
    }

    @Override
    public void onCatchSearchPageDataSuccessful(String json) {
        if (json != null){
            searchDataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }
}
