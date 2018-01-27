package com.u91porn.ui.about;

import com.u91porn.ui.update.UpdateView;

/**
 * @author flymegoc
 * @date 2017/12/23
 */

public interface AboutView extends UpdateView {
    void showCleanDialog(String message);

    void cleanCacheSuccess(String message);

    void cleanCacheFailure(String message);

    void finishCountCacheFileSize(String message);

    void countCacheFileSizeError(String message);
}
