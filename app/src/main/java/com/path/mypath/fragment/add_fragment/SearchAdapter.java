package com.path.mypath.fragment.add_fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.fragment.PhotoViewPagerAdapter;
import com.path.mypath.fragment.user_fragment.user_view.MapAdapter;
import com.path.mypath.tools.DistanceTool;
import com.path.mypath.tools.ImageLoaderProvider;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<DataArray> dataArrayList;

    private Context context;

    public SearchAdapter(ArrayList<DataArray> dataArrayList, Context context) {
        this.dataArrayList = dataArrayList;
        this.context = context;
    }

    private MapAdapter.OnMapItemClickListener listener;

    public void setOnMapItemClickListener(MapAdapter.OnMapItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_map_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataArray data = dataArrayList.get(position);


        /**
         * 這邊判斷要顯示MAP 還是 照片
         *
         */
        if (data.getLocationArray() != null && data.getLocationArray().size() != 0) {
            holder.mapView.setVisibility(View.VISIBLE);
            holder.ivPhoto.setVisibility(View.GONE);
            //設定MAP
            if (holder.mapView != null) {
                holder.mapView.onCreate(null);
                holder.mapView.onResume();
                holder.mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        PolylineOptions rectOptions = new PolylineOptions();
                        //繪製路線

                        ArrayList<LatLng> locationArray = data.getLocationArray();

                        for (LatLng latLng : locationArray) {
                            rectOptions.add(latLng).color(Color.RED);
                        }
                        googleMap.addPolyline(rectOptions);
                        int centerIndex = locationArray.size() / 2;
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArray.get(centerIndex)));
                        if (data.getDistance() != 0){
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(DistanceTool.getInstance().getZoomKmLevel(data.getDistance())));
                        }else {
                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                        }
                        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                listener.onClick(data);
                            }
                        });
                    }
                });
            }
        } else if (data.getPhotoArray() != null) {
            holder.mapView.setVisibility(View.GONE);
            holder.ivPhoto.setVisibility(View.VISIBLE);
            ImageLoaderProvider.getInstance(context).setImage(data.getPhotoArray().get(0),holder.ivPhoto);
            holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(data);
                }
            });
        }


//        if (holder.mapView != null){
//
//            holder.mapView.onCreate(null);
//            holder.mapView.onResume();
//            holder.mapView.getMapAsync(new OnMapReadyCallback() {
//                @Override
//                public void onMapReady(GoogleMap map) {
//                    PolylineOptions rectOptions = new PolylineOptions();
//                    //繪製路線
//                    ArrayList<LatLng> locationArray = data.getLocationArray();
//                    for (LatLng latLng : locationArray) {
//                        rectOptions.add(latLng).color(Color.RED);
//                    }
//                    holder.googleMap = map;
//                    holder.googleMap.addPolyline(rectOptions);
//                    holder.googleMap.moveCamera(CameraUpdateFactory.newLatLng(locationArray.get(data.getLocationArray().size() - 1)));
//                    holder.googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
//                    holder.googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//                        @Override
//                        public void onMapClick(LatLng latLng) {
//                            listener.onClick(data);
//                        }
//                    });
//                }
//            });
//        }
    }

    @Override
    public int getItemCount() {
        return dataArrayList == null ? 0 : dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private GoogleMap googleMap;

        private MapView mapView;

        private ConstraintLayout itemLayout;

        private RoundedImageView ivPhoto;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.map_layout);
            mapView = itemView.findViewById(R.id.map_item);
            ivPhoto = itemView.findViewById(R.id.map_photo);
            //設定框框大小

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            //取得螢幕框度
            float width = context.getResources().getDisplayMetrics().widthPixels;
            //除以3
            float singleItemSize = (float) (width / 3);
            //轉成DP
            float singleItemDb = singleItemSize / metrics.density;
            //強轉int
            int pix = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, singleItemDb, context.getResources().getDisplayMetrics());

            Log.i("Michael","強轉後的DB 為 : "+pix);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pix,pix);
            itemLayout.setLayoutParams(params);

            if (mapView != null) {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        googleMap = map;
                        //        holder.googleMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),
//                location.getLongitude())).title("目前位置"));
                        googleMap.setMyLocationEnabled(true);
                        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                        googleMap.getUiSettings().setZoomControlsEnabled(true);  // 右下角的放大縮小功能
                        googleMap.getUiSettings().setCompassEnabled(true);       // 左上角的指南針，要兩指旋轉才會出現
                        googleMap.getUiSettings().setMapToolbarEnabled(true);    // 右下角的導覽及開啟 Google Map功能

//        holder.googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
                        googleMap.animateCamera(CameraUpdateFactory.zoomTo(16));
                    }
                });
            }
        }
    }
}
