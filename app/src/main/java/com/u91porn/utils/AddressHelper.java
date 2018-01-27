package com.u91porn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.u91porn.data.Api;

/**
 * @author flymegoc
 * @date 2018/1/26
 */

public class AddressHelper {
    private static final String TAG = AddressHelper.class.getSimpleName();
    @SuppressLint("StaticFieldLeak")
    private static AddressHelper mAddressHelper;
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    private AddressHelper() {

    }

    /**
     * init
     *
     * @param ctx must be application context
     */
    public static void init(Context ctx) {
        if (mAddressHelper == null) {
            synchronized (AddressHelper.class) {
                mAddressHelper = new AddressHelper();
                context = ctx;
            }
        }
    }

    /**
     * must be call init before
     *
     * @return ob
     */
    public static AddressHelper getInstance() {
        return mAddressHelper;
    }

    public String getVideo91PornAddress() {
        String current91PornAddress = (String) SPUtils.get(context, Keys.KEY_SP_CUSTOM_ADDRESS, "");
        Logger.t(TAG).d("Video91PornAddress::" + current91PornAddress);
        if (TextUtils.isEmpty(current91PornAddress)) {
            current91PornAddress = Api.APP_DEFAULT_DOMAIN;
        }
        return current91PornAddress;
    }

    public String getForum91PornAddress() {
        String forum91PornAddress = (String) SPUtils.get(context, Keys.KEY_SP_FORUM_91_PORN_ADDRESS, "");
        Logger.t(TAG).d("Forum91PornAddress()::" + forum91PornAddress);
        if (TextUtils.isEmpty(forum91PornAddress)) {
            forum91PornAddress = Api.APP_91PRON_FROUM_DOMAIN;
        }
        return forum91PornAddress;
    }
}
