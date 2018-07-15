package com.u91porn.ui.porn91forum.browse91porn;

import android.support.annotation.NonNull;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.u91porn.data.Forum91PronServiceApi;
import com.u91porn.data.model.Content91Porn;
import com.u91porn.parser.ParseForum91Porn;
import com.u91porn.rxjava.CallBackWrapper;
import com.u91porn.rxjava.RxSchedulersHelper;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

/**
 * @author flymegoc
 * @date 2018/1/24
 */

public class Browse91Presenter extends MvpBasePresenter<Browse91View> implements IBrowse91 {
    private Forum91PronServiceApi forum91PronServiceApi;

    public Browse91Presenter(Forum91PronServiceApi forum91PronServiceApi) {
        this.forum91PronServiceApi = forum91PronServiceApi;
    }

    public void setForum91PronServiceApi(Forum91PronServiceApi forum91PronServiceApi) {
        this.forum91PronServiceApi = forum91PronServiceApi;
    }

    @Override
    public void loadContent(Long tid, final boolean isNightModel) {
        forum91PronServiceApi.forumItemContent(tid)
                .map(new Function<String, Content91Porn>() {
                    @Override
                    public Content91Porn apply(String s) throws Exception {
                        return ParseForum91Porn.parseContent(s, isNightModel).getData();
                    }
                })
                .compose(RxSchedulersHelper.<Content91Porn>ioMainThread())
                .subscribe(new CallBackWrapper<Content91Porn>() {

                    @Override
                    public void onBegin(Disposable d) {
                        ifViewAttached(new ViewAction<Browse91View>() {
                            @Override
                            public void run(@NonNull Browse91View view) {
                                view.showLoading(true);
                            }
                        });
                    }

                    @Override
                    public void onSuccess(final Content91Porn content91Porn) {
                        ifViewAttached(new ViewAction<Browse91View>() {
                            @Override
                            public void run(@NonNull Browse91View view) {
                                view.showContent();
                                view.loadContentSuccess(content91Porn);
                            }
                        });
                    }

                    @Override
                    public void onError(final String msg, int code) {
                        ifViewAttached(new ViewAction<Browse91View>() {
                            @Override
                            public void run(@NonNull Browse91View view) {
                                view.showError(msg);
                            }
                        });
                    }
                });
    }
}
