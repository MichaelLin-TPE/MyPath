package com.path.mypath.search_user_activity;

import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.heart_activity.UserData;

import java.util.ArrayList;

public interface SearchUserVu {
    void setRecyclerView(ArrayList<UserData> dataArray, ArrayList<ArrayList<ArticleLikeNotification>> likJsonArray);

    void closePage();

    void intentToUserPage(String email);

    String getNickname();

    String getPhoto();

    String getEmail();

    void updateLikeUserData(String json, String email);

    void searchUserToken(String email);
}
