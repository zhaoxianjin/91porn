package com.u91porn.ui.about;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.qmuiteam.qmui.util.QMUIPackageHelper;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.NoLimit91PornServiceApi;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.service.DownloadService;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.update.UpdatePresenter;
import com.u91porn.utils.ApkVersionUtils;
import com.u91porn.utils.DialogUtils;

import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flymegoc
 */
public class AboutActivity extends MvpActivity<AboutView, AboutPresenter> implements AboutView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.version)
    TextView mVersionTextView;
    @BindView(R.id.about_list)
    QMUIGroupListView mAboutGroupListView;
    @BindView(R.id.copyright)
    TextView mCopyrightTextView;

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

        mVersionTextView.setText(QMUIPackageHelper.getAppVersion(this));

        QMUIGroupListView.newSection(this)
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_github)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://github.com/techGay/91porn";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_item_homepage)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String url = "https://github.com/techGay/91porn/issues";
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url));
                        startActivity(intent);
                    }
                })
                .addItemView(mAboutGroupListView.createItemView(getResources().getString(R.string.about_check_update)), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int versionCode = ApkVersionUtils.getVersionCode(AboutActivity.this);
                        if (versionCode == 0) {
                            showMessage("获取应用本版失败", TastyToast.ERROR);
                            return;
                        }
                        alertDialog.show();
                        presenter.checkUpdate(versionCode);
                    }
                })
                .addTo(mAboutGroupListView);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy", Locale.CHINA);
        String currentYear = dateFormat.format(new java.util.Date());
        mCopyrightTextView.setText(String.format(getResources().getString(R.string.about_copyright), currentYear));

        alertDialog = DialogUtils.initLodingDialog(this, "正在检查更新，请稍后...");
    }

    @NonNull
    @Override
    public AboutPresenter createPresenter() {
        NoLimit91PornServiceApi noLimit91PornServiceApi = MyApplication.getInstace().getNoLimit91PornService();
        return new AboutPresenter(new UpdatePresenter(noLimit91PornServiceApi, new Gson(), provider));
    }

    private void showUpdateDialog(final UpdateVersion updateVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("发现新版本");
        builder.setMessage(updateVersion.getUpdateMessage());
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showMessage("开始下载", TastyToast.SUCCESS);
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
        showMessage("当前已是最新版本", TastyToast.SUCCESS);
    }

    @Override
    public void checkUpdateError(String message) {
        showMessage(message, TastyToast.ERROR);
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
    public void showMessage(String msg, int type) {
        super.showMessage(msg, type);
    }

    @Override
    public void showError(String message) {

    }
}
