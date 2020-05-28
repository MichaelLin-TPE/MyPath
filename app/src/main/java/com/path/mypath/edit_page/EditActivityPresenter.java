package com.path.mypath.edit_page;

public interface EditActivityPresenter {
    void onSaveButtonClickListener(String nickname, String sentence, String email);

    void onUserPhotoSelectClickListener();

    void onCatchUserPhotoByte(byte[] byteArray);

    void onCatchPhotoUrl(String downLoadUrl);

    void onDataUpdateSuccessfulListener();

    void onCatchAccountMode(boolean isChecked);

    void onCatchPersonalData(String json);
}
