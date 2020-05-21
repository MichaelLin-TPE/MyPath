package com.path.mypath.user_page_activity.user_presenter;

import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.user_fragment.user_view.MapAdapter;
import com.path.mypath.user_page_activity.view.ArticleViewHolder;
import com.path.mypath.user_page_activity.view.InformationViewHolder;

public class UserPresenterImpl implements UserPresenter {
    public static final int ARTICLE = 1;

    public static final int INFOMATION = 2;

    private DataObject data;

    private boolean isShowInformation,isShowArticle;

    @Override
    public void setData(DataObject object) {
        this.data = object;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            return INFOMATION;
        }
        if (position == 1){
            return ARTICLE;
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public void onBindInformationViewHolder(InformationViewHolder holder, int position) {
        holder.setData(data);
    }

    @Override
    public void onBindArticleViewHolder(ArticleViewHolder holder, int position) {
        holder.setData(data);
    }

    @Override
    public void setOnUserPageClickListener(InformationViewHolder holder, InformationViewHolder.OnUserPageClickListener listener) {
        holder.setOnUserPageClickListener(listener);
    }

    @Override
    public void setOnMapItemClickListener(ArticleViewHolder holder, MapAdapter.OnMapItemClickListener mapItemClickListener) {
        holder.setOnMapItemClickListener(mapItemClickListener);
    }
}
