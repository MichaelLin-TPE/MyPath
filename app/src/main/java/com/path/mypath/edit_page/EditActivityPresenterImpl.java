package com.path.mypath.edit_page;

import android.renderscript.Allocation;

import com.google.gson.Gson;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EditActivityPresenterImpl implements EditActivityPresenter {

    private EditActivityVu mView;

    private String downloadUrl ;

    private String message;

    private DataObject userData;

    private Gson gson;

    private boolean isPublicAccount;

    public EditActivityPresenterImpl(EditActivityVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onSaveButtonClickListener(String nickname, String sentence, String email) {
        if (nickname == null || nickname.isEmpty()){
            message = "請輸入暱稱";
            mView.showToast(message);
            return;
        }
        if (sentence == null || sentence.isEmpty()){
            message = "請輸入你的座右銘";
            mView.showToast(message);
            return;
        }
        if (userData.getUserPhoto() == null || userData.getUserPhoto().isEmpty()){
            if (downloadUrl == null || downloadUrl.isEmpty()){
                message = "新增一張照片來增加豐富度呀~";
                mView.showToast(message);
                return;
            }
        }

        if (userData.getDataArray() != null && userData.getDataArray().size() != 0){
            Map<String, Object> map = new HashMap<>();
            map.put("email",email);
            if (downloadUrl != null && !downloadUrl.isEmpty()){
                map.put("photo",downloadUrl);
            }
            map.put("display_name",nickname);
            map.put("sentence",sentence);
            userData.setUserNickname(nickname);
            userData.setSentence(sentence);
            if (downloadUrl != null && !downloadUrl.isEmpty()){
                userData.setUserPhoto(downloadUrl);
                mView.saveUserData(email,downloadUrl,nickname,sentence);
            }else {
                mView.saveUserData(email,userData.getUserPhoto(),nickname,sentence);
            }
            String userJson = gson.toJson(userData);
            Map<String,Object> mapJson = new HashMap<>();
            mapJson.put("user_json",userJson);
            mView.setDataToFirebase(map,mapJson);
        }else {
            Map<String, Object> map = new HashMap<>();
            map.put("email",email);
            map.put("photo",downloadUrl);
            map.put("display_name",nickname);
            map.put("sentence",sentence);

            DataObject data = new DataObject();
            data.setArticleCount(0);
            data.setChasingCount(0);
            data.setDataArray(new ArrayList<>());
            data.setFriendCount(0);
            data.setUserNickname(nickname);
            data.setUserPhoto(downloadUrl);
            data.setChaseArray(new ArrayList<>());
            data.setFansArray(new ArrayList<>());
            data.setSentence(sentence);
            data.setEmail(email);
            data.setPublicAccount(isPublicAccount);
            Gson gson = new Gson();
            String userJson = gson.toJson(data);
            Map<String,Object> mapJson = new HashMap<>();
            mapJson.put("user_json",userJson);
            mView.saveUserData(email,downloadUrl,nickname,sentence);
            mView.setDataToFirebase(map,mapJson);
        }

    }

    @Override
    public void onUserPhotoSelectClickListener() {
        mView.selectPhoto();
    }

    @Override
    public void onCatchUserPhotoByte(byte[] byteArray) {

        mView.uploadPhotoToFirebase(byteArray);
    }

    @Override
    public void onCatchPhotoUrl(String downLoadUrl) {
        this.downloadUrl = downLoadUrl;
    }

    @Override
    public void onDataUpdateSuccessfulListener() {
        mView.intentToShareActivity();
    }

    @Override
    public void onCatchAccountMode(boolean isChecked) {
        isPublicAccount = isChecked;
        if (isChecked){
            message = "公開";
            mView.setAccountInfo(message);
        }else {
            message = "私人";
            mView.setAccountInfo(message);
        }
    }

    @Override
    public void onCatchPersonalData(String json) {
        if (json != null){
            userData = gson.fromJson(json,DataObject.class);
            mView.setView(userData);
        }
    }
}
