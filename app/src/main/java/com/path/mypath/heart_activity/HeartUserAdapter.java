package com.path.mypath.heart_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.tools.ImageLoaderProvider;

import java.util.ArrayList;

public class HeartUserAdapter extends RecyclerView.Adapter<HeartUserAdapter.ViewHolder> {


    private ArrayList<DataUserPresHeart> dataArray;

    private Context context;

    private  OnHeartItemClickListener listener;

    public void setOnHeartItemClickListener(OnHeartItemClickListener listener){
        this.listener = listener;
    }

    public HeartUserAdapter(ArrayList<DataUserPresHeart> dataArray, Context context) {
        this.dataArray = dataArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.heart_user_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataUserPresHeart data = dataArray.get(position);
        ImageLoaderProvider.getInstance(context).setImage(data.getPhotoUrl(),holder.ivPhoto);
        holder.tvName.setText(data.getName());
        holder.clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(data.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataArray == null ? 0 : dataArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RoundedImageView ivPhoto;

        private TextView tvName;

        private ConstraintLayout clickArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.heart_user_item_photo);
            tvName = itemView.findViewById(R.id.heart_user_item_name);
            clickArea = itemView.findViewById(R.id.heart_user_item_click_area);
        }
    }

    public interface OnHeartItemClickListener{
        void onClick(String name);
    }
}
