package com.path.mypath.search_user_activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.heart_activity.UserData;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.util.ArrayList;

public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.ViewHolder> {

    private ArrayList<UserData> dataArray;

    private Context context;

    private OnChaseButtonClickListener listener;

    private ArrayList<ArrayList<ArticleLikeNotification>> likeArray;


    public void setOnChaseButtonClickListener(OnChaseButtonClickListener listener){
        this.listener = listener;
    }

    public SearchUserAdapter(ArrayList<UserData> dataArray, Context context) {
        this.dataArray = dataArray;
        this.context = context;
    }

    public void setLikeData(ArrayList<ArrayList<ArticleLikeNotification>> likeArray){
        this.likeArray = likeArray;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_user_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserData data = dataArray.get(position);
        ImageLoaderProvider.getInstance(context).setImage(data.getPhoto(),holder.ivPhoto);
        holder.tvName.setText(data.getNickname());
        String userNickname = UserDataProvider.getInstance(context).getUserNickname();
        Log.i("Michael","我的名字 : "+userNickname);
        for (ArrayList<ArticleLikeNotification> object : likeArray){
            boolean isDataFound  = false;
            for (ArticleLikeNotification notification : object){
                if (notification.getArticleCreatorName().equals(data.getNickname())){
                    if (notification.getUserNickname().equals(userNickname) && notification.getinviteStatusCode() == 1){
                        Log.i("Michael",userNickname+" 追蹤 "+data.getNickname());
                        holder.btnSend.setText(context.getString(R.string.already_chasing));
                        holder.btnSend.setEnabled(false);
                        isDataFound = true;
                        break;
                    }else if (notification.getUserNickname().equals(userNickname) && notification.getinviteStatusCode() == 3){
                        Log.i("Michael",userNickname+" 發送追蹤邀請");
                        holder.btnSend.setText(context.getString(R.string.send_invite_already));
                        holder.btnSend.setEnabled(false);
                        isDataFound = true;
                        break;
                    }else {
                        Log.i("Michael",userNickname+"沒追蹤");
                        holder.btnSend.setText(context.getString(R.string.chasing));
                        holder.btnSend.setEnabled(true);
                        isDataFound = true;
                    }

                }
            }
            if (!isDataFound){
                Log.i("Michael","沒有"+data.getNickname()+"的資料");
                holder.btnSend.setText(context.getString(R.string.chasing));
                holder.btnSend.setEnabled(true);
            }else {
                break;
            }
        }

        holder.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(data);
            }
        });
        holder.ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick(data);
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

        private Button btnSend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivPhoto = itemView.findViewById(R.id.search_user_item_photo);
            tvName = itemView.findViewById(R.id.search_user_item_name);
            btnSend = itemView.findViewById(R.id.search_user_item_btn);

        }
    }
    public interface OnChaseButtonClickListener{
        void onClick(UserData data);
        void onPhotoClick(UserData data);
    }
}
