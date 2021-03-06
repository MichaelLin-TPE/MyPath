package com.path.mypath.edit_page;

import com.path.mypath.data_parser.DataObject;

import java.util.Map;

public interface EditActivityVu {
    void selectPhoto();

    void showToast(String message);

    void setDataToFirebase(Map<String, Object> map, Map<String, Object> mapJson);

    void uploadPhotoToFirebase(byte[] byteArray);

    void intentToShareActivity();

    void saveUserData(String email, String downloadUrl, String nickname, String sentence);

    void setAccountInfo(String message);

    void setView(DataObject userData);
}
