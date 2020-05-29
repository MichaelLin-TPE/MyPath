package com.path.mypath.search_user_activity;

import com.path.mypath.heart_activity.UserData;

import java.util.ArrayList;

public interface SearchUserPresenter {
    void onCatchUserDataArray(ArrayList<UserData> dataArray);

    void onBackButtonClickListener();

    void onPhotoClickListener(UserData data);

    void onCatchLikeData(ArrayList<String> likJsonArray);

    void onChaseButtonClickListener(UserData data);

    void onCatchUserToken(String token);
}
