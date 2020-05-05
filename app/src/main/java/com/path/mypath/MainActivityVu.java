package com.path.mypath;

import java.util.Map;

public interface MainActivityVu {

    void startGoogleLogin();

    void intentToShareActivity();

    void setUserDataToFireStore(Map<String, Object> userMap, String email);

    void checkUserData(String email, String uid);

    void intentToEditActivity();
}
