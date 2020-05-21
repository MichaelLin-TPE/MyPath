package com.path.mypath.fragment;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private ArrayList<DataArray> dataArrayList;

    private Context context;

    private OnHomeItemClickListener listener;

    private String userNickname;

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
        DataArray itemData = dataArrayList.get(position);
        ImageLoaderProvider.getInstance(context).setImage(itemData.getUserPhoto(), holder.ivPhoto);
        holder.tvName.setText(itemData.getUserNickName());
        holder.tvTime.setText(new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(new Date(itemData.getCurrentTime())));
        holder.tvContent.setText(String.format(Locale.getDefault(), "%s : %s", itemData.getUserNickName(), itemData.getArticleTitle()));
        holder.tvHeartCount.setText(String.format(Locale.getDefault(), "%d 個讚", itemData.getHeartCount()));
        if (itemData.getReplyCount() == 0){
            holder.tvReplyCount.setVisibility(View.INVISIBLE);
        }else {
            holder.tvReplyCount.setVisibility(View.VISIBLE);
            holder.tvReplyCount.setText(String.format(Locale.getDefault(),"%d 則留言",itemData.getReplyCount()));
        }
        if (itemData.getDistance() == 0){
            holder.tvDistance.setVisibility(View.GONE);
        }else {
            holder.tvDistance.setVisibility(View.VISIBLE);
            holder.tvDistance.setText(String.format(Locale.getDefault(),"#移動 %1$,.2f 公尺",itemData.getDistance()));
        }
        if (itemData.getUserNickName().equals(userNickname)){
            holder.ivSend.setVisibility(View.GONE);
        }else {
            holder.ivSend.setVisibility(View.VISIBLE);
        }

        boolean isCheck = false;
        //判斷是否有按過讚
        if (itemData.getHeartPressUsers() != null && itemData.getHeartPressUsers().size() == 0) {
            holder.tvHeartCount.setVisibility(View.GONE);
            holder.ivHeart.setImageResource(R.drawable.heart_not_press);
            Log.i("Michael","沒按過");
        } else {
            holder.tvHeartCount.setVisibility(View.VISIBLE);

            ArrayList<DataUserPresHeart> heartArray = itemData.getHeartPressUsers();

            for (DataUserPresHeart userPress : heartArray) {
                if (userPress.getName().equals(UserDataProvider.getInstance(context).getUserNickname())) {
                    holder.ivHeart.setImageResource(R.drawable.heart_red);
                    isCheck = true;
                    break;
                }
            }
            if (!isCheck){
                holder.ivHeart.setImageResource(R.drawable.heart_not_press);
            }

        }

        /**
         * 這邊判斷要顯示MAP 還是 照片
         *
         */
        if (itemData.getLocationArray() != null && itemData.getLocationArray().size() != 0){
            holder.mapView.setVisibility(View.VISIBLE);
            holder.viewPager.setVisibility(View.GONE);
            //設定MAP
            if (holder.mapView != null) {
                holder.mapView.onCreate(null);
                holder.mapView.onResume();
                holder.mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        PolylineOptions rectOptions = new PolylineOptions();
                        //繪製路線

                        ArrayList<LatLng> locationArray = itemData.getLocationArray();

                        for (LatLng latLng : locationArray) {
                            rectOptions.add(latLng).color(Color.RED);
                        }
                        googleMap.addPolyline(rectOptions);
                        int centerIndex = itemData.getLocationArray().size() / 2;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArray.get(centerIndex)));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    }
                });
            }
        }else if (itemData.getPhotoArray() != null){
            holder.mapView.setVisibility(View.INVISIBLE);
            holder.viewPager.setVisibility(View.VISIBLE);
            PhotoViewPagerAdapter adapter = new PhotoViewPagerAdapter(context,itemData.getPhotoArray());
            holder.viewPager.setAdapter(adapter);
            adapter.setOnPhotoClickListener(new PhotoViewPagerAdapter.OnPhotoClickListener() {
                @Override
                public void onClick() {

                }
            });
        }



        /**
         * 以下是點擊事件
         */

        holder.ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = false;
                int selectIndex = 0;
                if (itemData.getHeartPressUsers() != null && itemData.getHeartPressUsers().size() != 0) {
                    ArrayList<DataUserPresHeart> heartArray = itemData.getHeartPressUsers();
                    for (DataUserPresHeart heart : heartArray){
                        if (heart.getName().equals(UserDataProvider.getInstance(context).getUserNickname())){
                            isCheck = true;
                            break;
                        }
                        selectIndex ++;
                    }

                    if (isCheck){
                        Log.i("Michael","空白愛心");
                        holder.ivHeart.setImageResource(R.drawable.heart_not_press);
                    }else {
                        Log.i("Michael","紅色愛心");
                        holder.ivHeart.setImageResource(R.drawable.heart_red);
                    }
                }else {
                    Log.i("Michael","特殊紅色愛心");
                    holder.ivHeart.setImageResource(R.drawable.heart_red);

                }
                listener.onHeartClick(itemData, position,isCheck,selectIndex);
            }
        });
        holder.ivReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReplyClick(itemData);
            }
        });
        holder.ivSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSendClick(itemData);
            }
        });
        holder.ivSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSortClick();
            }
        });

        holder.tvReplyCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onReplyCountClick(itemData);
            }
        });
        holder.tvHeartCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onHeartCountClick(itemData);
            }
        });
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onUserPhotoClick(itemData);
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataArrayList == null ? 0 : dataArrayList.size();
    }

    public void setData(ArrayList<DataArray> data) {
        this.dataArrayList = data;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView ivPhoto;

        private TextView tvName, tvTime, tvContent, tvHeartCount,tvDistance,tvReplyCount;

        private ImageView ivHeart, ivReply, ivSend, ivSort;

        private MapView mapView;

        private ViewPager viewPager;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReplyCount = itemView.findViewById(R.id.home_item_reply_count);
            viewPager = itemView.findViewById(R.id.home_item_view_pager);
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
            tvDistance = itemView.findViewById(R.id.home_item_article_distance);
        }
    }

    public interface OnHomeItemClickListener {
        void onHeartClick(DataArray data, int position, boolean isCheck,int selectIndex);

        void onReplyClick(DataArray data);

        void onSendClick(DataArray data);

        void onSortClick();

        void onHeartCountClick(DataArray data);
        void onReplyCountClick(DataArray data);
        void onUserPhotoClick(DataArray data);
    }
}
