package com.u91porn.ui.user;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * @author flymegoc
 * @date 2017/12/10
 */

public interface IBaseUser {
    /**
     * @param username     用户名
     * @param password     密码
     * @param fingerprint  人机识别码
     * @param fingerprint2 人机识别码
     * @param captcha      验证码
     * @param actionlogin  登录
     * @param x            x坐标
     * @param y            y坐标
     * @return ob
     */

    void login(String username, String password, String fingerprint, String fingerprint2, String captcha, String actionlogin, String x, String y,String referer);

}
