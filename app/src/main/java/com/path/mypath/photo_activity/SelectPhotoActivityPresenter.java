package com.path.mypath.photo_activity;

import java.util.ArrayList;

public interface SelectPhotoActivityPresenter {
    void onCatchUserPhotoByte(ArrayList<byte[]> photoArray);

    void onCatchUploadPhotoFailListener(String toString, int photoIndex);

    void onShowFinishInformation();

    void onPublicConfirmClickListener(ArrayList<String> photoUrlArray, String articleTitle);

    void onPrivateConfirmClickListener(ArrayList<String> photoUrlArray, String articleTitle);

    void onAddButtonClickListener();

    void onBackButtonClickListener();

    void onBackConfirmClickListener();

    void onFinishButtonClickListener(String articleTitle);

    void onCatchUserDataSuccessful(String json);

    void onCatchHomeDataSuccessful(String json);

    void onUpdateSuccessful();

    void onCatchSearchPageDataSuccessful(String json);
}
