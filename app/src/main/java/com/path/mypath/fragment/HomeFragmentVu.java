package com.path.mypath.fragment;

import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;
import java.util.Map;

public interface HomeFragmentVu {
    void setRecyclerView(ArrayList<DataArray> dataArrayList);

    String getNickname();

    String getPhotoUrl();

    void updateUserData(Map<String, Object> map);

    void searchUserPersonalData(String userEmail);

    String getUserEmail();

    void saveUserLikeData(String likeJson, String userEmail);

    void searchCreatorLikeData(String userEmail);

    void intentToReplyActivity(DataArray data);

    void intentToHomeActivity();

    void intentToUserPageReviewActivity(String userEmail);

    void showSendMessageDialog(String articleCreator, String userEmail);

    void createChatRoom(String userEmail, String articleCreator, String message);

    void searchForRoomId(String userNickname, String articleCreator, String message);

    void createPersonalChatRoom(String roomId, String msgJson);

    void reSearchRoomIdList();

    void showToast(String message);

    void searchUserData(String userEmail);

    void searchCreatorData(String creatorEmail);

    void updatePersonalUserData(String userJson, String userEmail);

    void searchPersonChatRoomData(String userEmail, String articleCreator, String message, String roomId);

    void updatePersonalChatData(String msgJson, String roomId);
}
