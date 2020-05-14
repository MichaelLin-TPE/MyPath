package com.path.mypath.home_activity;

import com.path.mypath.R;

import java.util.ArrayList;

public class HomeActivityPresenterImpl implements HomeActivityPresenter {
    public HomeActivityVu mView;

    public HomeActivityPresenterImpl(HomeActivityVu mView) {
        this.mView = mView;
    }

    @Override
    public void onShowTabLayout() {


        ArrayList<Integer> iconNotPress = new ArrayList<>();
        iconNotPress.add(R.drawable.home_not_press);
        iconNotPress.add(R.drawable.add_not_press);
        iconNotPress.add(R.drawable.heart_not_press);
        iconNotPress.add(R.drawable.user_not_press);

        ArrayList<Integer> iconPress = new ArrayList<>();
        iconPress.add(R.drawable.home_press);
        iconPress.add(R.drawable.add_press);
        iconPress.add(R.drawable.heart_press);
        iconPress.add(R.drawable.user_press);

        mView.showTabLayout(iconNotPress,iconPress);
    }

    @Override
    public void onShowViewPager() {
        mView.showViewPager();
    }

    @Override
    public void onAddIconClickListener() {
        mView.showModeSelectDialog();

    }

    @Override
    public void onLogoutClickListener() {
        mView.showLogoutDialog();
    }

    @Override
    public void onLogoutConfirmClickListener() {
        mView.googleLogout();
    }

    @Override
    public void onRecordPathClickListener() {
        mView.intentToShareActivity();
    }

    @Override
    public void onUploadPhotoClickListener() {
        mView.intentToSelectPhotoActivity();
    }
}
