package com.path.mypath.fragment.user_fragment;

public interface UserFragmentPresenter {
    void onSearchDataFromFirebase();

    void onCatchJsonSuccessful(String json);

    void onUserPhotoClick();

    void onCatchUserPhotoByte(byte[] byteArray);

    void onCatchPhotoUrl(String downLoadUrl);

    void onUpdateUserPhotoSuccessful();

    void onCatchFirebaseListenerFail(String toString);

    void onAddIconClickListener();
}
