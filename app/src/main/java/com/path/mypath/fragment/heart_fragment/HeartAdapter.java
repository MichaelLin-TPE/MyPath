package com.path.mypath.fragment.heart_fragment;

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
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.tools.ImageLoaderProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HeartAdapter extends RecyclerView.Adapter<HeartAdapter.ViewHolder> {

    private ArrayList<ArticleLikeNotification> dataArray;

    private Context context;

    private OnHeartLikeItemClickListener listener;

    public void setOnHeartLikeItemClickListener(OnHeartLikeItemClickListener listener){
        this.listener = listener;
    }

    public HeartAdapter(ArrayList<ArticleLikeNotification> dataArray, Context context) {
        this.dataArray = dataArray;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.heart_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ArticleLikeNotification data = dataArray.get(position);
        holder.tvTime.setText(String.format(Locale.getDefault(),"%s",new SimpleDateFormat("yyyy/MM/dd", Locale.TAIWAN).format(new Date(data.getPressedCurrentTime()))));
        ImageLoaderProvider.getInstance(context).setImage(data.getUserPhoto(),holder.ivPhoto);
        holder.tvContent.setText(String.format(Locale.getDefault(),"%s 說你的貼文 \"%s\" 很讚",data.getUserNickname(),data.getArticleTitle()));
        holder.clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(data);
            }
        });
        holder.tvSecond.setText(String.format(Locale.getDefault(),"%s",new SimpleDateFormat("hh:mm a",Locale.TAIWAN).format(new Date(data.getPressedCurrentTime()))));
    }

    @Override
    public int getItemCount() {
        return dataArray == null ? 0 : dataArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime,tvContent,tvSecond;

        private ConstraintLayout clickArea;

        private RoundedImageView ivPhoto;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSecond = itemView.findViewById(R.id.heart_item_second);
            clickArea = itemView.findViewById(R.id.heart_item_click_area);
            tvTime = itemView.findViewById(R.id.heart_item_time);
            tvContent = itemView.findViewById(R.id.heart_item_content);
            ivPhoto = itemView.findViewById(R.id.heart_item_photo);
        }
    }

    public interface OnHeartLikeItemClickListener{
        void onClick(ArticleLikeNotification data);
    }
}
