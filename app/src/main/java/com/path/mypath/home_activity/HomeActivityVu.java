package com.path.mypath.home_activity;

import java.util.ArrayList;

public interface HomeActivityVu {
    void showTabLayout(ArrayList<Integer> iconNotPress, ArrayList<Integer> iconPress);

    void showViewPager();

    void intentToShareActivity();

    void intentToMessageActivity();

    void showModeSelectDialog();

    void intentToSelectPhotoActivity();
}
