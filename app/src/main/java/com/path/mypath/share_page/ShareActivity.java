package com.path.mypath.share_page;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.tools.UserDataProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShareActivity extends AppCompatActivity implements ShareActivityVu {

    private ShareActivityPresenter presenter;

    private Handler handler = new Handler();

    private static final int REQUEST_LOCATION = 1;

    private Button btnStart,btnStop;

    private MyHandler myHandler = new MyHandler(this);

    private static String[] PERMISSION_LOCATION = {"android.permission.ACCESS_FINE_LOCATION"
            , "android.permission.ACCESS_COARSE_LOCATION"
            , "android.permission.ACCESS_BACKGROUND_LOCATION"};

    private static final int START_RECORD = 0;

    private ArrayList<LatLng> latLngArrayList;

    private GoogleMap googleMap;

    private MapView mapView;

    private EditText edContent;

    private FirebaseFirestore firestore;

    private static final String PERSONAL_DATA = "personal_data";

    private static final String PUBLIC_DATA = "public_data";

    private int permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        initPresenter();
        initFirebase();
        initView();
        latLngArrayList = new ArrayList<>();
        verifyLocationPermissions(this);
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void verifyLocationPermissions(ShareActivity shareActivity) {
        try {
            permission = ActivityCompat.checkSelfPermission(shareActivity,
                    "android.permission.ACCESS_FINE_LOCATION");

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(shareActivity, PERMISSION_LOCATION, REQUEST_LOCATION);
            } else {

                presenter.onLocationPermissionGranted();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        ImageView ivBack = findViewById(R.id.share_toolbar_back);
        btnStart = findViewById(R.id.btn_start_record);
        edContent = findViewById(R.id.share_edit_content);
        btnStop = findViewById(R.id.btn_stop_record);
        btnStop.setEnabled(false);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackIconClickListener();
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onStopToRecordButtonClickListener(edContent.getText().toString());
            }
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onStartToRecordButtonClickListener();
            }
        });
        mapView = findViewById(R.id.basic_map);
    }

    private void initPresenter() {
        presenter = new ShareActivityPresenterImpl(this);
    }

    @Override
    public void startToRecordMyPath() {
        handler.post(recordPath);
    }

    @Override
    public void stopToRecordMyPath(String articleContent) {
        handler.removeCallbacks(recordPath);
        PolylineOptions rectOptions = new PolylineOptions();
        //繪製路線
        for (LatLng latLng : latLngArrayList){
            rectOptions.add(latLng).color(Color.RED);
        }
        googleMap.addPolyline(rectOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLngArrayList.get(latLngArrayList.size()-1)));


        presenter.onCatchCurrentUserData(articleContent,latLngArrayList);

        Log.i("Michael", "停止錄製");
    }

    @Override
    public void catchCurrentLocation() {
        FusedLocationProviderClient client = new FusedLocationProviderClient(this);
        Task<Location> currentLocation = client.getLastLocation();
        currentLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {


                if (mapView != null && location != null) {
                    mapView.onCreate(null);
                    mapView.onResume();
                    mapView.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap Map) {
                            Log.i("Michael", "地圖顯示");
                            googleMap = Map;
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
                                    location.getLongitude())).title("目前位置"));
                            googleMap.setMyLocationEnabled(true);
                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
                            googleMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
                            googleMap.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能

                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                            Log.i("Michael", "錄製到的經度 : " + location.getLatitude() + " , 緯度 : " + location.getLongitude());
                        }
                    });
                } else {
                    Log.i("Michael", "mapView == null");
                }
            }
        });
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void showNoticeDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.record_path))
                .setMessage(getString(R.string.notice))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onRecordConfirmClickListener();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onRecordCancelClickListener();
                    }
                }).create();
        dialog.show();
    }

    @Override
    public void showFinishDialog(String articleContent) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.finish_record))
                .setMessage(getString(R.string.finish_notice))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onFinishRecordConfimClickListener(articleContent);
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void setBtnStartEnable(boolean isEnable) {
        btnStart.setEnabled(isEnable);
        btnStop.setEnabled(!isEnable);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void searchCurrentUserData() {
        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(this).getUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("user_json");
                            if (json != null){
                                presenter.onCatchCurrentUserDataSuccessful(json);
                            }else {
                                Log.i("Michael","抓不到JSON");
                            }
                        }else {
                            Log.i("Michael","firebase 連線失敗");
                        }
                    }
                });
    }

    @Override
    public String getUserNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public String getUserPhotoUrl() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public void updateUserData(String newJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",newJson);
        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(this).getUserEmail())
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            presenter.onUpdateUserDataSuccessful();
                        }else {
                            presenter.onUpdateUserDataFailure();
                        }
                    }
                });
    }

    @Override
    public void setBtnStopEnable() {
        btnStop.setEnabled(false);
    }

    @Override
    public void searchPublicData(DataArray dataArray) {
        firestore.collection(PUBLIC_DATA)
                .document(PUBLIC_DATA)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("public_json");
                            if (json != null){
                                presenter.onCatchPublicJson(json,dataArray);
                            }else {
                                presenter.onCatchNoPublicJson(dataArray);
                            }
                        }
                    }
                });
    }

    @Override
    public void updatePublicJson(String pubJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("public_json",pubJson);
        firestore.collection(PUBLIC_DATA)
                .document(PUBLIC_DATA)
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","公開資料更新成功");
                        }
                    }
                });
    }

    @Override
    public void showNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel_id", name, importance);
            channel.setDescription(description);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
        Intent intent = new Intent(this, ShareActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("開始錄製")
                .setContentText("點我一下回APP")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(0, builder.build());
    }

    private Runnable recordPath = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 10000);
            Log.i("Michael", "做事");

            Message message = new Message();
            message.what = START_RECORD;
            myHandler.sendMessage(message);

        }
    };

    //轉到 MainThread
    private static class MyHandler extends Handler {
        private WeakReference<ShareActivity> mChannel;

        MyHandler(ShareActivity shareActivity) {
            mChannel = new WeakReference<>(shareActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mChannel.get() == null) {
                return;
            }
            final ShareActivity mainActivity = mChannel.get();
            switch (msg.what) {
                case START_RECORD:

                    FusedLocationProviderClient client = new FusedLocationProviderClient(mainActivity);
                    Task<Location> currentLocation = client.getLastLocation();
                    currentLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(final Location location) {

                            if (location != null) {

                                if (mainActivity.latLngArrayList.size() != 0) {

                                    int lastIndex = mainActivity.latLngArrayList.size() - 1;
                                    LatLng latLng = mainActivity.latLngArrayList.get(lastIndex);

                                    if (latLng.latitude == location.getLatitude() && latLng.longitude == location.getLongitude()) {
                                        Log.i("Michael", "經緯度一樣不錄製");
                                    } else {
                                        Log.i("Michael", "經度 : " + location.getLatitude() + " , 緯度 : " + location.getLongitude() + " , 資料長度 : " + mainActivity.latLngArrayList.size());
                                        mainActivity.latLngArrayList.add(new LatLng(location.getLatitude(), location.getLongitude()));
                                    }

                                } else {
                                    mainActivity.latLngArrayList.add(new LatLng(location.getLatitude(),
                                            location.getLongitude()));
                                    Log.i("Michael", "第一次錄製到的經度 : " + location.getLatitude() + " , 緯度 : " + location.getLongitude() + " , 資料長度為 : " + mainActivity.latLngArrayList.size());
                                }


                            } else {
                                Log.i("Michael", "location == null");
                            }
                        }
                    });
                    break;
            }
        }
    }
}
