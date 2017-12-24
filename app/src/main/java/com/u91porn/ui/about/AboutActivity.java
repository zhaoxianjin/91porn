package com.u91porn.ui.about;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.trello.rxlifecycle2.LifecycleTransformer;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.service.DownloadService;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.utils.ApkVersionUtils;
import com.u91porn.utils.DialogUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rx_cache2.Reply;

/**
 * @author flymegoc
 */
public class AboutActivity extends MvpActivity<AboutView, AboutPresenter> implements AboutView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_check_update)
    TextView tvCheckUpdate;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setContentInsetStartWithNavigation(0);

        setTitle("关于");

        tvCheckUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int versionCode = ApkVersionUtils.getVersionCode(AboutActivity.this);
                if (versionCode == 0) {
                    showMessage("获取应用本版失败");
                    return;
                }
                alertDialog.show();
                presenter.checkUpdate(versionCode);
            }
        });

        alertDialog = DialogUtils.initLodingDialog(this, "正在检查更新，请稍后...");
    }

    @NonNull
    @Override
    public AboutPresenter createPresenter() {
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        return new AboutPresenter(new UpdatePresenter(noLimit91PornServiceApi, new Gson(),provider));
    }

    private void showUpdateDialog(final UpdateVersion updateVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本");
        builder.setMessage(updateVersion.getUpdateMessage());
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(AboutActivity.this, DownloadService.class);
                intent.putExtra("updateVersion", updateVersion);
                startService(intent);
            }
        });
        builder.setNegativeButton("稍后更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    @Override
    public void needUpdate(UpdateVersion updateVersion) {
        showUpdateDialog(updateVersion);
    }

    @Override
    public void noNeedUpdate() {
        showMessage("当前已是最新版本");
    }

    @Override
    public void checkUpdateError(String message) {
        showMessage(message);
    }

    @Override
    public String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {

    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        alertDialog.show();
    }

    @Override
    public void showContent() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void showMessage(String msg) {
        super.showMessage(msg);
    }
}
