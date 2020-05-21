package com.path.mypath.chat_room_activity.view;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.path.mypath.R;
import com.path.mypath.fragment.MessageArray;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RightMessageViewHolder extends RecyclerView.ViewHolder {

    private TextView tvTime,tvMessage;

    public RightMessageViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        tvTime = itemView.findViewById(R.id.chat_item_time);
        tvMessage = itemView.findViewById(R.id.chat_item_message);
    }

    public void setData(ArrayList<MessageArray> msgArray, int position) {
        MessageArray data = msgArray.get(position);
        tvTime.setText(new SimpleDateFormat("hh:mm a", Locale.TAIWAN).format(new Date(data.getTime())));
        tvMessage.setText(data.getMessage());
    }
}
