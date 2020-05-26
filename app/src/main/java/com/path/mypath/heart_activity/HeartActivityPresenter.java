package com.path.mypath.heart_activity;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;

public interface HeartActivityPresenter {
    void onBackButtonClickListener();

    void onCatchUserData(DataArray data, String mode);

    void onHeartItemClickListener(String name);

    void onCatchAllUserData(ArrayList<UserData> userDataArray);

    void onCatchFansData(DataObject userData,String mode);
}
