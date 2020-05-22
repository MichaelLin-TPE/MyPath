package com.path.mypath.user_page_activity;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

public interface UserPageActivityVu {
    void closePage();

    void setData(DataObject object);

    void sendChasePermission(String likeJson);

    String getNickname();

    String getUserPhoto();

    String getUserEmail();

    void intentToSingleViewActivity(DataArray data);
}
