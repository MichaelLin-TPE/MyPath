package com.path.mypath.user_page_activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.path.mypath.R;
import com.path.mypath.article_activity.ArticleActivity;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.user_fragment.user_view.MapAdapter;
import com.path.mypath.heart_activity.HeartActivity;
import com.path.mypath.single_view_activity.SingleViewActivity;
import com.path.mypath.tools.UserDataProvider;
import com.path.mypath.user_page_activity.user_presenter.UserPresenter;
import com.path.mypath.user_page_activity.user_presenter.UserPresenterImpl;
import com.path.mypath.user_page_activity.view.InformationViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserPageActivity extends AppCompatActivity implements UserPageActivityVu {

    private UserPageActivityPresenter presenter;

    private FirebaseFirestore firestore;

    private ImageView ivBack;

    private RecyclerView recyclerView;

    private String articleCreatorEmail;

    private static final String PERSONAL_DATA = "personal_data";

    private UserPresenter userPresenter;

    private static final String USER_LIKE = "user_like";

    private static final String USER = "user";

    private static final String HOME_DATA = "home_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        initPresenter();
        initFirebase();
        initBundle();
        initView();
        if (articleCreatorEmail != null){
            DocumentReference personalShot = firestore.collection(PERSONAL_DATA).document(articleCreatorEmail);
            personalShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        Log.i("Michael","個人資料取得失敗 : "+e.toString());
                        return;
                    }
                    if (documentSnapshot != null){
                        String json = (String) documentSnapshot.get("user_json");
                        presenter.onCatchPersonalDataSuccessful(json);
                    }
                }
            });
            DocumentReference likeShot = firestore.collection(USER_LIKE).document(articleCreatorEmail);
            likeShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if ( e != null){
                        Log.i("Michael","點讚資料取得失敗 : "+e.toString());
                        return;
                    }
                    if (documentSnapshot != null){
                        String json = (String) documentSnapshot.get("json");
                        presenter.onCatchUserLikeDataSuccessful(json);
                    }
                }
            });
            DocumentReference userShot = firestore.collection(USER).document(articleCreatorEmail);
            userShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        Log.i("Michael","個人資料取得失敗 : "+e.toString());
                        return;
                    }
                    if (documentSnapshot != null){
                        String json = (String) documentSnapshot.get("cloud_token");
                        presenter.onCatchUserDataSuccessful(json);
                    }
                }
            });

            DocumentReference homeShot = firestore.collection(HOME_DATA).document(HOME_DATA);
            homeShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null){
                        Log.i("Michael","主頁面資料取得失敗 : "+e.toString());
                        return;
                    }
                    if (documentSnapshot != null){
                        String json = (String) documentSnapshot.get("json");
                        presenter.onCatchHomeDataSuccessful(json);
                    }
                }
            });
        }

    }

    private void initBundle() {
        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if (bundle != null){
            articleCreatorEmail = bundle.getString("email");
        }
    }

    private void initView() {
        ivBack = findViewById(R.id.user_page_toolbar_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });
        recyclerView = findViewById(R.id.user_page_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initPresenter() {
        presenter = new UserPageActivityPresenterImpl(this);
        userPresenter = new UserPresenterImpl();
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void setData(DataObject object) {
        userPresenter.setData(object);
        UserPageAdapter adapter = new UserPageAdapter(userPresenter,this);
        recyclerView.setAdapter(adapter);
        adapter.setOnMapItemClickListener(new MapAdapter.OnMapItemClickListener() {
            @Override
            public void onClick(DataArray locationArray) {
                presenter.onMapItemClickListener(locationArray);
            }
        });
        adapter.setOnUserPageClickListener(new InformationViewHolder.OnUserPageClickListener() {
            @Override
            public void onBtnSendClick(String nickname) {
                presenter.onSendButtonClickListener(nickname);
            }

            @Override
            public void onArticleCountClick(ArrayList<DataArray> dataArray) {
                presenter.onArticleCountClickListener(dataArray);
            }

            @Override
            public void onChasingCountClick(DataObject data) {
                presenter.onChasingCountClickListener(data);
            }

            @Override
            public void onFansCountClick(DataObject data) {
                presenter.onFansCountClickListener(data);
            }
        });
    }

    @Override
    public void sendChasePermission(String likeJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",likeJson);
        firestore.collection(USER_LIKE)
                .document(articleCreatorEmail)
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","更新成功");
                        }
                    }
                });
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(this).getUserNickname();
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
    public void intentToSingleViewActivity(DataArray data) {
        Intent it = new Intent(this, SingleViewActivity.class);
        it.putExtra("data",data);
        startActivity(it);
    }

    @Override
    public void intentToMyArticleActivity(ArrayList<DataArray> userDataArray) {
        Intent it = new Intent(this, ArticleActivity.class);
        it.putExtra("data",userDataArray);
        startActivity(it);
    }

    @Override
    public void intentToHeartActivity(DataObject data, String mode) {
        Intent it = new Intent(this, HeartActivity.class);
        it.putExtra("mode_data",data);
        it.putExtra("mode",mode);
        startActivity(it);
    }
}
