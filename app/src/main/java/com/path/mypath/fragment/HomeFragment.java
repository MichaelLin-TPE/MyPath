package com.path.mypath.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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
import com.path.mypath.home_activity.HomeActivity;
import com.path.mypath.reply_activity.ReplyActivity;
import com.path.mypath.tools.UserDataProvider;
import com.path.mypath.user_page_activity.UserPageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment implements HomeFragmentVu {

    private Context context;

    private HomeFragmentPresenter presenter;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    private HomeAdapter adapter;

    private static final String PERSONAL_DATA = "personal_data";

    private static final String HOME_DATA = "home_data";

    private static final String USER_LIKE = "user_like";

    private static final String CHAT_ROOM = "chat_room";

    private static final String PERSONAL_CHAT = "personal_chat";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        initFriebase();

    }

    private void initFriebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initPresenter() {
        presenter = new HomeFragmentPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DocumentReference snapshot = firestore.collection(HOME_DATA).document(HOME_DATA);
        snapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","取得資料錯誤 : "+e.toString());
                    return;
                }
                String json = (String) snapshot.get("json");
                presenter.onCatchRealTimeData(json);
            }
        });
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
    public void onResume() {
        super.onResume();
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null){
                                String json = (String) snapshot.get("json");
                                presenter.onCatchUserDataSuccessful(json);
                            }else {
                                Log.i("Michael","取得資料失敗");
                            }
                        }
                    }
                });
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.home_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    @Override
    public void setRecyclerView(ArrayList<DataArray> dataArrayList) {

        if (adapter != null){
            adapter.setData(dataArrayList);
            adapter.notifyDataSetChanged();
        }

        adapter = new HomeAdapter(context);
        adapter.setData(dataArrayList);
        adapter.setUserNickname(UserDataProvider.getInstance(context).getUserNickname());
        recyclerView.setAdapter(adapter);

        adapter.setOnHomeItemClickListener(new HomeAdapter.OnHomeItemClickListener() {
            @Override
            public void onHeartClick(DataArray articleData, int position, boolean isCheck,int selectIndex) {
                Log.i("Michael","點擊Heart");
                presenter.onHeartClickListener(articleData,position,isCheck,selectIndex);
                adapter.notifyItemChanged(position);
            }
            @Override
            public void onReplyClick(DataArray data) {
                Log.i("Michael","點擊reply");
                presenter.onReplyButtonClickListener(data);
            }

            @Override
            public void onSendClick(DataArray data) {
                Log.i("Michael","點擊send");
                presenter.onSendButtonClickListener(data);
            }

            @Override
            public void onSortClick() {
                Log.i("Michael","點擊sort");
            }

            @Override
            public void onHeartCountClick(DataArray data) {

            }

            @Override
            public void onReplyCountClick(DataArray data) {
                presenter.onReplyCountClickListener(data);
            }

            @Override
            public void onUserPhotoClick(DataArray data) {
                presenter.onUserPhotoClickListener(data);
            }
        });
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(context).getUserNickname();
    }

    @Override
    public String getPhotoUrl() {
        return UserDataProvider.getInstance(context).getUserPHotoUrl();
    }

    @Override
    public void updateUserData(Map<String, Object> map) {
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","資料更新成功");
                        }
                    }
                });
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
    public String getUserEmail() {
        return UserDataProvider.getInstance(context).getUserEmail();
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
    public void intentToReplyActivity(DataArray data) {
        Intent it = new Intent(context, ReplyActivity.class);
        it.putExtra("data",data);
        context.startActivity(it);
    }

    @Override
    public void intentToHomeActivity() {
        Intent it = new Intent(context, HomeActivity.class);
        context.startActivity(it);
    }

    @Override
    public void intentToUserPageReviewActivity(String userEmail) {
        Intent it = new Intent(context, UserPageActivity.class);
        it.putExtra("email",userEmail);
        context.startActivity(it);
    }

    @Override
    public void showSendMessageDialog(String articleCreator, String creatorEmail) {
        View view = View.inflate(context,R.layout.send_message_dialog,null);
        TextView tvInfo = view.findViewById(R.id.send_message_info);
        EditText editMsg = view.findViewById(R.id.send_message_edit_message);
        tvInfo.setText(String.format("發送訊息給 %s",articleCreator));
        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(view).create();

        Window window = dialog.getWindow();
        if (window != null){
            window.setBackgroundDrawable(ContextCompat.getDrawable(context, R.color.picture_color_transparent));
        }
        dialog.show();

        editMsg.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND){
                    presenter.onEditTextSendTypeListener(editMsg.getText().toString(),UserDataProvider.getInstance(context).getUserEmail(),creatorEmail);
                    dialog.dismiss();
                }

                return true;
            }
        });
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
    public void showToast(String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
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
    public void updatePersonalUserData(String userJson, String userEmail) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",userJson);
        firestore.collection(PERSONAL_DATA)
                .document(userEmail)
                .set(map,SetOptions.merge());
        Log.i("Michael",userEmail +" 使用者資料 ROOM ID 新增成功 ");

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
}
