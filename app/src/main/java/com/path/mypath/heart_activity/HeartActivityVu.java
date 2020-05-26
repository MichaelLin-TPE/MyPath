package com.path.mypath.heart_activity;

import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.fragment.FansData;

import java.util.ArrayList;

public interface HeartActivityVu {
    void closePage();

    void setRecyclerView(ArrayList<DataUserPresHeart> heartPressUsers);

    void intentToUserPageActivity(String email);

    void setNewRecyclerView(ArrayList<FansData> fansArray);

    void setTitle(String mode);
}
