package com.path.mypath.fragment.heart_fragment;

import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;

public interface HeartFragmentVu {
    void setRecyclerView(ArrayList<ArticleLikeNotification> dataArray);

    void intentToSingleViewPage(DataArray homeData);
}
