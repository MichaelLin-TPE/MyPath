package com.path.mypath.user_page_activity.user_presenter;

import com.path.mypath.data_parser.DataObject;
import com.path.mypath.fragment.user_fragment.user_view.MapAdapter;
import com.path.mypath.user_page_activity.view.ArticleViewHolder;
import com.path.mypath.user_page_activity.view.InformationViewHolder;

public interface UserPresenter {
    void setData(DataObject object);

    int getItemViewType(int position);

    int getItemCount();

    void onBindInformationViewHolder(InformationViewHolder holder, int position);

    void onBindArticleViewHolder(ArticleViewHolder holder, int position);

    void setOnUserPageClickListener(InformationViewHolder holder, InformationViewHolder.OnUserPageClickListener listener);

    void setOnMapItemClickListener(ArticleViewHolder holder, MapAdapter.OnMapItemClickListener mapItemClickListener);
}
