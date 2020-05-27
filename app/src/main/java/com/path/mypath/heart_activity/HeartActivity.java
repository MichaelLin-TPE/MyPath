package com.path.mypath.heart_activity;

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
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.fragment.FansData;
import com.path.mypath.fragment.heart_fragment.HeartFragmentVu;
import com.path.mypath.tools.UserDataProvider;
import com.path.mypath.user_page_activity.UserPageActivity;

import java.util.ArrayList;

public class HeartActivity extends AppCompatActivity implements HeartActivityVu {

    private HeartActivityPresenter presenter;

    private ImageView ivBack;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    private static final String CHASING = "chasing";

    private static final String FANS = "fans";

    private static final String HEART = "heart";

    private String mode;

    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heart);

        initPresenter();
        initView();
        initBundle();
        initFirebase();
        firestore.collection("user")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            ArrayList<UserData> userDataArray = new ArrayList<>();
                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                UserData data = new UserData();
                                data.setEmail((String) snapshot.get("email"));
                                data.setNickname((String) snapshot.get("display_name"));
                                data.setPhoto((String) snapshot.get("photo"));
                                userDataArray.add(data);
                            }
                            if (userDataArray.size() != 0) {
                                presenter.onCatchAllUserData(userDataArray);
                            }
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
        if (bundle != null) {

            mode = bundle.getString("mode");

            DataArray data = bundle.getParcelable("data");

            DataObject userData = bundle.getParcelable("mode_data");

            if (mode != null){
                if (mode.equals(HEART)){
                    presenter.onCatchUserData(data,mode);
                }else {
                    presenter.onCatchFansData(userData,mode);
                }
            }

        }
    }

    private void initView() {
        tvTitle = findViewById(R.id.heart_toolbar_title);
        ivBack = findViewById(R.id.heart_toolbar_icon);
        recyclerView = findViewById(R.id.heart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBackButtonClickListener();
            }
        });
    }

    private void initPresenter() {
        presenter = new HeartActivityPresenterImpl(this);
    }

    @Override
    public void closePage() {
        finish();
    }

    @Override
    public void setRecyclerView(ArrayList<DataUserPresHeart> heartPressUsers) {
        HeartUserAdapter adapter = new HeartUserAdapter(heartPressUsers, this);
        recyclerView.setAdapter(adapter);
        adapter.setOnHeartItemClickListener(new HeartUserAdapter.OnHeartItemClickListener() {
            @Override
            public void onClick(String name) {
                presenter.onHeartItemClickListener(name);
            }
        });
    }

    @Override
    public void intentToUserPageActivity(String email) {
        Intent it = new Intent(this, UserPageActivity.class);
        it.putExtra("email", email);
        startActivity(it);
    }

    @Override
    public void setNewRecyclerView(ArrayList<FansData> fansArray) {
        FansUserAdapter adapter = new FansUserAdapter(fansArray,this);
        recyclerView.setAdapter(adapter);
        adapter.setOnHeartItemClickListener(new FansUserAdapter.OnHeartItemClickListener() {
            @Override
            public void onClick(String name) {
                presenter.onHeartItemClickListener(name);
            }
        });
    }

    @Override
    public void setTitle(String mode) {
        if (mode.equals(HEART)){
            tvTitle.setText(getString(R.string.heart_count));
        }else if (mode.equals(CHASING)){
            tvTitle.setText(getString(R.string.chasing_user));
        }else if (mode.equals(FANS)){
            tvTitle.setText(getString(R.string.fans_user));
        }
    }

    @Override
    public String getEmail() {
        return UserDataProvider.getInstance(this).getUserEmail();
    }
}
