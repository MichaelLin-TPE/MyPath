package com.path.mypath;

import java.util.Map;

public interface MainActivityVu {

    void startGoogleLogin();

    void intentToShareActivity();

    void setUserDataToFireStore(Map<String, Object> userMap, String email, String userJson);

    void checkUserData(String email, String uid);

    void intentToEditActivity();

    void updateUserData(String email);

    void showWaitDialog();
}
