package com.path.mypath.fragment.user_fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.path.mypath.R;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.user_fragment.user_presenter.UserPresenter;
import com.path.mypath.fragment.user_fragment.user_view.UserInfoViewHolder;
import com.path.mypath.fragment.user_fragment.user_view.UserMapViewHolder;

import static com.path.mypath.fragment.user_fragment.user_presenter.UserPresenterImpl.DOWN;
import static com.path.mypath.fragment.user_fragment.user_presenter.UserPresenterImpl.UP;

public class UserAdapter extends RecyclerView.Adapter {

    private Context context;

    private UserPresenter presenter;

    private UserMapViewHolder.OnUserDownClickListener downListener;

    private UserInfoViewHolder.OnUserInformationClickListener listener;

    public void setOnUserDownClickListener(UserMapViewHolder.OnUserDownClickListener downListener){
        this.downListener = downListener;
    }

    public void setOnUserInformationClickListener(UserInfoViewHolder.OnUserInformationClickListener listener){
        this.listener = listener;
    }

    public UserAdapter(Context context, UserPresenter presenter) {
        this.context = context;
        this.presenter = presenter;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case UP:
                return new UserInfoViewHolder(LayoutInflater.from(context).inflate(R.layout.user_information_up_view, parent, false), context);
            case DOWN:
                return new UserMapViewHolder(LayoutInflater.from(context).inflate(R.layout.user_information_down_view, parent, false), context);
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof UserInfoViewHolder){
            presenter.onBindUserInfoViewHolder((UserInfoViewHolder)holder,position);
            presenter.onSetUserInfoClickListener((UserInfoViewHolder)holder,listener);
        }
        if (holder instanceof UserMapViewHolder){
            presenter.onBindUserMapViewHolder((UserMapViewHolder)holder,position);
            presenter.onSetUserDownClickListener((UserMapViewHolder)holder,downListener);
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
