package com.path.mypath.chat_room_activity.view;

import com.path.mypath.fragment.MessageArray;

import java.util.ArrayList;

public interface AdapterPresenter {
    int getItemViewType(int position);

    int getItemCount();

    void onBindLeftMessageViewHolder(LeftMessageViewHolder holder, int position);

    void onBindRightMessageViewHolder(RightMessageViewHolder holder, int position);

    void setData(ArrayList<MessageArray> msgArray);

    void setUserNickname(String userNickname);
}
