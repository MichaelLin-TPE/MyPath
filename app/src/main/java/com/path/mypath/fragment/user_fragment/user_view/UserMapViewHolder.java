package com.path.mypath.fragment.user_fragment.user_view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.tools.UserDataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class UserMapViewHolder extends RecyclerView.ViewHolder {

    private RecyclerView recyclerView;

    private String nickname;

    private Context context;

    private TextView tvNotice;

    private ImageView ivIcon;

    private OnUserDownClickListener listener;

    private ArrayList<DataArray> dataArray;

    private Handler handler = new Handler();

    public void setOnUserDownClickListener(OnUserDownClickListener listener){
        this.listener = listener;
    }

    public UserMapViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        recyclerView = itemView.findViewById(R.id.user_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(context,3));
        nickname = UserDataProvider.getInstance(context).getUserNickname();
        tvNotice = itemView.findViewById(R.id.user_down_notice);
        ivIcon = itemView.findViewById(R.id.user_down_add_icon);
    }

    private Runnable showView = new Runnable() {
        @Override
        public void run() {

        }
    };

    public void setData(ArrayList<DataArray> dataArray){
        ArrayList<DataArray> locationArray = new ArrayList<>();

        if (dataArray != null && dataArray.size() != 0){

            Collections.sort(dataArray, new Comparator<DataArray>() {
                @Override
                public int compare(DataArray o1, DataArray o2) {
                    Date d1,d2;

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss", Locale.TAIWAN);

                    try{
                        String currentTime1 = sdf.format(new Date(o1.getCurrentTime()));
                        String currentTime2 = sdf.format(new Date(o2.getCurrentTime()));
                        d1 = sdf.parse(currentTime1);
                        d2 = sdf.parse(currentTime2);

                    }catch (Exception e){
                        e.printStackTrace();
                        return 0;
                    }
                    if (d1 != null && d2 != null){
                        if (d1.before(d2)){
                            return 1;
                        }else {
                            return -1;
                        }
                    }else {
                        return 0;
                    }
                }
            });


            for (DataArray data : dataArray){
                if (data.getUserNickName().equals(nickname)){
                    locationArray.add(data);
                }
            }
        }
        if (locationArray.size() != 0){
            MapAdapter adapter = new MapAdapter(locationArray,context);
            recyclerView.setAdapter(adapter);
            tvNotice.setVisibility(View.GONE);
            ivIcon.setVisibility(View.GONE);
            adapter.setOnMapItemClickListener(new MapAdapter.OnMapItemClickListener() {
                @Override
                public void onClick(DataArray locationArray) {
                    listener.onMapItemClick(locationArray);
                }
            });
        }else {
            tvNotice.setVisibility(View.VISIBLE);
            ivIcon.setVisibility(View.VISIBLE);
            ivIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onIconClick();
                }
            });
        }
    }

    public interface OnUserDownClickListener{
        void onIconClick();

        void onMapItemClick(DataArray locationArray);
    }
}
