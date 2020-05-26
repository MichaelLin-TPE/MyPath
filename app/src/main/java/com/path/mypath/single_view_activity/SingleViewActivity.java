package com.path.mypath.single_view_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.fragment.PhotoViewPagerAdapter;
import com.path.mypath.fragment.RoomIdObject;
import com.path.mypath.fragment.heart_fragment.HeartAdapter;
import com.path.mypath.heart_activity.HeartActivity;
import com.path.mypath.reply_activity.ReplyActivity;
import com.path.mypath.tools.DistanceTool;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;
import com.path.mypath.user_page_activity.UserPageActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class SingleViewActivity extends AppCompatActivity implements SingleViewVu {

    private static final String CHAT_ROOM = "chat_room";
    private static final String PERSONAL_CHAT = "personal_chat";
    private static final String PERSONAL_DATA = "personal_data";
    private SingleViewPresenter presenter;

    private ImageView ivBack, ivHeart, ivReply, ivSend, ivSort ,ivLogout;

    private RoundedImageView ivPhoto;

    private TextView tvNickname, tvTime, tvContent,tvHeartCount,tvDistance,tvReplyCount;

    private MapView mapView;

    private GoogleMap googleMap;

    private FirebaseFirestore firestore;

    private ViewPager viewPager;

    private DataArray data;

    private static final double EARTH_RADIUS = 6378.137;

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
        DocumentReference userShot = firestore.collection(PERSONAL_DATA).document(UserDataProvider.getInstance(this).getUserEmail());
        userShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if ( e != null){
                    Log.i("Michael","個人資料取得失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("user_json");
                    presenter.onCatchPersonalData(json);
                }
            }
        });

        firestore.collection(CHAT_ROOM)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (snapshots != null){
                            ArrayList<RoomIdObject> roomArray = new ArrayList<>();
                            for (QueryDocumentSnapshot data : snapshots){
                                String user1 = (String) data.get("user1");
                                String user2 = (String) data.get("user2");
                                RoomIdObject object = new RoomIdObject();
                                object.setUser1(user1);
                                object.setUser2(user2);
                                object.setRoomId(data.getId());
                                roomArray.add(object);
                            }
                            presenter.onCatchRoomIdList(roomArray);
                            Log.i("Michael","有取道聊天室資料 : "+roomArray.get(0).getRoomId());
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

        tvReplyCount = findViewById(R.id.single_item_reply_count);
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
        ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onReplyClickListener(data);
            }
        });
        tvReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onReplyClickListener(data);
            }
        });
        tvHeartCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onHeartCountClickListener(data);
            }
        });
        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendMessageClickListener(data);
            }
        });
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onPhotoClickListener(data);
            }
        });

    }


    @Override
    public void setView(DataArray data) {
        if (data.getReplyCount() == 0){
            tvReplyCount.setVisibility(View.INVISIBLE);
        }else {
            tvReplyCount.setVisibility(View.VISIBLE);
            tvReplyCount.setText(String.format(Locale.getDefault(),"%d 則留言",data.getReplyCount()));
        }
        if (data.getUserNickName().equals(UserDataProvider.getInstance(this).getUserNickname())){
            ivSend.setVisibility(View.GONE);
        }else {
            ivSend.setVisibility(View.VISIBLE);
        }

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

                        if (data.getDistance() != 0){

                            double firstLat = locationArray.get(0).latitude;
                            double firstLng = locationArray.get(0).longitude;

                            double secondLat = locationArray.get(locationArray.size() -1).latitude;
                            double secondLng = locationArray.get(locationArray.size() -1).longitude;

                            double distance = getDistance(firstLat,firstLng,secondLat,secondLng);

                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(DistanceTool.getInstance().getZoomKmLevel(distance)));
                        }else {
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                        }
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

    @Override
    public void intentToReplyActivity(DataArray data) {
        Intent it = new Intent(this, ReplyActivity.class);
        it.putExtra("data",data);
        startActivity(it);
    }

    @Override
    public void intentToHeartActivity(DataArray data) {
        Intent it = new Intent(this, HeartActivity.class);
        it.putExtra("data",data);
        it.putExtra("mode","heart");
        startActivity(it);
    }

    @Override
    public void showSendMessageDialog(String articleCreator, String creatorEmail) {
        View view = View.inflate(this,R.layout.send_message_dialog,null);
        TextView tvInfo = view.findViewById(R.id.send_message_info);
        EditText editMsg = view.findViewById(R.id.send_message_edit_message);
        tvInfo.setText(String.format("發送訊息給 %s",articleCreator));
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(view).create();

        Window window = dialog.getWindow();
        if (window != null){
            window.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.picture_color_transparent));
        }
        dialog.show();

        editMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND){
                    presenter.onEditTextSendTypeListener(editMsg.getText().toString(),UserDataProvider.getInstance(SingleViewActivity.this).getUserEmail(),creatorEmail);
                    dialog.dismiss();
                }

                return true;
            }
        });
    }

    @Override
    public void showToast(String content) {
        Toast.makeText(this,content,Toast.LENGTH_LONG).show();
    }

    @Override
    public void createChatRoom(String userEmail, String articleCreator, String message) {
        Map<String,Object> map = new HashMap<>();
        map.put("user1",userEmail);
        map.put("user2",articleCreator);
        firestore.collection(CHAT_ROOM)
                .document()
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","創建房間成功");
                            presenter.onCreateRoomSuccessful(message,userEmail,articleCreator);
                        }
                    }
                });
    }

    @Override
    public void searchForRoomId(String userEmail, String articleCreator, String message) {
        firestore.collection(CHAT_ROOM)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            for (QueryDocumentSnapshot data : task.getResult()){
                                String user1 = (String) data.get("user1");
                                String user2 = (String) data.get("user2");
                                if (user1 != null && user2 != null){
                                    if (user1.equals(userEmail) && user2.equals(articleCreator)){
                                        presenter.onCatchRoomIdSuccessful(data.getId(),userEmail,articleCreator,message);
                                        break;
                                    }
                                    if (user1.equals(articleCreator) && user2.equals(userEmail)){
                                        presenter.onCatchRoomIdSuccessful(data.getId(),userEmail,articleCreator,message);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                });
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public String getPhotoUrl() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public String getUserEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }

    @Override
    public void createPersonalChatRoom(String roomId, String msgJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",msgJson);
        firestore.collection(PERSONAL_CHAT)
                .document(roomId)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            presenter.onCreatePersonalChatRoomSuccessful(roomId);
                            Log.i("Michael","創建對話紀錄成功");
                        }
                    }
                });
    }

    @Override
    public void reSearchRoomIdList() {
        firestore.collection(CHAT_ROOM)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            ArrayList<RoomIdObject> roomArray = new ArrayList<>();
                            for (QueryDocumentSnapshot data : task.getResult()){
                                String user1 = (String) data.get("user1");
                                String user2 = (String) data.get("user2");
                                RoomIdObject object = new RoomIdObject();
                                object.setUser1(user1);
                                object.setUser2(user2);
                                object.setRoomId(data.getId());
                                roomArray.add(object);
                            }
                            presenter.onCatchRoomIdList(roomArray);
                        }
                    }
                });
    }

    @Override
    public void searchUserData(String userEmail) {
        firestore.collection(PERSONAL_DATA)
                .document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("user_json");
                            presenter.onCatchPersonalUserDataSuccessful(json,userEmail);
                        }
                    }
                });
    }

    @Override
    public void updatePersonalUserData(String userJson, String userEmail) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",userJson);
        firestore.collection(PERSONAL_DATA)
                .document(userEmail)
                .set(map,SetOptions.merge());
        Log.i("Michael",userEmail +" 使用者資料 ROOM ID 新增成功 ");
    }

    @Override
    public void searchCreatorData(String creatorEmail) {
        firestore.collection(PERSONAL_DATA)
                .document(creatorEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("user_json");
                            presenter.onCatchPersonalCreatorDataSuccessful(json);
                        }
                    }
                });
    }

    @Override
    public void searchPersonChatRoomData(String userEmail, String articleCreator, String message, String roomId) {
        firestore.collection(PERSONAL_CHAT)
                .document(roomId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("json");
                            presenter.onCatchPersonalChatData(json,userEmail,articleCreator,message);
                        }
                    }
                });
    }

    @Override
    public void updatePersonalChatData(String msgJson, String roomId) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",msgJson);
        firestore.collection(PERSONAL_CHAT)
                .document(roomId)
                .set(map,SetOptions.merge());
        Log.i("Michael","更新對話資料");
    }

    @Override
    public void intentToUserPageReviewActivity(String userEmail) {
        Intent it = new Intent(this, UserPageActivity.class);
        it.putExtra("email",userEmail);
        startActivity(it);
    }
}
