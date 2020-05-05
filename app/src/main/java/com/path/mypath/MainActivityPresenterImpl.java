package com.path.mypath;

public class MainActivityPresenterImpl implements MainActivityPresenter {

    private MainActivityVu mView;

    public MainActivityPresenterImpl(MainActivityVu mView) {
        this.mView = mView;
    }

    @Override
    public void onStartToRecordButtonClickListener() {
        mView.startToRecordMyPath();
        mView.showNotification();
    }

    @Override
    public void onStopToRecordButtonClickListener() {
        mView.stopToRecordMyPath();
    }

    @Override
    public void onLocationPermissionGranted() {
        mView.catchCurrentLocation();
    }
}
