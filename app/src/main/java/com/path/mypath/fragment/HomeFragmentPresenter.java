package com.path.mypath.fragment;

import com.path.mypath.data_parser.DataArray;

public interface HomeFragmentPresenter {
    void onCatchUserDataSuccessful(String json);

    void onHeartClickListener(DataArray articleData, int position, boolean isCheck, int selectIndex);

    void onCatchRealTimeData(String json);

    void onCatchFCMTokenSuccessful(String token);

    void onCatchCreatorLikeData(String json);
}
