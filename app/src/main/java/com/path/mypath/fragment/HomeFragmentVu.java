package com.path.mypath.fragment;

import com.path.mypath.data_parser.DataObject;

import java.util.Map;

public interface HomeFragmentVu {
    void setRecyclerView(DataObject data);

    String getNickname();

    String getPhotoUrl();

    void updateUserData(Map<String, Object> map);
}
