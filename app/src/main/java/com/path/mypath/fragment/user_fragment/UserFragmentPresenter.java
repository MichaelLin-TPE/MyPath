package com.path.mypath.fragment.user_fragment;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;

public interface UserFragmentPresenter {
    void onSearchDataFromFirebase();

    void onCatchJsonSuccessful(String json);

    void onUserPhotoClick();

    void onCatchUserPhotoByte(byte[] byteArray);

    void onCatchPhotoUrl(String downLoadUrl);

    void onUpdateUserPhotoSuccessful();

    void onCatchFirebaseListenerFail(String toString);

    void onAddIconClickListener();

    void onMapItemClickListener(DataArray locationArray);

    void onCatchHomeDataSuccessful(String json);

    void onEditNicknameClickListener();

    void onEditConfirmClickListener(String content, String oldNickname);

    void onEditSentenceClickListener();

    void onEditSentenceConfirmClickListener(String toString);

    void onCatchLikeDataSuccess(String json);

    void onLogoutClickListener();

    void onLogoutConfirmClickListener();

    void onCatchPersonalChatData(ArrayList<String> chatJsonArray, ArrayList<String> roomIdArray);

    void onUpdateNextChatData();

    void onCatchAllUserData(ArrayList<String> userJsonArray);

    void onUpdateNextUserData();

    void onCatchAllLikeData(ArrayList<String> likeJsonArray, ArrayList<String> emailArray);

    void onUpdateNextLikeData();

    void onArticleCountClick(ArrayList<DataArray> dataArray);

    void onChasingCountClickListener(DataObject data);

    void onFansCountClickListener(DataObject data);
}
