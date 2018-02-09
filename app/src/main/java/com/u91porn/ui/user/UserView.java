package com.u91porn.ui.user;

import com.u91porn.data.model.User;
import com.u91porn.ui.BaseView;

/**
 * @author flymegoc
 * @date 2017/12/10
 */

public interface UserView extends BaseView {

    void loginSuccess(User user);

    void loginError(String message);

    void registerSuccess(User user);

    void registerFailure(String message);
}
