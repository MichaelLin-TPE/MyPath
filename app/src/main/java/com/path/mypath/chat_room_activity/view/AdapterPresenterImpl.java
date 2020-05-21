package com.path.mypath.chat_room_activity.view;

import com.path.mypath.fragment.MessageArray;
import com.path.mypath.fragment.MessageObject;

import java.util.ArrayList;

public class AdapterPresenterImpl implements AdapterPresenter {
    public static final int LEFT = 0;

    public static final int RIGHT = 1;

    private ArrayList<MessageArray> msgArray;

    private String userNickname;

    @Override
    public int getItemViewType(int position) {

        if (msgArray.get(position).getUserNickname().equals(userNickname)){
            return RIGHT;
        }else {
            return LEFT;
        }
    }

    @Override
    public int getItemCount() {
        return msgArray == null ? 0 : msgArray.size();
    }

    @Override
    public void onBindLeftMessageViewHolder(LeftMessageViewHolder holder, int position) {
        holder.setData(msgArray,position);
    }

    @Override
    public void onBindRightMessageViewHolder(RightMessageViewHolder holder, int position) {
        holder.setData(msgArray,position);
    }

    @Override
    public void setData(ArrayList<MessageArray> msgArray) {
        this.msgArray = msgArray;
    }

    @Override
    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }
}
