package com.u91porn.ui.user;

import com.u91porn.ui.BaseView;

/**
 * @author flymegoc
 * @date 2017/12/10
 */

public interface UserView extends BaseView {

    void loginSuccess();

    void loginError(String message);

    void registerSuccess();

    void registerFailure(String message);
}
