package com.path.mypath.fragment;

import com.google.gson.Gson;
import com.path.mypath.data_parser.DataArray;
import com.path.mypath.data_parser.DataObject;
import com.path.mypath.data_parser.DataUserPresHeart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeFragmentPresenterImpl implements HomeFragmentPresenter {
    private HomeFragmentVu mView;

    private Gson gson;

    private DataObject dataObject;

    public HomeFragmentPresenterImpl(HomeFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchUserDataSuccessful(String json) {
        dataObject = gson.fromJson(json, DataObject.class);
        if (dataObject != null) {
            mView.setRecyclerView(dataObject);
        }
    }

    @Override
    public void onHeartClickListener(DataArray articleData, int position) {
        boolean isPressed = false;
        if (articleData.getHeartPressUsers() != null && articleData.getHeartPressUsers().size() != 0) {
            for (DataUserPresHeart data : articleData.getHeartPressUsers()) {
                if (data.getName().equals(articleData.getUserNickName())) {
                    articleData.getHeartPressUsers().remove(data);
                    isPressed = true;
                    break;
                }
            }
            if (isPressed) {
                int currentHeartCount = articleData.getHeartCount() - 1;
                articleData.setHeartCount(currentHeartCount);
                dataObject.getDataArray().set(position,articleData);
            } else {
                int currentHeartCount = articleData.getHeartCount() + 1;
                DataUserPresHeart data = new DataUserPresHeart();
                data.setPhotoUrl(mView.getPhotoUrl());
                data.setName(mView.getNickname());
                articleData.setHeartCount(currentHeartCount);
                articleData.getHeartPressUsers().add(data);
                dataObject.getDataArray().set(position,articleData);
            }
        } else {
            int currentHeartCount = articleData.getHeartCount();
            ArrayList<DataUserPresHeart> dataArray = new ArrayList<>();
            DataUserPresHeart data = new DataUserPresHeart();
            data.setName(mView.getNickname());
            data.setPhotoUrl(mView.getPhotoUrl());
            dataArray.add(data);
            articleData.setHeartPressUsers(dataArray);
            articleData.setHeartCount(currentHeartCount + 1);
            dataObject.getDataArray().set(position,articleData);
        }
//        mView.setRecyclerView(dataObject,position);
        String json = gson.toJson(dataObject);

        Map<String,Object> map = new HashMap<>();
        map.put("user_json",json);

        mView.updateUserData(map);

    }
}
