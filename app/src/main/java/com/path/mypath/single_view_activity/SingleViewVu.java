package com.path.mypath.single_view_activity;

import com.path.mypath.data_parser.DataArray;

public interface SingleViewVu {
    void setView(DataArray data);

    void closePage();

    String getUserNickname();

    void setHeartIcon(boolean isShow);

    void setHeartCountLess();

    void setHeartCountMore();

    String getUserPhoto();

    void update(String json);

    void intentToReplyActivity(DataArray data);

    void intentToHeartActivity(DataArray data);

    void showSendMessageDialog(String userNickName, String userEmail);

    void showToast(String content);

    void createChatRoom(String userEmail, String articleCreator, String message);

    void searchForRoomId(String userEmail, String articleCreator, String message);

    String getNickname();

    String getPhotoUrl();

    String getUserEmail();

    void createPersonalChatRoom(String roomId, String msgJson);

    void reSearchRoomIdList();

    void searchUserData(String userEmail);

    void updatePersonalUserData(String userJson, String userEmail);

    void searchCreatorData(String creatorEmail);

    void searchPersonChatRoomData(String userEmail, String articleCreator, String message, String roomId);

    void updatePersonalChatData(String msgJson, String roomId);

    void intentToUserPageReviewActivity(String userEmail);
}
