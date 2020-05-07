package com.path.mypath.fragment;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private DataObject data;

    private Context context;

    private OnHomeItemClickListener listener;

    private boolean isPressed;

    public void setOnHomeItemClickListener(OnHomeItemClickListener listener) {
        this.listener = listener;
    }

    public HomeAdapter(Context context) {
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.home_fragment_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataArray itemData = data.getDataArray().get(position);
        ImageLoaderProvider.getInstance(context).setImage(itemData.getUserPhoto(), holder.ivPhoto);
        holder.tvName.setText(data.getUserNickname());
        holder.tvTime.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(new Date(itemData.getCurrentTime())));
        holder.tvContent.setText(String.format(Locale.getDefault(), "%s : %s", data.getUserNickname(), itemData.getArticleTitle()));
        holder.tvHeartCount.setText(String.format(Locale.getDefault(), "%d 個讚", itemData.getHeartCount()));

        //判斷是否有按過讚
        if (itemData.getHeartPressUsers() != null && itemData.getHeartPressUsers().size() == 0) {
            holder.tvHeartCount.setVisibility(View.GONE);
            holder.ivHeart.setImageResource(R.drawable.heart_not_press);
        } else {
            holder.tvHeartCount.setVisibility(View.VISIBLE);
            for (DataUserPresHeart userPress : itemData.getHeartPressUsers()) {
                if (userPress.getName().equals(data.getUserNickname())) {
                    holder.ivHeart.setImageResource(R.drawable.heart_red);
                    isPressed = true;
                    break;
                }
            }
        }
        //設定MAP
        if (holder.mapView != null) {
            holder.mapView.onCreate(null);
            holder.mapView.onResume();
            holder.mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    PolylineOptions rectOptions = new PolylineOptions();
                    //繪製路線
                    for (LatLng latLng : itemData.getLocationArray()) {
                        rectOptions.add(latLng).color(Color.RED);
                    }
                    googleMap.addPolyline(rectOptions);
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(itemData.getLocationArray().get(itemData.getLocationArray().size() - 1)));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                }
            });
        }

        holder.ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = false;
                if (itemData.getHeartPressUsers() != null && itemData.getHeartPressUsers().size() != 0) {

                }else {
                    holder.ivHeart.setImageResource(R.drawable.heart_red);

                }


                listener.onHeartClick(itemData, position);
            }
        });
        holder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReplyClick();
            }
        });
        holder.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSendClick();
            }
        });
        holder.ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSortClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return data.getDataArray() == null ? 0 : data.getDataArray().size();
    }

    public void setData(DataObject data) {
        this.data = data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView ivPhoto;

        private TextView tvName, tvTime, tvContent, tvHeartCount;

        private ImageView ivHeart, ivReply, ivSend, ivSort;

        private MapView mapView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.home_item_photo);
            tvName = itemView.findViewById(R.id.home_item_name);
            tvTime = itemView.findViewById(R.id.home_item_time);
            tvContent = itemView.findViewById(R.id.home_item_article_content);
            tvHeartCount = itemView.findViewById(R.id.home_item_heart_count);
            ivHeart = itemView.findViewById(R.id.home_item_heart);
            ivReply = itemView.findViewById(R.id.home_item_reply);
            ivSend = itemView.findViewById(R.id.home_item_send);
            ivSort = itemView.findViewById(R.id.home_item_sort);
            mapView = itemView.findViewById(R.id.home_item_map_view);
        }
    }

    public interface OnHomeItemClickListener {
        void onHeartClick(DataArray data, int position);

        void onReplyClick();

        void onSendClick();

        void onSortClick();
    }
}
