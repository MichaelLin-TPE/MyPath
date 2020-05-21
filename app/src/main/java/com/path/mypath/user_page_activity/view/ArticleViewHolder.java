package com.path.mypath.user_page_activity.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.R;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.FansData;
import com.path.mypath.fragment.user_fragment.user_view.MapAdapter;
import com.path.mypath.tools.UserDataProvider;

import java.util.ArrayList;
import java.util.List;

public class ArticleViewHolder extends RecyclerView.ViewHolder {
    private RecyclerView recyclerView;

    private String nickname;

    private Context context;

    private TextView tvNotice;

    private ImageView ivIcon;

    private MapAdapter.OnMapItemClickListener listener;


    private FirebaseFirestore firestore;

    private ArrayList<ArticleLikeNotification> dataArray;

    private Gson gson;

    public void setOnMapItemClickListener(MapAdapter.OnMapItemClickListener listener){
        this.listener = listener;
    }

    public ArticleViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        recyclerView = itemView.findViewById(R.id.user_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        nickname = UserDataProvider.getInstance(context).getUserNickname();
        tvNotice = itemView.findViewById(R.id.user_down_notice);
        ivIcon = itemView.findViewById(R.id.user_down_add_icon);
        tvNotice.setVisibility(View.GONE);
        ivIcon.setVisibility(View.GONE);

        //initFirebase
        firestore = FirebaseFirestore.getInstance();
        gson = new Gson();
    }

    public void setData(DataObject data) {

        boolean isAccept = false;

        if (data.getFansArray() != null && data.getFansArray().size() != 0){
            for (FansData name : data.getFansArray()){
                if (name.getNickname().equals(UserDataProvider.getInstance(context).getUserNickname())){
                    isAccept = true;
                    break;
                }
            }
        }

        if (data.isPublicAccount()){
            tvNotice.setVisibility(View.GONE);
            MapAdapter adapter = new MapAdapter(data.getDataArray(),context);
            recyclerView.setAdapter(adapter);
            adapter.setOnMapItemClickListener(new MapAdapter.OnMapItemClickListener() {
                @Override
                public void onClick(DataArray locationArray) {
                    listener.onClick(locationArray);
                }
            });
        }else if (!data.isPublicAccount() && isAccept){
            tvNotice.setVisibility(View.GONE);
            MapAdapter adapter = new MapAdapter(data.getDataArray(),context);
            recyclerView.setAdapter(adapter);
            adapter.setOnMapItemClickListener(new MapAdapter.OnMapItemClickListener() {
                @Override
                public void onClick(DataArray locationArray) {
                    listener.onClick(locationArray);
                }
            });
        }else {
            tvNotice.setVisibility(View.VISIBLE);
            tvNotice.setText(context.getString(R.string.private_information));
        }
    }
}
