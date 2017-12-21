package com.u91porn.ui.download;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.liulishuo.filedownloader.FileDownloadConnectListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.orhanobut.logger.Logger;
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

    private DownloadFragmentAdapter downloadAdapter;
    private List<Fragment> fragmentList;
    FileDownloadConnectListener fileDownloadConnectListener = new FileDownloadConnectListener() {
        @Override
        public void connected() {
            Logger.t(TAG).d("connected连接上");
        }

        @Override
        public void disconnected() {
            Logger.t(TAG).d("disconnected断开连接？？？");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        ButterKnife.bind(this);
        setTitle(R.string.my_download);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setContentInsetStartWithNavigation(0);
        fragmentList = new ArrayList<>();
        fragmentList.add(new DownloadingFragment());
        fragmentList.add(new FinishedFragment());
        downloadAdapter = new DownloadFragmentAdapter(getSupportFragmentManager(), fragmentList);
        downloadViewpager.setAdapter(downloadAdapter);
        downloadTab.setupWithViewPager(downloadViewpager);

        FileDownloader.getImpl().addServiceConnectListener(fileDownloadConnectListener);

        if (!FileDownloader.getImpl().isServiceConnected()) {
            FileDownloader.getImpl().bindService();
            Logger.t(TAG).d("启动连接");
        } else {
            Logger.t(TAG).d("已经连接");
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FileDownloader.getImpl().removeServiceConnectListener(fileDownloadConnectListener);
    }
}
