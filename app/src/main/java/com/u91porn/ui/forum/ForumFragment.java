package com.u91porn.ui.forum;


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
public class ForumFragment extends BaseFragment {


    public ForumFragment() {
        // Required empty public constructor
    }

    public static ForumFragment getInstance(){
        return new ForumFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_forum, container, false);
    }

}
