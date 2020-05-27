package com.path.mypath.article_activity;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.fragment.RoomIdObject;

import java.util.ArrayList;

public interface ArticleActivityPresenter {
    void onBackButtonClickListener();

    void onCatchUserArticleData(ArrayList<DataArray> dataArray);

    void onHeartClickListener(DataArray data, int position, boolean isCheck, int selectIndex);

    void onCatchHomeDataSuccessful(String json);

    void onCatchFCMTokenSuccessful(String token);

    void onCatchCreatorLikeData(String json);

    void onReplyClickListener(DataArray data);

    void onSendClickListener(DataArray data);

    void onEditTextSendTypeListener(String toString, String userEmail, String creatorEmail);

    void onCatchRoomIdList(ArrayList<RoomIdObject> roomArray);

    void onCreateRoomSuccessful(String message, String userEmail, String articleCreator);

    void onCatchRoomIdSuccessful(String id, String userEmail, String articleCreator, String message);

    void onCreatePersonalChatRoomSuccessful(String roomId);

    void onCatchPersonalUserDataSuccessful(String json, String userEmail);

    void onCatchPersonalCreatorDataSuccessful(String json);

    void onCatchPersonalChatData(String json, String userEmail, String articleCreator, String message);

    void onReplyCountClickListener(DataArray data);

    void onHeartCountClickListener(DataArray data);

    void onSortClickListener(DataArray data);

    void onDeleteItemClickListener(DataArray data);

    void onCatchPersonalData(String json);

    void onCatchPublicData(String json);

    void onReportItemClickListener(DataArray data);
}
