package com.path.mypath.search_user_activity;

import androidx.activity.OnBackPressedDispatcherOwner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.path.mypath.R;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.heart_activity.UserData;
import com.path.mypath.tools.UserDataProvider;
import com.path.mypath.user_page_activity.UserPageActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchUserActivity extends AppCompatActivity implements SearchUserVu{


    private SearchUserPresenter presenter;

    private ImageView ivBack;

    private EditText edSearch;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);
        initPresenter();
        initFirebase();
        initView();


        firestore.collection("user_like")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null){
                            Log.i("Michael","取得 LIKE頁面 資料失敗 : "+e.toString());
                            return;
                        }
                        if (snapshots != null){
                            ArrayList<String> likJsonArray = new ArrayList<>();
                            for (QueryDocumentSnapshot data : snapshots){
                                String json = (String) data.get("json");
                                if (json != null){
                                    likJsonArray.add(json);
                                }
                            }
                            if (likJsonArray.size() != 0){
                                Log.i("Michael","likJsonArray != null");

                                firestore.collection("user")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful() && task.getResult() != null){
                                                    ArrayList<UserData> dataArray = new ArrayList<>();
                                                    for (QueryDocumentSnapshot snapshot : task.getResult()){
                                                        String email = (String) snapshot.get("email");
                                                        String nickname = (String) snapshot.get("display_name");
                                                        String photo = (String) snapshot.get("photo");
                                                        if (email != null && !email.isEmpty() && nickname != null && !nickname.isEmpty() && photo != null && !photo.isEmpty()){
                                                            if (!email.equals(UserDataProvider.getInstance(SearchUserActivity.this).getUserEmail())){
                                                                UserData data = new UserData();
                                                                data.setPhoto(photo);
                                                                data.setNickname(nickname);
                                                                data.setEmail(email);
                                                                dataArray.add(data);
                                                            }
                                                        }
                                                    }
                                                    if (dataArray.size() != 0){
                                                        presenter.onCatchUserDataArray(dataArray);
                                                        presenter.onCatchLikeData(likJsonArray);
                                                    }
                                                }
                                            }
                                        });

                            }else{
                                Log.i("Michael","likJsonArray == null");
                            }
                        }
                    }
                });


    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initView() {
        ivBack = findViewById(R.id.search_user_toolbar_icon);
        edSearch = findViewById(R.id.search_user_edit_email);
        recyclerView = findViewById(R.id.search_user_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });
    }

    private void initPresenter() {
        presenter = new SearchUserPresenterImpl(this);
    }

    @Override
    public void setRecyclerView(ArrayList<UserData> dataArray, ArrayList<ArrayList<ArticleLikeNotification>> likJsonArray) {
        SearchUserAdapter adapter = new SearchUserAdapter(dataArray,this);
        adapter.setLikeData(likJsonArray);
        recyclerView.setAdapter(adapter);
        adapter.setOnChaseButtonClickListener(new SearchUserAdapter.OnChaseButtonClickListener() {
            @Override
            public void onClick(UserData data) {
                Log.i("Michael","searchUserItemClick");
                presenter.onChaseButtonClickListener(data);
            }

            @Override
            public void onPhotoClick(UserData data) {
                presenter.onPhotoClickListener(data);
            }
        });
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void intentToUserPage(String email) {
        Intent it = new Intent(this, UserPageActivity.class);
        it.putExtra("email",email);
        startActivity(it);
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
    }

    @Override
    public String getPhoto() {
        return UserDataProvider.getInstance(this).getUserPHotoUrl();
    }

    @Override
    public String getEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }

    @Override
    public void updateLikeUserData(String json, String email) {
        Map<String, Object> map = new HashMap<>();
        map.put("json",json);
        firestore.collection("user_like")
                .document(email)
                .set(map, SetOptions.merge());
        Log.i("Michael","like頁面更新");
    }

    @Override
    public void searchUserToken(String email) {
        firestore.collection("user")
                .document(email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            String token = (String) snapshot.get("cloud_token");
                            if (token != null){
                                presenter.onCatchUserToken(token);
                            }
                        }
                    }
                });
    }

}
