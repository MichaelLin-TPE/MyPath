package com.path.mypath.single_view_activity;

import com.path.mypath.data_parser.DataArray;

public interface SingleViewVu {
    void setView(DataArray data);

    void closePage();

    String getUserNickname();

    void setHeartIcon(boolean isShow);

    void setHeartCountLess();

    void setHeartCountMore();

    String getUserPhoto();

    void update(String json);
}
