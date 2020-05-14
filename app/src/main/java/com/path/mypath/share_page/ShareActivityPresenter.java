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

    void onCatchCurrentUserData(String articleContent, ArrayList<LatLng> latLngArrayList, double distance);

    void onUpdateUserDataSuccessful();

    void onUpdateUserDataFailure();

    void onCatchHomeData(String json, DataArray dataArray);

    void onUpdateHomeDataSuccessful();

    void onBackConfirmClickListener();

    void onPublicButtonClickListener();

    void onPrivateButtonClickListener();

    void onCatchUserDataSuccessful(String json);

    void onCatchHomeDataSuccessful(String json);

    void onCatchSearchDataSuccessful(String json);
}
