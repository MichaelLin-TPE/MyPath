package com.path.mypath.user_page_activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.path.mypath.R;
import com.path.mypath.fragment.user_fragment.user_view.MapAdapter;
import com.path.mypath.user_page_activity.user_presenter.UserPresenter;
import com.path.mypath.user_page_activity.view.ArticleViewHolder;
import com.path.mypath.user_page_activity.view.InformationViewHolder;

import static android.bluetooth.BluetoothClass.Service.INFORMATION;
import static com.path.mypath.user_page_activity.user_presenter.UserPresenterImpl.ARTICLE;
import static com.path.mypath.user_page_activity.user_presenter.UserPresenterImpl.INFOMATION;

public class UserPageAdapter extends RecyclerView.Adapter {

    private UserPresenter presenter;

    private Context context;

    private InformationViewHolder.OnUserPageClickListener listener;

    private MapAdapter.OnMapItemClickListener mapItemClickListener;

    public void setOnMapItemClickListener(MapAdapter.OnMapItemClickListener mapItemClickListener){
        this.mapItemClickListener = mapItemClickListener;
    }

    public void setOnUserPageClickListener(InformationViewHolder.OnUserPageClickListener listener){
        this.listener = listener;
    }

    public UserPageAdapter(UserPresenter presenter, Context context) {
        this.presenter = presenter;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType) {
            case INFOMATION:
                return new InformationViewHolder(LayoutInflater.from(context).inflate(R.layout.user_information_up_view, parent, false), context);
            case ARTICLE:
                return new ArticleViewHolder(LayoutInflater.from(context).inflate(R.layout.user_information_down_view, parent, false), context);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof InformationViewHolder){
            presenter.onBindInformationViewHolder((InformationViewHolder)holder,position);
            presenter.setOnUserPageClickListener((InformationViewHolder)holder,listener);

        }
        if (holder instanceof ArticleViewHolder){
            presenter.onBindArticleViewHolder((ArticleViewHolder)holder,position);
            presenter.setOnMapItemClickListener((ArticleViewHolder)holder,mapItemClickListener);
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
