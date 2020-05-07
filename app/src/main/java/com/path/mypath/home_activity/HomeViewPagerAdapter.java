package com.path.mypath.home_activity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.path.mypath.fragment.HomeFragment;
import com.path.mypath.fragment.add_fragment.AddFragment;
import com.path.mypath.fragment.heart_fragment.HeartFragment;
import com.path.mypath.fragment.user_fragment.UserFragment;

public class HomeViewPagerAdapter extends FragmentStatePagerAdapter {


    public HomeViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        if (position == 0 ){
            return HomeFragment.newInstance();
        }
        if (position == 1){
            return AddFragment.newInstance();
        }
        if (position == 2){
            return HeartFragment.newInstance();
        }
        if (position == 3){
            return UserFragment.newInstance();
        }
        return null ;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
