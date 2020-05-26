package com.path.mypath.user_page_activity;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;

public interface UserPageActivityVu {
    void closePage();

    void setData(DataObject object);

    void sendChasePermission(String likeJson);

    String getNickname();

    String getUserPhoto();

    String getUserEmail();

    void intentToSingleViewActivity(DataArray data);

    void intentToMyArticleActivity(ArrayList<DataArray> userDataArray);

    void intentToHeartActivity(DataObject data, String chasing);
}
