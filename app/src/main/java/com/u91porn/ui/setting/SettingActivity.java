package com.u91porn.ui.setting;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.qmuiteam.qmui.widget.dialog.QMUIDialog;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.Api;
import com.u91porn.data.model.User;
import com.u91porn.ui.BaseAppCompatActivity;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.Constants;
import com.u91porn.utils.Keys;
import com.u91porn.utils.PlaybackEngine;
import com.u91porn.utils.RegexUtils;
import com.u91porn.utils.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.jessyan.retrofiturlmanager.RetrofitUrlManager;

/**
 * @author flymegoc
 */
public class SettingActivity extends BaseAppCompatActivity implements View.OnClickListener {

    private static final String TAG = SettingActivity.class.getSimpleName();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.mine_list)
    QMUIGroupListView qmuiGroupListView;
    @BindView(R.id.bt_setting_exit_account)
    Button btSettingExitAccount;
    private String playEngineStr;
    private String addressSettingStr;
    private String t66yAddressSettingStr;
    private String forumAddressSettingStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        setTitle("设置");
        initStr();
        initSettingSection();
        initListener();
        init();
    }

    private void init() {
        User user = MyApplication.getInstace().getUser();
        if (user == null) {
            btSettingExitAccount.setVisibility(View.INVISIBLE);
        }
    }

    private void initListener() {
        btSettingExitAccount.setOnClickListener(this);
    }

    private void initStr() {
        playEngineStr = getString(R.string.playback_engine);
        addressSettingStr = getString(R.string.address_91porn);
        t66yAddressSettingStr = getString(R.string.address_t66y);
        forumAddressSettingStr = getString(R.string.address_forum_91porn);
    }

    private void initSettingSection() {
        qmuiGroupListView.setSeparatorStyle(QMUIGroupListView.SEPARATOR_STYLE_NORMAL);
        QMUIGroupListView.Section tsec = QMUIGroupListView.newSection(this);
        //91pron地址
        QMUICommonListItemView addressItemWithChevron = qmuiGroupListView.createItemView(addressSettingStr);
        addressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        addressItemWithChevron.setDetailText(MyApplication.getInstace().getHost());
        addressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //91论坛地址
        QMUICommonListItemView forumAddressItemWithChevron = qmuiGroupListView.createItemView(forumAddressSettingStr);
        forumAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //草榴地址
        QMUICommonListItemView t66yAddressItemWithChevron = qmuiGroupListView.createItemView(t66yAddressSettingStr);
        t66yAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        tsec.addItemView(addressItemWithChevron, this);
        tsec.addItemView(forumAddressItemWithChevron, this);
        tsec.addItemView(t66yAddressItemWithChevron, this);
        tsec.addTo(qmuiGroupListView);

        //播放引擎
        QMUICommonListItemView playEnginItemWithChevron = qmuiGroupListView.createItemView(playEngineStr);
        playEnginItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        final int checkedIndex = (int) SPUtils.get(this, Keys.KEY_SP_PLAYBACK_ENGINE, PlaybackEngine.DEFAULT_PLAYER_ENGINE);
        playEnginItemWithChevron.setDetailText(PlaybackEngine.PLAY_ENGINE_ITEMS[checkedIndex]);
        playEnginItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);
        QMUIGroupListView.newSection(this)
                .addItemView(playEnginItemWithChevron, this)
                .addTo(qmuiGroupListView);

        //非Wi-Fi环境下下载视频
        QMUIGroupListView.Section sec = QMUIGroupListView.newSection(this);
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
        sec.addTo(qmuiGroupListView);
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

    @SuppressLint("SetTextI18n")
    private void showIPAddressSettingDialog(final QMUICommonListItemView qmuiCommonListItemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("访问地址设置");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_ip_address_setting, null);
        final RadioGroup radioGroup = view.findViewById(R.id.rg_address);
        RadioButton naverRadioButton = view.findViewById(R.id.rb_never_go_address);
        RadioButton willGoRadioButton = view.findViewById(R.id.rb_will_go_someday);
        RadioButton customRadioButton = view.findViewById(R.id.rb_now_custom_adress);
        final EditText editText = view.findViewById(R.id.et_custom_ip_address);
        final String customAddress = (String) SPUtils.get(this, Keys.KEY_SP_CUSTOM_ADDRESS, "");
        String nowAddress = (String) SPUtils.get(this, Keys.KEY_SP_NOW_ADDRESS, "");
        if (TextUtils.isEmpty(customAddress)) {
            customRadioButton.setVisibility(View.GONE);
        } else {
            customRadioButton.setText(customAddress + "(当前自定义地址)");
        }
        willGoRadioButton.setText(Api.APP_DEFAULT_DOMAIN + "(不需翻墙，但会被封杀)");
        if (!TextUtils.isEmpty(nowAddress)) {
            if (nowAddress.equals(Api.APP_91PORN_DOMAIN)) {
                naverRadioButton.setChecked(true);
            } else if (nowAddress.equals(Api.APP_DEFAULT_DOMAIN)) {
                willGoRadioButton.setChecked(true);
            } else if (nowAddress.equals(customAddress)) {
                customRadioButton.setVisibility(View.VISIBLE);
                customRadioButton.setText(customAddress + "(当前自定义地址)");
                customRadioButton.setChecked(true);
            }
        }
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String customAddress = editText.getText().toString().trim();
                //优先填入的自定义地址
                if (!TextUtils.isEmpty(customAddress)) {
                    //简单验证地址是否合法
                    if (RegexUtils.isURL(customAddress)) {
                        MyApplication.getInstace().setHost(customAddress);
                        SPUtils.put(SettingActivity.this, Keys.KEY_SP_CUSTOM_ADDRESS, customAddress);
                    } else {
                        showIPAddressSettingDialog(qmuiCommonListItemView);
                        showMessage("设置失败，输入地址格式不正确", TastyToast.ERROR);
                    }

                } else {
                    switch (radioGroup.getCheckedRadioButtonId()) {
                        case R.id.rb_never_go_address:
                            MyApplication.getInstace().setHost(Api.APP_91PORN_DOMAIN);
                            break;
                        case R.id.rb_will_go_someday:
                            MyApplication.getInstace().setHost(Api.APP_DEFAULT_DOMAIN);
                            break;
                        case R.id.rb_now_custom_adress:
                            MyApplication.getInstace().setHost(customAddress);
                            break;
                        default:
                    }
                }
                // 全局 BaseUrl 的优先级低于 Domain-Name header 中单独配置的,其他未配置的接口将受全局 BaseUrl 的影响
                RetrofitUrlManager.getInstance().setGlobalDomain(MyApplication.getInstace().getHost());
                qmuiCommonListItemView.setDetailText(MyApplication.getInstace().getHost());
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("退出登录");
        builder.setMessage("退出当前帐号？");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyApplication.getInstace().cleanCookies();
                MyApplication.getInstace().setUser(null);
                SPUtils.put(SettingActivity.this, Keys.KEY_SP_USER_LOGIN_USERNAME, "");
                SPUtils.put(SettingActivity.this, Keys.KEY_SP_USER_LOGIN_PASSWORD, "");
                SPUtils.put(SettingActivity.this, Keys.KEY_SP_USER_AUTO_LOGIN, false);

                Intent intent = new Intent(SettingActivity.this, UserLoginActivity.class);
                startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    private void showT66yAddressSettingDialog() {

    }

    private void showForumAddressSettingDialog() {

    }

    @Override
    public void onClick(View v) {
        if (v instanceof QMUICommonListItemView) {
            actionClickList((QMUICommonListItemView) v);
        } else {
            switch (v.getId()) {
                case R.id.bt_setting_exit_account:
                    showExitDialog();
                    break;
                default:
            }
        }
    }

    private void actionClickList(QMUICommonListItemView qmuiCommonListItemView) {
        String content = qmuiCommonListItemView.getText().toString();
        if (TextUtils.isEmpty(content)) {
            return;
        }
        if (content.equals(addressSettingStr)) {
            showIPAddressSettingDialog(qmuiCommonListItemView);
        } else if (content.equals(t66yAddressSettingStr)) {
            showT66yAddressSettingDialog();
        } else if (content.equals(playEngineStr)) {
            showPlaybackEngineChoiceDialog(qmuiCommonListItemView);
        } else if (content.equals(forumAddressSettingStr)) {
            showForumAddressSettingDialog();
        }
    }
}
