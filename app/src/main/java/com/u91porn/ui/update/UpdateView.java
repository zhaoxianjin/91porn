package com.u91porn.ui.update;

import com.u91porn.data.model.UpdateVersion;
import com.u91porn.ui.BaseView;

/**
 * @author flymegoc
 * @date 2017/12/22
 */

public interface UpdateView extends BaseView {
    void needUpdate(UpdateVersion updateVersion);

    void noNeedUpdate();

    void checkUpdateError(String message);
}
