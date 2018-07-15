package com.u91porn.ui.splash;

import com.u91porn.data.model.User;
import com.u91porn.ui.BaseView;

/**
 * @author flymegoc
 * @date 2017/12/21
 */

public interface SplashView extends BaseView{
    void loginSuccess(User user);

    void loginError(String message);
}
