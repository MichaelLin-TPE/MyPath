package com.path.mypath.single_view_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SingleViewActivity extends AppCompatActivity implements SingleViewVu {

    private SingleViewPresenter presenter;

    private ImageView ivBack, ivHeart, ivReply, ivSend, ivSort;

    private RoundedImageView ivPhoto;

    private TextView tvNickname, tvTime, tvContent,tvHeartCount;

    private MapView mapView;

    private GoogleMap googleMap;

    private FirebaseFirestore firestore;

    private DataArray data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_view);
        initPresenter();
        initFirebase();
        initBundle();
        initView();
        presenter.onCatchData(data);
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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
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


    }

    @Override
    public void closePage() {
        finish();
    }
}
