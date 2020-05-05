package com.path.mypath.edit_page;

import java.util.Map;

public interface EditActivityVu {
    void selectPhoto();

    void showToast(String message);

    void setDataToFirebase(Map<String, Object> map);

    void uploadPhotoToFirebase(byte[] byteArray);

    void intentToShareActivity();
}
