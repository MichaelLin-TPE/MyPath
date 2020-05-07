package com.path.mypath.share_page;

import com.google.android.gms.maps.model.LatLng;

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
}
