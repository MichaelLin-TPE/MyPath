package com.path.mypath.fragment.add_fragment;

import com.path.mypath.data_parser.DataArray;

import java.util.ArrayList;

public interface AddFragmentVu {

    void setRecyclerView(ArrayList<DataArray> dataArray);

    void intentToSingleViewActivity(DataArray locationData);
}
