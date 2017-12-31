package com.u91porn.ui.about;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.ui.update.UpdatePresenter;

/**
 * @author flymegoc
 * @date 2017/12/23
 */

public class AboutPresenter extends MvpBasePresenter<AboutView> implements IAbout {
    private UpdatePresenter updatePresenter;

    public AboutPresenter(UpdatePresenter updatePresenter) {
        this.updatePresenter = updatePresenter;
    }

    @Override
    public void checkUpdate(int versionCode) {
        updatePresenter.checkUpdate(versionCode, new UpdatePresenter.UpdateListener() {
            @Override
            public void needUpdate(final UpdateVersion updateVersion) {
                ifViewAttached(new ViewAction<AboutView>() {
                    @Override
                    public void run(@NonNull AboutView view) {
                        view.needUpdate(updateVersion);
                        view.showContent();
                    }
                });
            }

            @Override
            public void noNeedUpdate() {
                ifViewAttached(new ViewAction<AboutView>() {
                    @Override
                    public void run(@NonNull AboutView view) {
                        view.noNeedUpdate();
                        view.showContent();
                    }
                });
            }

            @Override
            public void checkUpdateError(final String message) {
                ifViewAttached(new ViewAction<AboutView>() {
                    @Override
                    public void run(@NonNull AboutView view) {
                        view.checkUpdateError(message);
                        view.showContent();
                    }
                });
            }
        });
    }
}
