package com.u91porn.ui.notice;

import android.arch.lifecycle.Lifecycle;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.u91porn.data.GitHubServiceApi;
import com.u91porn.data.model.Notice;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2018/1/26
 */

public class NoticePresenter extends MvpBasePresenter<NoticeView> implements INotice {
    private GitHubServiceApi gitHubServiceApi;
    private final static String CHECK_NEW_NOTICE_URL = "https://github.com/techGay/91porn/blob/master/notice.txt";
    private Gson gson;
    private LifecycleProvider<Lifecycle.Event> provider;

    public NoticePresenter(GitHubServiceApi gitHubServiceApi, Gson gson, LifecycleProvider<Lifecycle.Event> provider) {
        this.gitHubServiceApi = gitHubServiceApi;
        this.gson = gson;
        this.provider = provider;
    }

    @Override
    public void checkNewNotice(int versionCode) {
        checkNewNotice(versionCode, null);
    }

    public void checkNewNotice(final int versionCode, final CheckNewNoticeListener checkNewNoticeListener) {
        gitHubServiceApi.checkNewNotice(CHECK_NEW_NOTICE_URL)
                .map(new Function<String, Notice>() {
                    @Override
                    public Notice apply(String s) throws Exception {
                        Document doc = Jsoup.parse(s);
                        String text = doc.select("table.highlight").text();
                        return gson.fromJson(text, Notice.class);
                    }
                })
                .compose(RxSchedulersHelper.<Notice>ioMainThread())
                .compose(provider.<Notice>bindUntilEvent(Lifecycle.Event.ON_DESTROY))
                .subscribe(new CallBackWrapper<Notice>() {
                    @Override
                    public void onSuccess(final Notice notice) {
                        if (notice.getVersionCode() > versionCode) {
                            if (checkNewNoticeListener == null) {
                                ifViewAttached(new ViewAction<NoticeView>() {
                                    @Override
                                    public void run(@NonNull NoticeView view) {
                                        view.haveNewNotice(notice);
                                    }
                                });
                            } else {
                                checkNewNoticeListener.haveNewNotice(notice);
                            }
                        } else {
                            if (checkNewNoticeListener == null) {
                                ifViewAttached(new ViewAction<NoticeView>() {
                                    @Override
                                    public void run(@NonNull NoticeView view) {
                                        view.noNewNotice();
                                    }
                                });
                            } else {
                                checkNewNoticeListener.noNewNotice();
                            }
                        }
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        if (checkNewNoticeListener == null) {
                            ifViewAttached(new ViewAction<NoticeView>() {
                                @Override
                                public void run(@NonNull NoticeView view) {
                                    view.checkNewNoticeError(msg);
                                }
                            });
                        } else {
                            checkNewNoticeListener.checkNewNoticeError(msg);
                        }
                    }
                });
    }

    @Override
    public void checkUpdate(int versionCode) {

    }

    public interface CheckNewNoticeListener {
        void haveNewNotice(Notice notice);

        void noNewNotice();

        void checkNewNoticeError(String message);
    }
}
