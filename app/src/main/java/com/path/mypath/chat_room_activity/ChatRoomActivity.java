package com.path.mypath.chat_room_activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.path.mypath.R;
import com.path.mypath.chat_room_activity.view.AdapterPresenter;
import com.path.mypath.chat_room_activity.view.AdapterPresenterImpl;
import com.path.mypath.fragment.MessageArray;
import com.path.mypath.tools.UserDataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatRoomActivity extends AppCompatActivity implements ChatRoomVu{

    private ChatRoomPresenter presenter;

    private RecyclerView recyclerView;

    private EditText editMessage;

    private ImageView ivSend,ivBack;

    private TextView tvTitle;

    private FirebaseFirestore firestore;

    private static final String PERSONAL_CHAT = "personal_chat";

    private AdapterPresenter adapterPresenter;

    private String roomId;

    private int lastIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        initPresenter();
        initView();
        initFirebase();
        initBundle();
        DocumentReference chatShot = firestore.collection(PERSONAL_CHAT).document(roomId);
        chatShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","取得聊天資訊失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("json");
                    presenter.onCatchChatDataSuccessful(json);
                }
            }
        });
    }

    private void initBundle() {
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if (bundle != null){
            roomId = bundle.getString("roomId","");
        }
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initView() {
        recyclerView = findViewById(R.id.chat_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        editMessage = findViewById(R.id.chat_edit_message);
        ivSend = findViewById(R.id.chat_iv_send);
        tvTitle = findViewById(R.id.chat_toolbar_title);
        ivBack = findViewById(R.id.chat_toolbar_icon);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });


        editMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND){
                    presenter.onSendMessageClickListener(editMessage.getText().toString());
                    editMessage.setText("");
                }

                return true;
            }
        });


        ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSendMessageClickListener(editMessage.getText().toString());
                editMessage.setText("");
            }
        });



    }

    private void initPresenter() {
        presenter = new ChatRoomPresenterImpl(this);
        adapterPresenter = new AdapterPresenterImpl();
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void setRecyclerView(ArrayList<MessageArray> msgArray) {
        adapterPresenter.setData(msgArray);
        adapterPresenter.setUserNickname(UserDataProvider.getInstance(this).getUserNickname());
        ChatRoomAdapter adapter = new ChatRoomAdapter(adapterPresenter,this);
        recyclerView.setAdapter(adapter);
        recyclerView.scrollToPosition(msgArray.size() -1);
        lastIndex = msgArray.size() -1;
    }

    @Override
    public String getUserNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public void setTitle(String titleName) {
        tvTitle.setText(titleName);
    }

    @Override
    public void showToast(String content) {
        Toast.makeText(this,content,Toast.LENGTH_LONG).show();
    }

    @Override
    public String getUserPhoto() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public String getUserEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }

    @Override
    public void updateChatData(String json) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",json);
        firestore.collection(PERSONAL_CHAT)
                .document(roomId)
                .set(map, SetOptions.merge());
        Log.i("Michael","對話更新成功");
    }
}
