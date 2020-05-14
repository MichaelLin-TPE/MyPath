package com.path.mypath.single_view_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.fragment.PhotoViewPagerAdapter;
import com.path.mypath.fragment.heart_fragment.HeartAdapter;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SingleViewActivity extends AppCompatActivity implements SingleViewVu {

    private SingleViewPresenter presenter;

    private ImageView ivBack, ivHeart, ivReply, ivSend, ivSort;

    private RoundedImageView ivPhoto;

    private TextView tvNickname, tvTime, tvContent,tvHeartCount,tvDistance;

    private MapView mapView;

    private GoogleMap googleMap;

    private FirebaseFirestore firestore;

    private ViewPager viewPager;

    private DataArray data;

    private static final String HOME_DATA = "home_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_view);
        initPresenter();
        initFirebase();
        initBundle();
        initView();
        presenter.onCatchData(data);

        DocumentReference homeSnapshot = firestore.collection(HOME_DATA).document(HOME_DATA);
        homeSnapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","主資料取得失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("json");
                    presenter.onCatchHomeDataSuccessful(json);
                }
            }
        });
    }

    private void initBundle() {
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if (bundle != null){
            data = (DataArray) bundle.getParcelable("data");
        }
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initPresenter() {
        presenter = new SingleViewPresenterImpl(this);
    }

    private void initView() {
        ivBack = findViewById(R.id.single_toolbar_icon);
        ivHeart = findViewById(R.id.single_item_heart);
        ivReply = findViewById(R.id.single_item_reply);
        ivSend = findViewById(R.id.single_item_send);
        ivSort = findViewById(R.id.single_item_sort);
        ivPhoto = findViewById(R.id.single_item_photo);
        tvNickname = findViewById(R.id.single_item_name);
        tvTime = findViewById(R.id.single_item_time);
        tvContent = findViewById(R.id.single_item_article_content);
        tvHeartCount = findViewById(R.id.single_item_heart_count);
        mapView = findViewById(R.id.single_item_map_view);
        tvDistance = findViewById(R.id.single_item_article_distance);
        viewPager = findViewById(R.id.single_item_view_pager);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });
        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onHeartButtonClickListener(data);
            }
        });
    }


    @Override
    public void setView(DataArray data) {
        ImageLoaderProvider.getInstance(this).setImage(data.getUserPhoto(),ivPhoto);
        tvNickname.setText(data.getUserNickName());
        tvTime.setText(new SimpleDateFormat("yyyy/MM/dd",Locale.TAIWAN).format(new Date(data.getCurrentTime())));
        tvHeartCount.setText(String.format(Locale.getDefault(),"%d 個人按讚",data.getHeartCount()));
        tvContent.setText(String.format(Locale.getDefault(),"%s : %s",data.getUserNickName(),data.getArticleTitle()));
        if (data.getDistance() == 0){
            tvDistance.setVisibility(View.GONE);
        }else {
            tvDistance.setVisibility(View.VISIBLE);
            tvDistance.setText(String.format(Locale.getDefault(),"#移動 %1$,.2f 公尺",data.getDistance()));
        }

        boolean isCheck = false;
        ArrayList<DataUserPresHeart> heartArray = data.getHeartPressUsers();
        for (DataUserPresHeart object : heartArray){
            if (object.getName().equals(UserDataProvider.getInstance(this).getUserNickname())){
                isCheck = true;
                break;
            }
        }
        if (isCheck){
            ivHeart.setImageResource(R.drawable.heart_red);
        }else {
            ivHeart.setImageResource(R.drawable.heart_not_press);
        }
        if (data.getLocationArray() != null && data.getLocationArray().size() != 0){

            mapView.setVisibility(View.VISIBLE);
            viewPager.setVisibility(View.GONE);

            if (mapView != null){
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        googleMap = map;
                        PolylineOptions rectOptions = new PolylineOptions();
                        //繪製路線
                        ArrayList<LatLng> locationArray = data.getLocationArray();
                        for (LatLng latLng : locationArray) {
                            rectOptions.add(latLng).color(Color.RED);
                        }
                        googleMap.addPolyline(rectOptions);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArray.get(data.getLocationArray().size() - 1)));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    }
                });
            }
        }else if (data.getPhotoArray() != null && data.getPhotoArray().size() != 0){
            mapView.setVisibility(View.INVISIBLE);
            viewPager.setVisibility(View.VISIBLE);
            PhotoViewPagerAdapter adapter = new PhotoViewPagerAdapter(this,data.getPhotoArray());
            viewPager.setAdapter(adapter);
            adapter.setOnPhotoClickListener(new PhotoViewPagerAdapter.OnPhotoClickListener() {
                @Override
                public void onClick() {

                }
            });
        }




    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public String getUserNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public void setHeartIcon(boolean isShow) {
        ivHeart.setImageResource(isShow ? R.drawable.heart_not_press : R.drawable.heart_red);
    }

    @Override
    public void setHeartCountLess() {
        int heartCount = data.getHeartCount() - 1;
        tvHeartCount.setText(String.format(Locale.getDefault(),"%d 個人按讚",heartCount));
        data.setHeartCount(data.getHeartCount()-1);
    }

    @Override
    public void setHeartCountMore() {
        int heartCount = data.getHeartCount() +1;
        tvHeartCount.setText(String.format(Locale.getDefault(),"%d 個人按讚",heartCount));
        data.setHeartCount(data.getHeartCount()+1);
    }

    @Override
    public String getUserPhoto() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public void update(String json) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",json);
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","主資料更新成功");
                        }
                    }
                });
    }
}
