package com.path.mypath.fragment;

import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;
import java.util.Map;

public interface HomeFragmentVu {
    void setRecyclerView(ArrayList<DataArray> dataArrayList);

    String getNickname();

    String getPhotoUrl();

    void updateUserData(Map<String, Object> map);

    void searchUserPersonalData(String userEmail);

    String getUserEmail();

    void saveUserLikeData(String likeJson, String userEmail);

    void searchCreatorLikeData(String userEmail);
}
