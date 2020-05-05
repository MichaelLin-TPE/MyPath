package com.path.mypath.edit_page;

import android.renderscript.Allocation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EditActivityPresenterImpl implements EditActivityPresenter {

    private EditActivityVu mView;

    private String downloadUrl ;

    private String message;

    public EditActivityPresenterImpl(EditActivityVu mView) {
        this.mView = mView;
    }

    @Override
    public void onSaveButtonClickListener(String nickname, String sentence, String email) {
        if (nickname == null){
            message = "請輸入暱稱";
            mView.showToast(message);
            return;
        }
        if (sentence == null){
            message = "請輸入你的座右銘";
            mView.showToast(message);
            return;
        }
        if (downloadUrl == null){
            message = "新增一張照片來增加豐富度呀~";
            mView.showToast(message);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("email",email);
        map.put("photo",downloadUrl);
        map.put("display_name",nickname);
        map.put("sentence",sentence);
        mView.setDataToFirebase(map);
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
}