package com.path.mypath.article_activity;

import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;
import java.util.Map;

public interface ArticleActivityVu {
    void closePage();

    void setRecyclerView(ArrayList<DataArray> dataArray);

    String getPhotoUrl();

    String getNickname();

    void updateUserData(Map<String, Object> map);

    void searchUserPersonalData(String userEmail);

    void searchCreatorLikeData(String userEmail);

    String getUserEmail();

    void saveUserLikeData(String likeJson, String userEmail);

    void intentToReplyActivity(DataArray data);

    void showSendMessageDialog(String userNickName, String userEmail);

    void showToast(String content);

    void createChatRoom(String userEmail, String articleCreator, String message);

    void searchForRoomId(String userEmail, String articleCreator, String message);

    void createPersonalChatRoom(String roomId, String msgJson);

    void reSearchRoomIdList();

    void searchUserData(String userEmail);

    void updatePersonalUserData(String userJson, String userEmail);

    void searchCreatorData(String creatorEmail);

    void searchPersonChatRoomData(String userEmail, String articleCreator, String message, String roomId);

    void updatePersonalChatData(String msgJson, String roomId);

    void intentToHeartActivity(DataArray data);

    void showDeleteDialog(DataArray data);

    void showReportDialog(DataArray data);

    void updateHomeData(String json);

    void updatePublicData(String pubJson);

    void sendEmailToCreator(String emailBody);
}
