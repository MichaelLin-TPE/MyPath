package com.path.mypath.message_activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.path.mypath.R;
import com.path.mypath.chat_room_activity.ChatRoomActivity;
import com.path.mypath.fragment.MessageObject;
import com.path.mypath.tools.UserDataProvider;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity implements MessageActivityVu {

    private MessageActivityPresenter presenter;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    private static final String PERSONAL_DATA = "personal_data";

    private static final String PERSONAL_CHAT = "personal_chat";

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        initPresenter();
        initFirebase();
        initView();
        gson = new Gson();
    }

    @Override
    protected void onResume() {
        super.onResume();

        firestore.collection(PERSONAL_CHAT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            String email = UserDataProvider.getInstance(MessageActivity.this).getUserEmail();
                            ArrayList<MessageListDTO> msgArray = new ArrayList<>();
                            ArrayList<String> roomIdArray = new ArrayList<>();
                            for (QueryDocumentSnapshot snapshot : task.getResult()){
                                roomIdArray.add(snapshot.getId());
                                String json = (String) snapshot.get("json");
                                Log.i("Michael","取到的JSON : "+json);
                                MessageObject object = gson.fromJson(json, MessageObject.class);
                                if (object.getUser1().equals(email)) {
                                    MessageListDTO dto = new MessageListDTO();
                                    dto.setTime(object.getMessageArray().get(object.getMessageArray().size() - 1).getTime());
                                    dto.setUserEmail(object.getUser2());
                                    dto.setUserPhoto(object.getUser2PhotoUrl());
                                    dto.setUserNickname(object.getUser2Nickname());
                                    dto.setUserMessage(object.getMessageArray().get(object.getMessageArray().size() - 1).getMessage());
                                    msgArray.add(dto);
                                } else if (object.getUser2().equals(email)) {
                                    MessageListDTO dto = new MessageListDTO();
                                    dto.setTime(object.getMessageArray().get(object.getMessageArray().size() - 1).getTime());
                                    dto.setUserEmail(object.getUser1());
                                    dto.setUserPhoto(object.getUser1PhotoUrl());
                                    dto.setUserNickname(object.getUser1Nickname());
                                    dto.setUserMessage(object.getMessageArray().get(object.getMessageArray().size() - 1).getMessage());
                                    msgArray.add(dto);
                                }
                            }
                            if (msgArray.size() != 0 && roomIdArray.size() != 0){
                                Log.i("Michael","聊天有資料唷");
                                presenter.onCatchPersonChatData(msgArray,roomIdArray);
                            }else{
                                Log.i("Michael","msgArray.size = 0");
                            }
                        }
                    }
                });

    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initView() {
        ImageView ivBack = findViewById(R.id.message_toolbar_icon);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });
        TextView tvTitle = findViewById(R.id.message_toolbar_title);
        tvTitle.setText(getString(R.string.my_message));
        recyclerView = findViewById(R.id.message_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initPresenter() {
        presenter = new MessageActivityPresenterImpl(this);
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void setRecyclerView(ArrayList<MessageListDTO> msgArray, ArrayList<String> roomIdArray) {
        MessageAdapter adapter = new MessageAdapter(msgArray,this,roomIdArray);
        recyclerView.setAdapter(adapter);
        adapter.setOnMessageItemClickListener(new MessageAdapter.OnMessageItemClickListener() {
            @Override
            public void onClick(String roomId) {
                presenter.onMessageItemClickListener(roomId);
            }
        });
    }

    @Override
    public void intentToChatRoomActivity(String roomId) {
        //去聊天室
        Intent it = new Intent(this, ChatRoomActivity.class);
        it.putExtra("roomId",roomId);
        startActivity(it);
    }
}
