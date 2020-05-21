package com.path.mypath.chat_room_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.path.mypath.R;
import com.path.mypath.chat_room_activity.view.AdapterPresenter;
import com.path.mypath.chat_room_activity.view.LeftMessageViewHolder;
import com.path.mypath.chat_room_activity.view.RightMessageViewHolder;

import static com.path.mypath.chat_room_activity.view.AdapterPresenterImpl.LEFT;
import static com.path.mypath.chat_room_activity.view.AdapterPresenterImpl.RIGHT;

public class ChatRoomAdapter extends RecyclerView.Adapter {

    private AdapterPresenter presenter;

    private Context context;

    public ChatRoomAdapter(AdapterPresenter presenter, Context context) {
        this.presenter = presenter;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case LEFT:
                return new LeftMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_left_item_layout,parent,false),context);
            case RIGHT:
                return new RightMessageViewHolder(LayoutInflater.from(context).inflate(R.layout.chat_right_item_layout,parent,false),context);
                default:
                    return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LeftMessageViewHolder){
            presenter.onBindLeftMessageViewHolder((LeftMessageViewHolder)holder,position);
        }
        if (holder instanceof RightMessageViewHolder){
            presenter.onBindRightMessageViewHolder((RightMessageViewHolder)holder,position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return presenter.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return presenter.getItemCount();
    }
}
