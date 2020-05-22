package com.path.mypath.user_page_activity.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.makeramen.roundedimageview.RoundedImageView;
import com.path.mypath.R;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.tools.ImageLoaderProvider;
import com.path.mypath.tools.UserDataProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InformationViewHolder extends RecyclerView.ViewHolder {

    private RoundedImageView ivPhoto;

    private TextView tvNickname,tvSentence,tvArticleCount,tvFriendCount,tvChasingCount;

    private ImageView ivLogout;

    private Button btnEdit,btnSend;

    private ImageView ivEditName,ivEditSentence;

    private Context context;

    private OnUserPageClickListener listener;

    private FirebaseFirestore firestore;

    private ArrayList<ArticleLikeNotification> dataArray;

    private static final int SEND = 3;

    private static final int REJECTED = 2;

    private static final int ACCEPT = 1;

    private static final int NORMAL = 0;

    private Gson gson;

    public void setOnUserPageClickListener(OnUserPageClickListener listener){
        this.listener = listener;
    }

    public InformationViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        this.context = context;
        ivLogout = itemView.findViewById(R.id.user_logout);
        ivPhoto = itemView.findViewById(R.id.user_photo);
        tvNickname = itemView.findViewById(R.id.user_nickname);
        tvSentence = itemView.findViewById(R.id.user_sentence);
        tvArticleCount = itemView.findViewById(R.id.user_article_count);
        tvFriendCount = itemView.findViewById(R.id.user_friends_count);
        tvChasingCount = itemView.findViewById(R.id.user_chasing_count);
        btnEdit = itemView.findViewById(R.id.user_edit_info_btn);
        btnSend = itemView.findViewById(R.id.user_btn_send_invite);
        ivEditName = itemView.findViewById(R.id.user_edit_nickname);
        ivEditSentence = itemView.findViewById(R.id.user_edit_sentence);
        btnEdit.setVisibility(View.GONE);
        btnSend.setVisibility(View.VISIBLE);
        ivEditName.setVisibility(View.GONE);
        ivEditSentence.setVisibility(View.GONE);
        ivLogout.setVisibility(View.GONE);
        //initFirebase
        gson = new Gson();
        firestore = FirebaseFirestore.getInstance();

    }

    public void setData(DataObject data) {

        DocumentReference likeShot = firestore.collection("user_like").document(data.getEmail());

        likeShot.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null){
                    Log.i("Michael","LIKE 資料取得失敗 : "+e.toString());
                    return;
                }
                if (documentSnapshot != null){
                    String json = (String) documentSnapshot.get("json");
                    if (json != null){
                        dataArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());

                        for (ArticleLikeNotification notification : dataArray){
                            if (notification.getUserNickname().equals(UserDataProvider.getInstance(context).getUserNickname())&& notification.isInvite()){
                                if (notification.getinviteStatusCode() == SEND){
                                    btnSend.setEnabled(false);
                                    btnSend.setVisibility(View.VISIBLE);
                                    btnSend.setText(context.getString(R.string.send_invite_already));
                                    break;
                                }else if (notification.getinviteStatusCode() == ACCEPT){
                                    btnSend.setVisibility(View.GONE);
                                    break;
                                }else if (notification.getinviteStatusCode() == REJECTED){
                                    btnSend.setVisibility(View.VISIBLE);
                                    btnSend.setEnabled(true);
                                    btnSend.setText(context.getString(R.string.send_invite));
                                    break;
                                }
                            }
                        }
                        ImageLoaderProvider.getInstance(context).setImage(data.getUserPhoto(),ivPhoto);
                        tvNickname.setText(data.getUserNickname());
                        tvSentence.setText(data.getSentence());
                        tvArticleCount.setText(String.format(Locale.getDefault(),"%d",data.getArticleCount()));
                        tvFriendCount.setText(String.format(Locale.getDefault(),"%d",data.getFriendCount()));
                        tvChasingCount.setText(String.format(Locale.getDefault(),"%d",data.getChasingCount()));

                        btnSend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                btnSend.setEnabled(false);
                                btnSend.setText(context.getString(R.string.send_invite_already));
                                listener.onBtnSendClick(data.getEmail());
                            }
                        });

                    }
                }
            }
        });



    }

    public interface OnUserPageClickListener{
        void onBtnSendClick(String creatorEmail);
    }
}
