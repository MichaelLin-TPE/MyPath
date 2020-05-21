package com.path.mypath.fragment.user_fragment;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.DataReply;
import com.path.mypath.data_parser.DataUserPresHeart;
import com.path.mypath.fragment.FansData;

import java.util.ArrayList;
import java.util.List;

public class UserFragmentPresenterImpl implements UserFragmentPresenter {

    private UserFragmentVu mView;

    private DataObject data;

    private Gson gson;

    private String message;

    private ArrayList<DataArray> dataArray;

    private ArrayList<ArticleLikeNotification> likeArray;

    public UserFragmentPresenterImpl(UserFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onSearchDataFromFirebase() {
        mView.searchDataFromFirebase();
    }

    @Override
    public void onCatchJsonSuccessful(String json) {

        data = gson.fromJson(json,DataObject.class);
        if (data != null){
            mView.setRecyclerView(data);
        }else {
            Log.i("Michael","資料結構 == null");
        }

    }

    @Override
    public void onUserPhotoClick() {
        mView.selectPhoto();
    }

    @Override
    public void onCatchUserPhotoByte(byte[] byteArray) {
        message = "上傳中....請稍後";
        mView.showToast(message);
        mView.updateUserPhoto(byteArray);
    }

    @Override
    public void onCatchPhotoUrl(String downLoadUrl) {
        mView.saveUserPhoto(downLoadUrl);
        data.setUserPhoto(downLoadUrl);
        String json = gson.toJson(data);
        mView.updateFirebaseData(json);

        for (DataArray data : dataArray){
            if (data.getUserNickName().equals(mView.getNickname())){
                data.setUserPhoto(downLoadUrl);
            }
        }
        String homeJson = gson.toJson(dataArray);
        mView.updateHomeData(homeJson);
    }

    @Override
    public void onUpdateUserPhotoSuccessful() {
        message = "照片更新成功";
        mView.showToast(message);
    }

    @Override
    public void onCatchFirebaseListenerFail(String toString) {
        mView.showToast(toString);
    }

    @Override
    public void onAddIconClickListener() {
        mView.intentToRecordActivity();
    }

    @Override
    public void onMapItemClickListener(DataArray locationArray) {
        boolean isDataFound = false;
        if (dataArray != null){
            for (DataArray data : dataArray){
                if (data.getArticleTitle().equals(locationArray.getArticleTitle()) && data.getCurrentTime() == locationArray.getCurrentTime()){
                    mView.intentToSingleViewActivity(data);
                    isDataFound = true;
                    break;
                }
            }
            if (!isDataFound){
                mView.intentToSingleViewActivity(locationArray);
            }

        }


    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        //取得所有資料
        dataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
    }

    @Override
    public void onEditNicknameClickListener() {
        mView.showEditNicknameDialog();
    }

    @Override
    public void onEditConfirmClickListener(String nickname, String oldNickname) {
        //更改personal data 的 array
        if (data != null){
            data.setUserNickname(nickname);
            for (DataArray object : data.getDataArray()){
                if (object.getUserNickName().equals(oldNickname)){
                    object.setUserNickName(nickname);
                }
                if (object.getReplyCount() != 0){
                    for (DataReply reply : object.getReplyArray()){
                        if (reply.getNickname().equals(oldNickname)){
                            reply.setNickname(nickname);
                        }
                    }
                }
            }
            //更改粉絲名單
            if (data.getFansArray() != null && data.getFansArray().size() != 0){
                for (FansData object : data.getFansArray()){
                    if (object.getNickname().equals(oldNickname)){
                        object.setNickname(nickname);
                    }
                }
            }
            //更改追蹤名單
            if (data.getChaseArray() != null && data.getChaseArray().size() != 0){
                for (FansData object : data.getChaseArray()){
                    if (object.getNickname().equals(oldNickname)){
                        object.setNickname(nickname);
                    }
                }
            }

            String newJson = gson.toJson(data);
            mView.updateUserData(newJson,nickname);
            mView.updatePersonalData(newJson);
        }




        //更新 home data
        if (dataArray != null){
            for (DataArray object : dataArray){
                if (object.getUserNickName().equals(oldNickname)){
                    object.setUserNickName(nickname);
                }
                if (object.getReplyCount() != 0){
                    for (DataReply reply : object.getReplyArray()){
                        if (reply.getNickname().equals(oldNickname)){
                            reply.setNickname(nickname);
                        }
                    }
                }
                if (object.getHeartCount() != 0){
                    for (DataUserPresHeart heart : object.getHeartPressUsers()){
                        if (heart.getName().equals(oldNickname)){
                            heart.setName(nickname);
                        }
                    }
                }
            }

            String homeJson = gson.toJson(dataArray);
            mView.updateHomeData(homeJson);
        }

        //更改 user like data
        if (likeArray != null){
            for (ArticleLikeNotification object : likeArray){
                if (object.getUserNickname().equals(oldNickname)){
                    object.setUserNickname(nickname);
                }
                if (object.getArticleCreatorName().equals(oldNickname)){
                    object.setArticleCreatorName(nickname);
                }
            }
            String likeJson = gson.toJson(likeArray);
            mView.updateLikeData(likeJson);
        }
    }

    @Override
    public void onEditSentenceClickListener() {
        mView.showEditSentenceDialog();
    }

    @Override
    public void onEditSentenceConfirmClickListener(String sentence) {
        if (data != null){
            data.setSentence(sentence);
            String json = gson.toJson(data);
            mView.updateUserDataSentence(json,sentence);
        }
    }

    @Override
    public void onCatchLikeDataSuccess(String json) {
        if (json != null){
            likeArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());
        }
    }

    @Override
    public void onLogoutClickListener() {
        mView.showLogoutConfirmDialog();
    }

    @Override
    public void onLogoutConfirmClickListener() {
        mView.logout();
    }
}
