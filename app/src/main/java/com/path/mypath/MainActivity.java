package com.path.mypath;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements MainActivityVu {

    private MainActivityPresenter presenter;

    private Handler handler = new Handler();

    private static final int REQUEST_LOCATION = 1;

    private MyHandler myHandler = new MyHandler(this);

    private static String[] PERMISSION_LOCATION = {"android.permission.ACCESS_FINE_LOCATION"
            , "android.permission.ACCESS_COARSE_LOCATION"
            , "android.permission.ACCESS_BACKGROUND_LOCATION"};

    private static final int START_RECORD = 0;

    private ArrayList<LatLng> latLngArrayList;

    private GoogleMap googleMap;

    private MapView mapView;

    private int permission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPresenter();
        initView();

        latLngArrayList = new ArrayList<>();

        verifyLocationPermissions(this);
    }

    private void verifyLocationPermissions(MainActivity mainActivity) {
        try {
            permission = ActivityCompat.checkSelfPermission(mainActivity,
                    "android.permission.ACCESS_FINE_LOCATION");

            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(mainActivity, PERMISSION_LOCATION, REQUEST_LOCATION);
            } else {

                presenter.onLocationPermissionGranted();
                catchCurrentLocation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//


            }
        });
    }

    //取得權限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    NotificationManagerCompat manager = NotificationManagerCompat.from(MainActivity.this);
                    boolean isEnable = manager.areNotificationsEnabled();
                    if (!isEnable) {
                        AlertDialog dialog = new AlertDialog.Builder(this)
                                .setTitle(getString(R.string.permission))
                                .setMessage(getString(R.string.is_open_notification))
                                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
                                            Intent intent = new Intent();
                                            intent.setAction(Settings.ACTION_APPLICATION_SETTINGS);
                                            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                                            intent.putExtra(Settings.EXTRA_CHANNEL_ID, getApplicationInfo().uid);
                                            startActivity(intent);
                                        } else {
                                            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                                                toSystemConfig();
                                            } else {
                                                try {
                                                    toApplicationInfo();
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    toSystemConfig();
                                                }
                                            }
                                        }
                                    }
                                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create();
                        dialog.show();
                    }
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, PERMISSION_LOCATION, REQUEST_LOCATION);
                }
                break;
        }

    }

    private void toApplicationInfo() {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        localIntent.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(localIntent);
    }

    private void toSystemConfig() {
        try {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() {
        SignInButton btnLogin = findViewById(R.id.main_sign_in_btn);
        Button btnStart = findViewById(R.id.btn_start_record);
        Button btnStop = findViewById(R.id.btn_stop_record);




        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onStopToRecordButtonClickListener();
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
        presenter = new MainActivityPresenterImpl(this);
    }

    @Override
    public void startToRecordMyPath() {
        handler.post(recordPath);
    }

    @Override
    public void stopToRecordMyPath() {
        handler.removeCallbacks(recordPath);
        Log.i("Michael", "停止錄製");
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
        Intent intent = new Intent(this, MainActivity.class);
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
        private WeakReference<MainActivity> mChannel;

        MyHandler(MainActivity mainActivity) {
            mChannel = new WeakReference<>(mainActivity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mChannel.get() == null) {
                return;
            }
            final MainActivity mainActivity = mChannel.get();
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

                                    if (latLng.latitude == location.getLatitude() && latLng.longitude == location.getLongitude()){
                                        Log.i("Michael", "經緯度一樣不錄製");
                                    }else {
                                        Log.i("Michael","經度 : "+location.getLatitude()+" , 緯度 : "+location.getLongitude()+" , 資料長度 : "+mainActivity.latLngArrayList.size());
                                        mainActivity.latLngArrayList.add(new LatLng(location.getLatitude(),location.getLongitude()));
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
