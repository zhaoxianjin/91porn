package com.u91porn.ui.main;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.data.model.Notice;
import com.u91porn.data.model.UpdateVersion;
import com.u91porn.ui.notice.NoticePresenter;
import com.u91porn.ui.update.UpdatePresenter;

/**
 * @author flymegoc
 * @date 2017/12/23
 */

public class MainPresenter extends MvpBasePresenter<MainView> implements IMain {

    private UpdatePresenter updatePresenter;
    private NoticePresenter noticePresenter;

    public MainPresenter(UpdatePresenter updatePresenter, NoticePresenter noticePresenter) {
        this.updatePresenter = updatePresenter;
        this.noticePresenter = noticePresenter;
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

    @Override
    public void checkNewNotice(int versionCode) {
        noticePresenter.checkNewNotice(versionCode, new NoticePresenter.CheckNewNoticeListener() {
            @Override
            public void haveNewNotice(final Notice notice) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.haveNewNotice(notice);
                    }
                });
            }

            @Override
            public void noNewNotice() {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.noNewNotice();
                    }
                });
            }

            @Override
            public void checkNewNoticeError(final String message) {
                ifViewAttached(new ViewAction<MainView>() {
                    @Override
                    public void run(@NonNull MainView view) {
                        view.checkNewNoticeError(message);
                    }
                });
            }
        });
    }
}
