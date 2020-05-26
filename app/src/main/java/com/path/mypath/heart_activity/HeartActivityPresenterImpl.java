package com.path.mypath.heart_activity;

import android.util.Log;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;

public class HeartActivityPresenterImpl implements HeartActivityPresenter {

    private HeartActivityVu mView;

    private ArrayList<UserData> userDataArray;

    private String email;

    private static final String CHASING = "chasing";

    private static final String FANS = "fans";

    private static final String HEART = "heart";

    public HeartActivityPresenterImpl(HeartActivityVu mView) {
        this.mView = mView;
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onCatchUserData(DataArray data, String mode) {
        if (data != null) {
            mView.setTitle(mode);
            mView.setRecyclerView(data.getHeartPressUsers());
        }
    }

    @Override
    public void onHeartItemClickListener(String name) {
        if (userDataArray != null && userDataArray.size() != 0){
            for (UserData data : userDataArray){
                if (data.getNickname().equals(name)){
                    email = data.getEmail();
                    break;
                }
            }
            if (email != null && !email.isEmpty()){
                mView.intentToUserPageActivity(email);
            }else {
                Log.i("Michael","email == null");
            }
        }else {
            Log.i("Michael","userDataArray == null ");
        }

    }

    @Override
    public void onCatchAllUserData(ArrayList<UserData> userDataArray) {
        this.userDataArray = userDataArray;
    }

    @Override
    public void onCatchFansData(DataObject userData,String mode) {
        if (mode.equals(CHASING)){
            mView.setNewRecyclerView(userData.getChaseArray());
        }else {
            mView.setNewRecyclerView(userData.getFansArray());
        }
    }
}
