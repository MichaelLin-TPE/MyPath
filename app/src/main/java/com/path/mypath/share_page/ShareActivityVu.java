package com.path.mypath.share_page;

public interface ShareActivityVu {
    void startToRecordMyPath();

    void showNotification();

    void stopToRecordMyPath(String articleContent);

    void catchCurrentLocation();

    void closePage();

    void showNoticeDialog();

    void showFinishDialog(String articleContent);

    void setBtnStartEnable(boolean isEnable);

    void showToast(String message);

    void searchCurrentUserData();

    String getUserNickname();

    String getUserPhotoUrl();

    void updateUserData(String newJson);

    void setBtnStopEnable();
}
