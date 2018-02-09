package com.u91porn.ui.mine;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.qmuiteam.qmui.widget.grouplist.QMUICommonListItemView;
import com.qmuiteam.qmui.widget.grouplist.QMUIGroupListView;
import com.sdsmdg.tastytoast.TastyToast;
import com.u91porn.R;
import com.u91porn.data.model.User;
import com.u91porn.eventbus.ProxySetEvent;
import com.u91porn.ui.BaseFragment;
import com.u91porn.ui.about.AboutActivity;
import com.u91porn.ui.download.DownloadActivity;
import com.u91porn.ui.favorite.FavoriteActivity;
import com.u91porn.ui.history.HistoryActivity;
import com.u91porn.ui.main.MainActivity;
import com.u91porn.ui.proxy.ProxySettingActivity;
import com.u91porn.ui.setting.SettingActivity;
import com.u91porn.ui.user.UserLoginActivity;
import com.u91porn.utils.AddressHelper;
import com.u91porn.utils.SPUtils;
import com.u91porn.utils.UserHelper;
import com.u91porn.utils.constants.Constants;
import com.u91porn.utils.constants.Keys;
import com.u91porn.widget.ObservableScrollView;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 *
 * @author flymegoc
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = MineFragment.class.getSimpleName();
    @BindView(R.id.tv_nav_username)
    TextView tvNavUsername;
    @BindView(R.id.tv_nav_last_login_time)
    TextView tvNavLastLoginTime;
    @BindView(R.id.tv_nav_last_login_ip)
    TextView tvNavLastLoginIp;
    @BindView(R.id.mine_list)
    QMUIGroupListView mineList;
    Unbinder unbinder;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.ov_setting_wrapper)
    ObservableScrollView observableScrollView;
    private String myFavoriteStr;
    private String proxyStr;
    public String myDownloadStr;
    private String viewHistoryStr;
    private String nightModeStr;
    private String aboutMeStr;
    private String moreSettingStr;

    private int scrollYPosition = 0;
    private QMUICommonListItemView openProxyItemWithSwitch;

    public MineFragment() {

        // Required empty public constructor
    }

    public static MineFragment getInstance() {
        return new MineFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivityComponent().inject(this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        imageView.setOnClickListener(this);
        observableScrollView.setOnScollChangedListener(new ObservableScrollView.OnScollChangedListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                scrollYPosition = y;
            }
        });
        observableScrollView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int scrollYPosition = (int) SPUtils.get(context, Keys.KEY_SETTING_SCROLLVIEW_SCROLL_POSITION, 0);
                if (scrollYPosition > 0) {
                    SPUtils.put(context, Keys.KEY_SETTING_SCROLLVIEW_SCROLL_POSITION, 0);
                    observableScrollView.scrollTo(0, scrollYPosition);
                }
            }
        }, 200);
        initMineSection();
    }

    @Override
    public void onResume() {
        super.onResume();
        setUpUserInfo(user);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initStr();
    }

    private void initStr() {
        myFavoriteStr = getString(R.string.my_collect);
        proxyStr = getString(R.string.proxy_setting);
        myDownloadStr = getString(R.string.my_download);
        viewHistoryStr = getString(R.string.history_views);
        nightModeStr = getString(R.string.night_mode);
        aboutMeStr = getString(R.string.about_me);
        moreSettingStr = getString(R.string.more_setting);
    }

    private void initMineSection() {

        boolean openNightMode = (boolean) SPUtils.get(context, Keys.KEY_SP_OPEN_NIGHT_MODE, false);
        QMUICommonListItemView openNightModeItemWithSwitch = mineList.createItemView(nightModeStr);
        openNightModeItemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        openNightModeItemWithSwitch.getSwitch().setChecked(openNightMode);
        openNightModeItemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SPUtils.put(context, Keys.KEY_SP_OPEN_NIGHT_MODE, isChecked);
                SPUtils.put(context, Keys.KEY_SETTING_SCROLLVIEW_SCROLL_POSITION, scrollYPosition);
                AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(Keys.KEY_SELECT_INDEX, 4);
                startActivity(intent);
                activity.finish();
                activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        boolean openProxy = (boolean) SPUtils.get(context, Keys.KEY_SP_OPEN_HTTP_PROXY, false);
        openProxyItemWithSwitch = mineList.createItemView(proxyStr);
        openProxyItemWithSwitch.setOrientation(QMUICommonListItemView.VERTICAL);
        final String proxyHost = (String) SPUtils.get(context, Keys.KEY_SP_PROXY_IP_ADDRESS, "");
        final int port = (int) SPUtils.get(context, Keys.KEY_SP_PROXY_PORT, 0);
        if (TextUtils.isEmpty(proxyHost) || port == 0) {
            openProxyItemWithSwitch.setDetailText("长按设置");
        } else {
            openProxyItemWithSwitch.setDetailText(proxyHost + " : " + port);
        }

        openProxyItemWithSwitch.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_SWITCH);
        openProxyItemWithSwitch.getSwitch().setChecked(openProxy);
        openProxyItemWithSwitch.getSwitch().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    showMessage("长按可设置哟", TastyToast.INFO);
                }
                if (TextUtils.isEmpty(proxyHost) || port == 0) {
                    buttonView.setChecked(false);
                    SPUtils.put(context, Keys.KEY_SP_OPEN_HTTP_PROXY, false);
                    return;
                }
                SPUtils.put(context, Keys.KEY_SP_OPEN_HTTP_PROXY, isChecked);
                //重新实例化接口
                apiManager.init91PornRetrofitService(AddressHelper.getInstance().getVideo91PornAddress(), false);
                //通知已经存在的更改为最新的
                EventBus.getDefault().post(new ProxySetEvent(proxyHost, port));
            }
        });

        QMUICommonListItemView favoriteItemWithChevron = mineList.createItemView(myFavoriteStr);
        favoriteItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView downloadItemWithChevron = mineList.createItemView(myDownloadStr);
        downloadItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUICommonListItemView viewHistoryItemWithChevron = mineList.createItemView(viewHistoryStr);
        viewHistoryItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        mineList.setSeparatorStyle(QMUIGroupListView.SEPARATOR_STYLE_NORMAL);

        QMUIGroupListView.newSection(context)
                .addItemView(favoriteItemWithChevron, this)
                .addItemView(downloadItemWithChevron, this)
                .addItemView(viewHistoryItemWithChevron, this)
                .addItemView(openNightModeItemWithSwitch, null)
                .addTo(mineList);

        QMUIGroupListView.newSection(context)
                .addItemView(openProxyItemWithSwitch, null, new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Intent intent = new Intent(context, ProxySettingActivity.class);
                        startActivityWithAnimotion(intent);
                        return false;
                    }
                })
                .addTo(mineList);

        QMUICommonListItemView moreSettingItemWithChevron = mineList.createItemView(moreSettingStr);
        moreSettingItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(context)
                .addItemView(moreSettingItemWithChevron, this)
                .addTo(mineList);

        QMUICommonListItemView aboutItemWithChevron = mineList.createItemView(aboutMeStr);
        aboutItemWithChevron.setAccessoryType(QMUICommonListItemView.ACCESSORY_TYPE_CHEVRON);

        QMUIGroupListView.newSection(context)
                .addItemView(aboutItemWithChevron, this)
                .addTo(mineList);
    }


    @Override
    public void onProxySetEvent(ProxySetEvent proxySetEvent) {
        if (!TextUtils.isEmpty(proxySetEvent.getProxyIpAddress()) && proxySetEvent.getProxyPort() > 0) {
            openProxyItemWithSwitch.setDetailText(proxySetEvent.getProxyIpAddress() + " : " + proxySetEvent.getProxyPort());
        }
        boolean openProxy = (boolean) SPUtils.get(context, Keys.KEY_SP_OPEN_HTTP_PROXY, false);
        openProxyItemWithSwitch.getSwitch().setChecked(openProxy);
    }

    private void userImageViewClick() {
        if (UserHelper.isUserInfoComplete(user)) {
            return;
        }
        Intent intent = new Intent(context, UserLoginActivity.class);
        startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
    }

    @SuppressLint("SetTextI18n")
    private void setUpUserInfo(User user) {

        if (!UserHelper.isUserInfoComplete(user)) {
            tvNavUsername.setText("请登录");
            tvNavLastLoginTime.setText("---");
            tvNavLastLoginIp.setText("---");
            return;
        }

        if (!TextUtils.isEmpty(user.getStatus())) {
            String status = user.getStatus().contains("正常") ? "正常" : "异常";
            tvNavUsername.setText(user.getUserName() + "(" + status + ")");
        }
        if (!TextUtils.isEmpty(user.getLastLoginTime())) {
            tvNavLastLoginTime.setText(user.getLastLoginTime().replace("(如果你觉得时间不对,可能帐号被盗)", ""));
        }
        tvNavLastLoginIp.setText(user.getLastLoginIP());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.USER_LOGIN_REQUEST_CODE && resultCode == RESULT_OK) {
            setUpUserInfo(user);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        if (v instanceof QMUICommonListItemView) {
            String string = String.valueOf(((QMUICommonListItemView) v).getText());
            actionClickList(string);
        } else {
            switch (v.getId()) {
                case R.id.imageView:
                    userImageViewClick();
                    break;
                default:
            }
        }
    }

    private void actionClickList(String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        if (content.equals(myFavoriteStr)) {
            if (!UserHelper.isUserInfoComplete(user)) {
                Intent intent = new Intent(context, UserLoginActivity.class);
                intent.putExtra(Keys.KEY_INTENT_LOGIN_FOR_ACTION, UserLoginActivity.LOGIN_ACTION_FOR_LOOK_MY_FAVORITE);
                startActivityForResultWithAnimotion(intent, Constants.USER_LOGIN_REQUEST_CODE);
                return;
            }
            Intent intent = new Intent(context, FavoriteActivity.class);
            startActivityWithAnimotion(intent);
        } else if (content.equals(myDownloadStr)) {
            Intent intent = new Intent(context, DownloadActivity.class);
            startActivityWithAnimotion(intent);
        } else if (content.equals(viewHistoryStr)) {
            Intent intent = new Intent(context, HistoryActivity.class);
            startActivityWithAnimotion(intent);
        } else if (content.equals(aboutMeStr)) {
            Intent intent = new Intent(context, AboutActivity.class);
            startActivityWithAnimotion(intent);
        } else if (content.equals(moreSettingStr)) {
            Intent intent = new Intent(context, SettingActivity.class);
            startActivityWithAnimotion(intent);
        }
    }
}
