package com.path.mypath.fragment;

import android.content.Context;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.tools.UserDataProvider;

import java.util.Map;


public class HomeFragment extends Fragment implements HomeFragmentVu {

    private Context context;

    private HomeFragmentPresenter presenter;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    private HomeAdapter adapter;

    private static final String PERSONAL_DATA = "personal_data";

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

        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(context).getUserEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null){
                                String json = (String) snapshot.get("user_json");
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
    public void setRecyclerView(DataObject data) {

        adapter = new HomeAdapter(context);
        adapter.setData(data);
        recyclerView.setAdapter(adapter);

        adapter.setOnHomeItemClickListener(new HomeAdapter.OnHomeItemClickListener() {
            @Override
            public void onHeartClick(DataArray articleData, int position) {
                Log.i("Michael","點擊Heart");
                presenter.onHeartClickListener(articleData,position);
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
        firestore.collection(PERSONAL_DATA)
                .document(UserDataProvider.getInstance(context).getUserEmail())
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
}
