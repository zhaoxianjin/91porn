package com.u91porn.ui.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;

import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.eventbus.BaseUrlChangeEvent;
import com.u91porn.ui.MvpActivity;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.DialogUtils;
import com.u91porn.utils.PlaybackEngine;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.UserHelper;
import com.u91porn.utils.constants.Constants;
import com.u91porn.utils.constants.Keys;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;
import okhttp3.HttpUrl;

/**
 * @author flymegoc
 */
public class SettingActivity extends MvpActivity<SettingView, SettingPresenter> implements View.OnClickListener, SettingView {

    private static final String TAG = SettingActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mine_list)
    QMUIGroupListView qmuiGroupListView;
    @BindView(R.id.bt_setting_exit_account)
    Button btSettingExitAccount;

    @Inject
    SettingPresenter settingPresenter;

    private AlertDialog testAlertDialog;
    private boolean isTestSuccess = false;
    private String testBaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.t(TAG).d("onCreate(Bundle savedInstanceState)");
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        initSettingSection();
        initListener();
        init();
    }

    @NonNull
    @Override
    public SettingPresenter createPresenter() {
        Logger.t(TAG).d("createPresenter()");
        getActivityComponent().inject(this);

        return settingPresenter;
    }

    private void init() {
        if (UserHelper.isUserInfoComplete(user)) {
            btSettingExitAccount.setVisibility(View.VISIBLE);
        }
        testAlertDialog = DialogUtils.initLodingDialog(context, "测试中，请稍后...");
    }

    private void initListener() {
        btSettingExitAccount.setOnClickListener(this);
    }


    private void initSettingSection() {
        qmuiGroupListView.setSeparatorStyle(QMUIGroupListView.SEPARATOR_STYLE_NORMAL);
        QMUIGroupListView.Section tsec = QMUIGroupListView.newSection(this);
        //91pron地址
        QMUICommonListItemView addressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_91porn));
        addressItemWithChevron.setId(R.id.setting_item_91_porn_address);
        addressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String video91Address = AddressHelper.getInstance().getVideo91PornAddress();
        addressItemWithChevron.setDetailText(TextUtils.isEmpty(video91Address) ? "未设置" : video91Address);
        addressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //91论坛地址
        QMUICommonListItemView forumAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_forum_91porn));
        forumAddressItemWithChevron.setId(R.id.setting_item_t66y_forum_address);
        forumAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String forum91Address = AddressHelper.getInstance().getForum91PornAddress();
        forumAddressItemWithChevron.setDetailText(TextUtils.isEmpty(forum91Address) ? "未设置" : forum91Address);
        forumAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //朱古力视频地址
        QMUICommonListItemView pigAvAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_pig_av));
        pigAvAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        String pigAvAddress = AddressHelper.getInstance().getPigAvAddress();
        pigAvAddressItemWithChevron.setDetailText(TextUtils.isEmpty(pigAvAddress) ? "未设置" : pigAvAddress);
        pigAvAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //草榴地址
        QMUICommonListItemView t66yAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_t66y));
        t66yAddressItemWithChevron.setId(R.id.setting_item_t66y_forum_address);
        t66yAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        tsec.addItemView(addressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, Keys.KEY_SP_CUSTOM_ADDRESS);
            }
        });
        tsec.addItemView(forumAddressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, Keys.KEY_SP_FORUM_91_PORN_ADDRESS);
            }
        });
        tsec.addItemView(pigAvAddressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddressSettingDialog((QMUICommonListItemView) v, Keys.KEY_SP_PIG_AV_ADDRESS);
            }
        });
        tsec.addItemView(t66yAddressItemWithChevron, this);
        tsec.addTo(qmuiGroupListView);

        //播放引擎
        QMUICommonListItemView playEngineItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.playback_engine));
        playEngineItemWithChevron.setId(R.id.setting_item_player_engine_choice);
        playEngineItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        final int checkedIndex = (int) SPUtils.get(this, Keys.KEY_SP_PLAYBACK_ENGINE, PlaybackEngine.DEFAULT_PLAYER_ENGINE);
        playEngineItemWithChevron.setDetailText(PlaybackEngine.PLAY_ENGINE_ITEMS[checkedIndex]);
        playEngineItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUIGroupListView.newSection(this)
                .addItemView(playEngineItemWithChevron, this)
                .addTo(qmuiGroupListView);


        QMUIGroupListView.Section sec = QMUIGroupListView.newSection(this);

        boolean isForbidden = (boolean) SPUtils.get(this, Keys.KEY_SP_FORBIDDEN_AUTO_RELEASE_MEMORY_WHEN_LOW_MEMORY, false);
        QMUICommonListItemView itemWithSwitchForbidden = qmuiGroupListView.createItemView("禁用自动释放内存功能");
        itemWithSwitchForbidden.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemWithSwitchForbidden.getSwitch().setChecked(isForbidden);
        itemWithSwitchForbidden.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(SettingActivity.this, Keys.KEY_SP_FORBIDDEN_AUTO_RELEASE_MEMORY_WHEN_LOW_MEMORY, isChecked);
                if (isChecked) {
                    showForbiddenReleaseMemoryTipInfoDialog();
                }
            }
        });

        //非Wi-Fi环境下下载视频
        boolean isDownloadNeedWifi = (boolean) SPUtils.get(this, Keys.KEY_SP_DOWNLOAD_VIDEO_NEED_WIFI, false);
        QMUICommonListItemView itemWithSwitch = qmuiGroupListView.createItemView("非Wi-Fi环境下下载视频");
        itemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        itemWithSwitch.getSwitch().setChecked(!isDownloadNeedWifi);
        itemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(SettingActivity.this, Keys.KEY_SP_DOWNLOAD_VIDEO_NEED_WIFI, !isChecked);
            }
        });
        sec.addItemView(itemWithSwitch, null);
        sec.addItemView(itemWithSwitchForbidden, this);
        sec.addTo(qmuiGroupListView);
    }

    private String getAddressSettingTitle(String key) {
        switch (key) {
            case Keys.KEY_SP_CUSTOM_ADDRESS:
                return "91porn-地址设置";
            case Keys.KEY_SP_FORUM_91_PORN_ADDRESS:
                return "91论坛地址设置";
            case Keys.KEY_SP_PIG_AV_ADDRESS:
                return "朱古力地址设置";
            default:
                return "地址设置";
        }
    }

    private void showAddressSettingDialog(final QMUICommonListItemView qmuiCommonListItemView, final String key) {
        View view = getLayoutInflater().inflate(R.layout.dialog_setting_address, qmuiCommonListItemView, false);
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle(getAddressSettingTitle(key))
                .setView(view)
                .setCancelable(false)
                .show();
        AppCompatButton okAppCompatButton = view.findViewById(R.id.bt_dialog_address_setting_ok);
        AppCompatButton backAppCompatButton = view.findViewById(R.id.bt_dialog_address_setting_back);
        AppCompatButton testAppCompatButton = view.findViewById(R.id.bt_dialog_address_setting_test);
        final AppCompatAutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.atv_dialog_address_setting_address);
        autoCompleteTextView.setText(testBaseUrl);
        if (!TextUtils.isEmpty(testBaseUrl)) {
            autoCompleteTextView.setSelection(testBaseUrl.length());
        }
        final String[] address = {"http://", "https://", "http://www.", "https://www."};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_auto_complete_textview, address);
        autoCompleteTextView.setAdapter(adapter);
        okAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = autoCompleteTextView.getText().toString().trim();
                if (!checkAddress(address)) {
                    return;
                }
                testBaseUrl = address;
                alertDialog.dismiss();
                if (isTestSuccess) {
                    SPUtils.put(context, key, address);
                    qmuiCommonListItemView.setDetailText(address);
                    showMessage("设置成功", TastyToast.INFO);
                    sendUpdateSuccessMessage(key, address);
                } else {
                    showConfirmDialog(qmuiCommonListItemView, address, key);
                }
            }
        });
        backAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        testAppCompatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = autoCompleteTextView.getText().toString().trim();
                if (!checkAddress(address)) {
                    return;
                }
                testBaseUrl = address;
                alertDialog.dismiss();
                beginTestAddress(address, qmuiCommonListItemView, key);
            }
        });
    }

    private void showConfirmDialog(final QMUICommonListItemView qmuiCommonListItemView, final String address, final String key) {
        new AlertDialog.Builder(this, R.style.MyDialogTheme)
                .setTitle("温馨提示")
                .setMessage("地址还未测试成功，确认设置吗？")
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtils.put(context, key, address);
                        qmuiCommonListItemView.setDetailText(address);
                        showMessage("设置成功", TastyToast.INFO);
                        sendUpdateSuccessMessage(key, address);
                    }
                })
                .setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAddressSettingDialog(qmuiCommonListItemView, key);
                    }
                })
                .show();
    }

    private boolean checkAddress(String address) {
        HttpUrl httpUrl = HttpUrl.parse(address);
        if (httpUrl == null) {
            showMessage("设置失败，输入地址格式不正确，(不要忘了最后面的“/”)", TastyToast.ERROR);
            return false;
        }
        List<String> pathSegments = httpUrl.pathSegments();
        if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
            showMessage("设置失败，输入地址格式不正确，(不要忘了最后面的“/”)", TastyToast.ERROR);
            return false;
        }
        return true;
    }

    private void beginTestAddress(String address, QMUICommonListItemView qmuiCommonListItemView, String key) {
        switch (key) {
            case Keys.KEY_SP_CUSTOM_ADDRESS:
                presenter.test91PornVideo(address, qmuiCommonListItemView, key);
                break;
            case Keys.KEY_SP_FORUM_91_PORN_ADDRESS:
                presenter.test91PornForum(address, qmuiCommonListItemView, key);
                break;
            case Keys.KEY_SP_PIG_AV_ADDRESS:
                presenter.testPigAv(address, qmuiCommonListItemView, key);
                break;
            default:
        }
    }

    private void sendUpdateSuccessMessage(String key, String address) {
        switch (key) {
            case Keys.KEY_SP_CUSTOM_ADDRESS:
                // 全局 BaseUrl 的优先级低于 Domain-Name header 中单独配置的,其他未配置的接口将受全局 BaseUrl 的影响
                RetrofitUrlManager.getInstance().setGlobalDomain(address);
                break;
            case Keys.KEY_SP_FORUM_91_PORN_ADDRESS:
                apiManager.initForum91RetrofitService(address);
                EventBus.getDefault().post(new BaseUrlChangeEvent());
                break;
            case Keys.KEY_SP_PIG_AV_ADDRESS:
                apiManager.initPigAvRetrofitService(address);
                EventBus.getDefault().post(new BaseUrlChangeEvent());
                break;
            default:
        }
        testBaseUrl = "";
    }

    private void showForbiddenReleaseMemoryTipInfoDialog() {
        QMUIDialog.MessageDialogBuilder builder = new QMUIDialog.MessageDialogBuilder(this);
        builder.setTitle("温馨提示");
        builder.setMessage("为了获得较好的体验，新版本程序占用内存较高，这可能导致后台运行而系统内存不足时成为系统回收内存的优先对象（尤其在低内存手机上），因此我做了自动释放内存功能，但这同时也会使体验有所下降，你可以强制关闭次功能");
        builder.addAction("知道了", new QMUIDialogAction.ActionListener() {
            @Override
            public void onClick(QMUIDialog dialog, int index) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showPlaybackEngineChoiceDialog(final QMUICommonListItemView qmuiCommonListItemView) {
        final int checkedIndex = (int) SPUtils.get(this, Keys.KEY_SP_PLAYBACK_ENGINE, PlaybackEngine.DEFAULT_PLAYER_ENGINE);
        new QMUIDialog.CheckableDialogBuilder(this)
                .setTitle("播放引擎选择")
                .setCheckedIndex(checkedIndex)
                .addItems(PlaybackEngine.PLAY_ENGINE_ITEMS, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SPUtils.put(SettingActivity.this, Keys.KEY_SP_PLAYBACK_ENGINE, which);
                        qmuiCommonListItemView.setDetailText(PlaybackEngine.PLAY_ENGINE_ITEMS[which]);
                        showMessage("设置成功", TastyToast.SUCCESS);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("退出登录");
        builder.setMessage("退出当前帐号？");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                apiManager.cleanCookies();
                user.cleanProperties();

//                SPUtils.put(SettingActivity.this, Keys.KEY_SP_USER_LOGIN_USERNAME, "");
//                SPUtils.put(SettingActivity.this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
//                SPUtils.put(SettingActivity.this, Keys.KEY_SP_USER_AUTO_LOGIN, false);

                Intent intent = new Intent(SettingActivity.this, UserLoginActivity.class);
                startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
                finish();
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bt_setting_exit_account:
                showExitDialog();
                break;
            case R.id.setting_item_player_engine_choice:
                showPlaybackEngineChoiceDialog((QMUICommonListItemView) v);
                break;
            case R.id.setting_item_t66y_forum_address:
//                showAddressSettingDialog((QMUICommonListItemView) v, "");
                break;
            default:
        }
    }

    @Override
    public void showTesting(boolean isTest) {
        isTestSuccess = false;
        testAlertDialog.show();
    }

    @Override
    public void testSuccess(String message, QMUICommonListItemView qmuiCommonListItemView, String key) {
        isTestSuccess = true;
        dismissDialog();
        showMessage(message, TastyToast.SUCCESS);
        showAddressSettingDialog(qmuiCommonListItemView, key);
    }

    @Override
    public void testFailure(String message, QMUICommonListItemView qmuiCommonListItemView, String key) {
        isTestSuccess = false;
        showMessage(message, TastyToast.ERROR);
        showAddressSettingDialog(qmuiCommonListItemView, key);
        dismissDialog();
    }

    private void dismissDialog() {
        if (testAlertDialog.isShowing() && !isFinishing()) {
            testAlertDialog.dismiss();
        }
    }
}
