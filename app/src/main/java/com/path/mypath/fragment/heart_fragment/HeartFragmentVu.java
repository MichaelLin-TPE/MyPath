package com.path.mypath.fragment.heart_fragment;

import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;

public interface HeartFragmentVu {
    void setRecyclerView(ArrayList<ArticleLikeNotification> dataArray);

    void intentToSingleViewPage(DataArray homeData);

    void updateLikeData(String json);

    void updateUserData(String userJson);

    void searchFansData(ArticleLikeNotification data);

    String getEmail();

    String getNickname();

    String getPhoto();

    void updateFansData(String userNickname, String fansJson);

    void setView(boolean isShow);

    void showToast(String message);
}
