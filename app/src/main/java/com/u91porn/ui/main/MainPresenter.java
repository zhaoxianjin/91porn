package com.u91porn.ui.main;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.ui.update.UpdatePresenter;

/**
 * @author flymegoc
 * @date 2017/12/23
 */

public class MainPresenter extends MvpBasePresenter<MainView> implements IMain {

    private UpdatePresenter updatePresenter;

    public MainPresenter(UpdatePresenter updatePresenter) {
        this.updatePresenter = updatePresenter;
    }

    @Override
    public void checkUpdate(int versionCode) {
        updatePresenter.checkUpdate(versionCode, new UpdatePresenter.UpdateListener() {
            @Override
            public void needUpdate(UpdateVersion updateVersion) {
                if (isViewAttached()) {
                    getView().needUpdate(updateVersion);
                }
            }

            @Override
            public void noNeedUpdate() {
                if (isViewAttached()) {
                    getView().noNeedUpdate();
                }
            }

            @Override
            public void checkUpdateError(String message) {
                if (isViewAttached()) {
                    getView().checkUpdateError(message);
                }
            }
        });
    }
}
