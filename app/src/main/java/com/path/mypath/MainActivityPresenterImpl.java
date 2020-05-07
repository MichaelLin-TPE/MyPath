package com.path.mypath;

import com.google.gson.Gson;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;
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
    public void onCatchCurrentUser(String email) {
        mView.updateUserData(email);
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


        DataObject data = new DataObject();
        data.setArticleCount(0);
        data.setChasingCount(0);
        data.setDataArray(new ArrayList<>());
        data.setFriendCount(0);
        data.setUserNickname("");
        data.setUserPhoto("");
        Gson gson = new Gson();
        String userJson = gson.toJson(data);

        mView.setUserDataToFireStore(userMap,email,userJson);
    }
}
