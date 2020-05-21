package com.path.mypath.reply_activity;

import com.path.mypath.data_parser.DataArray;

public interface ReplyActivityVu {
    void closePage();

    void setRecyclerView(DataArray data);

    void setTitleView(DataArray data);

    String getUserPhoto();

    String getUserNickname();

    void updateHomeData(String json);

    void showToast(String message);

    String getNickname();

    String getEmail();

    void updateLikeData(String likeJson);
}
