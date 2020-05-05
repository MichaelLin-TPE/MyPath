package com.path.mypath;

public interface MainActivityPresenter {

    void onButtonLoginClickListener();

    void onCatchCurrentUser();

    void onRegisterAccountToFirebase(String email, String uid);

    void onSetFirebaseDataSuccessful();

    void onCatchNoData(String email, String uid);
}
