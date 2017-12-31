package com.u91porn.ui.main;

import android.support.annotation.NonNull;

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
            public void needUpdate(final UpdateVersion updateVersion) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.needUpdate(updateVersion);
                    }
                });
            }

            @Override
            public void noNeedUpdate() {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.noNeedUpdate();
                    }
                });
            }

            @Override
            public void checkUpdateError(final String message) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.checkUpdateError(message);
                    }
                });
            }
        });
    }
}
