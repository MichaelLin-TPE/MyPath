package com.path.mypath.fragment.user_fragment.user_view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.MainActivity;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.util.ArrayList;
import java.util.Locale;

public class UserInfoViewHolder extends RecyclerView.ViewHolder {

    private RoundedImageView ivPhoto;

    private TextView tvNickname,tvSentence,tvArticleCount,tvFriendCount,tvChasingCount,tvArticleInfo,tvFriendInfo,tvChasingInfo;

    private Button btnEdit;

    private Context context;

    private ImageView ivEditName,ivEditSentence,ivLogout;

    private OnUserInformationClickListener listener;

    public void setOnUserInformationClickListener(OnUserInformationClickListener listener){
        this.listener = listener;
    }

    public UserInfoViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        ivPhoto = itemView.findViewById(R.id.user_photo);
        tvNickname = itemView.findViewById(R.id.user_nickname);
        tvSentence = itemView.findViewById(R.id.user_sentence);
        tvArticleCount = itemView.findViewById(R.id.user_article_count);
        tvFriendCount = itemView.findViewById(R.id.user_friends_count);
        tvChasingCount = itemView.findViewById(R.id.user_chasing_count);
        btnEdit = itemView.findViewById(R.id.user_edit_info_btn);
        ivEditName = itemView.findViewById(R.id.user_edit_nickname);
        ivEditSentence = itemView.findViewById(R.id.user_edit_sentence);
        ivLogout = itemView.findViewById(R.id.user_logout);
        tvArticleInfo = itemView.findViewById(R.id.user_article_info);
        tvChasingInfo = itemView.findViewById(R.id.user_chasing_info);
        tvFriendInfo = itemView.findViewById(R.id.user_fans_info);
    }

    public void setData(DataObject data){

        ImageLoaderProvider.getInstance(context).setImage(data.getUserPhoto(),ivPhoto);
        String nickname = UserDataProvider.getInstance(context).getUserNickname();
        tvNickname.setText(nickname);
        tvSentence.setText(data.getSentence());
        tvArticleCount.setText(String.format(Locale.getDefault(),"%d",data.getArticleCount()));
        tvFriendCount.setText(String.format(Locale.getDefault(),"%d",data.getFriendCount()));
        tvChasingCount.setText(String.format(Locale.getDefault(),"%d",data.getChasingCount()));
        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoClick();
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditBtnClick();
            }
        });
        ivEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditNicknameClick();
            }
        });
        ivEditSentence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditSentenceClick();
            }
        });
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onLogoutClick();

            }
        });
        tvArticleCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArticleCountClick(data.getDataArray());
            }
        });
        tvFriendCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFansCountClick(data);
            }
        });
        tvChasingCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChasingCountClick(data);
            }
        });
        tvArticleInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onArticleCountClick(data.getDataArray());
            }
        });
        tvFriendInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onFansCountClick(data);
            }
        });
        tvChasingInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onChasingCountClick(data);
            }
        });
    }

    public interface OnUserInformationClickListener{
        void onPhotoClick();
        void onEditBtnClick();
        void onEditNicknameClick();
        void onEditSentenceClick();
        void onLogoutClick();
        void onArticleCountClick(ArrayList<DataArray> dataArray);
        void onChasingCountClick(DataObject data);
        void onFansCountClick(DataObject data);
    }
}
