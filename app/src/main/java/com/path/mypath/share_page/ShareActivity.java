package com.path.mypath.share_page;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlertDialog;
import android.app.Notification;
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
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.path.mypath.MainActivity;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.tools.UserDataProvider;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
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

    private static final String HOME_DATA = "home_data";

    private static final double EARTH_RADIUS = 6378.137;

    private double distance = 0;

    private int permission;

    private TextView tvDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        Log.i("Michael","onCreate");
        initPresenter();
        initFirebase();
        initView();
        latLngArrayList = new ArrayList<>();
        verifyLocationPermissions(this);


        DocumentReference userShot = firestore.collection(PERSONAL_DATA).document(UserDataProvider.getInstance(this).getUserEmail());
        userShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","個人資料取得失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("user_json");
                    presenter.onCatchUserDataSuccessful(json);
                }
            }
        });
        DocumentReference homeShot = firestore.collection(HOME_DATA).document(HOME_DATA);
        homeShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
        DocumentReference pubShot = firestore.collection(PUBLIC_DATA).document(PUBLIC_DATA);
        pubShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","搜尋資料取得失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("public_json");
                    presenter.onCatchSearchDataSuccessful(json);
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Michael","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Michael","onResume");
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
        tvDistance = findViewById(R.id.share_distance);
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

        presenter.onCatchCurrentUserData(articleContent,latLngArrayList,distance);

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
                            latLngArrayList = new ArrayList<>();
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
    public void searchHomeData(DataArray dataArray) {
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("json");
                            presenter.onCatchHomeData(json,dataArray);
                        }
                    }
                });
    }

    @Override
    public void updateHomeData(String homeJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",homeJson);

        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            presenter.onUpdateHomeDataSuccessful();
                        }
                    }
                });
    }

    @Override
    public void showIsRecordingDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.information))
                .setMessage(getString(R.string.is_recording))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onBackConfirmClickListener();
                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    public void stopRecord() {
        latLngArrayList = new ArrayList<>();
        handler.removeCallbacks(recordPath);
    }

    @Override
    public String getUserEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }

    @Override
    public void showDataModeDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.information))
                .setMessage(getString(R.string.confirm_public_or_private))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.public_data), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onPublicButtonClickListener();
                    }
                }).setNegativeButton(getString(R.string.private_data), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        presenter.onPrivateButtonClickListener();
                    }
                }).create();
        dialog.show();
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
        /**
         * 暫時先不要讓他回去
         */
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "channel_id")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("開始錄製")
                .setContentText("持續繪製您的路徑中.....")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(Notification.CATEGORY_MESSAGE)
                // Set the intent that will fire when the user taps the notification
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            presenter.onBackIconClickListener();
        }
        return true;
    }

    //轉弧度
    private double rad (double radius){
        return radius * Math.PI/180.0;
    }

    private double getDistance(double lat1,double long1,double lat2, double long2){

        double firstRadLat = rad(lat1);
        double firstRadLng = rad(long1);
        double secondRadLat = rad(lat2);
        double secondRadLng = rad(long2);

        double a = firstRadLat - secondRadLat;
        double b = firstRadLng - secondRadLng;

        double cal = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(firstRadLat)
                * Math.cos(secondRadLat) * Math.pow(Math.sin(b / 2), 2))) * EARTH_RADIUS;
        double result = Math.round(cal * 10000d) / 10000d;

        Log.i("Michael","計算出的距離 公尺 : "+result*1024);
        return result*1024;
    }

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


                                    //即時顯示路線圖
                                    PolylineOptions rectOptions = new PolylineOptions();
                                    //繪製路線
                                    for (LatLng latLngData : mainActivity.latLngArrayList){
                                        rectOptions.add(latLngData).color(Color.RED);
                                    }
                                    mainActivity.googleMap.addPolyline(rectOptions);
                                    mainActivity.googleMap.moveCamera(CameraUpdateFactory.newLatLng(mainActivity.latLngArrayList.get(mainActivity.latLngArrayList.size()-1)));

                                    //抓取距離
                                    if (mainActivity.latLngArrayList.size() > 2){
                                        int last = mainActivity.latLngArrayList.size() - 1;
                                        int lastSecond = mainActivity.latLngArrayList.size() - 2;
                                        LatLng lastLat = mainActivity.latLngArrayList.get(last);
                                        LatLng lastSecondLat = mainActivity.latLngArrayList.get(lastSecond);
                                        mainActivity.distance = mainActivity.distance + mainActivity.getDistance(lastLat.latitude,lastLat.longitude
                                                ,lastSecondLat.latitude,lastSecondLat.longitude);

                                        mainActivity.tvDistance.setText(String.format(Locale.getDefault(),"已經移動了 %1$,.2f 公尺",mainActivity.distance));

                                        Log.i("Michael",String.format(Locale.getDefault(),"已經移動了 : %1$.4f",mainActivity.distance));
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
