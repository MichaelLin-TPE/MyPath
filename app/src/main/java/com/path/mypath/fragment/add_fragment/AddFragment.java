package com.path.mypath.fragment.add_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.fragment.user_fragment.user_view.MapAdapter;
import com.path.mypath.single_view_activity.SingleViewActivity;

import java.util.ArrayList;


public class AddFragment extends Fragment implements AddFragmentVu {

    private AddFragmentPresenter presenter;

    private Context context;

    private TextView tvSearch;

    private ImageView ivSearch;

    private RecyclerView recyclerView;

    private FirebaseFirestore firestore;

    private static final String PUBLIC_DATA = "public_data";

    private static final String HOME_DATA = "home_data";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public static AddFragment newInstance() {
        AddFragment fragment = new AddFragment();
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
        presenter = new AddFragmentPresenterImpl(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        tvSearch = view.findViewById(R.id.search_text);
        ivSearch = view.findViewById(R.id.search_icon);
        recyclerView = view.findViewById(R.id.search_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DocumentReference snapshot = firestore.collection(HOME_DATA).document(HOME_DATA);
        snapshot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","搜尋頁面 蒐集資料失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("json");
                    presenter.onCatchHomeDataSuccess(json);
                }
            }
        });


        presenter.onShowPublicData();
    }

    @Override
    public void searchFirebaseData() {
        firestore.collection(PUBLIC_DATA)
                .document(PUBLIC_DATA)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null){
                            DocumentSnapshot snapshot = task.getResult();
                            if (snapshot != null){
                                String json = (String) snapshot.get("public_json");
                                if (json != null){
                                    presenter.onCatchPublicDataSuccessful(json);
                                }
                            }
                        }
                    }
                });

    }

    @Override
    public void setRecyclerView(ArrayList<DataArray> dataArray) {
        SearchAdapter adapter = new SearchAdapter(dataArray,context);
        recyclerView.setAdapter(adapter);
        adapter.setOnMapItemClickListener(new MapAdapter.OnMapItemClickListener() {
            @Override
            public void onClick(DataArray locationData) {
                Log.i("Michael","公開資料被點擊");
                presenter.onMapItemClickListener(locationData);
            }
        });
    }

    @Override
    public void intentToSingleViewActivity(DataArray locationData) {
        Intent it = new Intent(context, SingleViewActivity.class);
        it.putExtra("data",locationData);
        if (getActivity() != null){
            getActivity().startActivity(it);
        }
    }
}
