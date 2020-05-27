package com.path.mypath.fragment.add_fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;
import java.util.List;

public class AddFragmentPresenterImpl implements AddFragmentPresenter {

    private AddFragmentVu mView;

    private Gson gson;

    private ArrayList<DataArray> dataArray;

    public AddFragmentPresenterImpl(AddFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onCatchPublicDataSuccessful(String json) {
        ArrayList<DataArray> dataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
        if (dataArray != null){
            mView.setRecyclerView(dataArray);
        }
    }

    @Override
    public void onMapItemClickListener(DataArray locationData) {
        boolean isDataFound = false;
        for (DataArray data : dataArray){
            if (data.getArticleTitle().equals(locationData.getArticleTitle()) && data.getCurrentTime() == locationData.getCurrentTime()){
                mView.intentToSingleViewActivity(data);
                isDataFound = true;
                break;
            }
        }
        if (!isDataFound){
            mView.intentToSingleViewActivity(locationData);
        }


    }

    @Override
    public void onCatchHomeDataSuccess(String json) {
        dataArray = gson.fromJson(json,new TypeToken<List<DataArray>>(){}.getType());
    }
}
