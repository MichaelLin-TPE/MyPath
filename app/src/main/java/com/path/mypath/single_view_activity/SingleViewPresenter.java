package com.path.mypath.single_view_activity;

import com.path.mypath.data_parser.DataArray;

public interface SingleViewPresenter {
    void onCatchData(DataArray data);

    void onBackButtonClickListener();
}
