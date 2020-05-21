package com.path.mypath.user_page_activity;

public interface UserPageActivityPresenter {
    void onBackButtonClickListener();

    void onCatchPersonalDataSuccessful(String json);

    void onSendButtonClickListener(String creatorEmail);

    void onCatchUserLikeDataSuccessful(String json);

    void onCatchUserDataSuccessful(String json);
}
