package com.path.mypath.fragment.heart_fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.path.mypath.R;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.single_view_activity.SingleViewActivity;
import com.path.mypath.tools.UserDataProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class HeartFragment extends Fragment implements HeartFragmentVu{

    private Context context;

    private HeartFragmentPresenter presenter;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    private static final String USER_LIKE = "user_like";

    private static final String HOME_DATA = "home_data";

    private ImageView ivIcon;

    private TextView tvInfo;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static HeartFragment newInstance() {
        HeartFragment fragment = new HeartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initPresenter();
        initFirebase();
    }

    private void initFirebase() {
        firestore = FirebaseFirestore.getInstance();
    }

    private void initPresenter() {
        presenter = new HeartFragmentPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_heart, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.heart_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        ivIcon = view.findViewById(R.id.heart_icon);
        tvInfo = view.findViewById(R.id.heart_info);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DocumentReference snapshot = firestore.collection(USER_LIKE).document(UserDataProvider.getInstance(context).getUserEmail());
        snapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","取得資料失敗 : "+e.toString());
                    return;
                }
                if (snapshot != null){
                    String json = (String) snapshot.get("json");
                    presenter.onCatchLikeDataSuccessful(json);
                }
            }
        });
        DocumentReference homeSnapshot = firestore.collection(HOME_DATA).document(HOME_DATA);
        homeSnapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","HOME PAGE 取得資料失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot!= null){
                    String json = (String) documentSnapshot.get("json");
                    presenter.onCatchHomeDataSuccessful(json);
                }
            }
        });

        DocumentReference userShot = firestore.collection("personal_data").document(UserDataProvider.getInstance(context).getUserEmail());
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

    }

    @Override
    public void setRecyclerView(ArrayList<ArticleLikeNotification> dataArray) {
        HeartAdapter adapter = new HeartAdapter(dataArray,context);
        recyclerView.setAdapter(adapter);
        adapter.setOnHeartLikeItemClickListener(new HeartAdapter.OnHeartLikeItemClickListener() {
            @Override
            public void onClick(ArticleLikeNotification data) {
                Log.i("Michael","點擊了DATA");
                presenter.onHeartLikeItemClickListener(data);
            }

            @Override
            public void onCancelClick(ArticleLikeNotification data) {
                Log.i("Michael","點擊了取消");
                presenter.onCancelButtonClickListener(data);
            }

            @Override
            public void onAcceptClick(ArticleLikeNotification data) {
                Log.i("Michael","點擊了接受");
                presenter.onAcceptButtonClickListener(data);
            }
        });
    }

    @Override
    public void intentToSingleViewPage(DataArray homeData) {
        Intent it = new Intent(context, SingleViewActivity.class);
        it.putExtra("data",homeData);
        context.startActivity(it);
    }

    @Override
    public void updateLikeData(String json) {
        Map<String,Object> map = new HashMap<>();
        map.put("json",json);
        firestore.collection(USER_LIKE)
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","LIKE 資料更新成功");
                        }
                    }
                });
    }

    @Override
    public void updateUserData(String userJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",userJson);
        firestore.collection("personal_data")
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","更新個人資料成功");
                        }
                    }
                });
    }

    @Override
    public void searchFansData(ArticleLikeNotification data) {
        firestore.collection("personal_data")
                .document(data.getUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();

                            if (snapshot != null){
                                String json = (String) snapshot.get("user_json");
                                presenter.onCatchFansData(json,data);
                            }
                        }
                    }
                });
    }

    @Override
    public String getEmail() {
        return UserDataProvider.getInstance(context).getUserEmail();
    }

    @Override
    public String getNickname() {
        return UserDataProvider.getInstance(context).getUserNickname();
    }

    @Override
    public String getPhoto() {
        return UserDataProvider.getInstance(context).getUserPHotoUrl();
    }

    @Override
    public void updateFansData(String userEmail, String fansJson) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_json",fansJson);
        firestore.collection("personal_data")
                .document(userEmail)
                .set(map,SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.i("Michael","粉絲資料成功");
                        }
                    }
                });
    }

    @Override
    public void setView(boolean isShow) {
        tvInfo.setVisibility(isShow ? View.GONE : View.VISIBLE);
        ivIcon.setVisibility(isShow ? View.GONE : View.VISIBLE);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }
}
