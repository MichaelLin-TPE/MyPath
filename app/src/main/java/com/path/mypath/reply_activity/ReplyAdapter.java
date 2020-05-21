package com.path.mypath.reply_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataReply;
import com.path.mypath.tools.ImageLoaderProvider;

import java.util.Locale;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ViewHolder> {

    private DataArray data;

    private Context context;

    public ReplyAdapter(DataArray data, Context context) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.reply_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataReply dataReply = data.getReplyArray().get(position);
        ImageLoaderProvider.getInstance(context).setImage(dataReply.getPhoto(),holder.ivPhoto);

        holder.tvNickname.setText(String.format(Locale.getDefault(),"%s 說 : ",dataReply.getNickname()));

        holder.tvMessage.setText(dataReply.getMessage());
    }

    @Override
    public int getItemCount() {
        return data.getReplyArray() == null ? 0 : data.getReplyArray().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RoundedImageView ivPhoto;

        private TextView tvNickname,tvMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPhoto = itemView.findViewById(R.id.reply_item_photo);
            tvNickname = itemView.findViewById(R.id.reply_item_nickname);
            tvMessage = itemView.findViewById(R.id.reply_item_message);
        }
    }
}
