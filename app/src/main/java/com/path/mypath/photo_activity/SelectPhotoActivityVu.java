package com.path.mypath.photo_activity;

import java.util.ArrayList;

public interface SelectPhotoActivityVu {
    void uploadPhotoToFirebase(ArrayList<byte[]> photoArray);

    void showToast(String message);

    void showPublicConfirmDialog();

    void setViewPager(ArrayList<byte[]> byteArray);

    void showSelectPhotoPage();

    void showWaitingDialog(String message);

    void closePage();

    void setFinishButtonEnable(boolean enable);

    String getUserPhoto();

    String getUserEmail();

    String getUserNickname();

    void updateUserData(String json);

    void updateHomeData(String homeJson);

    void updateSearchData(String searchJson);
}
