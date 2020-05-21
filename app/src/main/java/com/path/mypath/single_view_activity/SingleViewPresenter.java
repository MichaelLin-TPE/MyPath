package com.path.mypath.single_view_activity;

import com.path.mypath.data_parser.DataArray;

public interface SingleViewPresenter {
    void onCatchData(DataArray data);

    void onBackButtonClickListener();

    void onCatchHomeDataSuccessful(String json);

    void onHeartButtonClickListener(DataArray data);

    void onReplyClickListener(DataArray data);
}
