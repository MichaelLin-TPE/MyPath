package com.path.mypath.fragment.user_fragment.user_view;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.util.Locale;

public class UserInfoViewHolder extends RecyclerView.ViewHolder {

    private RoundedImageView ivPhoto;

    private TextView tvNickname,tvSentence,tvArticleCount,tvFriendCount,tvChasingCount;

    private Button btnEdit;

    private Context context;

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
    }

    public void setData(DataObject data){

        ImageLoaderProvider.getInstance(context).setImage(data.getUserPhoto(),ivPhoto);
        String nickname = UserDataProvider.getInstance(context).getUserNickname();
        String sentence = UserDataProvider.getInstance(context).getSentence();
        tvNickname.setText(nickname);
        tvSentence.setText(sentence);
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
    }

    public interface OnUserInformationClickListener{
        void onPhotoClick();
        void onEditBtnClick();
    }
}
