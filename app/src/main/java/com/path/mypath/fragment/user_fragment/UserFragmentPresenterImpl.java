package com.path.mypath.fragment.user_fragment;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;

import java.util.ArrayList;
import java.util.List;

public class UserFragmentPresenterImpl implements UserFragmentPresenter {

    private UserFragmentVu mView;

    private DataObject data;

    private Gson gson;

    private String message;

    private ArrayList<DataArray> dataArray;

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
}
