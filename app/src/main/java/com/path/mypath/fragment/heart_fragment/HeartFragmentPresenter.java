package com.path.mypath.fragment.heart_fragment;

import com.path.mypath.data_parser.ArticleLikeNotification;

public interface HeartFragmentPresenter {
    void onCatchLikeDataSuccessful(String json);

    void onCatchHomeDataSuccessful(String json);

    void onHeartLikeItemClickListener(ArticleLikeNotification data);
}
