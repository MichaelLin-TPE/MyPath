package com.path.mypath.single_view_activity;

import com.path.mypath.data_parser.DataArray;

public class SingleViewPresenterImpl implements SingleViewPresenter {

    private SingleViewVu mView;

    public SingleViewPresenterImpl(SingleViewVu mView) {
        this.mView = mView;
    }

    @Override
    public void onCatchData(DataArray data) {
        mView.setView(data);
    }

    @Override
    public void onBackButtonClickListener() {
        mView.closePage();
    }
}
