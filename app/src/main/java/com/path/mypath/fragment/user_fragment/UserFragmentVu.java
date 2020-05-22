package com.path.mypath.fragment.user_fragment;

import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

public interface UserFragmentVu {
    void searchDataFromFirebase();

    void setRecyclerView(DataObject data);

    void selectPhoto();

    void updateUserPhoto(byte[] byteArray);

    void updateFirebaseData(String json);

    void showToast(String message);

    void intentToRecordActivity();

    void saveUserPhoto(String downLoadUrl);

    void intentToSingleViewActivity(DataArray locationArray);

    String getNickname();

    void updateHomeData(String homeJson);

    void showEditNicknameDialog();

    void showEditSentenceDialog();

    void updateUserData(String newJson, String nickname);

    void updatePersonalData(String newJson);

    void updateLikeData(String likeJson);

    void updateUserDataSentence(String json, String sentence);

    void showLogoutConfirmDialog();

    void logout();

    void updateChatData(String chatJson, String roomId);

    void updateAllUserData(String userEmail, String userJson);

    void updateAllLikeData(String likeJson, String email);
}
