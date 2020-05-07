package com.path.mypath.fragment.user_fragment.user_view;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    private ArrayList<DataArray> dataArray;

    private Context context;


    public MapAdapter(ArrayList<DataArray> dataArray, Context context) {
        this.dataArray = dataArray;
        this.context = context;
    }

    private OnMapItemClickListener listener;

    public void setOnMapItemClickListener(OnMapItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.user_map_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        DataArray data = dataArray.get(position);

        PolylineOptions rectOptions = new PolylineOptions();
        //繪製路線
        for (LatLng latLng : data.getLocationArray()) {
            rectOptions.add(latLng).color(Color.RED);
        }
        holder.googleMap.addPolyline(rectOptions);
        holder.googleMap.moveCamera(CameraUpdateFactory.newLatLng(data.getLocationArray().get(data.getLocationArray().size() - 1)));
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(data.getLocationArray());
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataArray == null ? 0 : dataArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private GoogleMap googleMap;

        private MapView mapView;

        private ConstraintLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.map_layout);
            mapView = itemView.findViewById(R.id.map_item);

            //設定框框大小

            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            //取得螢幕框度
            float width = context.getResources().getDisplayMetrics().widthPixels;
            //除以3
            float singleItemSize = (float) (width / 3);
            //轉成DP
            float singleItemDb = singleItemSize / metrics.density;
            //強轉int
            int dp = (int)singleItemDb;
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(dp,dp);
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

    public interface OnMapItemClickListener{
        void onClick(ArrayList<LatLng> locationArray);
    }
}
