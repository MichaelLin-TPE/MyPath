package com.path.mypath.chat_room_activity.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.fragment.MessageArray;
import com.path.mypath.tools.ImageLoaderProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LeftMessageViewHolder extends RecyclerView.ViewHolder {

    private RoundedImageView ivPhoto;

    private TextView tvMessage,tvTime,tvName;

    private Context context;

    public LeftMessageViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        ivPhoto = itemView.findViewById(R.id.chat_left_item_user_photo);
        tvMessage = itemView.findViewById(R.id.chat_left_item_message);
        tvTime = itemView.findViewById(R.id.chat_left_item_time);
        tvName = itemView.findViewById(R.id.chat_left_item_name);
    }

    public void setData(ArrayList<MessageArray> msgArray, int position) {
        MessageArray data = msgArray.get(position);
        ImageLoaderProvider.getInstance(context).setImage(data.getUserPhotoUrl(),ivPhoto);
        tvMessage.setText(data.getMessage());
        tvTime.setText(new SimpleDateFormat("hh:mm a", Locale.TAIWAN).format(new Date(data.getTime())));
        tvName.setText(data.getUserNickname());
    }
}
