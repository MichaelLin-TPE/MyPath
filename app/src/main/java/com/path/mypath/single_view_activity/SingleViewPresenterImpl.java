package com.path.mypath.single_view_activity;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataUserPresHeart;

import java.util.ArrayList;
import java.util.List;

public class SingleViewPresenterImpl implements SingleViewPresenter {

    private SingleViewVu mView;
    private Gson gson;
    
    private ArrayList<DataArray> homeDataArray;
    
    private DataArray homeData;

    public SingleViewPresenterImpl(SingleViewVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchData(DataArray data) {
        mView.setView(data);
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        if (json != null){
            homeDataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onHeartButtonClickListener(DataArray data) {
        boolean isPressedHeart = false;
        boolean isDataFound = false;
        for (DataArray object : homeDataArray){
            if (object.getArticleTitle().equals(data.getArticleTitle()) && object.getCurrentTime() == data.getCurrentTime()){
                isDataFound = true;
                for (DataUserPresHeart heart : object.getHeartPressUsers()){
                    if (heart.getName().equals(mView.getUserNickname())){
                        object.getHeartPressUsers().remove(heart);
                        if (object.getHeartPressUsers().size() == 0){
                            object.setHeartCount(0);
                        }else {
                            object.setHeartCount(object.getHeartPressUsers().size());
                        }

                        isPressedHeart = true;
                        break;
                    }
                }
            }
        }
        if (isDataFound){
            if (isPressedHeart){
                mView.setHeartIcon(true);
                mView.setHeartCountLess();

                String json = gson.toJson(homeDataArray);

                mView.update(json);

                Log.i("Michael","變成空白");
            }else {
                Log.i("Michael","變成紅心");
                mView.setHeartIcon(false);
                mView.setHeartCountMore();
                for (DataArray object : homeDataArray){
                    if (object.getArticleTitle().equals(data.getArticleTitle()) && object.getCurrentTime() == data.getCurrentTime()){
                        object.setHeartCount(object.getHeartCount()+1);
                        DataUserPresHeart heart = new DataUserPresHeart();
                        heart.setName(mView.getUserNickname());
                        heart.setPhotoUrl(mView.getUserPhoto());
                        if (object.getHeartPressUsers() != null && object.getHeartPressUsers().size() != 0){
                            object.getHeartPressUsers().add(heart);
                        }else {
                            ArrayList<DataUserPresHeart> heartArray = new ArrayList<>();
                            heartArray.add(heart);
                            object.setHeartPressUsers(heartArray);
                        }
                        break;
                    }
                }
                String json = gson.toJson(homeDataArray);
                mView.update(json);
            }
        }

    }

    @Override
    public void onReplyClickListener(DataArray data) {
        mView.intentToReplyActivity(data);
    }
}
