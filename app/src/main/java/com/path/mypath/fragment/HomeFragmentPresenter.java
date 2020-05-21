package com.path.mypath.fragment;

import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;

public interface HomeFragmentPresenter {
    void onCatchUserDataSuccessful(String json);

    void onHeartClickListener(DataArray articleData, int position, boolean isCheck, int selectIndex);

    void onCatchRealTimeData(String json);

    void onCatchFCMTokenSuccessful(String token);

    void onCatchCreatorLikeData(String json);

    void onReplyButtonClickListener(DataArray data);

    void onReplyCountClickListener(DataArray data);

    void onUserPhotoClickListener(DataArray data);

    void onSendButtonClickListener(DataArray data);

    void onEditTextSendTypeListener(String message, String userNickname, String articleCreator);

    void onCatchRoomIdList(ArrayList<RoomIdObject> roomArray);

    void onCreateRoomSuccessful(String message, String userEmail, String articleCreator);

    void onCatchRoomIdSuccessful(String roomId, String userNickname, String articleCreator, String message);

    void onCreatePersonalChatRoomSuccessful(String roomId);

    void onCatchPersonalUserDataSuccessful(String json, String userEmail);

    void onCatchPersonalCreatorDataSuccessful(String json);

    void onCatchPersonalChatData(String s, String userEmail, String articleCreator, String json);
}
