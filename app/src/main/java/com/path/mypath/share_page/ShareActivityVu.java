package com.path.mypath.share_page;

import com.path.mypath.data_parser.DataArray;

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

    void searchPublicData(DataArray dataArray);

    void updatePublicJson(String pubJson);

    void showPublicConfirmDialog(DataArray dataArray);

    void searchHomeData(DataArray dataArray);

    void updateHomeData(String homeJson);

    void showIsRecordingDialog();

    void stopRecord();

    String getUserEmail();
}
