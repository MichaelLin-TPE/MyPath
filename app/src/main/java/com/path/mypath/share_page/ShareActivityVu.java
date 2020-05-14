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

    String getUserNickname();

    String getUserPhotoUrl();

    void updateUserData(String newJson);

    void setBtnStopEnable();

    void updatePublicJson(String pubJson);


    void searchHomeData(DataArray dataArray);

    void updateHomeData(String homeJson);

    void showIsRecordingDialog();

    void stopRecord();

    String getUserEmail();

    void showDataModeDialog();
}
