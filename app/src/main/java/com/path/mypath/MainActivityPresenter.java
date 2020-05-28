package com.path.mypath;

public interface MainActivityPresenter {

    void onButtonLoginClickListener();

    void onCatchCurrentUser(String email);

    void onRegisterAccountToFirebase(String email, String uid);

    void onSetFirebaseDataSuccessful();

    void onCatchNoData(String email, String uid, String token);

    void onShowWaitDialog();

    void onUpdateDataSuccessful();

    void onCheckUserDataFail();
}
