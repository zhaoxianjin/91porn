package com.u91porn.ui.meizitu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u91porn.R;
import com.u91porn.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class MeiZiTuFragment extends BaseFragment {


    public MeiZiTuFragment() {
        // Required empty public constructor
    }

    public static MeiZiTuFragment getInstance() {
        return new MeiZiTuFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_mei_zi_tu, container, false);
    }

}
