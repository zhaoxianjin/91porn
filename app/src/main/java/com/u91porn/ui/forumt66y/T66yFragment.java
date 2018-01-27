package com.u91porn.ui.forumt66y;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.u91porn.R;
import com.u91porn.ui.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 * @author flymegoc
 */
public class T66yFragment extends BaseFragment {


    public T66yFragment() {
        // Required empty public constructor
    }

    public static T66yFragment getInstance(){
        return new T66yFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_t66y, container, false);
    }

}
