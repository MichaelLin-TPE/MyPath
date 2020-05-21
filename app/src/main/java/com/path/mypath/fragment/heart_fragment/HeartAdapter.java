package com.path.mypath.fragment.heart_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private static final int SEND = 3;

    private static final int REJECTED = 2;

    private static final int ACCEPT = 1;

    private static final int REPLY = 4;

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
        holder.tvSecond.setText(String.format(Locale.getDefault(),"%s",new SimpleDateFormat("hh:mm a",Locale.TAIWAN).format(new Date(data.getPressedCurrentTime()))));
        ImageLoaderProvider.getInstance(context).setImage(data.getUserPhoto(),holder.ivPhoto);
        if (data.isInvite() && data.getinviteStatusCode() == SEND){
            holder.tvContent.setText(String.format(Locale.getDefault(),"%s 發送了追蹤邀請.",data.getUserNickname()));
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCancelClick(data);
                }
            });
            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onAcceptClick(data);
                }
            });
        }else if (data.isInvite() && data.getinviteStatusCode() == REJECTED){
            holder.tvContent.setText(String.format(Locale.getDefault(),"你已經拒絕 %s 的追蹤.",data.getUserNickname()));
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

        }else if (data.isInvite() && data.getinviteStatusCode() == ACCEPT){
            holder.tvContent.setText(String.format(Locale.getDefault(),"你已經接受 %s 的追蹤.",data.getUserNickname()));
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);

        }else if (data.getinviteStatusCode() == REPLY){
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.tvContent.setText(String.format(Locale.getDefault(),"%s 在你的\"%s\"底下留言 : %s",data.getUserNickname(),data.getArticleTitle(),data.getReplyMessage()));
            holder.clickArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(data);
                }
            });
        }else {
            holder.btnAccept.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.tvContent.setText(String.format(Locale.getDefault(),"%s 說你的貼文 \"%s\" 很讚",data.getUserNickname(),data.getArticleTitle()));
            holder.clickArea.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(data);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dataArray == null ? 0 : dataArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTime,tvContent,tvSecond;

        private ConstraintLayout clickArea;

        private RoundedImageView ivPhoto;

        private Button btnCancel,btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnCancel = itemView.findViewById(R.id.heart_item_btn_cancel);
            btnAccept = itemView.findViewById(R.id.heart_item_btn_confirm);
            tvSecond = itemView.findViewById(R.id.heart_item_second);
            clickArea = itemView.findViewById(R.id.heart_item_click_area);
            tvTime = itemView.findViewById(R.id.heart_item_time);
            tvContent = itemView.findViewById(R.id.heart_item_content);
            ivPhoto = itemView.findViewById(R.id.heart_item_photo);
        }
    }

    public interface OnHeartLikeItemClickListener{
        void onClick(ArticleLikeNotification data);
        void onCancelClick(ArticleLikeNotification data);
        void onAcceptClick(ArticleLikeNotification data);
    }
}
