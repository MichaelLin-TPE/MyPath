package com.path.mypath.share_page;

public class ShareActivityPresenterImpl implements ShareActivityPresenter {

    private ShareActivityVu mView;

    public ShareActivityPresenterImpl(ShareActivityVu mView) {
        this.mView = mView;
    }

    @Override
    public void onStopToRecordButtonClickListener() {
        mView.stopToRecordMyPath();
    }

    @Override
    public void onStartToRecordButtonClickListener() {
        mView.startToRecordMyPath();
        mView.showNotification();
    }

    @Override
    public void onLocationPermissionGranted() {
        mView.catchCurrentLocation();
    }
}
