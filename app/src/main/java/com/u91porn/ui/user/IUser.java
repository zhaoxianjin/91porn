package com.u91porn.ui.user;

import retrofit2.http.Field;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * @author flymegoc
 * @date 2017/12/10
 */

public interface IUser extends IBaseUser {
    void register(String next, String username, String password1, String password2, String email, String captchaInput, String fingerprint, String vip, String actionSignup, String submitX, String submitY, String ipAddress,String referer);
}
