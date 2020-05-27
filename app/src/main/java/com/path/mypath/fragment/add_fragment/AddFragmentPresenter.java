package com.path.mypath.fragment.add_fragment;

import com.path.mypath.data_parser.DataArray;

public interface AddFragmentPresenter {

    void onCatchPublicDataSuccessful(String json);

    void onMapItemClickListener(DataArray locationData);

    void onCatchHomeDataSuccess(String json);
}
