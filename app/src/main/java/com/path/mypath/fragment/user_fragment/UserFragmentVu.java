package com.path.mypath.fragment.user_fragment;

import com.path.mypath.data_parser.DataObject;

public interface UserFragmentVu {
    void searchDataFromFirebase();

    void setRecyclerView(DataObject data);

    void selectPhoto();

    void updateUserPhoto(byte[] byteArray);

    void updateFirebaseData(String json);

    void showToast(String message);

    void intentToRecordActivity();
}
