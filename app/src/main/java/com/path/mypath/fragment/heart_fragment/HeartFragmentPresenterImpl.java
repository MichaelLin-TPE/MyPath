package com.path.mypath.fragment.heart_fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.ArticleLikeNotification;
import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HeartFragmentPresenterImpl implements HeartFragmentPresenter {

    private HeartFragmentVu mView;

    private Gson gson;

    private ArrayList<DataArray> dataArray;

    public HeartFragmentPresenterImpl(HeartFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchLikeDataSuccessful(String json) {
        if (json != null){
            ArrayList<ArticleLikeNotification> dataArray = gson.fromJson(json,new TypeToken<List<ArticleLikeNotification>>(){}.getType());
            if (dataArray != null){
                Collections.sort(dataArray, new Comparator<ArticleLikeNotification>() {
                    @Override
                    public int compare(ArticleLikeNotification o1, ArticleLikeNotification o2) {
                        return (int) (o2.getPressedCurrentTime() - o1.getPressedCurrentTime());
                    }
                });
                mView.setRecyclerView(dataArray);
            }
        }
    }

    @Override
    public void onCatchHomeDataSuccessful(String json) {
        if (json != null){
            dataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        }
    }

    @Override
    public void onHeartLikeItemClickListener(ArticleLikeNotification data) {
        for (DataArray homeData : dataArray){
            if (homeData.getArticleTitle().equals(data.getArticleTitle())&& homeData.getCurrentTime() == data.getArticlePostTime()){
                mView.intentToSingleViewPage(homeData);
                break;
            }
        }
    }
}
