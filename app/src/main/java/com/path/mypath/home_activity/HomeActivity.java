package com.path.mypath.home_activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.tabs.TabLayout;
import com.path.mypath.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HomeActivityVu {

    private HomeActivityPresenter presenter;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private ImageView ivTabIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initPresenter();
        initView();
        presenter.onShowTabLayout();
    }

    private void initView() {
        tabLayout = findViewById(R.id.home_tab_layout);
        viewPager = findViewById(R.id.home_view_pager);

        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }

    private void initPresenter() {
        presenter = new HomeActivityPresenterImpl(this);
    }

    @Override
    public void showTabLayout(ArrayList<Integer> iconNotPress, ArrayList<Integer> iconPress) {
        tabLayout.removeAllTabs();
        for (int i = 0 ; i < iconNotPress.size() ; i ++){
            TabLayout.Tab tab = tabLayout.newTab();
            tab.setCustomView(prepareView(iconNotPress.get(i)));
            tabLayout.addTab(tab);
        }
        TabLayout.Tab firstTab = tabLayout.getTabAt(0);
        if (firstTab != null){
            if (firstTab.getCustomView() != null){
                ivTabIcon = firstTab.getCustomView().findViewById(R.id.bottom_tab_icon);
                ivTabIcon.setImageResource(iconPress.get(0));
            }
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("Michael", "我點擊的位置 : " + tab.getPosition());
                int position = tab.getPosition();
                TabLayout.Tab singleTab = tabLayout.getTabAt(position);
                if (singleTab != null) {
                    if (singleTab.getCustomView() != null) {
                        ivTabIcon = singleTab.getCustomView().findViewById(R.id.bottom_tab_icon);
                        ivTabIcon.setImageResource(iconPress.get(position));
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                TabLayout.Tab singleTab = tabLayout.getTabAt(position);
                if (singleTab != null) {
                    if (singleTab.getCustomView() != null) {
                        ivTabIcon = singleTab.getCustomView().findViewById(R.id.bottom_tab_icon);
                        ivTabIcon.setImageResource(iconNotPress.get(position));
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        presenter.onShowViewPager();
    }

    @Override
    public void showViewPager() {
        FragmentManager manager = getSupportFragmentManager();
        HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(manager);
        viewPager.setAdapter(adapter);
    }

    private View prepareView(Integer iconPress) {

        View view = View.inflate(this,R.layout.home_bottom_tablayout_custom_view,null);
        ivTabIcon = view.findViewById(R.id.bottom_tab_icon);
        ivTabIcon.setImageResource(iconPress);
        return view;

    }
}
