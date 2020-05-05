package com.path.mypath;

import java.util.HashMap;
import java.util.Map;


public class MainActivityPresenterImpl implements MainActivityPresenter {

    private MainActivityVu mView;

    public MainActivityPresenterImpl(MainActivityVu mView) {
        this.mView = mView;
    }


    @Override
    public void onButtonLoginClickListener() {
        mView.startGoogleLogin();
    }

    @Override
    public void onCatchCurrentUser() {
        mView.intentToShareActivity();
    }

    @Override
    public void onRegisterAccountToFirebase(String email, String uid) {
        mView.checkUserData(email,uid);
    }

    @Override
    public void onSetFirebaseDataSuccessful() {
        mView.intentToEditActivity();
    }

    @Override
    public void onCatchNoData(String email, String uid) {
        Map<String,Object> userMap = new HashMap<>();
        userMap.put("email",email);
        userMap.put("uid",uid);
        userMap.put("display_name","");
        userMap.put("photo","");
        userMap.put("sentence","");
        mView.setUserDataToFireStore(userMap,email);
    }
}
