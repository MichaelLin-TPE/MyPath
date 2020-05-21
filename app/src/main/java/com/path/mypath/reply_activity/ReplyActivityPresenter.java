package com.path.mypath.reply_activity;

import com.path.mypath.data_parser.DataArray;

public interface ReplyActivityPresenter {
    void onBackButtonClickListener();

    void onCatchCurrentData(DataArray data);

    void onCatchHomeDataSuccessful(String json);

    void onCatchReplyMessageListener(String message);

    void onReplyMessageSuccessful();

    void onCatchUserTokenSuccessful(String token);

    void onCatchLikeDataSuccessful(String json);
}
