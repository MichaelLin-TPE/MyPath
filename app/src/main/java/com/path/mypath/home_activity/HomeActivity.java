package com.path.mypath.home_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.path.mypath.MainActivity;
import com.path.mypath.R;
import com.path.mypath.message_activity.MessageActivity;
import com.path.mypath.message_activity.MessageActivityPresenterImpl;
import com.path.mypath.photo_activity.SelectPhotoActivity;
import com.path.mypath.share_page.ShareActivity;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements HomeActivityVu {

    private HomeActivityPresenter presenter;

    private TabLayout tabLayout;

    private ViewPager viewPager;

    private ImageView ivTabIcon;


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        TabLayout.Tab tab = tabLayout.getTabAt(3);
        if (tab != null){
            tab.select();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initPresenter();
        initView();
        presenter.onShowTabLayout();

        //判斷是否已開啟通知權限
        if (!isNotificationEnabled(this)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Intent it = new Intent();
                it.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                it.putExtra(Settings.EXTRA_APP_PACKAGE,getPackageName());
                it.putExtra(Settings.EXTRA_CHANNEL_ID,getApplicationInfo().uid);
                startActivity(it);
            }else {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1){
                    try {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else {
                    try{
                        Intent localIntent = new Intent();
                        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
                        startActivity(localIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    //確認通知權限
    private boolean isNotificationEnabled(Context context){
        return NotificationManagerCompat.from(context.getApplicationContext()).areNotificationsEnabled();
    }

    private void initView() {
        tabLayout = findViewById(R.id.home_tab_layout);
        viewPager = findViewById(R.id.home_view_pager);
        ImageView ivAdd = findViewById(R.id.home_toolbar_icon);
        ImageView ivSend = findViewById(R.id.home_toolbar_send);
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendMessageClickListener();
            }
        });
        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddIconClickListener();
            }
        });
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

    @Override
    public void intentToShareActivity() {
        Intent it = new Intent(this, ShareActivity.class);
        startActivity(it);
    }

    @Override
    public void intentToMessageActivity() {

        Intent it = new Intent(this, MessageActivity.class);
        startActivity(it);

    }

    @Override
    public void showModeSelectDialog() {
        View view = View.inflate(this,R.layout.mode_select_dialog,null);
        LinearLayout linPhoto = view.findViewById(R.id.mode_select_photo);
        LinearLayout linPath = view.findViewById(R.id.mode_select_path);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view).create();
        dialog.show();
        linPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onRecordPathClickListener();
                dialog.dismiss();
            }
        });
        linPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onUploadPhotoClickListener();
                dialog.dismiss();
            }
        });
    }

    @Override
    public void intentToSelectPhotoActivity() {
        Intent it = new Intent(this, SelectPhotoActivity.class);
        startActivity(it);
    }

    private View prepareView(Integer iconPress) {

        View view = View.inflate(this,R.layout.home_bottom_tablayout_custom_view,null);
        ivTabIcon = view.findViewById(R.id.bottom_tab_icon);
        ivTabIcon.setImageResource(iconPress);
        return view;

    }
}
