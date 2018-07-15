package com.u91porn.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.u91porn.utils.constants.Keys;

import java.util.Random;

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
    private static Random mRandom = new Random();
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

    /**
     * 获取随机ip地址
     *
     * @return
     */
    public static String getRandomIPAddress() {

        return String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255)) + "." + String.valueOf(mRandom.nextInt(255));
    }

    public String getVideo91PornAddress() {
        return (String) SPUtils.get(context, Keys.KEY_SP_CUSTOM_ADDRESS, "");
    }

    public String getForum91PornAddress() {
        return (String) SPUtils.get(context, Keys.KEY_SP_FORUM_91_PORN_ADDRESS, "");
    }

    public String getPigAvAddress() {
        return (String) SPUtils.get(context, Keys.KEY_SP_PIG_AV_ADDRESS, "");
    }

    public boolean isEmpty(String key) {
        String address = (String) SPUtils.get(context, key, "");
        return TextUtils.isEmpty(address);
    }
}
