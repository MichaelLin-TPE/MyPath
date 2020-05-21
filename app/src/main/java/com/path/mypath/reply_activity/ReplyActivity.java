package com.path.mypath.reply_activity;

import androidx.annotation.NonNull;
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
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.util.HashMap;
import java.util.Map;

public class ReplyActivity extends AppCompatActivity implements ReplyActivityVu {

    private ReplyActivityPresenter presenter;

    private FirebaseFirestore firestore;

    private RecyclerView recyclerView;

    private ImageView ivBack;

    private DataArray data;

    private RoundedImageView ivPhoto;

    private TextView tvNickname,tvContent;

    private EditText edMessage;

    private static final String HOME_DATA = "home_data";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        initPresenter();
        initFirebase();
        initBundle();
        initView();
        presenter.onCatchCurrentData(data);

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
        firestore.collection("user").document(data.getUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String token = (String) snapshot.get("cloud_token");
                            presenter.onCatchUserTokenSuccessful(token);
                        }
                    }
                });
        DocumentReference likeShot = firestore.collection("user_like").document(data.getUserEmail());
        likeShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","取得LIKE DATA 失敗");
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("json");
                    presenter.onCatchLikeDataSuccessful(json);
                }

            }
        });
    }

    private void initBundle() {
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if (bundle != null){
            data = bundle.getParcelable("data");
        }
    }

    private void initView() {
        edMessage = findViewById(R.id.reply_edit_content);
        ivPhoto = findViewById(R.id.reply_photo);
        tvContent = findViewById(R.id.reply_message);
        tvNickname = findViewById(R.id.reply_nickname);
        recyclerView = findViewById(R.id.reply_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ivBack = findViewById(R.id.reply_toolbar_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });

        edMessage.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEND){
                    presenter.onCatchReplyMessageListener(edMessage.getText().toString());
                    edMessage.setText("");
                }

                return false;
            }
        });
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void  initPresenter() {
        presenter = new ReplyActivityPresenterImpl(this);
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void setRecyclerView(DataArray data) {
        ReplyAdapter adapter = new ReplyAdapter(data,this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void setTitleView(DataArray data) {
        ImageLoaderProvider.getInstance(this).setImage(data.getUserPhoto(),ivPhoto);
        tvNickname.setText(data.getUserNickName());
        tvContent.setText(data.getArticleTitle());
    }

    @Override
    public String getUserPhoto() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public String getUserNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public void updateHomeData(String json) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",json);
        firestore.collection(HOME_DATA)
                .document(HOME_DATA)
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            presenter.onReplyMessageSuccessful();
                        }
                    }
                });
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public String getEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }

    @Override
    public void updateLikeData(String likeJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",likeJson);
        firestore.collection("user_like")
                .document(data.getUserEmail())
                .set(map,SetOptions.merge());

    }
}
