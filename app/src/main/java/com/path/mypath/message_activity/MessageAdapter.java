package com.path.mypath.message_activity;

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
import com.path.mypath.tools.ImageLoaderProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private ArrayList<MessageListDTO> dataArray;

    private ArrayList<String> roomIdArray;

    private Context context;

    private OnMessageItemClickListener listener;

    public void setOnMessageItemClickListener(OnMessageItemClickListener listener){
        this.listener = listener;
    }

    public MessageAdapter(ArrayList<MessageListDTO> dataArray, Context context,ArrayList<String> roomIdArray) {
        this.dataArray = dataArray;
        this.context = context;
        this.roomIdArray = roomIdArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.message_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageListDTO data =dataArray.get(position);
        String  roomId = roomIdArray.get(position);
        ImageLoaderProvider.getInstance(context).setImage(data.getUserPhoto(),holder.ivPhoto);
        holder.tvMessage.setText(data.getUserMessage());
        holder.tvName.setText(data.getUserNickname());
        holder.tvTime.setText(new SimpleDateFormat("hh:mm a", Locale.TAIWAN).format(new Date(data.getTime())));
        holder.clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(roomId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataArray == null ? 0 : dataArray.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView ivPhoto;

        private TextView tvName,tvMessage,tvTime;

        private ConstraintLayout clickArea;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.message_item_photo);
            tvName = itemView.findViewById(R.id.message_item_name);
            tvTime = itemView.findViewById(R.id.message_item_time);
            tvMessage = itemView.findViewById(R.id.message_item_message);
            clickArea = itemView.findViewById(R.id.message_item_click_area);
        }
    }

    public interface OnMessageItemClickListener{
        void onClick(String roomId);
    }
}
