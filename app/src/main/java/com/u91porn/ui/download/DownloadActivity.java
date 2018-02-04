package com.u91porn.ui.download;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.u91porn.R;
import com.u91porn.adapter.DownloadFragmentAdapter;
import com.u91porn.ui.BaseAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class DownloadActivity extends BaseAppCompatActivity {

    private static final String TAG = DownloadActivity.class.getSimpleName();


    @BindView(R.id.download_viewpager)
    ViewPager downloadViewpager;
    @BindView(R.id.download_tab)
    TabLayout downloadTab;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new DownloadingFragment());
        fragmentList.add(new FinishedFragment());
        DownloadFragmentAdapter downloadAdapter = new DownloadFragmentAdapter(getSupportFragmentManager(), fragmentList);
        downloadViewpager.setAdapter(downloadAdapter);
        downloadTab.setupWithViewPager(downloadViewpager);
    }

}
