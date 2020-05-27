package com.path.mypath.single_view_activity;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.fragment.RoomIdObject;

import java.util.ArrayList;

public interface SingleViewPresenter {
    void onCatchData(DataArray data);

    void onBackButtonClickListener();

    void onCatchHomeDataSuccessful(String json);

    void onHeartButtonClickListener(DataArray data);

    void onReplyClickListener(DataArray data);

    void onHeartCountClickListener(DataArray data);

    void onSendMessageClickListener(DataArray data);

    void onEditTextSendTypeListener(String toString, String userEmail, String creatorEmail);

    void onCatchRoomIdList(ArrayList<RoomIdObject> roomArray);

    void onCreateRoomSuccessful(String message, String userEmail, String articleCreator);

    void onCatchRoomIdSuccessful(String id, String userEmail, String articleCreator, String message);

    void onCreatePersonalChatRoomSuccessful(String roomId);

    void onCatchPersonalUserDataSuccessful(String json, String userEmail);

    void onCatchPersonalData(String json);

    void onCatchPersonalCreatorDataSuccessful(String json);

    void onCatchPersonalChatData(String json, String userEmail, String articleCreator, String message);

    void onPhotoClickListener(DataArray data);

    void onSortClickListener(DataArray data);

    void onDeleteItemClickListener(DataArray data);

    void onCatchPublicData(String json);

    void onReportItemClickListener(DataArray data);
}
