package com.path.mypath.fragment.add_fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;
import java.util.List;

public class AddFragmentPresenterImpl implements AddFragmentPresenter {

    private AddFragmentVu mView;

    private Gson gson;

    public AddFragmentPresenterImpl(AddFragmentVu mView) {
        this.mView = mView;
        gson = new Gson();
    }

    @Override
    public void onShowPublicData() {
        mView.searchFirebaseData();
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
        mView.intentToSingleViewActivity(locationData);
    }
}
