package com.u91porn.ui.setting;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
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
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction;
import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.MyApplication;
import com.u91porn.R;
import com.u91porn.data.Api;
import com.u91porn.data.ApiManager;
import com.u91porn.data.model.User;
import com.u91porn.eventbus.BaseUrlChangeEvent;
import com.u91porn.ui.BaseAppCompatActivity;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.Constants;
import com.u91porn.utils.Keys;
import com.u91porn.utils.PlaybackEngine;
import com.u91porn.utils.RegexUtils;
import com.u91porn.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

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
    private AlertDialog setting91PornAddressAlertDialog;
    private AlertDialog settingForum91AddressAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        initToolBar(toolbar);
        initSettingSection();
        initListener();
        init();
    }

    private void init() {
        User user = MyApplication.getInstace().getUser();
        if (user != null) {
            btSettingExitAccount.setVisibility(View.VISIBLE);
        }
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
        addressItemWithChevron.setDetailText(AddressHelper.getInstance().getVideo91PornAddress());
        addressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //91论坛地址
        QMUICommonListItemView forumAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_forum_91porn));
        forumAddressItemWithChevron.setId(R.id.setting_item_t66y_forum_address);
        forumAddressItemWithChevron.setOrientation(QMUICommonListItemView.VERTICAL);
        forumAddressItemWithChevron.setDetailText(AddressHelper.getInstance().getForum91PornAddress());
        forumAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        //草榴地址
        QMUICommonListItemView t66yAddressItemWithChevron = qmuiGroupListView.createItemView(getString(R.string.address_t66y));
        t66yAddressItemWithChevron.setId(R.id.setting_item_t66y_forum_address);
        t66yAddressItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        tsec.addItemView(addressItemWithChevron, this);
        tsec.addItemView(forumAddressItemWithChevron, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForumAddressSettingDialog((QMUICommonListItemView) v);
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

    @SuppressLint("SetTextI18n")
    private void showIPAddressSettingDialog(final QMUICommonListItemView qmuiCommonListItemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("访问地址设置");
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_layout_ip_address_setting, null);
        final RadioGroup radioGroup = view.findViewById(R.id.rg_address);
        RadioButton neverRadioButton = view.findViewById(R.id.rb_never_go_address);
        RadioButton willGoRadioButton = view.findViewById(R.id.rb_will_go_someday);

        final EditText editText = view.findViewById(R.id.et_custom_ip_address);
        final String customAddress = (String) SPUtils.get(this, Keys.KEY_SP_CUSTOM_ADDRESS, "");

        willGoRadioButton.setText(Api.APP_DEFAULT_DOMAIN + "(不需翻墙，但会被封杀)");
        if (!TextUtils.isEmpty(customAddress)) {
            switch (customAddress) {
                case Api.APP_91PORN_DOMAIN:
                    neverRadioButton.setChecked(true);
                    break;
                case Api.APP_DEFAULT_DOMAIN:
                    willGoRadioButton.setChecked(true);
                    break;
                default:
                    neverRadioButton.setChecked(false);
                    willGoRadioButton.setChecked(false);
                    editText.setText(customAddress);
                    break;
            }
        }
        builder.setView(view);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String address;
                switch (checkedId) {
                    case R.id.rb_never_go_address:
                        address = Api.APP_91PORN_DOMAIN;
                        break;
                    case R.id.rb_will_go_someday:
                        address = Api.APP_DEFAULT_DOMAIN;
                        break;
                    default:
                        address = Api.APP_DEFAULT_DOMAIN;
                }
                updateVideo91PornAddress(qmuiCommonListItemView, address);
                setting91PornAddressAlertDialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String customAddress = editText.getText().toString().trim();
                //优先填入的自定义地址
                if (!TextUtils.isEmpty(customAddress)) {
                    //简单验证地址是否合法
                    if (RegexUtils.isURL(customAddress) && customAddress.endsWith("/")) {
                        updateVideo91PornAddress(qmuiCommonListItemView, customAddress);
                    } else {
                        showIPAddressSettingDialog(qmuiCommonListItemView);
                        showMessage("设置失败，输入地址格式不正确，(不要忘了最后面的“/”)", TastyToast.ERROR);
                    }
                } else {
                    showMessage("没有填写地址，设置未更改", TastyToast.INFO);
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        setting91PornAddressAlertDialog = builder.show();
    }

    private void updateVideo91PornAddress(QMUICommonListItemView qmuiCommonListItemView, String address) {
        SPUtils.put(SettingActivity.this, Keys.KEY_SP_CUSTOM_ADDRESS, address);
        // 全局 BaseUrl 的优先级低于 Domain-Name header 中单独配置的,其他未配置的接口将受全局 BaseUrl 的影响
        RetrofitUrlManager.getInstance().setGlobalDomain(address);
        qmuiCommonListItemView.setDetailText(address);
        showMessage("设置成功", TastyToast.INFO);
    }

    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("退出登录");
        builder.setMessage("退出当前帐号？");
        builder.setPositiveButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ApiManager.getInstance().cleanCookies();
                MyApplication.getInstace().setUser(null);

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

    private void showT66yAddressSettingDialog() {

    }

    private void showForumAddressSettingDialog(final QMUICommonListItemView qmuiCommonListItemView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        builder.setTitle("论坛地址设置");
        View view = View.inflate(this, R.layout.dialog_91_forum_address_setting, null);
        view.findViewById(R.id.bt_dialog_setting_forum_91_porn_reset_default_address).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingForum91AddressAlertDialog.dismiss();
                updateForum91PronAddress(qmuiCommonListItemView, "");
            }
        });
        final AppCompatEditText editText = view.findViewById(R.id.et_dialog_setting_forum_91_porn_address);
        String address = (String) SPUtils.get(this, Keys.KEY_SP_FORUM_91_PORN_ADDRESS, "");
        editText.setText(address);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(address)) {
                    if (RegexUtils.isURL(address) && address.endsWith("/")) {
                        updateForum91PronAddress(qmuiCommonListItemView, address);
                    } else {
                        showForumAddressSettingDialog(qmuiCommonListItemView);
                        showMessage("设置失败，输入地址格式不正确，(不要忘了最后面的“/”)", TastyToast.ERROR);
                    }
                } else {
                    showMessage("没有填写，木有任何改变", TastyToast.INFO);
                }
            }
        });
        builder.setNegativeButton("取消", null);
        settingForum91AddressAlertDialog = builder.show();
    }

    private void updateForum91PronAddress(QMUICommonListItemView qmuiCommonListItemView, String address) {
        SPUtils.put(SettingActivity.this, Keys.KEY_SP_FORUM_91_PORN_ADDRESS, address);
        ApiManager.getInstance().initForum91RetrofitService();
        if (TextUtils.isEmpty(address)) {
            qmuiCommonListItemView.setDetailText(Api.APP_91PRON_FROUM_DOMAIN);
        } else {
            qmuiCommonListItemView.setDetailText(address);
        }
        showMessage("设置成功", TastyToast.INFO);
        EventBus.getDefault().post(new BaseUrlChangeEvent());
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
            case R.id.setting_item_91_porn_address:
                showIPAddressSettingDialog((QMUICommonListItemView) v);
                break;
            case R.id.setting_item_91_porn_forum_address:
                showForumAddressSettingDialog((QMUICommonListItemView) v);
                break;
            case R.id.setting_item_t66y_forum_address:
                showT66yAddressSettingDialog();
                break;
            default:
        }
    }
}
