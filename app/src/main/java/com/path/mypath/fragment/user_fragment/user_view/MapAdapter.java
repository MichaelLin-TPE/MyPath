package com.path.mypath.fragment.user_fragment.user_view;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.fragment.PhotoViewPagerAdapter;
import com.path.mypath.tools.DistanceTool;
import com.path.mypath.tools.ImageLoaderProvider;

import java.util.ArrayList;

public class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

    private ArrayList<DataArray> dataArray;

    private Context context;

    private static final double EARTH_RADIUS = 6378.137;


    public MapAdapter(ArrayList<DataArray> dataArray, Context context) {
        this.dataArray = dataArray;
        this.context = context;
    }

    private OnMapItemClickListener listener;

    public void setOnMapItemClickListener(OnMapItemClickListener listener) {
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


        /**
         * 這邊判斷要顯示MAP 還是 照片
         *
         */
        if (data.getLocationArray() != null && data.getLocationArray().size() != 0) {
            holder.mapView.setVisibility(View.VISIBLE);
            holder.ivPhoto.setVisibility(View.GONE);
            //設定MAP
            if (holder.mapView != null) {
//                holder.mapView.onCreate(null);
//                holder.mapView.onResume();
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

                            double firstLat = locationArray.get(0).latitude;
                            double firstLng = locationArray.get(0).longitude;

                            double secondLat = locationArray.get(locationArray.size() -1).latitude;
                            double secondLng = locationArray.get(locationArray.size() -1).longitude;

                            double distance = getDistance(firstLat,firstLng,secondLat,secondLng);

                            googleMap.animateCamera(CameraUpdateFactory.zoomTo(DistanceTool.getInstance().getZoomKmLevel(distance)));
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
        } else if (data.getPhotoArray() != null && data.getPhotoArray() != null) {
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


    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder.googleMap != null){
            holder.googleMap.clear();
            holder.googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }
    }

    //轉弧度
    private double rad (double radius){
        return radius * Math.PI/180.0;
    }

    private double getDistance(double lat1,double long1,double lat2, double long2){

        double firstRadLat = rad(lat1);
        double firstRadLng = rad(long1);
        double secondRadLat = rad(lat2);
        double secondRadLng = rad(long2);

        double a = firstRadLat - secondRadLat;
        double b = firstRadLng - secondRadLng;

        double cal = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(firstRadLat)
                * Math.cos(secondRadLat) * Math.pow(Math.sin(b / 2), 2))) * EARTH_RADIUS;
        double result = Math.round(cal * 10000d) / 10000d;

        Log.i("Michael","計算出的距離 公尺 : "+result*1024);
        return result*1024;
    }

    @Override
    public int getItemCount() {
        return dataArray == null ? 0 : dataArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private GoogleMap googleMap;

        private MapView mapView;

        private ConstraintLayout itemLayout;

        private ImageView ivPhoto;

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

            Log.i("Michael", "強轉後的DB 為 : " + pix);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(pix, pix);
            itemLayout.setLayoutParams(params);

            if (mapView != null) {
                mapView.onCreate(null);
                mapView.onResume();
                mapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap map) {
                        googleMap = map;
                    }
                });
            }

        }
    }

    public interface OnMapItemClickListener {
        void onClick(DataArray locationArray);
    }
}
