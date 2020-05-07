package com.path.mypath.fragment.user_fragment.user_presenter;

import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.user_fragment.user_view.UserInfoViewHolder;
import com.path.mypath.fragment.user_fragment.user_view.UserMapViewHolder;

public class UserPresenterImpl implements UserPresenter {

    public static final int UP = 0;

    public static final int DOWN = 1;

    private DataObject data;

    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            return UP;
        }
        if (position == 1){
            return DOWN;
        }

        return 0;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onBindUserInfoViewHolder(UserInfoViewHolder holder, int position) {
        holder.setData(data);
    }

    @Override
    public void onBindUserMapViewHolder(UserMapViewHolder holder, int position) {
        holder.setData(data.getDataArray());
    }

    @Override
    public void onSetData(DataObject data) {
        this.data = data;
    }

    @Override
    public void onSetUserInfoClickListener(UserInfoViewHolder holder, UserInfoViewHolder.OnUserInformationClickListener listener) {
        holder.setOnUserInformationClickListener(listener);
    }

    @Override
    public void onSetUserDownClickListener(UserMapViewHolder holder, UserMapViewHolder.OnUserDownClickListener downListener) {
        holder.setOnUserDownClickListener(downListener);
    }
}
