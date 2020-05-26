package com.path.mypath.user_page_activity;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;

public interface UserPageActivityPresenter {
    void onBackButtonClickListener();

    void onCatchPersonalDataSuccessful(String json);

    void onSendButtonClickListener(String creatorEmail);

    void onCatchUserLikeDataSuccessful(String json);

    void onCatchUserDataSuccessful(String json);

    void onCatchHomeDataSuccessful(String json);

    void onMapItemClickListener(DataArray locationArray);

    void onArticleCountClickListener(ArrayList<DataArray> dataArray);

    void onChasingCountClickListener(DataObject data);

    void onFansCountClickListener(DataObject data);
}
