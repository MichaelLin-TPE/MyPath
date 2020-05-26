package com.path.mypath.article_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.fragment.HomeAdapter;
import com.path.mypath.fragment.RoomIdObject;
import com.path.mypath.heart_activity.HeartActivity;
import com.path.mypath.reply_activity.ReplyActivity;
import com.path.mypath.tools.UserDataProvider;
import com.path.mypath.user_page_activity.UserPageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ArticleActivity extends AppCompatActivity implements ArticleActivityVu{

    private static final String USER_LIKE = "user_like";
    private static final String PERSONAL_CHAT = "personal_chat";
    private static final String PERSONAL_DATA = "personal_data";
    private ArticleActivityPresenter presenter;

    private ImageView ivBack;

    private RecyclerView recyclerView;

    private static final String HOME_DATA = "home_data";

    private FirebaseFirestore firestore;

    private static final String CHAT_ROOM = "chat_room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        initPresenter();
        initView();
        initBundle();
        initFirebase();
        DocumentReference homeShot = firestore.collection(HOME_DATA).document(HOME_DATA);
        homeShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","取得主頁資訊失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("json");
                    presenter.onCatchHomeDataSuccessful(json);
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

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initBundle() {
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if (bundle != null){
            ArrayList<DataArray> dataArray = bundle.getParcelableArrayList("data");
            presenter.onCatchUserArticleData(dataArray);
        }
    }

    private void initView() {
        ivBack = findViewById(R.id.article_toolbar_icon);
        recyclerView = findViewById(R.id.article_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });
    }

    private void initPresenter() {
        presenter = new ArticleActivityPresenterImpl(this);
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void setRecyclerView(ArrayList<DataArray> dataArray) {
        HomeAdapter adapter = new HomeAdapter(this);
        adapter.setData(dataArray);
        adapter.setUserNickname(UserDataProvider.getInstance(this).getUserNickname());
        recyclerView.setAdapter(adapter);
        adapter.setOnHomeItemClickListener(new HomeAdapter.OnHomeItemClickListener() {
            @Override
            public void onHeartClick(DataArray data, int position, boolean isCheck, int selectIndex) {
                Log.i("Michael","點擊Heart");
                presenter.onHeartClickListener(data,position,isCheck,selectIndex);
                adapter.notifyItemChanged(position);
            }

            @Override
            public void onReplyClick(DataArray data) {
                presenter.onReplyClickListener(data);
            }

            @Override
            public void onSendClick(DataArray data) {
                presenter.onSendClickListener(data);
            }

            @Override
            public void onSortClick() {

            }

            @Override
            public void onHeartCountClick(DataArray data) {
                presenter.onHeartCountClickListener(data);
            }

            @Override
            public void onReplyCountClick(DataArray data) {
                presenter.onReplyCountClickListener(data);
            }

            @Override
            public void onUserPhotoClick(DataArray data) {

            }
        });
    }

    @Override
    public String getPhotoUrl() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public void updateUserData(Map<String, Object> map) {
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .set(map, SetOptions.merge());
    }

    @Override
    public void searchUserPersonalData(String userEmail) {
        firestore.collection("user")
                .document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String token = (String) snapshot.get("cloud_token");
                            if (token != null){
                                Log.i("Michael","取得TOKEN : "+token);
                                presenter.onCatchFCMTokenSuccessful(token);
                            }
                        }
                    }
                });
    }

    @Override
    public void searchCreatorLikeData(String userEmail) {
        firestore.collection(USER_LIKE)
                .document(userEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult()!= null){
                            DocumentSnapshot snapshot = task.getResult();
                            String json = (String) snapshot.get("json");
                            presenter.onCatchCreatorLikeData(json);
                        }
                    }
                });
    }

    @Override
    public String getUserEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }

    @Override
    public void saveUserLikeData(String likeJson, String userEmail) {
        Map<String, Object> map = new HashMap<>();
        map.put("json",likeJson);
        firestore.collection(USER_LIKE)
                .document(userEmail)
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","點讚傳送成功");
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
                    presenter.onEditTextSendTypeListener(editMsg.getText().toString(),UserDataProvider.getInstance(ArticleActivity.this).getUserEmail(),creatorEmail);
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
    public void intentToHeartActivity(DataArray data) {
        Intent it = new Intent(this, HeartActivity.class);
        it.putExtra("data",data);
        it.putExtra("mode","heart");
        startActivity(it);
    }
}
