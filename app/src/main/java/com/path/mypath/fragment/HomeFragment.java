package com.path.mypath.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.renderscript.Allocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.tools.UserDataProvider;

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
        recyclerView.setAdapter(adapter);

        adapter.setOnHomeItemClickListener(new HomeAdapter.OnHomeItemClickListener() {
            @Override
            public void onHeartClick(DataArray articleData, int position, boolean isCheck,int selectIndex) {
                Log.i("Michael","點擊Heart");
                presenter.onHeartClickListener(articleData,position,isCheck,selectIndex);
                adapter.notifyItemChanged(position);
            }
            @Override
            public void onReplyClick() {
                Log.i("Michael","點擊reply");
            }

            @Override
            public void onSendClick() {
                Log.i("Michael","點擊send");
            }

            @Override
            public void onSortClick() {
                Log.i("Michael","點擊sort");
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
}
