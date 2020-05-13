package com.path.mypath.share_page;

import com.google.android.gms.maps.model.LatLng;
import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;

public interface ShareActivityPresenter {
    void onStopToRecordButtonClickListener(String articleContent);

    void onStartToRecordButtonClickListener();

    void onLocationPermissionGranted();

    void onBackIconClickListener();

    void onRecordConfirmClickListener();

    void onRecordCancelClickListener();

    void onFinishRecordConfimClickListener(String articleContent);

    void onCatchCurrentUserData(String articleContent, ArrayList<LatLng> latLngArrayList);

    void onCatchCurrentUserDataSuccessful(String json);

    void onUpdateUserDataSuccessful();

    void onUpdateUserDataFailure();

    void onCatchPublicJson(String json, DataArray dataArray);

    void onCatchNoPublicJson(DataArray dataArray);

    void onPublicConfirmClickListener(DataArray dataArray);

    void onCatchHomeData(String json, DataArray dataArray);

    void onUpdateHomeDataSuccessful();

    void onBackConfirmClickListener();
}
