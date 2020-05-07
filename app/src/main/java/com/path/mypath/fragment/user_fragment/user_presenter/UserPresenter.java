package com.path.mypath.fragment.user_fragment.user_presenter;

import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.user_fragment.user_view.UserInfoViewHolder;
import com.path.mypath.fragment.user_fragment.user_view.UserMapViewHolder;

public interface UserPresenter {
    int getItemViewType(int position);

    int getItemCount();

    void onBindUserInfoViewHolder(UserInfoViewHolder holder, int position);

    void onBindUserMapViewHolder(UserMapViewHolder holder, int position);

    void onSetData(DataObject data);

    void onSetUserInfoClickListener(UserInfoViewHolder holder, UserInfoViewHolder.OnUserInformationClickListener listener);

    void onSetUserDownClickListener(UserMapViewHolder holder, UserMapViewHolder.OnUserDownClickListener downListener);
}
